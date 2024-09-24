1.92.114.12
120.46.149.118
1.92.86.3
120.46.87.42

# Lab6-final 大数据实时数据分析综合实践

## 文件预操作及目录

cloudshell: 所有 vim 文本操作可以在 CloudShell 中进行，也可以在类似 WinSCP 等的远程工具中修改，方便也不容易出错
名称根据实际情况替换
noden = 节点n名称; nodenip = 节点n IP 地址; nodeniip = 节点n内网IP;
node1 = 节点1名称; node1ip = 节点1 IP 地址; node1iip = 节点1内网IP;
node2 = 节点2名称; node2ip = 节点2 IP 地址; node2iip = 节点2内网IP;
node3 = 节点3名称; node3ip = 节点3 IP 地址; node3iip = 节点3内网IP;
node4 = 节点4名称; node4ip = 节点4 IP 地址; node4iip = 节点4内网IP;

* [Lab6-final 大数据实时数据分析综合实践](#lab6-final-大数据实时数据分析综合实践)
  * [文件预操作及目录](#文件预操作及目录)
  * [TODO](#todo)
  * [实验综述](#实验综述)
  * [操作手册](#操作手册)
    * [程序编写](#程序编写)
      * [创建maven工程](#创建maven工程)
      * [Java 代码](#java-代码)
      * [程序打包](#程序打包)
    * [服务器处理](#服务器处理)
      * [上传文件及移动解压](#上传文件及移动解压)
      * [修改 /etc/profile](#修改-etcprofile)
    * [实验1-local模式部署安装步骤](#实验1-local模式部署安装步骤)
    * [实验2-standalone模式部署安装步骤](#实验2-standalone模式部署安装步骤)
    * [实验3-standalone模式的HA环境步骤](#实验3-standalone模式的ha环境步骤)
    * [实验4步骤](#实验4步骤)
    * [实验5-Flink消费Kafka数据步骤](#实验5-flink消费kafka数据步骤)
      * [安装kafka](#安装kafka)
      * [运行jar包](#运行jar包)
  * [实验结果与分析](#实验结果与分析)
    * [实验结束后应得到](#实验结束后应得到)
    * [实验给分点](#实验给分点)
  * [概念解释及参考](#概念解释及参考)

## TODO

* [ ] 安装jdk8

## 实验综述

* 一、local模式部署安装Flink
  + 实验介绍
    - local模式下的Flink部署安装只需要使用单台机器，仅用本地线程来模拟其程序运行，不需要启动任何进程，适用于软件测试等情况。这种模式下，机器不用更改任何配置，只需要安装JDK 8的运行环境即可。
  + 实验目的
    - 实现Flink的安装；
    - 学会Flink的脚本启动；
    - 使用Flink自带的单词统计程序进行测试。
* 二、standalone模式部署安装Flink
  + 实验介绍
    - 使用standalone模式需要启动Flink的主节点JobManager以及从节点的TaskManager，具体的任务进程划分见下表。
    | 服务及IP    | node1 | node2 | node3 | node4 |
    | ----------- | ----- | ----- | ----- | ----- |
    | JobManager  | 是    | 否    | 否    | 否    |
    | TaskManager | 是    | 是    | 是    | 是    |

  + 实验目的
    - 实现standalone模式下Flink进程的启动。
* 三、standalone模式的HA环境
  + 实验介绍
    - 上一节实现了Flink的standalone模式部署安装，并且能够正常提交任务到集群上。其中主节点是 `JobManager` ，但 `JobManager` 是单节点，必然会有单节点故障问题产生，所以也可以在standalone模式下借助ZooKeeper将 `JobManager` 实现为高可用模式。
  + 实验目的
    - 安装ZooKeeper，并实现standalone模式的HA环境；
    - 实现Flink在standslone模式的HA环境下提交任务。
* 四、Flink on Yarn模式
  + 实验介绍
    - Flink任务也可以运行在Yarn上，将Flink任务提交到Yarn平台可以实现统一的任务资源调度管理，方便开发人员管理集群中的CPU和内存等资源。
    - 本模式需要先启动集群，然后再提交作业，接着会向Yarn申请资源空间，之后资源保持不变。如果资源不足，下一个作业就无法提交，需要等到Yarn中的一个作业执行完成后释放资源。
  + 实验目的
    - 完成Flink on Yarn模式的配置；
    - 在Yarn中启动Flink集群；
    - 以文件的形式进行任务提交。
* 五、Flink消费Kafka数据
  + 实验介绍
    - 对于实时处理，实际工作中的数据源一般都是使用Kafka。Flink提供了一个特有的Kafka连接器去读写Kafka topic的数据。本实验通过本地打jar包上传到Flink集群，去处理终端Kafka输入的数据。
  + 实验目的
    - 安装Kafka；
    - 本地编辑代码读取Kafka数据，并且打成jar包；
    - 将jar包上传到Flink集群运行。

## 操作手册

### 程序编写

#### 创建maven工程

创建项目，打开IDEA，创建maven工程WordCount(具体步骤同实验二)。
Maven项目模板(org.apache.maven.archetype): maven-archetype-quickstart
GroupID: kafkawc
ArtifactId: wordcount
Version: 1.0

pom.xml 添加以下内容并更改主函数路径:
[检测依赖是否正确](https://mvnrepository.com/)

```xml
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <hadoop.version>2.7.7</hadoop.version>
</properties>

<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-clients_2.11</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-java</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-streaming-java_2.11</artifactId>
    <version>1.8.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-kafka-0.8_2.11</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <!--需要在java/main/resources路径下配置log4jproperties文件-->
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>

<plugin>
  <artifactId>maven-assembly-plugin</artifactId>
  <!-- mvn package assembly:assembly -->
  <version>2.2</version>
  <configuration>
    <archive>
      <manifest>
        <mainClass>kafkawc.WordCount</mainClass> <!-- 此处为主入口-->
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
        <mainClass>kafkawc.WordCount</mainClass> <!-- 此处为主入口-->
      </manifest>
    </archive>
  </configuration>
</plugin>
```

#### Java 代码

<!--TODO: 程序引包-->
编写Flink读取Kafka数据的代码(可以在本机尝试运行代码正确后再打jar包)。注意 import 的包对不对

#### 程序打包

带依赖打包WordCount.jar

> mvn clean
> mvn package assembly:assembly

### 服务器处理

**在安全组中打开 `8081` , `8088` , `8032` 端口**

登录服务器
**先不改 `/etc/hosts` 保证 `localhost` 能被 node1 找到, 做完第一个小实验再改hosts**

#### 上传文件及移动解压

准备单词文件 `wordcount.txt`

```text
hello world
flink hadoop
hive spark
```

上传文件

```shell
# 上传 flink-1.8.0-bin-scala_2.11.tgz 到 node1
# 上传 apache-zookeeper-3.5.7-bin.tar.gz 到 node1
# 上传 flink-shaded-hadoop-2-uber-2.7.5-10.0.jar 到 node1
# 上传 kafka_2.10-0.8.2.1.tgz 到 node1
# 上传 生成的jar包
# 上传 wordcount.txt

# SFTP 版
sftp root@node1ip
lcd Filepath
lpwd # 检查本机路径是否正确
put flink-1.8.0-bin-scala_2.11.tgz
put apache-zookeeper-3.5.7-bin.tar.gz
put flink-shaded-hadoop-2-uber-2.7.5-10.0.jar
put kafka_2.10-0.8.2.1.tgz
put WordCount.jar
put wordcount.txt
exit

# SCP 版
scp flink-1.8.0-bin-scala_2.11.tgz root@node1ip:~/
scp apache-zookeeper-3.5.7-bin.tar.gz root@node1ip:~/
scp flink-shaded-hadoop-2-uber-2.7.5-10.0.jar root@node1ip:~/
scp kafka_2.10-0.8.2.1.tgz root@node1ip:~/
scp WordCount.jar root@node1ip:~/
scp wordcount.txt root@node1ip:~/
```

文件移动与解压

```shell
cp flink-1.8.0-bin-scala_2.11.tgz /home/modules/
cp apache-zookeeper-3.5.7-bin.tar.gz /home/modules/
cp kafka_2.10-0.8.2.1.tgz /home/modules/
cd /home/modules/
tar -zxvf flink-1.8.0-bin-scala_2.11.tgz
tar -xzvf apache-zookeeper-3.5.7-bin.tar.gz
tar -xzvf kafka_2.10-0.8.2.1.tgz
cd ~
cp flink-shaded-hadoop-2-uber-2.7.5-10.0.jar /home/modules/flink-1.8.0/lib/
```

#### 修改 /etc/profile

在四台服务器上,配置全局环境变量,每个都要配

> vim /etc/profile # vim方式

添加以下内容

```shell
# Flink
export FLINK_HOME=/home/modules/flink-1.8.0
export PATH=$FLINK_HOME/bin:$PATH
# Zookeeper
export ZK_HOME=/home/modules/apache-zookeeper-3.5.7-bin
export PATH=$ZK_HOME/bin:$PATH
# Kafka
export KAFKA_HOME=/home/modules/kafka_2.10-0.8.2.1
export PATH=$KAFKA_HOME/bin:$PATH
```

> source /etc/profile

### 实验1-local模式部署安装步骤

```shell
# On node1 ~
cd /home/modules/flink-1.8.0/conf/
# 更改Flink配置文件
vim flink-conf.yaml

# flink-conf.yaml:添加如下配置
# 指定taskmananger的地址，如果是单机部署，指定localhost
taskmanager.host: localhost
# end

start-cluster.sh # 脚本启动Flink进程
jps #查看最新启动的两个进程
<!--TODO: 截图1.1, 只用主节点jps-->

# jps结果:
StandaloneSessionClusterEntrypoint
TaskManagerRunner
Jps
# end
```

Web界面访问:成功启动两个进程后,通过8081端口访问Flink的Web管理界面<http://1.92.114.12:8081/#/overview>

```shell
# 运行Flink自带的测试用例
sudo yum -y install nc # 安装nc工具
# 在node1上使用Linux的nc命令向Socket发送一些单词。
nc -lk 9000
# <!--TODO: 截图1.2, 单词-->

# 另外打开一个node1的shell页面，在node1上启动Flink自带的单词统计程序，接收输入的Socket数据并进行统计。
cd /home/modules/flink-1.8.0
bin/flink run examples/streaming/SocketWindowWordCount.jar --hostname localhost --port 9000

# 查看统计结果
# 在Flink的Web管理界面进入Task Managers目录下，选择Stdout选项卡，得到统计结果。
# <!--TODO: 截图1.2, Web页面-->
# Flink自带的测试用例统计结果在log文件夹路径下。在node1上执行以下命令查看统计结果。tail命令第三个参数是文件名，根据实际情况来
cd /home/modules/flink-1.8.0/log/
tail -200f flink-root-taskexecutor-0-node1.out
# <!--TODO: 截图1.2, 终端窗口-->

# 关闭local模式。
stop-cluster.sh
```

### 实验2-standalone模式部署安装步骤

停止node1服务器local模式下的进程后，修改配置文件。
**注释掉 node1 节点 hosts 文件中 127 开头部分**

```shell
# On node1
cd /home/modules/flink-1.8.0/conf/
vim flink-conf.yaml # 修改Flink配置文件
vim slaves # 修改slaves配置文件。
```

flink-conf.yaml:删除实验1中进行的修改( `taskmanager.host: localhost` 那行); 找到包含 `jobmanager.rpc.address` 的那行，替换成下面的内容

```text
# 指定JobManager所在的服务器为node1
jobmanager.rpc.address: node1
```

slaves:替换原内容

```text
node1
node2
node3
node4
```

```shell
# 分发配置文件
cd /home/modules/
scp -r flink-1.8.0 root@node2:$PWD/
scp -r flink-1.8.0 root@node3:$PWD/
scp -r flink-1.8.0 root@node4:$PWD/

# 启动Flink集群
start-cluster.sh
jps
# <!--TODO: 截图2.1, 4台服务器都运行jps, 共4张-->
```

```shell
#jps结果
# node1
StandaloneSessionClusterEntrypoint
TaskManagerRunner
Jps
# other node
TaskManagerRunner
Jps
```

进入Web管理页面能看到Task Managers和Task Slots数量为4，说明集群正确启动。
<http://node1ip:8081/#/overview>
<!--TODO: 截图2.1, Web页面Task Managers页面-->

```shell
# 运行Flink自带测试用例
# 在node1启动Socket服务，输入单词。
nc -lk 9000
# <!--TODO: 单词2.2-->

# 另外打开一个node1的shell页面，在node1上启动Flink自带的单词统计程序，接收输入的Socket数据并进行统计。
cd /home/modules/flink-1.8.0
bin/flink run examples/streaming/SocketWindowWordCount.jar --hostname node1 --port 9000

# 在Web管理界面的Task Managers目录中，选择Free Slots为 0 的一项，选中后可以在它对应的Stdout中看到单词的统计结果。
# <!--TODO: 截图2.2, Web页面-->

# 根据执行的Task Manager，在Free Slots为 0 的服务器的命令行中执行以下命令查看统计结果。tail命令第三个参数是文件名，根据实际情况来
# 例如 On node2
cd /home/modules/flink-1.8.0/log/
tail -200f flink-root-taskexecutor-0-node2.out
# <!--TODO: 截图2.2, 终端窗口-->
```

### 实验3-standalone模式的HA环境步骤

```shell
# 安装ZooKeeper
# 使用复制命令生成配置文件
cd /home/modules/apache-zookeeper-3.5.7-bin/conf/
cp zoo_sample.cfg zoo.cfg
# 修改zoo.cfg配置文件，在zoo.cfg中添加以下内容。
vim zoo.cfg
```

zoo.cfg:找到 `dataDir=/tmp/zookeeper` 那行，用前两行替换，剩下四行直接添加到文件中

```text
dataDir=/home/modules/apache-zookeeper-3.5.7-bin/data
dataLogDir=/home/modules/apache-zookeeper-3.5.7-bin/logs
server.1=node1iip:2888:3888
server.2=node2iip:2888:3888
server.3=node3iip:2888:3888
server.4=node4iip:2888:3888
```

```shell
cd .. #进入上层目录
# 或 cd /home/modules/apache-zookeeper-3.5.7-bin/
mkdir -p data
mkdir -p logs
echo "1" > data/myid

# 将ZooKeeper安装文件夹复制到其他节点。
cd /home/modules/
scp -r apache-zookeeper-3.5.7-bin root@node2:$PWD
scp -r apache-zookeeper-3.5.7-bin root@node3:$PWD
scp -r apache-zookeeper-3.5.7-bin root@node4:$PWD

#登录 node2、node3、node4，修改 myid 内容。
# On node2
cd /home/modules/apache-zookeeper-3.5.7-bin
echo "2" > data/myid
# On node3
cd /home/modules/apache-zookeeper-3.5.7-bin
echo "3" > data/myid
# On node4
cd /home/modules/apache-zookeeper-3.5.7-bin
echo "4" > data/myid

# 验证ZooKeeper集群是否安装成功，在四个节点分别运行如下命令。leader 的选择与启动顺序有关
# 在四个节点都启动
zkServer.sh start
# 查看各个节点状态(全部节点启动后再进行查询，否则会失败)
zkServer.sh status
# <!--TODO: 截图3.1, 4台服务器都运行, 含上面两条指令, 共4张-->
# 使用stop命令终止集群运行
zkServer.sh stop

# 修改配置文件

# 停止Flink的standalone模式，并启动ZooKeeper和Hadoop集群服务。
stop-cluster.sh
start-all.sh
zkServer.sh start
# hadoop dfsadmin -report
# zkServer.sh status

# On node1
# 修改FLink的配置文件。
cd /home/modules/flink-1.8.0/conf/
# 修改flink-conf.yaml配置文件。
vim flink-conf.yaml
# 修改masters配置文件。
vim masters
```

flink-conf.yaml: 直接添加到文件中

```text
high-availability: zookeeper
high-availability.storageDir: hdfs://node1:8020/flink
high-availability.zookeeper.path.root: /flink
high-availability.zookeeper.quorum: node1:2181,node2:2181,node3:2181,node4:2181
```

masters: 覆盖原文件内容

```shell
node1:8081
node2:8081
```

```shell
# On node1
# 在HDFS上创建flink文件夹
hadoop fs -mkdir -p /flink

# 分发配置文件
cd /home/modules/flink/conf/
scp -r flink-conf.yaml masters root@node2:$PWD
scp -r flink-conf.yaml masters root@node3:$PWD
scp -r flink-conf.yaml masters root@node4:$PWD

cd /home/modules/flink-1.8.0/lib/
scp -r flink-shaded-hadoop-2-uber-2.7.5-10.0.jar root@node2:$PWD
scp -r flink-shaded-hadoop-2-uber-2.7.5-10.0.jar root@node3:$PWD
scp -r flink-shaded-hadoop-2-uber-2.7.5-10.0.jar root@node4:$PWD

# 启动Flink集群
# 正常启动Flink，jps可以看到Flink对应的进程已经正常启动。
start-cluster.sh
jps
```

```shell
# jps结果
Jps
SecondaryNameNode
TaskManagerRunner
NameNode
QuorumPeerMain
StandaloneSessionClusterEntrypoint
```

访问node1服务器的Web界面，直接在浏览器中访问<http://node2ip:8081/#/overview>，node2服务器类似。

```shell
# 在HA环境下提交任务与standalone单节点模式下是一样的，即使JobManager所在服务器宕机也没有关系，JobManager会自动切换。

# 在node1上启动Socket服务，输入单词。
nc -lk 9000
# <!--TODO: 截图3.2, 单词-->
# 另外打开一个node1的shell页面，在node1上启动Flink自带的单词统计程序，接收输入的Socket数据并进行统计。
cd /home/modules/flink-1.8.0
bin/flink run examples/streaming/SocketWindowWordCount.jar --hostname node1 --port 9000
# 在Web管理界面的Task Managers目录中，选择Free Slots为 0 的一项，选中后可以在它对应的Stdout中看到单词的统计结果。
# <!--TODO: 截图2.2, Web页面-->
# 执行以下命令并查看统计结果
cd /home/modules/flink-1.8.0/log/
tail -200f flink-root-taskexecutor-0-node1.out
# <!--TODO: 截图3.2, 终端窗口-->

# 模拟宕机情况实现自动切换

# 将node1服务器的JobManager进程关闭，过一段时间之后查看node2的JobManager是否能够访问。
jps
kill StandaloneSessionClusterEntrypoint_PID #改成实际pid
jps
```

关闭node1的JobMananger后刷新Web页面已经无法访问了。
<http://node1ip:8081/#/overview>
但是node2的Web界面依旧能够正常访问，并且能够看到jobmanager.rpc.address地址已经变为node2。
<http://node2ip:8081/#/overview>

<!--TODO: 截图3.3, Web管理界面的Job Manager目录-->

### 实验4步骤

```shell
# 将上个实验启动的进程全部关闭
stop-cluster.sh
stop-all.sh
zkServer.sh stop

# 修改yarn-site.xml配置文件
# 在node1上添加以下配置属性到该文件中进行修改。
cd /home/modules/hadoop-2.7.7/etc/hadoop/
vim yarn-site.xml
```

yarn-site.xml: 添加到 `<configuration></configuration>` 标签内

```xml
<property>
  <name>yarn.resourcemanager.am.max-attempts</name>
  <value>4</value>
  <description>The maximum number of application master execution attempts.</description>
</property>
<property>
  <name>yarn.application.classpath</name>
  <value>/home/modules/hadoop-2.7.7/etc/hadoop,/home/modules/hadoop-2.7.7/share/hadoop/common/*,/home/modules/hadoop-2.7.7/share/hadoop/common/lib/*,/home/modules/hadoop-2.7.7/hdfs/*,/home/modules/hadoop-2.7.7/share/hadoop/hdfs/lib/*,/home/modules/hadoop-2.7.7/share/hadoop/yarn/*,/home/modules/hadoop-2.7.7/share/hadoop/yarn/lib/*,/home/modules/hadoop-2.7.7/mapreduce/*,/home/modules/hadoop-2.7.7/share/hadoop/mapreduce/lib/*</value>
</property>


```

```shell
# 然后将修改后的配置文件分发到其他节点上
cd /home/modules/hadoop-2.7.7/etc/hadoop/
scp yarn-site.xml root@node2:$PWD
scp yarn-site.xml root@node3:$PWD
scp yarn-site.xml root@node4:$PWD

# 修改Flink配置文件
# 在node1上执行以下命令修改配置文件
cd /home/modules/flink-1.8.0/conf/
vim flink-conf.yaml
```

flink-conf.yaml:用以下文本覆盖上一个小实验添加的配置

```shell
high-availability: zookeeper
high-availability.storageDir: hdfs://node1:8020/flink_yarn_ha
high-availability.zookeeper.path.root: /flink-yarn
high-availability.zookeeper.quorum: node1:2181,node2:2181,node3:2181,node4:2181
yarn.application-attempts: 10
```

```shell

cd /home/modules/flink/lib/
scp commons-cli-1.4.jar root@node2:$PWD
scp commons-cli-1.4.jar root@node3:$PWD
scp commons-cli-1.4.jar root@node4:$PWD

scp flink-shaded-hadoop-3-uber-3.1.1.7.2.1.0-327-9.0.jar root@node2:$PWD
scp flink-shaded-hadoop-3-uber-3.1.1.7.2.1.0-327-9.0.jar root@node3:$PWD
scp flink-shaded-hadoop-3-uber-3.1.1.7.2.1.0-327-9.0.jar root@node4:$PWD

# 启动HDFS和Yarn集群
start-all.sh

# 在4个节点上启动ZooKeeper
zkServer.sh start

# 在HDFS上创建文件夹
hadoop fs -mkdir -p /flink
hadoop fs -ls /

# 在Yarn中启动Flink集群
# 直接在node1上执行以下命令，在Yarn中启动一个全新的Flink集群。可以使用--help查看yarn-session.sh的参数设置。
cd /home/modules/flink-1.8.0/
bin/yarn-session.sh -n 2 -jm 1024 -tm 1024 -d

# 查看Yarn管理界面
# 访问Yarn的 8088 管理界面<http://node1ip:8088/cluster>，可以看到其中有一个应用，这是为Flink单独启动的一个Session。
# <!--TODO: 截图4.1, 含应用的yarn管理页面-->

# 提交任务
cd ~
# 在HDFS上创建文件夹并上传文件
hadoop fs -mkdir -p /flink_input
hadoop fs -put wordcount.txt /flink_input
# 在node1上执行以下命令，提交任务到Flink集群。
cd /home/modules/flink-1.8.0
bin/flink run examples/batch/WordCount.jar -input hdfs://node1:8020/flink_input -output hdfs://node1:8020/flink_output/wordcount-result.txt
# <!--TODO: 截图4.2, 命令运行结果-->
# 如果不是第一次执行且出现报错，可以尝试运行下面这个指令
# hadoop fs -rm /flink_output/wordcount-result.txt
# 查看输出文件内容
hadoop fs -cat /flink_output/wordcount-result.txt
# <!--TODO: 截图4.3, 命令运行结果-->
```

### 实验5-Flink消费Kafka数据步骤

#### 安装kafka

```shell
# 安装Kafka
cd /home/modules/
# 将Kafka安装包分发到各个节点
scp -r kafka_2.10-0.8.2.1 root@node2:$PWD
scp -r kafka_2.10-0.8.2.1 root@node3:$PWD
scp -r kafka_2.10-0.8.2.1 root@node4:$PWD

# 进入各个节点Kafka安装包的config目录，在server.properties配置文件中添加以下属性。
cd /home/modules/kafka_2.10-0.8.2.1/config
vim server.properties
```

server.properties: 每个节点用以下内容(每个节点3行)覆盖含 `broker.id=` 那一行

```shell
# node1
broker.id=1
host.name=node1
zookeeper.connect=node1:2181,node2:2181,node3:2181,node4:2181

# node2
broker.id=2
host.name=node2
zookeeper.connect=node1:2181,node2:2181,node3:2181,node4:2181

# node3
broker.id=3
host.name=node3
zookeeper.connect=node1:2181,node2:2181,node3:2181,node4:2181

# node4
broker.id=4
host.name=node4
zookeeper.connect=node1:2181,node2:2181,node3:2181,node4:2181
```

```shell
# 验证是否安装成功(前提要启动ZooKeeper)
zkServer.sh start
# 在4个节点分别启动Kafka。
cd /home/modules/kafka_2.10-0.8.2.1/config/
kafka-server-start.sh -daemon server.properties
jps
# <!--TODO: 截图5.1, 4台服务器都运行jps, 共4张-->
```

```shell
# jps结果
Kafka #出现该进程时说明安装成功
QuorumPeerMain
NameNode
SecondaryNameNode
ResourceManager
Jps
# end
```

#### 运行jar包

将生成的jar包上传到node1节点。

```shell
# 停止所有服务并重启服务器
stop-cluster.sh
stop-all.sh
zkServer.sh stop
kafka-server-stop.sh

# 重启服务器，重启后记得改hosts

# 在node1节点启动Hadoop和Yarn集群。
start-all.sh
# start-cluster.sh
# On node1 启动yarn-session
cd /home/modules/flink-1.8.0/
bin/yarn-session.sh -n 2 -jm 1024 -tm 1024 -d
# 另外开启一个node1的终端，启动KafKa自带的ZooKeeper。
cd /home/modules/kafka_2.10-0.8.2.1
./bin/zookeeper-server-start.sh config/zookeeper.properties
# 另外开启一个node1的终端，启动node1的Kafka。
cd /home/modules/kafka_2.10-0.8.2.1/config/
kafka-server-start.sh -daemon server.properties
jps
# 创建一个自定义名称为 test 的 topic，在终端启动一个生产者。
cd /home/modules/kafka_2.10-0.8.2.1
./bin/kafka-topics.sh --create --zookeeper node1:2181 --replication-factor 1 --partitions 1 --topic test
kafka-console-producer.sh --broker-list node1:9092 --topic test
# <!--TODO: 截图5.3, 生产者终端, 截图含以上两条指令-->

# 另启动一个node1的终端，运行jar包。记录yarn-session应用所在服务器及端口(在运行结果最后一行左右)
cd ~
flink run -c Flink_Kafka WordCount.jar
# <!--TODO: 截图5.3, 终端图-->

# 在生产者终端中输入单词
# <!--TODO: 截图5.3，单词-->
```

在华为云安全组里开启之前记录的Yarn Session所在的端口
访问 <http://nodenip:port/#/taskmanagers>, 进入Task Managers目录下点击正在运行的任务。进入后点击Stdout页面查看读取到的Kafka数据。
<!--TODO: nodenip为jar包输出结果中Yarn Session所在的机器公网ip;port为对应的端口-->
<!--TODO: 截图5.3, Web界面输出结果-->

```shell
# 停掉所有服务,关机
flink list #查看所有flink任务,记录要停止的id
flink cancel jobID #jobID 为list里找到的任务id
yarn application -kill app-id # app-id 为yarn-session中应用的名称
cd /home/modules/kafka_2.10-0.8.2.1
./bin/zookeeper-server-stop.sh config/zookeeper.properties
cd /home/modules/kafka_2.10-0.8.2.1/config/
kafka-server-stop.sh -daemon server.properties
stop-all.sh
```

## 实验结果与分析

### 实验结束后应得到

1. Hadoop集群
2. ZooKeeper集群
3. Flink集群
4. maven工程压缩包(只包含代码及pom文件即可)

### 实验给分点

> 注：以下截图需包含具体 学号/IP 信息，否则不得分。此外每个实验的输入单词不可以完全相同。

1. 第一部分：local模式部署安装
    - 主节点jps查看当前进程 **(1分)**
    - 终端单词输入截图，Web管理界面或主节点终端输出out文件查看单词统计程序结果 **(1分)**
2. 第二部分：standalone模式部署安装
    - 四个节点jps查看当前进程或Web管理界面查看Task Manager目录信息 **(1分)**
    - 终端单词输入截图，Web管理界面或对应节点终端输出out文件查看单词统计程序结果 **(2分)**
3. 第三部分：standalone模式的HA环境
    - 给出四个节点启动ZooKeeper后的状态 **(1分)**
    - 终端单词输入截图，Web管理界面或对应节点终端输出out文件查看单词统计程序结果 **(2分)**
    - 宕机处理后node2节点Web管理界面的Job Manager目录 **(1分)**
4. 第四部分：Flink on Yarn模式
    - 给出Yarn的Web界面，体现出当前启动的Flink应用 **(1分)**
    - 任务提交并运行结果 **(2分)**
    - 查看hdfs上的输出文件 **(2分)**
5. 第五部分：Flink消费Kafka数据
    - 四个节点jps出现Kafka进程截图 **(1分)**
    - maven项目压缩包，包含代码和pom文件即可 **(3分)**
    - 启动生产者、运行jar包、Web界面输出结果截图 **(2分)**

## 概念解释及参考

[flink启动没有taskmangerrunner](https://blog.csdn.net/jmx_bigdata/article/details/98610653)
