# Spark Streaming项目实战

## 1.需求说明

- 需求1：今天到现在为止实战课程的访问量
- 需求2：今天到现在为止从搜索引擎引流过来的实战课程的访问量

## 2.互联网的访问日志概述

- 为什么要记录用户访问行为日志？
  1. 网站页面的访问量
  2. 网站用户的黏性
  3. 推荐

- 用户行为日志内容
  1. IP，账号，访问时间，区域
  2. 访问者使用客户端
  3. 进入模块
  4. 跳转链接地址

- 用户行为日志分析的意义
  1. 网站的眼睛
  2. 网站的神经
  3. 网站的大脑

## 3.功能开发及本地运行

- 1)使用Python脚本实时产生数据
  - 1.Python实时日志产生器开发

  ```python
    # coding=UTF-8/
    import random
    import time

    url_path = [
        "class/112.html",
        "class/128.html",
        "class/145.html",
        "class/146.html",
        "class/131.html",
        "class/130.html",
        "learn/821",
        "course/list"
    ]

    ip_slices = [132, 156, 124, 10, 29, 167, 143, 178, 123, 111, 56, 44, 32, 11, 0, 45, 66]

    http_referer = [
        "http://www.baidu.com/s?wd={query}",
        "http://www.sogou.com/web?query={query}",
        "http://cn.bing.com/search?q={query}",
        "http://search.yahoo.com/search?p={query}",
    ]

    search_keyword = [
        "Spark SQL实战",
        "Hadoop基础",
        "Storm实战",
        "Spark Streaming实战",
        "大数据面试"
    ]


    status_codes = ["200", "404", "500"]


    def sample_url():
        return random.sample(url_path, 1)[0]


    def sample_ip():
        slices = random.sample(ip_slices, 4)
        return ".".join([str(node) for node in slices])


    def sample_referer():
        if random.uniform(0, 1) > 0.5:
            return "-"
        refer_str = random.sample(http_referer, 1)
        query_str = random.sample(search_keyword, 1)
        return refer_str[0].format(query=query_str[0])


    def sample_status_code():
        return random.sample(status_codes, 1)[0]


    def generator_log(count=10):
        time_str = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
        f = open("/home/k.o/data/project/logs/access.log", "w+")
        while count >= 1:
            query_log = "{ip}\t{localTime}\t\"GET /{url} HTTP/1.1\"\t{status}\t{referer}".format(
                url=sample_url(),
                ip=sample_ip(),
                referer=sample_referer(),
                status=sample_status_code(),
                localTime=time_str)
            f.write(query_log + "\n")
            print(query_log)
            count = count - 1


    if __name__ == '__main__':
        generator_log(100)
  ```

  - 2.通过定时任务定时调度自动产生日志：linux crontab
    - 1.网站：[http://tool.lu/crontab](http://tool.lu/crontab)
    - 2.每一分钟执行一次的crontab表达式

    ```shell
    */1 * * * *
    ```

    - 3.创建linux脚本：generator_log.sh

    ```shell
    python3 /home/k.o/data/project/generator_log.py
    ```

    - 4.添加可执行权限

    ```shell
    chmod u+x generator_log.sh
    ```

    - 5.配置Crontab

    ```shell
    */1 * * * * /home/k.o/data/project/generator_log.sh
    ```

- 2)对接Python日志产生器输出的日志到Flume，streaming_project.conf
  - 选型：access.log ==> 控制台输出
  - exec
  - memory
  - log(后续对接kafka)

  ```properties
    # Agent exec-memory-logger
    exec-memory-logger.sources = exec-source
    exec-memory-logger.sinks = logger-sink
    exec-memory-logger.channels = memory-channel

    # define sources
    exec-memory-logger.sources.exec-source.type = exec
    exec-memory-logger.sources.exec-source.command = tail -F /home/k.o/data/project/logs/access.log
    exec-memory-logger.sources.exec-source.shell = /bin/sh -c

    # define channel
    exec-memory-logger.channels.memory-channel.type = memory

    # define sink
    exec-memory-logger.sinks.logger-sink.type = logger

    exec-memory-logger.sources.exec-source.channels = memory-channel
    exec-memory-logger.sinks.logger-sink.channel = memory-channel
  ```

  - 启动Flume

  ```shell
    flume-ng agent \
    --name exec-memory-logger \
    --conf $FLUME_HOME/conf \
    --conf-file /home/k.o/data/project/streaming_project.conf \
    -Dflume.root.logger=INFO,console
  ```

