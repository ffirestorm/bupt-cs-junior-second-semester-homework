# Lab04

安装 HBase、Zookeeper 及 HBase 应用实践

## 前提

cloudshell: 所有 vim 文本操作可以在 CloudShell 中进行，也可以在类似 WinSCP 等的远程工具中修改，方便也不容易出错
namenumber 改成对应的姓名缩写+学号
名称根据实际情况替换
noden = 节点n名称; nodenip = 节点n 公网IP 地址; nodeniip = 节点n内网IP;
node1 = 节点1名称; node1ip = 节点1 公网IP 地址; node1iip = 节点1内网IP;
node2 = 节点2名称; node2ip = 节点2 公网IP 地址; node2iip = 节点2内网IP;
node3 = 节点3名称; node3ip = 节点3 公网IP 地址; node3iip = 节点3内网IP;
node4 = 节点4名称; node4ip = 节点4 公网IP 地址; node4iip = 节点4内网IP;

机器名称 | 服务名称
---|---
node1 | QuorumPeerMain、NameNode、ResourceManager、Hmaster
node2 | QuorumPeerMain、DataNode、NodeManager、JournalNode、HRegionServer
node3 | QuorumPeerMain、DataNode、NodeManager、JournalNode、HRegionServer
node4 | QuorumPeerMain、DataNode、NodeManager、JournalNode、HRegionServer

开始本次实验前请确保已安装好 Hadoop 并配置好环境变量

## TODO

* [ ] log4j
* [ ] 固定监控ip
* [x] wegt apt 等下载方式
* [x] hbase shell 运行`list`卡住
* [x] jps 的 HMaster 会自动挂掉
* [x] hbase shell 运行`create`出现上面问题
* [ ] web页面
* [ ] export JAVA_LIBRAY_PATH=/home/modules/hadoop-2.7.7/lib/native

## 实验步骤

启动服务器，修改hosts，保证4台服务器 `/etc/hosts` 文件内 `127.0.0` 开头的行注释掉或删除
华为云开放2181端口,需要再开，一般不用开

### 准备 ZooKeeper 与 HBase

#### 下载与安装

在用户目录下(root或~)下载 zookeeper 压缩包并解压

```shell
# On node1 ~
# 下载安装包
wget https://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz #下载 zookeeper 安装包
wget https://archive.apache.org/dist/hbase/2.0.2/hbase-2.0.2-bin.tar.gz #下载 hbase 安装包
# 将安装包移动到 "/usr/local" 目录下
mv zookeeper-3.4.6.tar.gz /usr/local #移动 zookeeper
mv hbase-2.0.2-bin.tar.gz /usr/local #移动 hbase
# 解压安装
cd /usr/local
tar -zxvf zookeeper-3.4.6.tar.gz #解压安装 zookeeper
tar -zxvf hbase-2.0.2-bin.tar.gz #解压安装 hbase
# 建立软链接(快捷方式)，便于后期版本更换
ln -s zookeeper-3.4.6 zookeeper #建立 zookeeper 软链接
ln -s hbase-2.0.2 hbase #建立 hbase 软链接

# On node1~node4 respectively
# 配置系统环境变量
vim /etc/profile #改了就行，不一定用 vim 编辑器，比如 WinSCP 也行
# 使环境变量生效
source /etc/profile #改完再运行
```

`/etc/profile` 内容

```txt
# for zookeeper
export ZOOKEEPER_HOME=/usr/local/zookeeper
export PATH=$ZOOKEEPER_HOME/bin:$PATH
# for hbase
export HBASE_HOME=/usr/local/hbase
export PATH=$HBASE_HOME/bin:$HBASE_HOME/sbin:$PATH
```

#### 配置 ZooKeeper

