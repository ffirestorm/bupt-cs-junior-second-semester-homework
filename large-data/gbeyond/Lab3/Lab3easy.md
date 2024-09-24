# Lab03

MapReduce 单词计数

## 环境

1. 系统版本：Centos 7.5
2. Hadoop版本：Apache Hadoop 2.7.7
3. JDK版本：1.8.*
4. IDEA版本：IDEA2021.2。

## TODO

* [ ] log4j
* [ ] 三个还是三遍
* [ ] 固定监控ip
* [ ] java1.8 环境安装
* [ ] web页面

## 实验步骤

### 创建 Maven 项目

pom.xml

```xml
<!-- pom.xml 修改内容-->
  <properties>
    <!--1.8 或 8 都行-->
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <hadoop.version>2.7.7</hadoop.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <!--需要在java/main/resources路径下配置log4jproperties文件-->
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
      <version>1.2.17</version>
    </dependency>
    <!--${hadoop.version} 表示上述配置的 hadoop.version 变量-->
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-client</artifactId>
      <version>${hadoop.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-common</artifactId>
      <version>${hadoop.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-hdfs</artifactId>
      <version>${hadoop.version}</version>
    </dependency>
  </dependencies>

  <build>
    <pluginManagement>
      <!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
      <plugins>
        <plugin>
          <artifactId>maven-assembly-plugin</artifactId>
          <!-- mvn assembly:assembly -->
          <version>2.2</version>
          <configuration>
            <archive>
              <manifest>
                <mainClass>wc.WordCount</mainClass>
              </manifest>
            </archive>
            <descriptorRefs>
              <descriptorRef>
             jar-with-dependencies
            </descriptorRef>
            </descriptorRefs>
          </configuration>
        </plugin>
        <plugin>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.0.2</version>
          <configuration>
            <archive>
              <manifest>
                <addClasspath>true</addClasspath>
                <mainClass>wc.WordCount</mainClass> <!-- 此处为主入口-->
              </manifest>
            </archive>
          </configuration>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
```

修改pom.xml文件后，右键点击工程名，点击更新项目；

## java 代码

注意 import 的包对不对, 按照实验指导里的图片敲或

## 程序打包

带依赖打包MyWordCount.jar

> mvn clean
> mvn package assembly:assembly

填写主类名称
使用压缩软件打开生成的jar包(可以去本地电脑里找)
找到META-INF目录，并删除MANIFEST.MF文件

## 运行

按要求修改 input.txt 文件
启动服务器
修改hosts

上传input.txt和jar包,到root目录下

```shell
# 启动 hadoop 并验证运行情况
start-all.sh
hadoop dfsadmin -report #验证运行情况

hadoop fs -ls / #查看目录
hadoop fs -ls -R / #查看目录及子目录下所文件
hadoop fs -mkdir /testmr #创建testmr文件夹

# 在~目录下
# 传输input.txt到 testmr 文件夹下,指令二选一
hadoop fs -put input.txt /testmr
hadoop fs -put /root/input.txt /testmr
# 使用“hadoop jar jar包名主函数”命令，在hadoop运行程序
hadoop jar mrwc.jar wc.WordCount /testmr/input.txt /testmr/output #参数:运行jar包的指令,要运行的程序的完整路径,输入文件的路径,输出文件存放的文件夹

# 做错了, 删除
hadoop fs -rm -r -f /testmr

# 查看输出
hadoop fs -cat /testmr/output/part-r-00000 #查看内容

# 下载文件
hadoop fs -get /testmr/output/part-r-00000 /root/testmr/part-r-00000.txt #下载文件

# 查看服务器ip
ifconfig
```

将下载下来的文件传输到本地电脑上

记得关闭服务器

## 实验结果与分析

注：以上实验步骤使用的是样例数据，同学们做实验时需要使用自己的数据：**在原有样例数据的基础上添加3个学号，3个姓名**。同时将样例文件名称修改为 `姓名+学号+input` ，输出文件为 `姓名+学号+output` 。
提交压缩包包括：

1. 测试数据
2. 输出文件(2分)
3. 实验报告
粘贴实验结果(7分)
图29: 本地电脑里的jar包截图
图30: 压缩软件查看jar包的图
图32: 运行指令的图
图35: ifconfig命令截图
描述MapReduce程序的工作流程(3分)；

## 问题

问题|解决方案
-----|-----
关于环境|在客户端打包程序的时候，使用实验提供的环境，要求jdk版本为1.8，否则会出错
关于防火墙|一定要检查三个节点是否关闭了防火墙
文件拷贝|注意主节点配置的文件是否全部拷贝给了数据节点，主要包括(Hadoop文件夹、hosts、.bash_profile)
Hadoop需要进一步验证是否能够正常使用|运行：hadoop jar ./hadoop-2.7.3/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.3.jar pi 10 10</br>输出结果：3.2000
关于web页面|web页面默认在本地端是无法打开的，需要配置华为云的安全组，开放指定的端口，如：50070

## 概念解释及参考

[hadoop api 2.7.7](https://hadoop.apache.org/docs/r2.7.7/api/)
[hadoop api 3.3.1](https://hadoop.apache.org/docs/current/api/)
[在不使用JobConf的情况下运行Hadoop作业](https://codingdict.com/questions/132292)
[10天Hadoop快速突击（4）——MapReduce应用案例](https://blog.csdn.net/arpospf/article/details/80709600)
[大数据Hadoop实验3MapReduce](https://blog.csdn.net/weixin_46065591/article/details/120801690)
[Hadoop：hadoop fs、hadoop dfs与hdfs dfs命令的区别](https://blog.csdn.net/pipisorry/article/details/51340838)
[hadoop fs与hdfs dfs命令的区别](https://blog.csdn.net/qq_40433737/article/details/104149674)

### hadoop 与 hdfs

`hdfs fs` : 只HDFS文件系统相关（包括与Local FS间的操作），已经Deprecated

`hdfs dfs` : 只能操作 HDFS 文件系统相关

`hadoop fs` : 使用面最广，可以操作任何文件系统。可以作用于hadoop的所有子系统和其他文件系统，不止是hdfs文件系统内。

`hadoop dfs` 与 `hdfs dfs` ：只能操作HDFS文件系统相关（包括与Local FS间的操作），前者已经Deprecated，一般使用后者。

现在基本上都只使用hadoop fs

### 文件拷贝

从Linux本地文件系统拷贝到HDFS文件系统
copyFromLocal = moveFromLocal
hdfs dfs -copyFromLocal /root/test.txt
hdfs dfs -put /root/test.txt /user/root/test.txt

从HDFS 文件系统拷贝到Linux本地文件系统
copyToLocal = moveToLocal
hdfs dfs -copyToLocal /user/root/test.txt /root/test.txt
hdfs dfs -get /user/root/test.txt /root/test.txt
