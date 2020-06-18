# GreenPlum不完全指北

## Greenplum数据库管理

### 0x01.数据库管理

Greenplum和PostgreSql对数据库的管理基本类似，这里不在重复介绍了。

```sql
CREATE DATABASE <dbname> [ [WITH] [OWNER [=] <dbowner>] [ENCODING [=] <encoding>];
```

- `<dbname>`：待创建的数据库名称。
- `<dbowner>`：拥有新数据库的数据库用户名，默认为执行该命令的用户。
- `<encoding>`：在新数据库中使用的字符集编码。指定一个字符串常量（例如 '`SQL_ASCII`'）, 一个整数编码号，默认为`utf-8`。

### 0x02.Schema管理

Schema是数据库的命名空间，它是一个数据库内部的对象（表、索引、视图、存储过程、操作符）的集合。Schema在每个数据库中是唯一的。每个数据库都一个名为public的默认Schema。

如果用户没有创建任何Schema，对象会被创建在public schema中。所有的该数据库角色（用户）都在默认的public schema中拥有CREATE和USAGE特权。

#### 1.创建Schema

- 使用`**CREATE SCHEMA**`命令来创建一个新的Schema，命令如下：

```sql
CREATE SCHEMA <schema_name> [AUTHORIZATION <username>]
```

- `<schema_name>`：schema名称。
- `<username>`：如指定authorization username，则创建的schema属于该用户。否则，属于执行该命令的用户。

#### 2.**设置Schema搜索路径**

数据库的search_path用于配置参数设置Schema的搜索顺序。

使用**`ALTER DATABASE`**命令可以设置搜索路径。例如：

```sql
ALTER DATABASE mydatabase SET search_path TO myschema, public, pg_catalog;
```

您也可以使用`**ALTER ROLE**`命令为特定的角色（用户）设置search_path。例如：

```sql
ALTER ROLE sally SET search_path TO myschema, public, pg_catalog;
```

#### 3.**查看当前Schema**

使用`current_schema()`函数可以查看当前的Schema。例如：

```sql
SELECT current_schema();
```

您也可以使用`SHOW`命令查看当前的搜索路径。例如：

```sql
SHOW search_path;
```

#### 4.**删除Schema**

使用`DROP SCHEMA`命令删除一个空的Schema。例如：

```sql
DROP SCHEMA myschema;
```

**说明：**默认情况下，Schema它必须为空才可以删除。

删除一个Schema连同其中的所有对象（表、数据、函数等等），可以使用：

```sql
DROP SCHEMA myschema CASCADE;
```

### 0x03.空间回收

#### 1.**背景信息**

表中的数据被删除或更新后（UPDATE/DELTE），物理存储层面并不会直接删除数据，而是标记这些数据不可见，所以会在数据页中留下很多“空洞”，在读取数据时，这些“空洞”会随数据页一起加载，拖慢数据扫描速度，需要定期回收删除的空间。

#### **空间回收方法**

使用

```sql
VACUUM
```

命令，可以对表进行重新整理，回收空间，以便获取更好的数据读取性能。VACUUM命令如下：

```sql
VACUUM [FULL] [FREEZE] [VERBOSE] [table];
```

VACUUM 会在页内进行整理，VACUUM FULL会跨数据页移动数据。 VACUUM执行速度更快， VACUUM FULL执行地更彻底，但会请求排他锁。建议定期对系统表进行VACUUM（每周一次）。

#### **使用建议**

什么情况下做VACUUM？

- 不锁表回收空间，只能回收部分空间。
- 频率：对于有较多实时更新的表，每天做一次。
- 如果更新是每天一次批量进行的，可以在每天批量更新后做一次。
- 对系统影响：不会锁表，表可以正常读写。会导致CPU、I/O使用率增加，可能影响查询的性能。

什么情况下做VACUUM FULL？

