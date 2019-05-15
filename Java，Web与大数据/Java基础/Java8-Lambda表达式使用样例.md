# Java8-Lambda表达式使用与Stream API

## 0x00. 前置数据

```java

private List<People> peoples = null;

@BeforeEach void before () {
    peoples = new ArrayList<>();
    peoples.add(new People("K.O1", 21, new Date()));
    peoples.add(new People("K.O3", 23, new Date()));
    peoples.add(new People("K.O4", 24, new Date()));
    peoples.add(new People("K.O5", 25, new Date()));
    peoples.add(new People("K.O2", 22, new Date()));
    peoples.add(new People("K.O6", 26, new Date()));
}
```

## 0x01. 提取对象中的一列

```java
/**
* 提取1列
*/
@Test void whenExtractColumnSuccess () {
    //第一种写法
    List<Integer> ages1 = peoples.stream().map(people -> people.getAge()).collect(Collectors.toList());
    System.out.println("###println: args1----");
    ages1.forEach(System.out::println);

    //简单一点的写法
    List<Integer> ages2 = peoples.stream().map(People::getAge).collect(Collectors.toList());
    System.out.println("###println: args2----");
    ages1.forEach(System.out::println);
}
```

```log
###println: args1----
21
22
23
24
25
26
###println: args2----
21
22
23
24
25
26
```

## 0x02. 通过字段中条件过滤集合列表

```java
/**
    * 只要年纪大于25岁的人
    */
@Test void whenFilterAgeGT25Success () {
    List<People> peoples1 = peoples.stream().filter(x -> x.getAge() > 25).collect(Collectors.toList());
    peoples1.forEach(x -> System.out.println(x.toString()));
}
```

```log
People{name='K.O6', age=26, birthday=Wed May 15 22:20:22 CST 2019}
```

## 0x03. 列表中对象数值型列数据求和

```java
/**
    * 求和全部年纪
    */
@Test void sumAllPeopleAgeSuccess () {
    Integer sum1 = peoples.stream().collect(Collectors.summingInt(People::getAge));
    System.out.println("###sum1: " + sum1);
    Integer sum2 = peoples.stream().mapToInt(People::getAge).sum();
    System.out.println("###sum2: " + sum2);
}
```

```log
    ###sum1: 141
    ###sum2: 141
```

## 0x04. 取出集合符合条件的第一个元素

```java
/**
    * 取出年纪为25岁的人
    */
@Test void extractAgeEQ25Success () {
    Optional<People> optionalPeople =  peoples.stream().filter(x -> x.getAge() == 25).findFirst();
    if (optionalPeople.isPresent()) System.out.println("###name1: " + optionalPeople.get().getName());

    //简写
    peoples.stream().filter(x -> x.getAge() == 25).findFirst().ifPresent(x -> System.out.println("###name2: " + x.getName()));
}
```

```log
###name1: K.O5
###name2: K.O5
```

## 0x05. 对集合中对象字符列按规则拼接

```java
/**
    * 逗号拼接全部名字
    */
@Test void printAllNameSuccess () {
    String names = peoples.stream().map(People::getName).collect(Collectors.joining(","));
    System.out.println(names);
}
```

```log
K.O1,K.O2,K.O3,K.O4,K.O5,K.O6
```

## 0x06. 将集合元素提取，转为Map

```java
/**
    * 将集合转成(name, age) 的map
    */
@Test void list2MapSuccess () {
    Map<String, Integer> map1 = peoples.stream().collect(Collectors.toMap(People::getName, People::getAge));
    map1.forEach((k, v) -> System.out.println(k + ":" + v));

    System.out.println("--------");

    //(name object)
    Map<String, People> map2 = peoples.stream().collect(Collectors.toMap(People::getName, People::getThis));
    map2.forEach((k, v) -> System.out.println(k + ":" + v.toString()));
}

//People中自己实现的方法
public People getThis () {
    return this;
}
```

```log
K.O2:22
K.O3:23
K.O1:21
K.O6:26
K.O4:24
K.O5:25
--------
K.O2:People{name='K.O2', age=22, birthday=Wed May 15 22:42:39 CST 2019}
K.O3:People{name='K.O3', age=23, birthday=Wed May 15 22:42:39 CST 2019}
K.O1:People{name='K.O1', age=21, birthday=Wed May 15 22:42:39 CST 2019}
K.O6:People{name='K.O6', age=26, birthday=Wed May 15 22:42:39 CST 2019}
K.O4:People{name='K.O4', age=24, birthday=Wed May 15 22:42:39 CST 2019}
K.O5:People{name='K.O5', age=25, birthday=Wed May 15 22:42:39 CST 2019}
```

## 0x07. 按集合某一属性进行分组

```java
/**
    * 按名字分组
    */
@Test void listGroupByNameSuccess() {
    //添加一个元素方便看效果
    peoples.add(new People("K.O1", 29, new Date()));
    Map<String, List<People>> map = peoples.stream().collect(Collectors.groupingBy(People::getName));

    map.forEach((k, v) -> System.out.println(k + ":" + v.size()));
}
```

```log
K.O2:1
K.O3:1
K.O1:2
K.O6:1
K.O4:1
K.O5:1
```

## 0x08. 求集合对象数值列平均数

```java
/**
    * 求人平均年龄
    */
@Test void averagingAgeSuccess () {
    Double avgAge = peoples.stream().collect(Collectors.averagingInt(People::getAge));
    System.out.println(avgAge);
}
```

```log
23.5
```

## 0x09. 对集合按某一列排序

```java
/**
    * 按年龄排序
    */
@Test void sortByAgeSuccess () {
    System.out.println("###排序前---");
    peoples.forEach(x -> System.out.println(x.getAge()));

    peoples.sort((x, y) -> {
        if (x.getAge() > y.getAge()) {
            return 1;
        } else if (x.getAge() == y.getAge()) {
            return 0;
        }
        return -1;
    });

    System.out.println("###排序后---");
    peoples.forEach(x -> System.out.println(x.getAge()));
}
```

```log
###排序前---
21
23
24
25
22
26
###排序后---
21
22
23
24
25
26
```

## 未完待续

<源码地址：[https://github.com/cos2a/learning-repo/tree/master/core-java8](https://github.com/cos2a/learning-repo/tree/master/core-java8)>