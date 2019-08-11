# Spark Streaming Java扩展

## 1.使用Java开发Spark Core应用程序

```java
package org.ko.kafka.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

/**
 * 使用Java开发Spark应用程序
 */
public class SparkSQLWordCountApp {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("SparkSQLWordCountApp")
                .master("local[2]")
                .getOrCreate();

        //TODO...处理我们的业务逻辑

        //1. 读取文件，转成Java RDD
        JavaRDD<String> lines = spark.read().textFile("hello.txt").javaRDD();

        //2. 拆分单词
        JavaRDD<String> words = lines.flatMap(x -> Arrays.asList(x.split(" ")).iterator());

        //3. 为每个单词赋值1
        JavaPairRDD<String, Integer> counts = words.mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((x, y) -> x + y);

        //4. 获取结果
        List<Tuple2<String, Integer>> output = counts.collect();

        //5. 输出
        output.forEach(x -> System.out.println(x._1 + ": " + x._2));

        spark.stop();
    }
}

```

## 2.使用Java开发Spark Streaming应用程序

```java
package org.ko.kafka.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;

/**
 * 使用Java开发Spark Streaming应用程序
 */
public class SparkStreamingWordCountApp {

    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]")
                .setAppName("SparkStreamingWordCountApp");

        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        //1. 读取
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9999);

        //2. 处理数据，拆分-转换
        JavaPairDStream<String, Integer> counts = lines.flatMap(x -> Arrays.asList(x.split(" ")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((x, y) -> x + y);

        //3. 获取结果
        counts.print();

        //4. 开始ssc
        jssc.start();
        jssc.awaitTermination();
    }
}

```