- 锁表，通过重建表回收空间,可回收所有空洞空间。对做了大量更新后的表，建议尽快执行VACUUM FULL。
- 频率：至少每周执行一次。如果每天会更新几乎所有数据，需要每天做一次。
- 对系统影响：会对正在进行vacuum full的表锁定，无法读写。会导致CPU、I/O使用率增加。建议在维护窗口进行操作。

#### **查询需要执行VACUUM的表**

AnalyticDB for PostgreSQL提供了一个gp_bloat_diag视图，统计当前页数和实际需要页数的比例。通过analyze table来收集统计信息之后，查看该视图。

```
gpadmin=# SELECT * FROM gp_toolkit.gp_bloat_diag;
bdirelid | bdinspname | bdirelname | bdirelpages | bdiexppages |                bdidiag                
----------+------------+------------+-------------+-------------+---------------------------------------
    21488 | public     | t1         |          97 |           1 | significant amount of bloat suspected
(1 row)
```

其结果只包括发生了中度或者显著膨胀的表。当实际页面数和预期页面的比率超过4但小于10时，就会报告为中度膨胀。当该比率超过10时就会报告显著膨胀。对于这些表，可以考虑进行VACUUM FULL来回收空间。

#### **VACUUM FREEZE的使用**

AnalyticDB for PostgreSQL执行的所有事务都有唯一的事务ID(XID)，XID是单调递增的，上限是20亿。

随着数据库执行事务的增多，为防止XID超过极限，在XID接近xid_stop_limit-xid_warn_limit(默认500000000)时， ADB for PG会对执行事务的sql返回warning信息，提醒用户：

```
WARNING: database "database_name" must be vacuumed within number_of_transactions transactions
```

## Greenplum数据表管理

Created: Apr 14, 2020 12:53 PM

**官网：**[Pivotal Greenplum 官方文档](http://gpdb.docs.pivotal.io/43330/ref_guide/sql_commands/CREATE_TABLE.html)

### 0x01.表管理

Greenplum 数据库中的表与任何一种关系型数据库中的表类似，不同的是表中的行被分布在不同Segment上，表的分布策略决定了在不同Segment上面的分布情况。

#### 1.创建普通表

CREATE TABLE命令用于创建一个表，创建表时可以定义以下内容：

- 表的列以及数据类型
- 表的约束
- 表分布键定义
- 表存储格式
- 分区表定义

使用`CREATE TABLE`命令创建表，格式如下：

```sql
CREATE TABLE table_name ( 
[ { column_name data_type [ DEFAULT default_expr ]   -- 表的列定义
   [column_constraint [ ... ]                        -- 列的约束定义
] 
   | table_constraint                                -- 表级别的约束定义                            
   ])
   [ WITH ( storage_parameter=value [, ... ] )       -- 表存储格式定义
   [ DISTRIBUTED BY (column, [ ... ] ) | DISTRIBUTED RANDOMLY ]  -- 表的分布键定义          
   [ partition clause]                               -- 表的分区定义
```

**示例：**

示例中的建表语句创建了一个表，使用**trans_id**作为分布键，并基于 **date** 设置了RANGE分区功能。

```sql
CREATE TABLE sales (
  trans_id int,
  date date, 
  amount decimal(9,2), 
  region text)
  DISTRIBUTED BY (trans_id)  
  PARTITION BY RANGE(date)    
  (start (date '2018-01-01') inclusive
   end (date '2019-01-01') exclusive every (interval '1 month'),
   default partition outlying_dates);
```

#### 2.创建临时表

临时表（Temporary Table）会在会话结束时自动删除，或选择性地在当前事务结束的时候删除，用于存储 临时中间结果。创建临时表的命令如下：

```sql
CREATE TEMPORARY TABLE table_name(…)
    [ON COMMIT {PRESERVE ROWS | DELETE ROWS | DROP}]
```

**说明**

临时表的行为在事务块结束时的行为可以通过上述语句中的`ON COMMIT`来控制。

- `PRESERVE ROWS`：在事务结束时候保留数据，这是默认的行为。
- `DELETE ROWS`：在每个事务块结束时，临时表的所有行都将被删除。
- `DROP`：在当前事务结束时，会删除临时表。

**示例：**

创建一个临时表，事务结束时候删除该临时表。

```sql
CREATE TEMPORARY TABLE temp_foo (a int, b text) ON COMMIT DROP;
```

#### 3.**表约束的定义**

您可以在列和表上定义约束来限制表中的数据，但是有以下一些限制：

- `CHECK`约束引用的列只能在其所在的表中。
- `UNIQUE`和`PRIMARY KEY`约束必须包含分布键列，`UNIQUE`和`PRIMARY KEY`约束不支持追加优化表和列存表。
- 允许`FOREIGN KEY`约束，但实际上并不会做外键约束检查。
- 分区表上的约束必须应用到所有的分区表上，不能只应用于部分分区表。

约束命令格式如下：

```sql
UNIQUE ( column_name [, ... ] )
   | PRIMARY KEY ( column_name [, ... ] ) 
   | CHECK ( expression )
   | FOREIGN KEY ( column_name [, ... ] )
            REFERENCES table_name [ ( column_name [, ... ] ) ]
            [ key_match_type ]
            [ key_action ]
            [ key_checking_mode ]
```

**检查约束（Check Constraints)**