```shell
# On node1
# 配置 ZooKeeper
cd /usr/local/zookeeper/conf #进入 ZooKeeper 配置文件所在目录
cp zoo_sample.cfg zoo.cfg #拷贝配置文件
# 修改配置文件，修改数据目录。
vim zoo.cfg
# 修改内容
dataDir=/usr/local/zookeeper/tmp
# 在最后添加如下代码，server.1-4 是部署 ZooKeeper 的节点，1，2，3，4 分别是各服务器 /usr/local/zookeeper/tmp/myid 文件的内容。这里 192.168.0.xxx 对应的是运行 QuorumPeerMain 的服务器的内网 IP，需要改成自己集群的。
server.1=node1iip:2888:3888
server.2=node2iip:2888:3888
server.3=node3iip:2888:3888
server.4=node4iip:2888:3888
# 创建 tmp 目录作数据目录。
mkdir /usr/local/zookeeper/tmp
# 在 tmp 目录中创建一个空文件 myid，并向该文件写入 ID。
touch /usr/local/zookeeper/tmp/myid
echo 1 > /usr/local/zookeeper/tmp/myid
```

#### 配置 HBase

```shell
# On node1
# 配置 HBase
cd $HBASE_HOME/conf #进入 HBase 配置文件所在目录
# 修改配置文件
vim hbase-env.sh
#修改环境变量 JAVA_HOME 为绝对路径,注意 JAVA_HOME 和 HBASE_LIBRARY_PATH 要与自己实际安装配置的一致，HBASE_MANAGES_ZK 设为 false。
```

```txt
export JAVA_HOME=/usr/lib/jvm/jdk8u292-b10
export HBASE_MANAGES_ZK=false
export HBASE_LIBRARY_PATH=/home/modules/hadoop-2.7.7/lib/native
```

```shell
#修改 hbase-site.xml 文件。
vim hbase-site.xml
#添加或修改 configuration 标签范围内的部分参数。
<configuration>
  <property>
    <name>hbase.rootdir</name>
    <value>hdfs://node1:8020/HBase</value>
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
    <value>node2:2181,node3:2181,node4:2181</value>
  </property>
  <property>
    <name>hbase.unsafe.stream.capability.enforce</name>
    <value>false</value>
  </property>
</configuration>

#编辑 regionservers 文件。
vim regionservers
#将 regionservers 文件内容替换为 agent 节点 IP（可用主机名代替,记得改名）。
node2
node3
node4

#拷贝 hadoop 目录下的的的 hdfs-site.xml 文件到“hbase/conf/”目录，可选择软链接或拷贝。
cp /home/modules/hadoop-2.7.7/etc/hadoop/hdfs-site.xml /usr/local/hbase/conf/hdfs-site.xml
```

#### 分发 ZooKeeper 与 HBase

```shell
#将配置好的 ZooKeeper 拷贝到其它节点。（也可以将 zookeeper 压缩包拷贝到其他节点，在进行相同的配置，这样等待时间较短）
scp -r /usr/local/zookeeper-3.9.2 root@node2:/usr/local
scp -r /usr/local/zookeeper-3.9.2 root@node3:/usr/local
scp -r /usr/local/zookeeper-3.9.2 root@node4:/usr/local

#拷贝 hbase-2.0.2 到 node2、node3、node4 节点的“/usr/local”目录（也可以将 zookeeper 压缩包拷贝到其他节点，在进行相同的配置，这样等待时间较短）。
for i in {2..4};
do
    scp -r /usr/local/hbase-2.5.8 root@node${i}:/usr/local/ ;
done
# 或
scp -r /usr/local/hbase-2.5.8 root@node2:/usr/local/
scp -r /usr/local/hbase-2.5.8 root@node3:/usr/local/
scp -r /usr/local/hbase-2.5.8 root@node4:/usr/local/
```

配置子节点 ZooKeeper 和 hbase

