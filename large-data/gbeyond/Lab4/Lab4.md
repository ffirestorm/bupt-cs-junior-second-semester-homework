# Lab04

安装 HBase、Zookeeper 及 HBase 应用实践

## 前提

集群各节点的软件规划：
本实验手册示例命令中，节点名称是 name-number-000{编号}，学生需要修改主
机名为对应的姓名缩写+学号
机器名称 | 服务名称
---|---
name-number-0001 | QuorumPeerMain、NameNode、ResourceManager、Hmaster
name-number-0002 | QuorumPeerMain、DataNode、NodeManager、JournalNode、HRegionServer
name-number-0003 | QuorumPeerMain、DataNode、NodeManager、JournalNode、HRegionServer
name-number-0004 | QuorumPeerMain、DataNode、NodeManager、JournalNode、HRegionServer

开始本次实验前请确保已安装好 Hadoop 并配置好环境变量

## TODO

* [ ] log4j
* [ ] wegt apt 等下载方式
* [ ] touch
* [ ] 固定监控ip
* [ ] web页面
* [ ] hbase shell 运行ls卡住
* [ ] jps 的
* [ ] hbase create

## 实验步骤

### 下载并安装 zookeeper

在用户目录下下载 zookeeper 压缩包并解压

```shell
# On node1 ~
wget https://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz #下载安装包
mv zookeeper-3.4.6.tar.gz /usr/local #移动
cd /usr/local
tar -zxvf zookeeper-3.4.6.tar.gz #解压安装
ln -s zookeeper-3.4.6 zookeeper #建立软链接，便于后期版本更换

#打开配置文件，添加 ZooKeeper 到环境变量。
vim /etc/profile
export ZOOKEEPER_HOME=/usr/local/zookeeper
export PATH=$ZOOKEEPER_HOME/bin:$PATH
source /etc/profile #使环境变量生效

cd /usr/local/zookeeper/conf #进入 ZooKeeper 所在目录。
cp zoo_sample.cfg zoo.cfg #拷贝配置文件。
# 修改配置文件，修改数据目录。
vim zoo.cfg
dataDir=/usr/local/zookeeper/tmp
# 在最后添加如下代码，server.1-4 是部署 ZooKeeper 的节点，1，2，3，4 分别是各服务器 /usr/local/zookeeper/tmp/myid 文件的内容。这里 192.168.0.xxx 对应的是运行 QuorumPeerMain 的服务器的内网 IP，需要改成自己集群的。
server.1=192.168.0.132:2888:3888
server.2=192.168.0.83:2888:3888
server.3=192.168.0.62:2888:3888
server.4=192.168.0.154:2888:3888
修改后的 zoo.cfg 如下：

#创建 tmp 目录作数据目录。
mkdir /usr/local/zookeeper/tmp
#在 tmp 目录中创建一个空文件 myid，并向该文件写入 ID。
touch /usr/local/zookeeper/tmp/myid
echo 1 > /usr/local/zookeeper/tmp/myid

#将配置好的 ZooKeeper 拷贝到其它节点。（也可以将 zookeeper 压缩包拷贝到其他节点，在进行相同的配置，这样等待时间较短）
scp -r /usr/local/zookeeper-3.4.6 root@name-number-0002:/usr/local
scp -r /usr/local/zookeeper-3.4.6 root@name-number-0003:/usr/local
scp -r /usr/local/zookeeper-3.4.6 root@name-number-0004:/usr/local

#登录 name-number-0002、name-number-0003、name-number-0004，创建软链接并修改 myid 内容。
name-number-0002：
cd /usr/local
ln -s zookeeper-3.4.6 zookeeper
echo 2 > /usr/local/zookeeper/tmp/myid
name-number-0003：
cd /usr/local
ln -s zookeeper-3.4.6 zookeeper
echo 3 > /usr/local/zookeeper/tmp/myid
name-number-0004：
cd /usr/local
ln -s zookeeper-3.4.6 zookeeper
echo 4 > /usr/local/zookeeper/tmp/myid

#分别在 name-number-0002，name-number-0003，name-number-0004 上启动 ZooKeeper。
cd /usr/local/zookeeper/bin
./zkServer.sh start
查看 ZooKeeper 状态，注意，Mode 应为 leader 或 follower。
./zkServer.sh status
```