检查约束（Check Constraints）指定列中的值必须满足一个布尔表达式，例如：

```sql
CREATE TABLE products
            ( product_no integer,
              name text,
              price numeric CHECK (price > 0) );
```

**非空约束（Not-Null Constraints)**

非空约束（Not-Null Constraints）指定列不能有空值，例如：

```sql
CREATE TABLE products
       ( product_no integer NOT NULL,
         name text NOT NULL,
         price numeric );
```

**唯一约束（Unique Constraints)**

唯一约束（Unique Constraints）确保一列或者一组列中包含的数据对于表中所有的行都是唯一的。包含唯一约束的表必须是哈希分布，并且约束列需要包含分布键列，例如：

```sql
CREATE TABLE products
       ( product_no integer UNIQUE,
         name text,
         price numeric)
      DISTRIBUTED BY (product_no);
```

**主键（Primary Keys)**

主键约束（Primary Keys Constraints）是一个UNIQUE约束和一个 NOT NULL约束的组合。包含主键约束的表必须是哈希分布，并且约束列需要包含分布键列。如果一个表具有主键，这个列（或者这一组列）会被默认选中为该表的分布键，例如：

```sql
CREATE TABLE products
       ( product_no integer PRIMARY KEY,
         name text,
         price numeric)
      DISTRIBUTED BY (product_no);
```

# 0x02.表分布定义

#### 1.**选择表分布策略**

Greenplum 支持三种数据在节点间的分布方式，按指定列的哈希（HASH）分布、随机（RANDOMLY）分布、复制（REPLICATED）分布。

```sql
CREATE TABLE table_name (...) [ DISTRIBUTED BY (column  [,..] ) | DISTRIBUTED RANDOMLY | DISTRIBUTED REPLICATED ]
```

建表语句`**CREATE TABLE**`支持如下三个分布策略的子句：

- **`DISTRIBUTED BY (column, [ ... ]）`**指定数据按分布列的哈希值在节点（Segment）间分布，根据分布列哈希值将每一行分配给特定节点（Segment）。相同的值将始终散列到同一个节点。选择唯一的分布键（例如Primary Key）将确保较均匀的数据分布。哈希分布是表的默认分布策略， 如果创建表时未提供DISTRIBUTED子句，则将PRIMARY KEY或表的第一个合格列用作分布键。如果表中没有合格的列，则退化为随机分布策略。
- **`DISTRIBUTED RANDOMLY`**指定数据按循环的方式均匀分配在各节点 （Segment） 间，与哈希分布策略不同，具有相同值的数据行不一定位于同一个segment上。虽然随机分布确保了数据的平均分布，但只建议当表没有合适的离散分布的数据列作为哈希分布列时采用随机分布策略。
- **`DISTRIBUTED REPLICATED`** 指定数据为复制分布，即每个节点（Segment）上有该表的全量数据，这种分布策略下表数据将均匀分布，因为每个segment都存储着同样的数据行，当有大表与小表join，把足够小的表指定为replicated也可能提升性能。