```shell
#登录 node2、node3、node4，创建软链接并修改 myid 内容。
# On node2
cd /usr/local
ln -s hbase-2.0.2 hbase
ln -s zookeeper-3.4.6 zookeeper
echo 2 > /usr/local/zookeeper/tmp/myid
# On node3
cd /usr/local
ln -s hbase-2.0.2 hbase
ln -s zookeeper-3.4.6 zookeeper
echo 3 > /usr/local/zookeeper/tmp/myid
# On node4
cd /usr/local
ln -s hbase-2.0.2 hbase
ln -s zookeeper-3.4.6 zookeeper
echo 4 > /usr/local/zookeeper/tmp/myid
```

#### 启动

```shell
start-all.sh
#依次启动 ZooKeeper 和 Hadoop。

#分别在 node2，node3，node4 上启动 ZooKeeper。
cd /usr/local/zookeeper/bin
./zkServer.sh start
#####输出
JMX enabled by default
Using config: /usr/local/zookeeper/bin/../conf/zoo/cfg
Starting zookeeper ... STARTED
#####

#查看 ZooKeeper 状态，注意，Mode 应为 leader 或 follower。
./zkServer.sh status
#####输出
JMX enabled by default
Using config: /usr/local/zookeeper/bin/../conf/zoo/cfg
Mode: follower
#####

#在 node1 节点上启动 HBase 集群。
/usr/local/hbase/bin/start-hbase.sh
#或
start-hbase.sh
jps #观察进程是否都正常启动
```

```shell
# On node1
SecondaryNameNode
HMaster
Jps
QuorumPeerMain
ResourceManager
NameNode
WrapperSimpleApp #不出现也能正常运行

# On node2
QuorumPeerMain
DataNode
HRegionServer
Jps
NodeManager
WrapperSimpleApp #不出现也能正常运行
```

### HBase 实践

#### 启动服务器与相关软件

启动服务器
修改hosts，保证4台服务器 `/etc/hosts` 文件内 `127.0.0` 开头的行注释掉或删除

```shell
# 启动 Hadoop 集群并验证运行情况
# On node1
start-all.sh
#start-dfs.sh
#start-yarn.sh
hadoop dfsadmin -report #验证运行情况

# 启动 Zookeeper 集群
# 需要在 node{1..4}分别运行
cd /
./usr/local/zookeeper/bin/zkServer.sh stop #关闭zookeeper,首次不用运行
./usr/local/zookeeper/bin/zkServer.sh start #启动
./usr/local/zookeeper/bin/zkServer.sh status #状态查看

# 启动 HBase 集群
# On node1
start-hbase.sh #启动
```

补充指令

```shell
stop-all.sh
#stop-dfs.sh
#stop-yarn.sh
./usr/local/zookeeper/bin/zkServer.sh stop #关闭zookeeper
stop-hbase.sh #关闭hbase
```

#### 创建表格

数据库表格设计要求：(未按要求设计扣分)
a. 表格命名：**学号+姓名**
b. 行数不限定，字段名不限定
c. ROW 命名：**学号+姓名+编号**
**【截图 1：数据库表格】(截图需要包含标记信息，未按要求扣分)**

```shell {.line-numbers}
# 进入 HBase Shell 创建实验用表
# 输入 hbase shell 进入 hbase 交互式环境
hbase shell

# 创建表格
create '2021211138cpy','cf1'

# 向表“学号+姓名”中插入数据
put '2021211138cpy','2021211138cpy001','cf1:keyword','Honor 20'
put '2021211138cpy','2021211138cpy002','cf1:keyword','Apple 18'
put '2021211138cpy','2021211138cpy003','cf1:keyword','Huawei 70'

# 扫描整个表
scan '2021211138cpy'
```

| member_user                      |||
| ----------- | --------- | -------- |
| 行键        | cf1(列族)            ||
| ^           | keyword   | keyword2 |
| rk001       | applicate | word 1   |
| rk002       | OnePlus 5 | word 2   |
| rk003       | iphone 6s | word 3   |

<table>
  <caption>member_user</caption>
  <tr><td rowspan="2">行键</td><td colspan="2">cf1(列族)</td></tr>
  <tr><td>keyword</td><td>keyword</td></tr>
  <tr><td>rk001</td><td>applicate</td><td>word 1</td></tr>
  <tr><td>rk002</td><td>OnePlus 5</td><td>word 2</td></tr>
  <tr><td>rk003</td><td>iphone 6s</td><td>word 3</td></tr>