### 下载并安装 HBase

下载 HBase，下载地址：

```shell
wget https://archive.apache.org/dist/hbase/2.0.2/hbase-2.0.2-bin.tar.gz
#将 hbase-2.0.2.tar.gz 放置于 name-number-0001 节点的“/usr/local”目录，并解压。
mv hbase-2.0.2-bin.tar.gz /usr/local
cd /usr/local
tar -zxvf hbase-2.0.2.tar.gz
ln -s hbase-2.0.2 hbase #建立软链接，便于后期版本更换。

# 编辑“/etc/profile”文件，在文件底部添加环境变量，如下所示。
vim /etc/profile
export HBASE_HOME=/usr/local/hbase
export PATH=$HBASE_HOME/bin:$HBASE_HOME/sbin:$PATH
source /etc/profile #使环境变量生效

# 修改 HBase 配置文件
#HBase 所有的配置文件都在“HBASE_HOME/conf”目录下，修改以下配置文件前，切换到“HBASE_HOME/conf”目录。
cd $HBASE_HOME/conf
#修改 hbase-env.sh 文件。
vim hbase-env.sh
#修改环境变量 JAVA_HOME 为绝对路径,注意 JAVA_HOME 和 HBASE_LIBRARY_PATH 要与自己实际安装配置的一致，HBASE_MANAGES_ZK 设为 false。
export JAVA_HOME=/usr/lib/jvm/jdk8u292-b10
export HBASE_MANAGES_ZK=false
export HBASE_LIBRARY_PATH=/usr/local/hadoop/lib/native

#修改 hbase-site.xml 文件。
vim hbase-site.xml
#添加或修改 configuration 标签范围内的部分参数。
<configuration>
 <property>
 <name>hbase.rootdir</name>
 <value>hdfs://name-number-0001:8020/HBase</value>
 </property>
 <property>
 <name>hbase.tmp.dir</name>
 <value>/usr/local/hbase/tmp</value>
 </property>
 <property>
 <name>hbase.cluster.distributed</name>
 <value>true</value>
 </property>
 <property>
 <name>hbase.unsafe.stream.capability.enforce</name>
 <value>false</value>
 </property>
 <property>
 <name>hbase.zookeeper.quorum</name>
<value>name-number-0002:2181,name-number-0003:2181,name-number-0004:2181</value>
 </property>
 <property>
 <name>hbase.unsafe.stream.capability.enforce</name>
 <value>false</value>
 </property>
</configuration>

#修改 regionservers
#编辑 regionservers 文件。
vim regionservers
#将 regionservers 文件内容替换为 agent 节点 IP（可用主机名代替,记得改名）。
name-number-0002
name-number-0003
name-number-0004

#拷贝 hdfs-site.xml
#拷贝 hadoop 目录下的的的 hdfs-site.xml 文件到“hbase/conf/”目录，可选择软链接或拷贝。
cp /home/modules/hadoop-2.7.7/etc/hadoop/hdfs-site.xml /usr/local/hbase/conf/hdfs-site.xml

#拷贝 hbase-2.0.2 到 name-number-0002、name-number-0003、name-number-0004 节点的“/usr/local”目录（也可以将 zookeeper 压缩包拷贝到其他节点，在进行相同的配置，这样等待时间较短）。
for i in {1..3};do scp -r /usr/local/hbase-2.0.2
root@name-number-000${i}:/usr/local/ ;done

#分别登录到 name-number-0002、name-number-0003、name-number-0004 节点，为 hbase-2.0.2 建立软链接。
cd /usr/local
ln -s hbase-2.0.2 hbase

#依次启动 ZooKeeper 和 Hadoop。
#在 name-number-0001 节点上启动 HBase 集群。
/usr/local/hbase/bin/start-hbase.sh
Jps #观察进程是否都正常启动
```