![https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/9923419751/p74594.png](https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/9923419751/p74594.png)

**示例：**

示例中的建表语句创建了一个哈希（Hash）分布的表，数据将按分布键的哈希值被分配到对应的节点Segment 数据节点。

```sql
CREATE TABLE products (name varchar(40), 
                       prod_id integer,
                       supplier_id integer)
                       DISTRIBUTED BY (prod_id);                
```

示例中的建表语句创建了一个随机（Randomly）分布的表，数据被循环着放置到各个 Segment 数据节点。 当表没有合适的离散分布的数据列作为哈希分布列时，可以采用随机分布策略。

```sql
CREATE TABLE random_stuff (things text,
                           doodads text,
                           etc text)
                           DISTRIBUTED RANDOMLY;
```

示例中的建表语句创建了一个复制（Replicated）分布的表，每个 Segment 数据节点都存储有一个全量的表数据。

```sql
CREATE TABLE replicated_stuff (things text,
                           doodads text,
                           etc text)
                           DISTRIBUTED REPLICATED;
```

对于按分布键的简单查询，包括 UPDATE/DELETE 等语句，AnalyticDB for PostgreSQL 具有按节点的分布键进行数据节点裁剪的功能，例如products表使用prod_id作为分布键，以下查询只会被发送到满足prod_id=101的segment上执行，从而极大提升该 SQL 执行性能：

```sql
select * from products where prod_id = 101;
```

#### 2.**表分布键选择原则**

合理规划分布键，对表查询的性能至关重要，有以下原则需要关注：

- 选择数据分布均匀的列或者多个列：若选择的分布列数值分布不均匀，则可能导致数据倾斜。某些Segment分区节点存储数据多(查询负载高)。根据木桶原理，时间消耗会卡在数据多的节点上。故不应选择bool类型，时间日期类型数据作为分布键。
- 选择经常需要 JOIN 的列作为分布键，可以实现图一所示**本地关联（Collocated JOIN）**计算，即当JOIN键和分布键一致时，可以在 Segment分区节点内部完成JOIN。否则需要将一个表进行重分布（**Redistribute motion**）来实现图二所示**重分布关联（Redistributed Join）**或者广播其中小表(**Broadcast motion**)来实现图三所示**广播关联（Broadcast Join）**，**后两种方式都会有较大的网络开销**。
- 尽量选择高频率出现的查询条件列作为分布键，从而可能实现按分布键做节点 segment 的裁剪。
- 若未指定分布键，默认表的主键为分布键，若表没有主键，则默认将第一列当做分布键。
- 分布键可以被定义为一个或多个列。例如：

    ```sql
    create table t1(c1 int, c2 int) distributed by (c1,c2);
    ```

- 谨慎选择随机分布DISTRIBUTED RANDOMLY，这将使得上述本地关联，或者节点裁剪不可能实现。

![https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/9923419751/p51143.png](https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/9923419751/p51143.png)

![https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/0033419751/p51142.png](https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/0033419751/p51142.png)

![https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/0033419751/p51137.png](https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/0033419751/p51137.png)

#### 3.**表分布键的约束**

- 分布键的列不能被更新（UPDATE）。
- 主键和唯一键必须包含分布键。例如：**说明** 由于主键c1不包含分布键c2，所以建表语句返回失败。

    ```sql
    create table t1(c1 int, c2 int, primary key (c1)) distributed by (c2);
    ```

    ```sql
    ERROR: PRIMARY KEY and DISTRIBUTED BY definitions incompatible
    ```

- Geometry类型和用户自定义数据类型不能作为分布键。

#### 4.**数据倾斜检查和处理**

