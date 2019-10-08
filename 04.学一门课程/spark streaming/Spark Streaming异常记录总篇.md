# Spark Streaming 异常记录总篇

>**spark version**: 2.2.1
>**scala version**: 2.11.12
>**kafka version**: 2.0.0

## 1. java.lang.ClassNotFoundException: com.fasterxml.jackson.annotation.JsonMerge

- 1.异常信息

```log
java.lang.reflect.InvocationTargetException
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
    at org.apache.spark.metrics.MetricsSystem$$anonfun$registerSinks$1.apply(MetricsSystem.scala:200)
    at org.apache.spark.metrics.MetricsSystem$$anonfun$registerSinks$1.apply(MetricsSystem.scala:194)
    at scala.collection.mutable.HashMap$$anonfun$foreach$1.apply(HashMap.scala:130)
    at scala.collection.mutable.HashMap$$anonfun$foreach$1.apply(HashMap.scala:130)
    at scala.collection.mutable.HashTable$class.foreachEntry(HashTable.scala:236)
    at scala.collection.mutable.HashMap.foreachEntry(HashMap.scala:40)
    at scala.collection.mutable.HashMap.foreach(HashMap.scala:130)
    at org.apache.spark.metrics.MetricsSystem.registerSinks(MetricsSystem.scala:194)
    at org.apache.spark.metrics.MetricsSystem.start(MetricsSystem.scala:102)
    at org.apache.spark.SparkContext.<init>(SparkContext.scala:522)
    at org.apache.spark.streaming.StreamingContext$.createNewSparkContext(StreamingContext.scala:839)
    at org.apache.spark.streaming.StreamingContext.<init>(StreamingContext.scala:85)
    at org.ko.spark.streaming.NetworkWordCount$.main(NetworkWordCount.scala:20)
    at org.ko.spark.streaming.NetworkWordCount.main(NetworkWordCount.scala)
Caused by: java.lang.NoClassDefFoundError: com/fasterxml/jackson/annotation/JsonMerge
    at com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector.<clinit>(JacksonAnnotationIntrospector.java:50)
    at com.fasterxml.jackson.databind.ObjectMapper.<clinit>(ObjectMapper.java:291)
    at org.apache.spark.metrics.sink.MetricsServlet.<init>(MetricsServlet.scala:48)
    ... 18 more
Caused by: java.lang.ClassNotFoundException: com.fasterxml.jackson.annotation.JsonMerge
    at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
    at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:335)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
    ... 21 more
```

- 2.解决办法

  - 项目缺少JsonMerge，在pom中添加即可

  ```xml
  <dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-scala_2.11</artifactId>
    <version>${jackson.version}</version>
  </dependency>
  ```

## 2. com.fasterxml.jackson.databind.JsonMappingException: Incompatible Jackson version: 2.9.2

- 1.异常信息

```log
Exception in thread "main" java.lang.ExceptionInInitializerError
    at org.apache.spark.streaming.StreamingContext.withNamedScope(StreamingContext.scala:274)
    at org.apache.spark.streaming.StreamingContext.socketTextStream(StreamingContext.scala:302)
    at org.ko.spark.streaming.NetworkWordCount$.main(NetworkWordCount.scala:21)
    at org.ko.spark.streaming.NetworkWordCount.main(NetworkWordCount.scala)
Caused by: com.fasterxml.jackson.databind.JsonMappingException: Incompatible Jackson version: 2.9.2
    at com.fasterxml.jackson.module.scala.JacksonModule$class.setupModule(JacksonModule.scala:64)
    at com.fasterxml.jackson.module.scala.DefaultScalaModule.setupModule(DefaultScalaModule.scala:19)
    at com.fasterxml.jackson.databind.ObjectMapper.registerModule(ObjectMapper.java:751)
    at org.apache.spark.rdd.RDDOperationScope$.<init>(RDDOperationScope.scala:82)
    at org.apache.spark.rdd.RDDOperationScope$.<clinit>(RDDOperationScope.scala)
    ... 4 more
```

- 2.产生原因
  - 1.**spark** 和 **kafka** 集成引起
  - 2.引入jackson jar包错误，spark中应引入 **jackson-module-scala** 版本

- 3.解决办法
  - 1.exclusion kafka jackson jar 包

  ```xml
  <!--Kafka 依赖-->
  <dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka_2.11</artifactId>
    <version>${kafka.version}</version>
    <exclusions>
      <!--排除JacksonJar-->
      <exclusion>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>*</artifactId>
      </exclusion>
    </exclusions>
  </dependency>
  ```

  - 2.删除pom中多引入的jackson其他包

## 3. org.apache.spark.SparkException: Task not serializable

- 出现这个异常注意看信息下文，一般都会有不能序列化的类，多是Driver向Executor中传递数据时，数据不能被序列化
- 目前碰到过的：
  - 1.在forEachRDD中创建各种（MySql、MongoDB）数据库连接。