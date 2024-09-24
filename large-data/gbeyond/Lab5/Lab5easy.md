# Lab05

本实验使用 Scala 语言编写 Spark 程序，完成单词计数任务。首先，在华为云购买 4 台服务器，然后搭建 Hadoop 集群和 Spark 集群(YARN模式)，接着使用 Scala 语言利用 Spark Core 编写程序，最后将程序打包在集群上运行。

## 文件预操作及目录

cloudshell: 所有 vim 文本操作可以在 CloudShell 中进行，也可以在类似 WinSCP 等的远程工具中修改，方便也不容易出错
名称根据实际情况替换
noden = 节点n名称; nodenip = 节点n IP 地址; nodeniip = 节点n内网IP;
node1 = 节点1名称; de1inop = 节点1 IP 地址; node1iip = 节点1内网IP;
node2 = 节点2名称; node2ip = 节点2 IP 地址; node2iip = 节点2内网IP;
node3 = 节点3名称; node3ip = 节点3 IP 地址; node3iip = 节点3内网IP;
node4 = 节点4名称; node4ip = 节点4 IP 地址; node4iip = 节点4内网IP;

* [Lab05](#lab05)
  * [文件预操作及目录](#文件预操作及目录)
  * [实验环境](#实验环境)
  * [TODO](#todo)
  * [实验步骤](#实验步骤)
    * [Scala 程序编写](#scala-程序编写)
      * [程序打包](#程序打包)
    * [服务器预处理](#服务器预处理)
    * [Spark 集群搭建（On Yarn 模式）](#spark-集群搭建on-yarn-模式)
    * [运行程序](#运行程序)
  * [实验结果与分析](#实验结果与分析)
  * [概念解释及参考](#概念解释及参考)

## 实验环境

1. 服务器节点数量: 4
2. 系统版本: Centos 7.6
3. Hadoop 版本: Apache Hadoop 2.7.7
4. Spark 版本: Apache Spark 2.1.1
5. JDK 版本: 1.8.0_191-b12
6. Scala 版本: scala2.11.8
7. IDEA 版本: IntelliJ IDEA Community Edition 2021.2.3

## TODO

* [ ] 安装jdk8

## 实验步骤

名称根据实际情况替换
noden = 节点n名称; nodenip = 节点n IP 地址; nodeniip = 节点n内网IP;
node1 = 节点1名称; node1ip = 节点1 IP 地址; node1iip = 节点1内网IP;
node2 = 节点2名称; node2ip = 节点2 IP 地址; node2iip = 节点2内网IP;
node3 = 节点3名称; node3ip = 节点3 IP 地址; node3iip = 节点3内网IP;
node4 = 节点4名称; node4ip = 节点4 IP 地址; node4iip = 节点4内网IP;

### Scala 程序编写

scala相关插件
[Scala Syntax (official)](https://marketplace.visualstudio.com/items?itemName=scala-lang.scala)
[Scala (Metals)](https://marketplace.visualstudio.com/items?itemName=scalameta.metals)
[Scala Snippets](https://marketplace.visualstudio.com/items?itemName=scala-lang.scala-snippets)

Maven项目模板(More里面搜索,选net.alchim32.maven那个)：scala-archetype-simple
GroupID: org.example
ArtifactId: spark-test
Version: 1.0-ANAPSHOT

pom.xml 添加以下内容并更改主函数路径,注释掉org.scalatest插件:

```xml
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <scala.version>2.11.8</scala.version>
    <spark.version>2.1.1</spark.version>
    <scala.compat.version>2.11</scala.compat.version>
</properties>

<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.11</artifactId>
    <version>${spark.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.11</artifactId>
    <version>${spark.version}</version>
    <scope>provided</scope>
</dependency>

<plugin>
  <artifactId>maven-assembly-plugin</artifactId>
  <!-- mvn package assembly:assembly -->
  <version>2.2</version>
  <configuration>
    <archive>
      <manifest>
        <mainClass>org.example.ScalaWordCount</mainClass> <!-- 此处为主入口-->
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
        <mainClass>org.example.ScalaWordCount</mainClass> <!-- 此处为主入口-->
      </manifest>
    </archive>
  </configuration>
</plugin>

```

删除模板中test...下的类，src/main...下的APP文件夹
新建scala类 `ScalaWordCount`

#### 程序打包

填写主类名称: org.example.ScalaWordCount
带依赖打包

> mvn clean
> mvn package assembly:assembly

使用压缩软件打开生成的jar包(可以去本地电脑里找)
找到META-INF目录，并删除MANIFEST.MF文件

### 服务器预处理

启动服务器，改hosts，注释掉127的地址

上传 `spark-test.jar` 包和 spark 压缩包 `spark-2.1.1-bin-hadoop2.7.tgz`

```shell
# On my computer
# sftp 版
sftp root@node1ip
lcd Filepath
lpwd # 检查本机路径是否正确
put spark-test.jar
put spark-2.1.1-bin-hadoop2.7.tgz
exit
# scp 版
scp spark-test.jar root@node1ip:~/
scp spark-2.1.1-bin-hadoop2.7.tgz root@node1ip:~/
```

启动hadoop , 并在四个节点的终端执行 `jps` 命令: 并执行 `ifconfig` .

```shell
start-all.sh
# On node1~node4 respectively
jps
ifconfig #报告截图需要的内容
# 第1-4张: 截图1-4
```

<!--TODO: 截图1-4-->

```shell
# 主节点结果
ResourceManage
SecondaryNameNode
NameNode
Jps
WrapperSimpleApp #没有也可运行

# 子节点结果
NodeManager
DataNode
Jps
WrapperSimpleApp #没有也可运行
```

在主节点的 root 测试 Hadoop 的集群可用性，并执行 `fconfig` 。或使用 `hadoop dfsadmin -report` .

```shell
# Only on node1
hadoop jar ../home/modules/hadoop-2.7.7/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.7.jar pi 10 1
# 或
cd /
hadoop jar ./home/modules/hadoop-2.7.7/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.7.jar pi 10 1
# 第5张: 截图5

ifconfig #报告截图需要的内容
# 第6张: 截图6
```

<!--TODO: 截图5-6-->

### Spark 集群搭建（On Yarn 模式）

```shell
# Only on node1 ~
tar -xzvf spark-2.1.1-bin-hadoop2.7.tgz -C ./ #解压 spark 压缩包

# 配置环境变量
cd /root/
cd ~
vim .bash_profile #修改用户变量，添加如下内容
```

```text
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HDFS_CONF_DIR=$HADOOP_HOME/etc/hadoop
export YARN_CONF_DIR=$HADOOP_HOME/etc/hadoop
export PATH=$PATH:/root/spark/bin
```

```shell
source .bash_profile #使添加变量生效

# 配置 yarn-site.xml 文件。
cd ../home/modules/hadoop-2.7.7/etc/hadoop #进入文件所在目录
vim yarn-site.xml #编辑配置文件，添加如下内容
```

```xml
<property>
  <name>yarn.nodemanager.pmem-check-enabled</name>
  <value>false</value>
</property>
<property>
  <name>yarn.nodemanager.vmem-check-enabled</name>
  <value>false</value>
</property>
```

```shell
# 将 yarn-site.xml 文件发送到从节点:
scp yarn-site.xml root@node2:/home/modules/hadoop-2.7.7/etc/hadoop/yarn-site.xml
scp yarn-site.xml root@node3:/home/modules/hadoop-2.7.7/etc/hadoop/yarn-site.xml
scp yarn-site.xml root@node4:/home/modules/hadoop-2.7.7/etc/hadoop/yarn-site.xml

# 重启 Hadoop 集群。
cd ~
stop-all.sh
start-all.sh
jps #查看集群是否启动成功(结果应与图1-图4一致)；

# 运行如下指令检验 spark 是否部署成功:
spark-submit --class org.apache.spark.examples.SparkPi --master yarn --num-executors 4 --driver-memory 1g --executor-memory 1g --executor-cores 1 spark/examples/jars/spark-examples_2.13-3.5.1.jar 10
# 第7张: 截图7

spark-shell #查看 spark 和 scala 版本信
# 第8张: 截图8
```

退出 `:quit`

<!--TODO: 截图7-8-->

### 运行程序

```shell
# On node1
cd ~
# 使用 spark-submit 命令，在 hadoop 运行程序:
spark-submit --class org.example.ScalaWordCount --master yarn --num-executors 3 --driver-memory 1g --executor-memory 1g --executor-cores 1 spark-test.jar
# 第9张: 截图46

# 如果已经存在spark_test文件夹，则要手动删除

# 在 hdfs 上查看程序的输出:
hadoop fs -ls / # 第10张: 截图47
# 或 hadoop fs -ls -R / 建议 hadoop fs -ls -R /spark_test
hadoop fs -cat /spark_test/part-00000
hadoop fs -cat /spark_test/part-00001
hadoop fs -cat /spark_test/part-00002
hadoop fs -cat /spark_test/part-00003 #不一定有
ifconfig #报告截图需要的内容，与程序输出无关
# 第11张: 截图48
```

<!--TODO: 截图46-48-->

## 实验结果与分析

实验结果截图需要按要求带有ifconfig的ip信息。
* Hadoop集群测试结果(图1-图6)----------2分
* Spark集群搭建及测试结果(图7-图8)-----3分
* Scala单词计数实验结果(图46-图48)-----5分
* 整体实验报告撰写---------------------2分

## 概念解释及参考

[.bash_profile和/etc/profile区别](https://blog.csdn.net/sqlquan/article/details/120039809)
/etc/profile
为系统的每个用户设置环境信息和启动程序，当用户第一次登录时，该文件被执行，其配置对所有登录的用户都有效。当被修改时，重启或使用命令 source /etc/profile 才会生效。英文描述：”System wide environment and startup programs, for login setup.”

~/.bash_profile
为当前用户设置专属的环境信息和启动程序，当用户登录时该文件执行一次。默认情况下，它用于设置环境变量，并执行当前用户的 .bashrc 文件。理念类似于 /etc/profile，只不过只对当前用户有效，需要重启或使用命令 source ~/.bash_profile 才能生效。(注意：Centos7系统命名为.bash_profile，其他系统可能是.bash_login或.profile。)