当某些表上的查询性能差时，可以查看是否是分区键设置不合理造成了数据倾斜，例如：

```sql
create table t1(c1 int, c2 int) distributed by (c1);
```

您可以通过下述语句来查看表的数据倾斜情况。

```sql
select gp_segment_id,count(1) from  t1 group by 1 order by 2 desc;
 gp_segment_id | count  
---------------+--------
             2 | 131191
             0 |     72
             1 |     68
(3 rows)
```

如果发现某些 Segment上存储的数据明显多于其他 Segment，该表存在数据倾斜。建议选取数据分布平均的列作为分布列，比如通过`ALTER TABLE`命令更改C2为分布键。

```sql
alter table t1 set distributed by (c2);
```

表t1的分布键被改为c2，该表的数据按照c2被重新分布，数据不再倾斜。

### 0x03.表分区的定义

将大表定义为分区表，从而将其分成较小的存储单元，根据查询条件，会只扫描满足条件的分区而避免全表扫描，从而显著提升查询性能。

#### 1.**支持的表分区类型**

- **范围（RANGE）分区**：基于一个数值型范围划分数据，例如按着日期区间定义。
- **值（LIST）分区**：基于一个值列表划分数据，例如按着 城市属性定义。
- **多级分区表**：上述两种类型的多级组合。

![https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/8664544851/p51136.jpg](https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/8664544851/p51136.jpg)

上图为一个多级分区表设计实例，一级分区采用按月的区间（Range）分区，二级分区采用按地区的值（List）分区设计。

#### 2.**创建范围(RANGE)分区表**

用户可以通过给出一个START值、一个END值以及一个定义分区增量值的子句让数据库自动产生分区。默认情况下，START值总是被包括在内而END值总是被排除在外，例如：

```sql
CREATE TABLE sales (id int, date date, amt decimal(10,2))
DISTRIBUTED BY (id)
PARTITION BY RANGE (date)
( START (date '2016-01-01') INCLUSIVE
   END (date '2017-01-01') EXCLUSIVE
   EVERY (INTERVAL '1 day') );
```

也可以创建一个按数字范围分区的表，使用单个数字数据类型列作为分区键列，例如：

```sql
CREATE TABLE rank (id int, rank int, year int, gender char(1), count int)
DISTRIBUTED BY (id)
PARTITION BY RANGE (year)
( START (2006) END (2016) EVERY (1), 
  DEFAULT PARTITION extra ); 
```

#### 3.**创建值（LIST )分区表**

一个按列表分区的表可以使用任意允许等值比较的数据类型列作为它的分区键列。对于列表分区，您必须为每一个用户想要创建的分区（列表值）声明一个分区说明，例如：

```sql
CREATE TABLE rank (id int, rank int, year int, gender char(1), count int ) 
DISTRIBUTED BY (id)
PARTITION BY LIST (gender)
( PARTITION girls VALUES ('F'), 
  PARTITION boys VALUES ('M'), 
  DEFAULT PARTITION other );
```

#### 4.**创建多级分区表**

支持创建多级的分区表。下述建表语句创建了具有三级表分区的表。一级分区在year字段上使用RANGE分区，二级分区在month字段上做RANGE分区，三级分区在region上做了LIST分区。

```sql
CREATE TABLE sales (id int, year int, month int, day int, 
region text)
DISTRIBUTED BY (id)
PARTITION BY RANGE (year)            
  SUBPARTITION BY RANGE (month)     
  SUBPARTITION TEMPLATE (
       START (1) END (13) EVERY (1), 
       DEFAULT SUBPARTITION other_months )
  SUBPARTITION BY LIST (region)    
    SUBPARTITION TEMPLATE (
       SUBPARTITION usa VALUES ('usa'),
       SUBPARTITION europe VALUES ('europe'),
       SUBPARTITION asia VALUES ('asia'),
       DEFAULT SUBPARTITION other_regions)
( START (2008) END (2016) EVERY (1),
  DEFAULT PARTITION outlying_years);
```