- 3)日志数据对接到Kafka
  - 1.启动Zookeeper：zkServer.sh start
  - 2.启动Kafka：sudo ./kafka-server-start.sh -daemon ../config/server.properties
  - 3.修改Flume配置文件，使flume数据到Kafka，flume_kafka_stream.conf
  
  ```properties
    # Agent exec-memory-kafka
    exec-memory-kafka.sources = exec-source
    exec-memory-kafka.sinks = kafka-sink
    exec-memory-kafka.channels = memory-channel

    # define sources
    exec-memory-kafka.sources.exec-source.type = exec
    exec-memory-kafka.sources.exec-source.command = tail -F /home/k.o/data/project/logs/access.log
    exec-memory-kafka.sources.exec-source.shell = /bin/sh -c

    # define channel
    exec-memory-kafka.channels.memory-channel.type = memory

    # define sink
    exec-memory-kafka.sinks.kafka-sink.type = org.apache.flume.sink.kafka.KafkaSink
    exec-memory-kafka.sinks.kafka-sink.kafka.topic = kafka-streaming-topic
    exec-memory-kafka.sinks.kafka-sink.kafka.bootstrap.servers = 192.168.37.131:9092
    exec-memory-kafka.sinks.kafka-sink.kafka.kafka.flumeBatchSize = 20
    exec-memory-kafka.sinks.kafka-sink.kafka.requiredAcks = 1

    exec-memory-kafka.sources.exec-source.channels = memory-channel
    exec-memory-kafka.sinks.kafka-sink.channel = memory-channel
  ```

- 4)启动flume，kafka消费校验

```shell
flume-ng agent \
--name exec-memory-kafka \
--conf $FLUME_HOME/conf \
--conf-file /home/k.o/data/project/flume_kafka_stream.conf \
-Dflume.root.logger=INFO,console

# Kafka消费校验
sudo ./bin/kafka-console-consumer.sh --bootstrap-server 192.168.37.131:9092 --topic "kafka-streaming-topic" --from-beginning
```

- 5)数据清洗操作
  - 数据：ClickLog(44.0.124.45,20181031221801,146,500,http://search.yahoo.com/search?p=Spark SQL实战)
  - 代码

  ```scala
    package org.ko.spark.streaming.project.spark

    import kafka.serializer.StringDecoder
    import org.apache.spark.SparkConf
    import org.apache.spark.streaming.kafka.KafkaUtils
    import org.apache.spark.streaming.{Seconds, StreamingContext}
    import org.ko.spark.streaming.project.domain.ClickLog
    import org.ko.spark.streaming.project.utils.DateUtils

    /**
    * 192.168.37.131:9092 kafka-streaming-topic
    * 要求运行时kafka版本为0.8Q
    */
    object LogStatStreamingApp {

    def main(args: Array[String]): Unit = {

        if (args.length != 2) {
        System.err.println("Usage: LogStatStreamingApp <brokers> <topics>")
        System.exit(1)
        }

        val sparkConf = new SparkConf()
        .setAppName("LogStatStreamingApp")
        .setMaster("local[2]")

        val Array(brokers, topics) = args

        val ssc = new StreamingContext(sparkConf, Seconds(60))

        val kafkaParams = Map[String, String](
        "metadata.broker.list" -> brokers
        )

        val topicSet = topics.split(",").toSet

        //TODO... Spark Streaming 如何对接Kafka
        val messages = KafkaUtils
        .createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicSet)

        //测试步骤一：测试数据接收
    //    messages.map(_._2).count().print()

        //测试步骤二： 数据清洗
        val logs = messages.map(_._2)
        val cleanData = logs.map {line =>
        val infos = line.split("\t")

        //infos(2) = "GET /class/112.html HTTP/1.1"
        //url = /class/112.html
        val url = infos(2).split(" ")(1)
        var courseId = 0

        //拿到课程编号
        if (url.startsWith("/class")) {
            val courseIdHTML = url.split("/")(2)
            courseId = courseIdHTML.substring(0, courseIdHTML.lastIndexOf(".")).toInt
        }
        ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId, infos(3).toInt, infos(4))
        }.filter(_.courseId != 0)

        cleanData.print()

        ssc.start()
        ssc.awaitTermination()

    }

    }

  ```

- 6)需求1：今天到现在为止实战课程的访问量

  - 需求分析

  ```log
  # 今天到现在为止 实战课程 访问量

  yyyyMMdd courseId

  使用数据库来进行存储我们的统计结果
    Spark Streaming把统计结果写入到数据库里面
    可视化前端根据：yyyyMMdd courseId 把数据库里面的统计结果展示出来

  选择什么数据库作为统计结果存储呢？
    1.RDBMS: MySQL, Oracle
        day | courseId | click_count
        20171111 | 1 | 10
        20171111 | 2 | 10
      下一个批次数据进来以后:
        20171111 + 1 ===> click_count + 下一个批次的统计结果

    2.NoSQL: HBase, Redis, MongoDB
        HBase: 20171111 + 1 ===> click_count + 下一个批次的统计结果

    3.前提：ZK，HDFS，HBase

    4.HBase表设计
      - 创建HBase表：create 'course_clickcount', 'info'
      - RowKey设计：day_courseId

  ```

- 6)需求1：功能一 + 从搜索引擎引流过来的

```log
HBase表设计：
    create 'course_search_clickcount', 'info'

rowKey设计：根据业务需求设计
    20171111 + search + 1

```

## 4.生产环境运行