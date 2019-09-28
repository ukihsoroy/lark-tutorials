# Spark Streaming整合Kafka实战

## 实战一：Receiver-based

1. 启动Zookeeper：zkServer.sh start
2. 启动Kafka：sudo ./kafka-server-start.sh -daemon ../config/server.properties
3. 创建Topic：sudo ./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafka-streaming-topic
4. 查看是否创建成功：kafka-topics.sh --list --zookeeper localhost:2181
5. 测试队列：
   - 生产：sudo ./kafka-console-producer.sh --broker-list PLAINTEXT://192.168.37.131:9092 --topic "kafka-streaming-topic"
   - 消费：sudo ./bin/kafka-console-consumer.sh --bootstrap-server 192.168.37.131:9092 --topic "kafka-streaming-topic" --from-beginning
6. 代码开发

```scala
   package org.ko.spark.streaming.kafka

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Spark Streaming对接Kafka的方式一
  * 192.168.37.131:2181 test kafka-streaming-topic 1
  * 要求运行时kafka版本为0.8
  */
object KafkaReceiverWordCount {
  def main(args: Array[String]): Unit = {

    if (args.length != 4) {
      System.err.println("Usage: KafkaReceiverWordCount <zkQuorum> <group> <topics> <numThreads>")
      System.exit(0)
    }

    val sparkConf = new SparkConf()
      .setAppName("KafkaReceiverWordCount")
      .setMaster("local[2]")

    val Array(zkQuorum, group, topics, numThreads) = args

    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap

    //TODO... Spark Streaming 如何对接Kafka
    val messages = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)

    messages
      .map(_._2)
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .print()

    ssc.start()
    ssc.awaitTermination()
  }
}
```

## 实战二：Direct Approach

- 使用Direct，只是使用KafkaUtils方法改变，其他的基本一致。
- 这种方式是Spark Streaming去Kafka中查询，避免了消息丢失，现在主流都是使用这种方式。

```scala
package org.ko.spark.streaming.kafka

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Spark Streaming对接Kafka的方式一
  * 192.168.37.131:9092 kafka-streaming-topic
  * 要求运行时kafka版本为0.8Q
  */
object KafkaDirectWordCount {

  def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      System.err.println("Usage: KafkaDirectWordCount <brokers> <topics>")
      System.exit(1)
    }

    val sparkConf = new SparkConf()
      .setAppName("KafkaDirectWordCount")
      .setMaster("local[2]")

    val Array(brokers, topics) = args

    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers
    )

    val topicSet = topics.split(",").toSet

    //TODO... Spark Streaming 如何对接Kafka
    val messages = KafkaUtils
      .createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicSet)

    messages
      .map(_._2)
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .print()

    ssc.start()
    ssc.awaitTermination()


  }

}

```