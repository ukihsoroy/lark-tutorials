# JAVA可视化监控程序JVisualVM的使用

## 监控本地JAVA进程

- JVisualVM是Oracle程序，安装JDK默认在bin目录下
- 打开JVisualVM默认会显示本机JAVA进程
  ![jvisualvm1](./images/jvisualvm1.png)

### 1.Tab简介

- 概述：显示线程基本状态，线程号，JVM参数以及一些系统属性
  ![jvisualvm2](./images/jvisualvm2.png)
- 监视：显示CPU，堆/metaspace，类加载信息，线程信息
  ![jvisualvm3](./images/jvisualvm3.png)
  - 执行垃圾回收：点击会进程垃圾回收
  - 堆dump：类似jmap dump，图形界面类似MAT
    ![jvisualvm4](./images/jvisualvm4.png)
    ![jvisualvm5](./images/jvisualvm5.png)
    - 可以看到该类的实例数
    - 双击类可以看到实例，字段，以及该对象的引用
      ![jvisualvm6](./images/jvisualvm6.png)
- 线程：将当前JAVA进程的全部线程信息显示
  ![jvisualvm7](./images/jvisualvm7.png)
  - 线程dump：类似于jstack打印出的文件
    ![jvisualvm8](./images/jvisualvm8.png)
- 抽样器
  - CPU：可以查看热点方法，那些方法时间长
    ![jvisualvm9](./images/jvisualvm9.png)
  - 内存：类似每秒钟执行一次jstat，可以实时查看内存
    ![jvisualvm10](./images/jvisualvm10.png)
- 插件：Visual GC: 查看GC情况，请见插件安装
  ![jvisualvm14](./images/jvisualvm14.png)

## 监控远程TOMCAT

- 修改catalina.sh，添加jmx配置
  ![jvisualvm15](./images/jvisualvm15.png)
- 添加JvisualVM连接
  ![jvisualvm16](./images/jvisualvm16.png)

## 插件安装

![jvisualvm11](./images/jvisualvm11.png)

1. 选择顶部Tab页，选择工具，插件
2. 直接勾选无法下载，**需要网址与JDK版本一致**
   1. 点击设置
   2. 右键Java VisualVM 插件中心
   3. 去[插件中心](http://visualvm.github.io/pluginscenters.html)查看符合自己版本的地址
     ![jvisualvm12](./images/jvisualvm12.png)
   4. 替换插件源网址
     ![jvisualvm13](./images/jvisualvm13.png)
3. 修改完后可用修改