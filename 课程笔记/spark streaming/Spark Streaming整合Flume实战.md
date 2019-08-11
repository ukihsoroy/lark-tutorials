# Spark Streaming整合Flume实战

## 实战1：Flume-style Push-based Approach

- 1.Flume Agent的编写： flume_push_streaming.conf

```properties
# Agent flume-agent

flume-agent.sources = netcat-source
flume-agent.sinks = avro-sink
flume-agent.channels = memory-channel

flume-agent.sources.netcat-source.type = netcat
flume-agent.sources.netcat-source.bind = localhost
flume-agent.sources.netcat-source.port = 44444

flume-agent.sinks.avro-sink.type = avro
flume-agent.sinks.avro-sink.hostname = 192.168.37.1
flume-agent.sinks.avro-sink.port = 41414

flume-agent.channels.memory-channel.type = memory

flume-agent.sources.netcat-source.channels = memory-channel
flume-agent.sinks.avro-sink.channel = memory-channel
```

- 2.代码

```scala
package org.ko.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Spark Streaming整合Flume的第一种方式
  */
object FlumePushWordCount {

  def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      println("Usage: Flume push word count <hostname> <port>")
      System.exit(1)
    }

    val Array(hostname, port) = args

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("FlumePushWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    //Todo... 如何使用spark streaming整合flume
    val flumeStream = FlumeUtils.createStream(ssc, hostname, port.toInt)

    flumeStream.map(x => new String(x.event.getBody.array()).trim)
        .flatMap(_.split(" "))
        .map((_, 1))
        .reduceByKey(_ + _)
        .print()

    ssc.start()
    ssc.awaitTermination()
  }


}

```

- 3.flume启动

```shell
flume-ng agent \
--name flume-agent \
--conf $FLUME_HOME/conf \
--conf-file $FLUME_HOME/conf/flume_push_streaming.conf \
-Dflume.root.logger=INFO,console
```

- 4.本地测试总结
  1. 启动Spark Streaming作业
  2. 启动Flume agent
  3. 通过telnet输入数据，观察IDEA控制台输出

## 实战2：Pull-based Approach using a Custom Sink

- 1.Flume Agent的编写： flume_pull_streaming.conf

```properties
# Agent flume-agent

flume-agent.sources = netcat-source
flume-agent.sinks = spark-sink
flume-agent.channels = memory-channel

flume-agent.sources.netcat-source.type = netcat
flume-agent.sources.netcat-source.bind = localhost
flume-agent.sources.netcat-source.port = 44444

flume-agent.sinks.spark-sink.type = org.apache.spark.streaming.flume.sink.SparkSink
flume-agent.sinks.spark-sink.hostname = 192.168.37.131
flume-agent.sinks.spark-sink.port = 41414

flume-agent.channels.memory-channel.type = memory

flume-agent.sources.netcat-source.channels = memory-channel
flume-agent.sinks.spark-sink.channel = memory-channel
```

- 2.代码

```scala
package org.ko.spark.streaming.flume

import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Spark Streaming整合Flume的第二种方式
  */
object FlumePullWordCount {

  def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      println("Usage: Flume pull word count <hostname> <port>")
      System.exit(1)
    }

    val Array(hostname, port) = args

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("FlumePushWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    //Todo... 如何使用spark streaming整合flume
    val flumeStream = FlumeUtils.createPollingStream(ssc, hostname, port.toInt)

    flumeStream.map(x => new String(x.event.getBody.array()).trim)
        .flatMap(_.split(" "))
        .map((_, 1))
        .reduceByKey(_ + _)
        .print()

    ssc.start()
    ssc.awaitTermination()
  }

}
```

- 3.先启动Flume，后启动Spark

```shell
flume-ng agent \
--name flume-agent \
--conf $FLUME_HOME/conf \
--conf-file $FLUME_HOME/conf/flume_pull_streaming.conf \
-Dflume.root.logger=INFO,console
```

- 4.注意
  - 需要上传spark-streaming-flume-sink_2.11依赖包到flume lib目录下
  - scala-library 需要2.11.x版本，请删除多余版本

- 5.异常错误

```log
java.lang.IllegalStateException: begin() called when transaction is OPEN!

# scala library 依赖包冲突，删除多余的依赖包，只保留2.11.x版本即可。
```