```shell
# On node1
ResourceManager
SecondaryNameNode
NameNode
WrapperSimpleApp
Jps
QuorumPeerMain
HMaster

# On node2
DataNode
WrapperSimpleApp
NodeManager
HRegionServer
Jps
QuorumPeerMain
```

### HBase 实践

启动 Hadoop 集群
在 name-number-0001 运行：
start-dfs.sh
start-yarn.sh

启动 Zookeeper 集群
需要在 name-number-000{2..4}分别运行：./usr/local/zookeeper/bin/zkServer.sh
start

启动 HBase 集群
在 name-number-0001 运行：
进入 HBase Shell 创建实验用表
输入 hbase shell 进入 hbase 交互式环境：

数据库表格设计要求：（未按要求设计扣分）
a. 表格命名：**学号+姓名**
b. 行数不限定，字段名不限定
c. ROW 命名：**学号+姓名+编号**
**【截图 1：数据库表格】（截图需要包含标记信息，未按要求扣分）**
创建表格
create 'member_user','cf1'
向表“member_user”中插入数据
put 'member_user','rk001','cf1:keyword','applicate'
put 'member_user','rk002','cf1:keyword','OnePlus 5'
put 'member_user','rk003','cf1:keyword','iphone 6s'

扫描整个表
scan 'member_user'

#### 编写代码，将 Hbase 中的数据导出到 hdfs 指定目录

##### 创建 Maven 项目

工程名 MyHBase
在 src/java 目录下新建 package，名称 org/namenumber/hbase/inputSource
（namenumber 改成对应的姓名缩写+学号）
pom.xml

```xml
<!-- pom.xml 修改内容-->
  <properties>
    <!--1.8 或 8 都行-->
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <hadoop.version>2.7.7</hadoop.version><!--TODO:2.8.3?-->
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
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-mapreduce</artifactId>
      <version>${hadoop.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-yarn</artifactId>
      <version>${hadoop.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hbase</groupId>
      <artifactId>hbase</artifactId>
      <version>2.0.2</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hbase</groupId>
      <artifactId>hbase-mapreduce</artifactId>
      <version>2.0.2</version>
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

##### java 代码

注意 import 的包对不对, 按照实验指导里的图片敲或

新建类 MemberMapper，完整代码如下
**【截图 2：完整 Mapper 代码】**
**（截图需要包含标记信息，未按要求扣分，代码不完整扣分）**
新建类 Main，完整代码如下

##### 程序打包

带依赖打包MyWordCount.jar

> mvn clean
> mvn package assembly:assembly

填写主类名称,选择主类

### 运行

启动服务器
修改hosts

上传jar包,到node1的root目录下

```shell
# 启动 hadoop 并验证运行情况
start-all.sh
hadoop dfsadmin -report #验证运行情况

hadoop fs -ls / #查看目录
hadoop fs -ls -R / #查看目录及子目录下所文件
hadoop fs -mkdir /testmr #创建testmr文件夹

# 在~目录下
# 传输input.txt到 testmr 文件夹下,指令二选一
hadoop fs -put input.txt /test
hadoop fs -put /root/input.txt /test
# 使用“hadoop jar jar包名主函数”命令，在hadoop运行程序
hadoop jar MyHBase.jar org.namenumber.hbase.inputSource.Main

# 查看输出
hadoop fs -cat /tmp/member_user/part-m-00000 #查看内容

```

将下载下来的文件传输到本地电脑上

记得关闭服务器

### 实验结果与分析

提交压缩包包括：
1）实验报告（命名方式：学号-姓名-实验四报告）需要至少包含标红的三张截图。每张图 3 分，相关文字描述 3 分。
2）项目src文件夹（文件夹重命名方式：学号-姓名-实验四代码）

## 概念解释及参考