</table>

#### 编写代码，将 Hbase 中的数据导出到 hdfs 指定目录

##### 创建 Maven 项目

工程名 MyHBase
**namenumber 改成对应的姓名缩写+学号**

在 src/java 目录下新建 package，名称 org/namenumber/hbase/inputSource
org.namenumber.hbase.inputsource

pom.xml
[检测依赖是否正确](https://mvnrepository.com/)

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
      <type>pom</type>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-yarn</artifactId>
      <version>${hadoop.version}</version>
      <type>pom</type>
    </dependency>
    <dependency>
      <groupId>org.apache.hbase</groupId>
      <artifactId>hbase</artifactId>
      <version>2.0.2</version>
      <type>pom</type>
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
                <mainClass>org.namenumber.hbase.inputSource.Main</mainClass>
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
                <mainClass>org.namenumber.hbase.inputSource.Main</mainClass> <!-- 此处为主入口-->
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
**(截图需要包含标记信息，未按要求扣分，代码不完整扣分)**
新建类 Main，完整代码如下

##### 程序打包

带依赖打包MyWordCount.jar

> mvn clean
> mvn package assembly:assembly

填写主类名称,选择主类

#### 运行

上传jar包,到node1的root目录下

```shell
hadoop fs -ls / #查看目录
hadoop fs -ls -R / #查看目录及子目录下所文件

# 在~目录下
# 使用“hadoop jar jar包名主函数”命令，在hadoop运行程序
hadoop jar MyHBase.jar org.cpy2021211138.hbase.inputSource.Main
# 查看输出
hadoop fs -cat /tmp/2021211138cpy/part-m-00000 #查看内容
```

<!--TODO:运行指令里的namenumber及项目里的路径和表名称 -->

**【截图 3：结果截图】(截图需要包含标记信息，未按要求扣分)**

记得关闭服务器

### 实验结果与分析

提交压缩包包括：
1)实验报告(命名方式：学号-姓名-实验四报告)需要至少包含标红的三张截图。每张图3分，相关文字描述3分。
2)项目src文件夹(文件夹重命名方式：学号-姓名-实验四代码)

## 概念解释及参考

`echo >` 和 `echo >>` 的区别: `>` 输出重定向; `>>` 输出追加重定向

Linux touch命令用于修改文件或者目录的时间属性，包括存取时间和更改时间。若文件不存在，系统会建立一个新的文件。
使用指令 `touch` 时，如果指定的文件不存在，则将创建一个新的空白文件。例如，在当前目录下，使用该指令创建一个空白文件"file"，输入如下命令： `touch file`

## hbase shell 常用指令

```shell
list  #列出Hbase中存在的所有表
describe #显示表相关的详细信息
exists #测试表是否存在
exit #退出Hbaseshell

# 创建表student和stu

create 'student','cf1'
create 'stu','cf1','cf2','cf3'

# put插入值:

put 表 行健 列定义 值
put 'stu2','rk01','cf1:age',22
get  表  行健
get  表  行健  列簇 
get  表  行健  列簇:列 
get  表  行健  列簇:列  时间戳
scan  表
scan  表  起始行健  结束行健
scan  表  列簇
scan  表  列簇:列

# 删除

enable #启用表，使表有效
truncate 'User' #删除表中所有数据
delete #删除指定对象的值(可以为表，行、列对应的值，外也可以指定时间戳的值)
delete 'User', 'row1', 'info:age' #删除列
deleteall #删除指定行的所有元素值
deleteall 'User', 'row2' #删除所有行

# 删除表，删除前，必须先disable

disable 'student' #使表无效
drop 'student' #删除表

[linux中wegt、apt-get、pip三种安装命令的区别](https://blog.csdn.net/weixin_43286092/article/details/102095209)