#### 5.**分区定义的粒度**

通常分区表的定义都涉及到粒度问题，比如按时间分区，究竟是按天，按周，按月等。粒度越细，每张表的数据就越少，但是分区的数量就越多，反之亦然。关于分区的数量，没有绝对的标准，一般分区的数量在 **200** 左右已经算是比较多了。分区表数目过多，会有多方面的影响，比如查询优化器生成执行计划较慢，同时很多维护工作也会变慢，比如VACUUM等。

**注意**

请对多级分区格外谨慎，因为分区文件的数量可能会增长得非常快。例如，如果一个表被按照月和城市划分并且有24个月以及1,00个城市，那么表分区的总数就是2400。特别对于列存表，会把每一列存在一个物理表中，因此如果这个表有100个列，系统就需要为该表管理十多万个文件。

#### 6.**分区表查询优化**

Greenplum 支持分区表的分区裁剪功能，根据查询条件会只扫描所需的数据分区而避免扫描整个表的全部内容，提升查询性能。例如对于如下查询：

```sql
explain select * from sales 
  where year = 2008 
    and  month = 1 
    and  day = 3 
    and region = 'usa';
```

由于查询条件落在一级分区2008的二级子分区1的三级子分区 'usa' 上，查询只会扫描读取这一个三级子分区数据。如下其查询计划显示，总计468个三级子分区中，只有一个分区被读取。

```sql
Gather Motion 4:1  (slice1; segments: 4)  (cost=0.00..431.00 rows=1 width=24)
  ->  Sequence  (cost=0.00..431.00 rows=1 width=24)
        ->  Partition Selector for sales (dynamic scan id: 1)  (cost=10.00..100.00 rows=25 width=4)
              Filter: year = 2008 AND month = 1 AND region = 'usa'::text
              Partitions selected:  1 (out of 468)
        ->  Dynamic Table Scan on sales (dynamic scan id: 1)  (cost=0.00..431.00 rows=1 width=24)
              Filter: year = 2008 AND month = 1 AND day = 3 AND region = 'usa'::text
```

#### 7.**查询分析表定义**

可以通过如下 SQL 语句查询一个分区表的所有分区定义信息：

```sql
SELECT 
  partition boundary, 
  partition tablename, 
  partition name,
  partition level, 
  partition rank
FROM pg_partitions 
WHERE tablename='sales';
```

### 0x04.表存储格式的定义

Greenplum支持多种存储格式。当您创建一个表时，可以选择表的存储格式为行存表或者列存表。

#### 1.**行存表**

默认情况下，Greenplum 创建的是行存表（Heap Table），使用的 PostgreSQL 堆存储模型。行存表适合数据更新较频繁的场景，或者采用INSERT方式的实时写入的场景，同时当行存表建有于B-Tree索引时，具备更好的点查询数据检索性能。

**示例：**

下述语句创建了一个默认堆存储类型的行存表。

```sql
CREATE TABLE foo (a int, b text) DISTRIBUTED BY (a);
```

#### 2.**列存表**

列存表（Column-Oriented Table）的按列存储格式，数据访问只会读取涉及的列，适合少量列的数据查询、聚集等数据仓库应用场景，在此类场景中，列存表能够提供更高效的I/O。但列存表不适合频繁的更新操作或者大批量的INSERT写入场景，这时其效率较低。列存表的数据写入建议采用COPY等批量加载方式。列存表可以提供平均 3-5倍的较高数据压缩率。

**示例：**

列存表必须是追加优化表。例如，要创建一个列存表，必须指定为 "appendonly=true“ 。

```sql
CREATE TABLE bar (a int, b text) 
    WITH (appendonly=true, orientation=column)
    DISTRIBUTED BY (a);
```

#### 3.**压缩**

压缩主要用于列存表或者追加写（"appendonly=true“）的行存表，有以下两种类型的压缩可用。

- 应用于整个表的表级压缩。
- 应用到指定列的列级压缩。用户可以为不同的列应用不同的列级压缩算法。

