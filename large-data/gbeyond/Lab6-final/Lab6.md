   0               1                 2               3             4
    
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |V=2|P|X|  CC |M|     PT          | sequence number             |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   | timestamp                                                     |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   | synchronization source (SSRC) identifier                      |
   +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
   | contributing source (CSRC) identifiers                        |
   | ....                                                          |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    80 88 3f 9b 00 00 00 a0 54 d6 61 cb 

    1000 0000 1000 1000 0011 1111 1001 1011 
    0000 0000 0000 0000 0000 0000 1010 0000
    0101 0100 1011 0110 0110 0001 1100 0101


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
  * [实验步骤](#实验步骤)
  * [一、local模式部署安装Flink](#一local模式部署安装flink)
    * [实验介绍](#实验介绍)
    * [实验目的](#实验目的)
    * [实验步骤](#实验步骤-1)
  * [二、standalone模式部署安装Flink](#二standalone模式部署安装flink)
    * [实验介绍](#实验介绍-1)
    * [实验目的](#实验目的-1)
    * [实验步骤](#实验步骤-2)
  * [三、standalone模式的HA环境](#三standalone模式的ha环境)
    * [实验介绍](#实验介绍-2)
    * [实验目的](#实验目的-2)
    * [实验步骤](#实验步骤-3)
  * [四、Flink on Yarn模式](#四flink-on-yarn模式)
    * [实验介绍](#实验介绍-3)
    * [实验目的](#实验目的-3)
    * [实验步骤](#实验步骤-4)
  * [五、Flink消费Kafka数据](#五flink消费kafka数据)
    * [实验介绍](#实验介绍-4)
    * [实验目的](#实验目的-4)
    * [实验步骤](#实验步骤-5)
      * [安装kafka](#安装kafka)
      * [程序编写](#程序编写)
        * [创建maven工程](#创建maven工程)
        * [Java 代码](#java-代码)
        * [程序打包](#程序打包)
      * [运行jar包](#运行jar包)
  * [实验结果与分析](#实验结果与分析)
    * [实验结束后应得到](#实验结束后应得到)
    * [实验给分点](#实验给分点)
  * [概念解释及参考](#概念解释及参考)

## TODO

* [ ] 安装jdk8
* [ ] 整理实验顺序
* [ ] Web页面端口8081

## 实验步骤

## 一、local模式部署安装Flink

### 实验介绍

local模式下的Flink部署安装只需要使用单台机器，仅用本地线程来模拟其程序运行，不需要启动任何进程，适用于软件测试等情况。这种模式下，机器不用更改任何配置，只需要安装JDK 8的运行环境即可。

### 实验目的

* 实现Flink的安装；
* 学会Flink的脚本启动；
* 使用Flink自带的单词统计程序进行测试。

### 实验步骤

```shell
# 上传安装包并解压
将压缩包内的 flink-1.8.0-bin-scala_2.11.tgz 上传到node1服务器中，拷贝到 /home/modules 路径下，然后进行解压。

# On node1 ~
cp flink-1.8.0-bin-scala_2.11.tgz /home/modules/
cd /home/modules/
tar -zxvf flink-1.8.0-bin-scala_2.11.tgz

#### 配置全局环境变量

在/etc/profile配置文件中添加flink路径如下。
vim /etc/profile

# FLINK
export FLINK_HOME=/home/modules/flink-1.8.0
export PATH=$FLINK_HOME/bin:$PATH

start-cluster.sh # 脚本启动Flink进程
jps #查看最新启动的两个进程
```

> 注：在实验一中将 node1 节点 hosts 文件中 127.0.0.1 地址注释掉的同学在这一部分需要取消 node1 节点中 hosts 文件的注释，保证 localhost 能被 node1 找到。

```text
StandaloneSessionClusterEntrypoint
TaskManagerRunner
Jps
```

Web界面访问
成功启动两个进程后，访问 8081 端口即可访问Flink的Web管理界面
<http://node1ip:8081/#/overview>

```shell
# 运行Flink自带的测试用例
# 在node1上使用Linux的nc命令向Socket发送一些单词。
sudo yum -y install nc
nc -lk 9000

# 另外打开一个node1的shell页面，在node1上启动Flink自带的单词统计程序，接收输入的Socket数据并进行统计。
cd /home/modules/flink-1.8.0
bin/flink run examples/streaming/SocketWindowWordCount.jar --hostname localhost --port 9000

# 查看统计结果

# 在Flink的Web管理界面进入Task Managers目录下，选择Stdout选项卡，得到统计结果。
<!--TODO: web 图-->
# Flink自带的测试用例统计结果在log文件夹路径下。在node1上执行以下命令查看统计结果。
cd /home/modules/flink-1.8.0/log/
tail -200f flink-root-taskexecutor-0-node1.out

# 关闭local模式。
stop-cluster.sh
```

## 二、standalone模式部署安装Flink

### 实验介绍

使用standalone模式需要启动Flink的主节点JobManager以及从节点的TaskManager，具体的任务进程划分见下表。

| 服务及IP    | node1 | node2 | node3 | node4 |
| ----------- | ----- | ----- | ----- | ----- |
| JobManager  | 是    | 否    | 否    | 否    |
| TaskManager | 是    | 是    | 是    | 是    |

### 实验目的

实现standalone模式下Flink进程的启动。

### 实验步骤

停止node1服务器local模式下的进程后，修改配置文件。

```shell

# On node1
cd /home/modules/flink-1.8.0/conf/

# 更改Flink配置文件
vim flink-conf.yaml
```

```text
# 指定JobManager所在的服务器为node1
jobmanager.rpc.address: node1
```

```shell
# 更改slaves配置文件。
vim slaves
```

```text
node1
node2
node3
node4
```

```shell
# 分发配置文件
<!--TODO: 不用分发这么多文件;路径带不带/-->
cd /home/modules/
scp -r flink-1.8.0 root@node2:$PWD/
scp -r flink-1.8.0 root@node3:$PWD/
scp -r flink-1.8.0 root@node4:$PWD/

# 启动Flink集群
start-cluster.sh
jps
```

```text
node1
StandaloneSessionClusterEntrypoint
TaskManagerRunner
Jps

other node
TaskManagerRunner
Jps
```

> 注：在实验第一部分中将 node1 节点 hosts 文件中 127.0.0.1 地址注释取消掉了，这一部分需要再次注释掉 node1 节点 hosts 文件中 127.0.0.1 的注释，保证本机的 TaskManager 能够正确启动

进入Web管理页面能看到Task Managers和Task Slots数量为 4 ，说明集群正确启动。
<http://node1ip:8081/#/overview>

```shell

#### 运行Flink自带测试用例

# 在node1启动Socket服务，输入单词。
nc -lk 9000

# 另外打开一个node1的shell页面，在node1上启动Flink自带的单词统计程序，接收输入的Socket数据并进行统计。
cd /home/modules/flink-1.8.0
bin/flink run examples/streaming/SocketWindowWordCount.jar --hostname node1 --port 9000

# 在Web管理界面的Task Managers目录中，选择Free Slots为 0 的一项，选中后可以在它对应的Stdout中看到单词的统计结果。
<!--TODO: web 图-->

# 根据执行的Task Manager，在Free Slots为 0 的服务器的命令行中执行以下命令查看统计结果

# On node2
cd /home/modules/flink-1.8.0/log/
tail -200f flink-root-taskexecutor-0-node2.out
```

## 三、standalone模式的HA环境

### 实验介绍

上一节实现了Flink的standalone模式部署安装，并且能够正常提交任务到集群上。其中主节点是 `JobManager` ，但 `JobManager` 是单节点，必然会有单节点故障问题产生，所以也可以在standalone模式下借助ZooKeeper将 `JobManager` 实现为高可用模式。

### 实验目的

安装ZooKeeper，并实现standalone模式的HA环境；
实现Flink在standslone模式的HA环境下提交任务。

### 实验步骤

将压缩包中的apache-zookeeper-3.5.7-bin.tar.gz上传到node1节点中

```shell

#### 安装ZooKeeper

# 解压安装包。

cp apache-zookeeper-3.5.7-bin.tar.gz /home/modules/
cd /home/modules/
tar -xzvf apache-zookeeper-3.5.7-bin.tar.gz

# 在4台服务器配置全局环境变量，在/etc/profile文件中添加ZooKeeper路径。
source /etc/profile

# ZOOKEEPER
export ZK_HOME=/home/modules/apache-zookeeper-3.5.7-bin
export PATH=$ZK_HOME/bin:$PATH

# 使用复制命令生成配置文件
cd apache-zookeeper-3.5.7-bin/conf/
cp zoo_sample.cfg zoo.cfg
# 修改zoo.cfg配置文件，在zoo.cfg中添加以下内容。
vim zoo.cfg

dataDir=/home/modules/apache-zookeeper-3.5.7-bin/data
dataLogDir=/home/modules/apache-zookeeper-3.5.7-bin/logs
server.1=node1iip:2888:3888
server.2=node2iip:2888:3888
server.3=node3iip:2888:3888
server.4=node4iip:2888:3888

<!--TODO: 和lab4区别>
cd ..
mkdir -p data
mkdir -p logs
echo "1" > data/myid

将ZooKeeper安装文件夹复制到其他节点。

scp -r apache-zookeeper-3.5.7-bin root@node2:$PWD
scp -r apache-zookeeper-3.5.7-bin root@node3:$PWD
scp -r apache-zookeeper-3.5.7-bin root@node4:$PWD

#登录 node2、node3、node4，创建软链接并修改 myid 内容。
# On node2
cd /home/modules/apache-zookeeper-3.5.7-bin
echo "2" > data/myid
# On node3
cd /home/modules/apache-zookeeper-3.5.7-bin
echo "3" > data/myid
# On node4
cd /home/modules/apache-zookeeper-3.5.7-bin
echo "4" > data/myid

# 验证ZooKeeper集群是否安装成功，在四个节点分别运行如下命令。

# leader 的选择与启动顺序有关，具体原理可以自己查询。
zkServer.sh start
# 可以使用zkServer.sh status命令查看各个节点状态（待全部节点启动后再进行查询，否则会失败）
zkServer.sh status
# 使用stop命令终止集群运行
zkServer.sh stop

# 修改配置文件

# 停止Flink的standalone模式，并启动ZooKeeper和Hadoop集群服务。
stop-cluster.sh
start-all.sh
zkServer.sh start
hadoop dfsadmin -report
zkServer.sh status

# On node1
# 修改FLink的配置文件。
cd /home/modules/flink-1.8.0/conf/
# 修改flink-conf.yaml配置文件。
vim flink-conf.yaml

high-availability: zookeeper
high-availability.storageDir: hdfs://node1:8020/flink
high-availability.zookeeper.path.root: /flink
high-availability.zookeeper.quorum: node1:2181,node2:2181,node3:2181,node4:2181

# 修改masters配置文件。
vim masters

node1:8081
node2:8081

# On node1
# 在HDFS上创建flink文件夹
hadoop fs -mkdir -p /flink

# 分发配置文件
scp -r flink-conf.yaml masters root@node2:$PWD
scp -r flink-conf.yaml masters root@node3:$PWD
scp -r flink-conf.yaml masters root@node4:$PWD

# 启动Flink集群

#在启动之前，先将期末实验一压缩包中的flink-shaded-hadoop-2-uber-2.7.5-10.0.jar文件复制到下图中的路径，然后分发到其他三个节点。因为在flink1.8之后的Flink版本把hadoop的一些依赖删除了，所以会报错找不到相应的jar包，需要手动导入对应的jar包。

cp flink-shaded-hadoop-2-uber-2.7.5-10.0.jar /home/modules/flink-1.8.0/lib/
cd /home/modules/flink-1.8.0/lib/

scp -r flink-shaded-hadoop-2-uber-2.7.5-10.0.jar root@node2:$PWD
scp -r flink-shaded-hadoop-2-uber-2.7.5-10.0.jar root@node3:$PWD
scp -r flink-shaded-hadoop-2-uber-2.7.5-10.0.jar root@node4:$PWD

然后正常启动Flink，jps可以看到Flink对应的进程已经正常启动。
start-cluster.sh
jps

Jps
SecondaryNameNode
TaskManagerRunner
NameNode
QuorumPeerMain
StandaloneSessionClusterEntrypoint

# 访问node1服务器的Web界面，直接在浏览器中访问<http://node1ip:8081/#/overview>，node2服务器类似。

# 3.7 在HA环境下提交任务

# 在HA环境下提交任务与standalone单节点模式下是一样的，即使JobManager所在服务器宕机也没有关系，JobManager会自动切换。

# 在node1上启动Socket服务，输入单词。
nc -lk 9000
<!--TODO: 单词>

# 启动Flink自带的单词统计程序，接收输入的Socket数据并进行统计。
cd /home/modules/flink-1.8.0
bin/flink run examples/streaming/SocketWindowWordCount.jar --hostname node1 --port 9000
# 执行以下命令并查看统计结果
cd /home/modules/flink-1.8.0/log/
tail -200f flink-root-taskexecutor-0-node1.out

# 模拟宕机情况实现自动切换

# 将node1服务器的JobManager进程关闭，过一段时间之后查看node2的JobManager是否能够访问。
jps
kill StandaloneSessionClusterEntrypoint_PID #改成实际pid
jps
# 上图关闭node1的JobMananger后可以看到如下图其Web页面已经无法访问了。
http://node1ip:8081/#/overview
# 但是node2的Web界面依旧能够正常访问，并且能够看到jobmanager.rpc.address地址已经变为node2。
http://node2ip:8081/#/overview
```

## 四、Flink on Yarn模式

### 实验介绍

Flink任务也可以运行在Yarn上，将Flink任务提交到Yarn平台可以实现统一的任务资源调度管理，方便开发人员管理集群中的CPU和内存等资源。

本模式需要先启动集群，然后再提交作业，接着会向Yarn申请资源空间，之后资源保持不变。如果资源不足，下一个作业就无法提交，需要等到Yarn中的一个作业执行完成后释放资源。

### 实验目的

完成Flink on Yarn模式的配置；
在Yarn中启动Flink集群；
以文件的形式进行任务提交。

### 实验步骤

```shell

#### 修改yarn-site.xml配置文件

# 将上个实验启动的进程全部关闭
stop-cluster.sh
stop-all.sh
zkServer.sh stop

# 在node1上添加以下配置属性到该文件中进行修改。
cd /home/modules/hadoop-2.7.7/etc/hadoop/
vim yarn-site.xml

<property>
  <name>yarn.resourcemanager.am.max-attempts</name>
  <value>4</value>
  <description>The maximum number of application master execution attempts.</description>
</property>

# 然后将修改后的配置文件分发到其他节点上
scp -r yarn-site.xml root@node2:$PWD
scp -r yarn-site.xml root@node3:$PWD
scp -r yarn-site.xml root@node4:$PWD
# 启动HDFS和Yarn集群
start-all.sh

# 修改Flink配置文件

# 在node1上执行以下命令修改配置文件

cd /home/modules/flink-1.8.0/conf/
vim flink-conf.yaml

high-availability: zookeeper
high-availability.storageDir: hdfs://node1:8020/flink_yarn_ha
high-availability.zookeeper.path.root: /flink-yarn
high-availability.zookeeper.quorum: node1:2181,node2:2181,node3:2181,node4:2181
yarn.application-attempts: 10

# 在各个节点上启动ZooKeeper。
zkServer.sh start

# 在HDFS上创建文件夹
hadoop fs -mkdir -p /flink_yarn_ha
hadoop fs -ls /

# 在Yarn中启动Flink集群

# 直接在node1上执行以下命令，在Yarn中启动一个全新的Flink集群。可以使用--help查看yarn-session.sh的参数设置。
cd /home/modules/flink-1.8.0/conf/
cd ..
bin/yarn-session.sh -n 2 -jm 1024 -tm 1024 -d
<!--TODO: 成功图-->

# 查看Yarn管理界面
# 访问Yarn的 8088 管理界面<http://node1ip:8088/cluster>，可以看到其中有一个应用，这是为Flink单独启动的一个Session。

# 提交任务

# 在node1上准备单词文件wordcount.txt。

hello world
flink hadoop
hive spark

# 在HDFS上创建文件夹并上传文件
hadoop fs -mkdir -p /flink_input
hadoop fs -put wordcount.txt /flink_input
# 在node1上执行以下命令，提交任务到Flink集群。
cd /home/modules/flink-1.8.0
bin/flink runexamples/batch/WordCount.jar -input hdfs://node1:8020/flink_input -output hdfs://node1:8020/flink_output/wordcount-result.txt
# 查看输出文件内容
hadoop fs -cat /flink_output/wordcount-result.txt
```

## 五、Flink消费Kafka数据

### 实验介绍

对于实时处理，实际工作中的数据源一般都是使用Kafka。Flink提供了一个特有的Kafka连接器去读写Kafka topic的数据。本实验通过本地打jar包上传到Flink集群，去处理终端Kafka输入的数据。

### 实验目的

* 安装Kafka；
* 本地编辑代码读取Kafka数据，并且打成jar包；
* 将jar包上传到Flink集群运行。

### 实验步骤

#### 安装kafka

上传压缩包中的kafka_2.10-0.8.2.1.tgz安装包到node1并解压到指定路径下。

```shell

# 安装Kafka
cp kafka_2.10-0.8.2.1.tgz /home/modules/
cd /home/modules
tar -xzvf kafka_2.10-0.8.2.1.tgz

# 将Kafka安装包分发到各个节点
scp -r kafka_2.10-0.8.2.1 root@node2:$PWD
scp -r kafka_2.10-0.8.2.1 root@node3:$PWD
scp -r kafka_2.10-0.8.2.1 root@node4:$PWD

# 配置每个节点的全局环境变量，在/etc/profile文件中添加Kafka路径。
vim /etc/profile

# Kafka
export KAFKA_HOME=/home/modules/kafka_2.10-0.8.2.1
export PATH=$KAFKA_HOME/bin:$PATH

source /etc/profile
# 进入各个节点Kafka安装包的config目录，在server.properties配置文件中添加以下属性。
vim server.properties
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

# 验证是否安装成功（前提要启动ZooKeeper），在各个节点分别启动Kafka。

zkServer.sh start
kafka-server-start.sh -daemon server.properties
jps

Kafka #出现该进程时说明安装成功
QuorumPeerMain
NameNode
SecondaryNameNode
ResourceManager
Jps
```

#### 程序编写

##### 创建maven工程

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

##### Java 代码

<!--TODO: 程序引包-->
编写Flink读取Kafka数据的代码(可以在本机尝试运行代码正确后再打jar包)。注意 import 的包对不对

##### 程序打包

带依赖打包WordCount.jar

> mvn clean
> mvn package assembly:assembly

#### 运行jar包

将生成的jar包上传到node1节点。

```shell
# 在node1节点首先执行以下命令启动Hadoop和Yarn集群。
start-dfs.sh
start-yarn.sh
# 或 start-all.sh
# 启动KafKa自带的ZooKeeper。
cd /home/modules/kafka_2.10-0.8.2.1
./bin/zookeeper-server-start.sh config/zookeeper.properties
# 另外开启一个node1的终端，启动node1的Kafka。
./bin/kafka-server-start.sh -daemon server.properties
jps
# 创建一个自定义名称为 test 的 topic，在终端启动一个生产者。
./bin/kafka-topics.sh --create --zookeeper node1:2181 --replication-factor 1 --partitions 1 --topic wordsendertest
kafka-console-producer.sh --broker-list node1:9092 --topic test

# 另启动一个node1的终端，运行jar包。
flink run -c kafkawc.WordCount WordCount.jar

# 在生产者终端中输入单词
<!--TODO: 如何输入-->
```

<http://nodenip:port/#/taskmanagers>
<!--TODO: nodenip为程序输出结果中Yarn Session所在的机器公网ip;port为对应的端口-->
根据前面Yarn Session所在的机器公网ip(Found application JobManager host name 'noden' and port 'port')访问Flink的Web管理界面。进入Task Managers目录下点击正在运行的任务。进入后点击Stdout页面就可以看见读取到的Kafka数据了。

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

[root@cpy-2021211138 flink]# bin/flink run examplex/batch/WordCount.jar -inputhdfs://node1:8020/flink_input -output hdfs://node1:8020/flink_coutput/wordcount-result.txt
2024-06-01 23:33:01,626 INF0 org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - Found Yarn properties file under /tmp/.yarn-properties-root.
2024-06-01 23:33:01,626 INF0 org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - Found Yarn properties file under /tmp/.yarn-properties-root.
2024-06-01 23:33:01,980 INFO org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - YARN properties set default parallelism to 2
2024-06-01 23:33:01,980 INFO org.apache.flink.yarn.cli.FlinkYarnSessionCli                  - YARN properties set default parallelism to 2
YARN properties set default parallelism to 2
2024-06-01 23:33:01,059 INF0 org.apache.hadoop.yarn.client.RMProxy                          - Connecting to ResourceManager at node1/192.168.0.30:8032
2024-06-01 23:33:02,162 INF0 org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - No path for the flink jar passed. Using the location of class org.apache.flink.yarn.YarnclusterDescriptor to locate the jar
2024-06-01 23:33:02,162 INF0 org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - No path for the flink jar passed. Using the location of class org.apache.flink.yarn.YarnclusterDescriptor to locate the jar
2024-06-01 23:33:02,165 WARN org.apache.flink.yarn.AbstractYarnclusterDescriptor            - Neither the HADOOP_CONF_DIR nor the YARN_CONF_DIR environment variable is set. The Flink YARN Client needs one of these to be set to properly load the Hadoop configuration for accessing YARN.
2024-06-01 23:33:02,217 INF0 org.apache.flink.yarn.AbstractYarnclusterDescriptor            - Found application Job Manager host name 'node2' and port '44739' from supplied application id 'application 1717254001945 0003'
Starting execution of program
Program execution finished
Job with JobID ea0b4f433a7dc8ec8c86deeb514b68e has finished.
Job Runtime: 13538 ms

[root@cpy-2021211138 flink]# hdfs dfs -cat /flink_output/wordcount-result.txt
24/06/01 23:34:25 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
flink 1
cpy 1
hello 1
test 1
spark 1
world 1

[root@cpy-2021211138-0001 ~]# flink run -c Flink_Kafka WordCount.jar
2024-06-02 11:39:25,626 INF0 org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - Found Yarn properties file under /tmp/.yarn-properties-root.
2024-06-02 11:39:25,626 INF0 org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - Found Yarn properties file under /tmp/.yarn-properties-root.
2024-06-02 11:39:25,980 INFO org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - YARN properties set default parallelism to 2
2024-06-02 11:39:25,980 INFO org.apache.flink.yarn.cli.FlinkYarnSessionCli                  - YARN properties set default parallelism to 2
YARN properties set default parallelism to 2
2024-06-02 11:39:25,059 INF0 org.apache.hadoop.yarn.client.RMProxy                          - Connecting to ResourceManager at cpy-2021211138-0003/192.168.0.128:8032
2024-06-02 11:39:25,162 INF0 org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - No path for the flink jar passed. Using the location of class org.apache.flink.yarn.YarnclusterDescriptor to locate the jar
2024-06-02 11:39:25,162 INF0 org.apache.flink.yarn.cli.FlinkYarnSessioncli                  - No path for the flink jar passed. Using the location of class org.apache.flink.yarn.YarnclusterDescriptor to locate the jar
2024-06-02 11:39:25,165 WARN org.apache.flink.yarn.AbstractYarnclusterDescriptor            - Neither the HADOOP_CONF_DIR nor the YARN_CONF_DIR environment variable is set. The Flink YARN Client needs one of these to be set to properly load the Hadoop configuration for accessing YARN.
2024-06-02 11:39:25,217 INF0 org.apache.flink.yarn.AbstractYarnclusterDescriptor            - Found application Job Manager host name 'cpy-2021211138-0003' and port '44739' from supplied application id 'application 1717309318704 0003'
Starting execution of program