目前Greenplum支持zlib和RLE_TYPE压缩算法（如果指定QuickLZ压缩算法，内部会使用zlib算法替换），RLE_TYPE算法只适用于列存表。

**示例：**

创建一个使用zlib压缩且压缩级别为5的追加优化表。

```sql
CREATE TABLE foo (a int, b text) 
   WITH (appendonly=true, compresstype=zlib, compresslevel=5);
```

## Greenplum索引管理

### 0x01.**索引类型**

Greenplum支持B-tree索引，位图索引，GIN索引（6.0版本支持）和GiST索引（6.0版本支持），不支持Hash索引。

**说明** B-tree索引是默认的索引类型。位图索引（Bitmap Index）为每一个键值都存储一个位图，位图索引提供了和常规索引相同的功能但索引空间大大减少。对于拥有100至100,000个可区分值的列并且当被索引列常常与其他被索引列联合查询时，位图索引表现最好。

### 0x02.**创建索引**

使用**`CREATE INDEX`**命令在表上创建一个B-tree索引。

**示例：**

在employee表的gender列上创建一个B-tree索引。

```sql
CREATE INDEX gender_idx ON employee (gender);
```

在films表中的title列上创建一个位图索引。

```sql
CREATE INDEX title_bmp_idx ON films USING bitmap (title);
```

在lineitem表的l_comment列上创建一个GIN索引支持全文搜索（6.0版本支持）。

```sql
CREATE INDEX lineitem_idx ON lineitem USING gin(to_tsvector('english', l_comment));
```

在arrayt表的intarray数组类型列上创建一个GIN索引（6.0版本支持）。

```sql
CREATE INDEX arrayt_idx ON arrayt USING gin(intarray);
```

在customer表的c_comment列上创建一个GiST索引支持全文搜索（6.0版本支持）。

```sql
CREATE INDEX customer_idx ON customer USING gist(to_tsvector('english', c_comment));
```

### 0x03.**重建索引**

使用**`REINDEX INDEX`**命令重建索引。

**示例：**

重建索引my_index。

```sql
REINDEX INDEX my_index;
```

重建my_table表上的所有索引。

```sql
REINDEX TABLE my_table;
```

### 0x04.**删除索引**

使用**`DROP INDEX`**命令删除一个索引。

**示例：**

```sql
DROP INDEX title_idx;
```

**说明** 在加载大量数据时，先删除所有索引并载入数据，然后重建索引会更快。

### 0x05.**索引的选择原则**

- 用户的查询负载。

    索引能改进查询返回单一记录或者非常小的数据集的性能，例如OLTP类型查询。

- 压缩表。

    在被压缩过的追加优化表上，索引也可以提高返回一个目标行集合的查询的性能，只有必要的行才会被解压。

- 避免在频繁更新的列上建立索引。

    在一个被频繁更新的列上建立索引会增加该列被更新时所要求的写操作数据量。

- 创建选择性高的B-tree索引。

    例如，如果一个表有1000行并且一个列中有800个不同的值，则该索引的选择度为0.8，索引的选择性会比较高。唯一索引的选择度总是1.0。

- 为低选择度的列使用位图索引。对于区分值区间在100至100,000之间的列位图索引表现最好。
- 索引在连接中用到的列。

    在被用于频繁连接的一个列（例如一个外键列）上的索引能够提升连接性能，因为这让查询优化器有更多的连接方法可以使用。

- 索引在谓词中频繁使用的列。

    频繁地在WHERE子句中被引用的列是索引的首选。

- 避免创建重叠的索引。

    例如在多列索引中，具有相同前导列的索引是冗余的。

- 批量载入前删掉索引。

    对于载入大量数据到一个表中，请考虑先删掉索引并且在数据装载完成后重建它们，这常常比更新索引更快。

- 测试并且比较使用索引和不使用索引的查询性能。

    只有被索引列的查询性能有提升时才增加索引。

- 创建完索引，建议对标执行analyze。