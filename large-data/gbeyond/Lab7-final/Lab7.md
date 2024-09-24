# Lab7-final 基于华为云安装Hive、华为DVL，大数据分析与可视化实战

## 文件预操作及目录

cloudshell: 所有 vim 文本操作可以在 CloudShell 中进行，也可以在类似 WinSCP 等的远程工具中修改，方便也不容易出错
名称根据实际情况替换
noden = 节点n名称; nodenip = 节点n IP 地址; nodeniip = 节点n内网IP;
node1 = 节点1名称; node1ip = 节点1 IP 地址; node1iip = 节点1内网IP;
node2 = 节点2名称; node2ip = 节点2 IP 地址; node2iip = 节点2内网IP;
node3 = 节点3名称; node3ip = 节点3 IP 地址; node3iip = 节点3内网IP;
node4 = 节点4名称; node4ip = 节点4 IP 地址; node4iip = 节点4内网IP;

* [Lab7-final 基于华为云安装Hive、华为DVL，大数据分析与可视化实战](#lab7-final-基于华为云安装hive华为dvl大数据分析与可视化实战)
    * [文件预操作及目录](#文件预操作及目录)
    * [TODO](#todo)
    * [实验描述](#实验描述)
    * [实验目的](#实验目的)
    * [实验步骤](#实验步骤)
        * [元数据库Mysql 安装](#元数据库mysql-安装)
        * [Hive 安装部署](#hive-安装部署)
            * [方式一](#方式一)
            * [方式二：使用华为云MRS服务直接购买HIVE集群](#方式二使用华为云mrs服务直接购买hive集群)
        * [Hive SQL 数据分析](#hive-sql-数据分析)
        * [数据可视化](#数据可视化)
            * [4.4.1 基于 Python 的数据可视化](#441-基于-python-的数据可视化)
            * [4.4.2 基于华为云 DLV 的数据可视化](#442-基于华为云-dlv-的数据可视化)
    * [实验考核方法](#实验考核方法)
    * [概念解释及参考](#概念解释及参考)
        * [grep](#grep)
        * [head](#head)
        * [wc](#wc)

## TODO

* [ ] 安装jdk8

## 实验描述

利用 hive 命令行完成搜狗日志各项数据分析，使用 Python 进行数据可视化。主要步骤包括：安装部署 Hive、启动 Hadoop 集群、进入 Hive 命令行、创建数据库和数据表、加载或导入数据、用 Hive SQL 完成需求、使用 Python 实现数据可视化。

## 实验目的

1. 掌握安装 Hive 的方法
2. 掌握 Hive 创建数据库、导入数据的方法
3. 学会使用 Hive SQL 分析数据
4. 学会数据可视化的方法

## 实验步骤

实验开始前，请确保 Hadoop 集群已经安装成功(可参考前面的实验)。接下来的步骤主要是：元数据库 Mysql 安装、Hive 安装部署、Hive SQL 数据分析、数据可视化。

文件上传

```shell
# 使用 WinScp 上传或使用 wget 下载
# mysql:
wget https://obs-mirror-ftp4.obs.cn-north-4.myhuaweicloud.com/database/mysql-5.7.30.tar.gz
# apache-hive-2.1.1-bin.tar.gz:
wget http://archive.apache.org/dist/hive/hive-2.1.1/apache-hive-2.1.1-bin.tar.gz
#上传 sougo.100w.utf8 文件和 extract.py 文件。
```

### 元数据库Mysql 安装

本实验安装 MySQL 是为了给 Hive 提供元数据存储库，主要包括：yum 安装 MySQL、修改 MySQL root密码、添加 zkpk 用户并赋予远程访问权限、修改数据库默认编码。

```shell
# 查看并卸载系统自带的 mariadb-lib 数据库
# [root@master ~]#
rpm -qa|grep mariadb
# 结果
# mariadb-5.5.68-1.el7.aarch64
# mariadb-libs-5.5.68-1.el7.aarch64
yum -y remove mariadb-*

# 添加 MySQL yum 源(首先确保能访问网络)；
# 安装 mysql 所需依赖：
yum install -y perl openssl openssl-devel libaio perl-JSON autoconf
# 解压 mysql 安装包：
tar -xvf mysql-5.7.30.tar.gz
# 进入 aarch64 目录，对 rpm 包进行安装:
cd aarch64
yum install *.rpm

# 启动 MySQL 服务：
systemctl start mysqld
# 查看启动状态
systemctl status mysqld

# 修改 root 默认密码：
# [root@master ~]#
# 查看 mysql 安装生成的随机默认密码(/var/log/mysqld.log 文件中)
grep 'temporary password' /var/log/mysqld.log
# 登录 mysql(密码为上图红框标注部分)
mysql -uroot -p
# 修改 mysql 密码为:MyNewPass4!
ALTER USER 'root'@'localhost' IDENTIFIED BY 'MyNewPass4!';
# 注意：mysql5.7 默认安装了密码安全检查插件(validate_password)，默认密码检查策略要求密码必须包含：大小写字母、数字和特殊符号，并且长度不能少于 8 位。否则会提示 ERROR

# 修改 mysql 密码策略
# 查看 msyql 密码策略的相关信息:
show variables like '%password%';
# : 向 my.cnf 文件中[mysqld]下添加如下配置(/etc/my.cnf)：
exit
vim /etc/my.cnf
```

my.cnf 配置内容:

```shell
[mysqld]
# 禁用密码策略
validate_password = off
init_connect='SET NAMES utf8'
#
# Remove leading # and set to the amount of RAM for the most important data
# cache in MySQL. Start at 70% of total RAM for dedicated server, else 10%.
# innodb_buffer_pool_size = 128M
#
# Remove leading # to turn on a very important data integrity option: logging
# changes to the binary log between backups.
# log_bin
#
# Remove leading # to set options mainly useful for reporting servers.
# The server defaults are faster for transactions and fast SELECTs.
# Adjust sizes as needed, experiment to find the optimal values.
# join_buffer_size = 128M
# sort_buffer_size = 2M
# read_rnd_buffer_size = 2M
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
# Disabling symbolic-links is recommended to prevent assorted security risks
symbolic-links=0

log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid
# 添加 client 模块，输入 client 模块的相关编码格式;
[client]
# 配置默认编码为 utf8
default-character-set=utf8
```

```shell
# 重新启动 mysql 服务使配置生效
systemctl restart mysqld
# 登录 mysql:
mysql -uroot -p
# 查看编码
show variables like '%character%';
exit
ifconfig
```

<!--TODO: 截图1-->

### Hive 安装部署

本节内容是 Hive 安装部署，主要内容包括：启动 hadoop 集群、解压并安装 Hive、创建 Hive 的元数据库、修改配置文件、添加并生效环境变量、初始化元数据

#### 方式一

基于前面实验搭建的环境搭建HIVE 集群(后面的1.3.1 到 1.3.2 的步骤，依据此方式进行讲解)
(后面附了：方式二：使用华为云MRS服务直接购买HIVE集群，也可以选择用方式二来完成Hive 集群环境的搭建工作)

```shell
# [root@master ~]#
# 在 master 启动 Hadoop 集群:
start-all.sh
# 在 master、slave01、slave02 运行 JPS 指令，查看 Hadoop 是否启动成功；
# 解压并安装 Hive
tar -zxvf /root/apache-hive-2.1.1-bin.tar.gz

# 向 MySQL 中添加 hadoop 用户和创建名为(hive)的数据库；
# 登录 mysql
mysql –uroot -p
# 创建 hadoop 用户(密码:hadoop):
grant all on *.* to hadoop@'%' identified by 'hadoop';
grant all on *.* to hadoop@'localhost' identified by 'hadoop';
grant all on *.* to hadoop@'master' identified by 'hadoop';
flush privileges;
# 创建数据库连接
create database hive;
exit

# 配置 Hive ([root@master ~]#)
# 进入 hive 安装目录下的配置目录:
cd /root/apache-hive-2.1.1-bin/conf/
# 创建 hive 配置文件并添加如下内容:
vim hive-site.xml
```

hive-site.xml内容

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>hive.metastore.local</name>
        <value>true</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://master:3306/hive?characterEncoding=UTF-
            8</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>hadoop</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>hadoop</value>
    </property>
    <property>
        <name>hive.server2.authentication</name>
        <value>NOSASL</value>
    </property>
</configuration>
```

```shell
# 复制 MySQL 连接驱动到 hive 根目录下的 lib 目录中：
cp /root/mysql-connector-java-5.1.28.jar /root/apache-hive-2.1.1-bin/lib/
cd apache-hive-2.1.1-bin/lib/
ll | grep mysql-connector-java-5.1.28.jar
# 配置系统 zkpk 用户环境变量
# 打开配置文件：
cd ~
vim /root/.bash_profile

## 将下面两行配置添加到环境变量中:

#HIVE
export HIVE_HOME=/root/apache-hive-2.1.1-bin
export PATH=$PATH:$HIVE_HOME/bin

## end

# 使环境变量生效
source /root/.bash_profile
# 初始化 Hive 元数据库。说明：该命令是把 hive 的元数据都同步到 mysql 中
schematool -dbType mysql -initSchema
# 启动 hive 客户端
hive
# 退出:exit、quit、ctrl+c
exit
ifconfig
```

<!--TODO: 截图2-->

#### 方式二：使用华为云MRS服务直接购买HIVE集群

* 购买HIVE集群
  1. 最新活动
  2. 免费使用专区
  3. AI与大数据
  4. MapReduce服务
  5. 集群名改为姓名-学号，如：张三-2019211000
  6. 分析组件：Hive
  7. 输入密码
  8. 确认付款
* 绑定动态IP:
  1. 弹性云服务器
  2. 更多
  3. 网络设置
  4. 绑定弹性公网IP
  5. 购买弹性公网IP
  6. 计费模式：按需计费
  7. 弹性公网IP名称
  8. 购买完后返回页面绑定动态IP。
* 修改安全组
  1. 点开任一服务器
  2. 安全组
  3. 配置规则
  4. 优先级:1;协议端口:全部放通;源地址:0.0.0.0/0;
购买完成后即可通过ssh进入服务器
进入后执行下面两条命令

```shell
cd /opt/client
source bigdata_env
```

执行完成后即可正常启动HIVE

### Hive SQL 数据分析

利用 hive 命令行完成搜狗日志各项数据分析，本节内容包括：进入 Hive 命令行、创建数据库和数据表、加载或导入数据、用 Hive SQL 完成需求。

```shell
# 数据预处理 ([root@master ~]#)
# 对 sougo.100w.utf8 文件，用学号做种子，随机抽取 50 万条，构成新的数据集。执行以下指令(BUPT-ID 需替换为你的学号):
python extract.py BUPT-ID
# 查看数据内容
head -1 sogou.100w.utf8
# 查看总行数
wc -l sogou.100w.utf8
# 将时间字段拆分，添加年、月、日、小时字段
awk -F '\t' '{print $0"\t"substr($1,1,4)"\t"substr($1,5,2)"\t"substr($1,7,2)"\t"substr($1,9,2)}' sogou.100w.utf8 > sogou.100w.utf8.1
# 查看拓展后的字段
head -3 sogou.100w.utf8.1
# 在数据扩展的结果上，过滤第 2 个字段(UID)或者第 3 个字段(搜索关键词)为空的行
awk -F"\t" '{if($2 != "" && $3 != "" && $2 != " " && $3 != " ") print $0}' sogou.100w.utf8.1 > sogou.100w.utf8.2
# 重命名数据文件
cp sogou.100w.utf8.2 sogou.100w.utf8

# 加载数据到 HDFS 上:
# 在 hdfs 上创建目录/sogou_ext/20111230
hadoop fs -mkdir -p /sogou_ext/20111230
# 上传数据
hadoop fs -put sogou.100w.utf8 /sogou_ext/20111230
```

```shell
# 基于 Hive 构建日志数据的数据仓库
# 进入 hive 客户端命令行:
hive
# 查看数据库
show databases;
# 创建数据库表
create database sogou_100w;
# 使用数据库：
use sogou_100w;
# 创建扩展 4 个字段(年、月、日、小时)数据的外部表 sogou_ext_20111230
CREATE EXTERNAL TABLE sogou_ext_20111230(
ts STRING,
uid STRING,
keyword STRING,
rank INT,
`order` INT,
url STRING,
year INT,
month INT,
day INT,
hour INT
)
COMMENT 'This is the sogou search data of extend data'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/sogou_ext/20111230';
# 创建分区表 sogou_partition
CREATE EXTERNAL TABLE sogou_partition(
ts STRING,
uid STRING,
keyword STRING,
rank INT,
`order` INT,
url STRING
)
COMMENT 'This is the sogou search data by partition'
partitioned by (
year INT,
month INT,
day INT,
hour INT
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;
# 开启动态分区
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;
# 往分区表中灌入表 sogou_ext_20111230 的数据
INSERT OVERWRITE TABLE sogou_partition PARTITION(year,month,day,hour) select * from sogou_ext_20111230;
# (如果此处报错 Number of reduce tasks is set to 0 since there's no reduce operator 需要启动 yarn)
# 查询分区表的结果：
select * from sogou_partition limit 3;

# 数据分析需求
# 计总条数
select count(*) from sogou_ext_20111230;
# 统计非空查询条数，即关键词不为空或者 null,用 where 语句排除关键词为 null 和空的字段，在此基础上用 count()函数统计.
select count(*) from sogou_ext_20111230 where keyword is not null and keyword !='';
# 无重复总条数(根据 ts、uid、keyword、url)先按照以上四个字段分组，再用 having 语句选出 count()数为 1 的记录为表 a，再用 count()函数统计表 a 中的条数。
select count(*) from (select ts,uid,keyword,url from sogou_ext_20111230 group by ts,uid,keyword,url having count(*) =1) a;
exit
ifconfig
# <!--TODO: 截图3-->
# 独立 UID 条数使用distinct()函数对 uid 字段去重，再用 count()函数统计出条数:
# 重新进入 hive 并切换数据库：
hive
use sogou_100w
# 自行设计查询语句
exit
ifconfig
# <!--TODO: 截图4-->

# 实际需求分析：
# 查询关键词平均长度统计。提示:先使用 split 函数对关键词进行切分，然后用 size()函数统计关键词的大小，然后再用 avg 函数获取长度的平均值：
# 重新进入 hive 并切换数据库：
hive
use sogou_100w
# 自行设计查询语句
exit
ifconfig
# <!--TODO: 截图5-->
# 查询频度排名(频度最高的前 50 词)对关键词做 groupby，然后对每组进行 count()，再按照count()的结果进行倒序排序。
select keyword,count(*) as cnt from sogou_ext_20111230 group by keyword order by cnt desc limit 50;

# UID 分析
# UID 的查询次数分布(查询 1 次的 UID 个数，2 次的，3 次的，大于 3 次的 UID 个数)先按照 uid 分组，并用 count()函数对每组进行统计，然后再用 sum、if 函数对查询次数为 1，查询次数为 2，查询次数为3，查询次数大于 3 的 uid 进行统计
select SUM(IF(uids.cnt=1,1,0)),SUM(IF(uids.cnt=2,1,0)),SUM(IF(uids.cnt=3,1,0)),SUM(IF(uids.cnt>3,1,0)) from (select uid,count(*) as cnt from sogou_ext_20111230 group by uid) uids;
# 查询次数大于 2 次的用户总数。提示：先对 uid 进行 groupby，并用 having 函数过滤出查询次数大于2 的用户，然后再用 count 函数统计用户的总数。 
# 自行设计查询语句(结果中需有 ip)
exit
ifconfig
# <!--TODO: 截图6-->

# 用户行为分析 
# 点击次数与 Rank 之间的关系分析: Rank 在 10 以内的点击次数
select count(*) from sogou_ext_20111230 where rank < 11;
# 直接输入 url 查询的点击次数。通过 where 条件选出直接通过 url 查询的记录再用 count 函数进行统计
select count(*) from sogou_ext_20111230 where keyword like '%www%';
```

### 数据可视化

#### 4.4.1 基于 Python 的数据可视化

* 安装 Anaconda
  + 登陆 Anaconda 官网下载安装包 <https://www.anaconda.com/download/>，选择 Python3.6 version，点击 64-Bit 下载(32 位电脑请下载 32-Bit)。
  + 双击下载好的  Anaconda3-x.x.x-Windows-x86_64.exe 文件，出现如下界面，点击 Next;
  + Install for: Just me  还是  All Users,这里直接  Just Me,继续点击 Next 。
  + 选择软件安装地址，继续 Next;
  + 两个都不勾选，第一个是加入环境变量，第二个是默认使用 Python 3.6，我们使用Anaconda 自带的环境，点击 Install 开始安装;
  + 等待完成安装，点击 finish 。
* 开放服务器的 10000 端口
  + 登录华为云
  + 控制台，点击安装 Hive 的服务器；
  + 安全组
  + 配置规则
  + 添加入方向规则
  + 添加规则
  + 优先级:2;协议端口:TCP/10000;源地址:0.0.0.0/0;
* 修改 Hadoop 集群配置
  + 在`/root/hadoop-2.7.3/etc/hadoop`路径下，修改文件 `core-site.xml`，添加如下内容：

```xml
<property>
    <name>hadoop.proxyuser.root.hosts</name>
    <value>*</value>
</property>
<property>
    <name>hadoop.proxyuser.root.groups</name>
    <value>*</value>
</property>
```

```shell
# 开启 Hive 远程模式：
hive --service metastore &
hive --service hiveserver2 &
```

* Python 可视化程序编写
  1. 在安装好的 Anaconda 文件夹中打开 Anaconda Prompt:
  2. 使用 pip 安装 matplotlib 包
     - `pip install matplotlib`

  3. 使用 pip 安装 pyhive(python 远程连接 hive)
     - `pip install pyhive`

  4. 使用 pip 安装 thrift(python 远程连接 hive)
     - `pip install thrift`

  5. 使用 pip 安装 sasl(python 远程连接 hive)(若 pip 安装失败，可使用 `conda install sasl` 安装)
     - `pip install sasl`

  6. 输入：`jupyter notebook`，回车，浏览器会启动 Jupyter notebook,此对话框不要关闭。
  7. 拷贝 url 到浏览器
  8. 新建 python 文件
  9. 编写如下程序，完成关键词搜索前 10 的可视化

```python
from pyhive import hive
import matplotlib.pyplot as plt
plt.rcParams['font.sans-serif'] = ['SimHei'] #  步骤一(替换 sans-serif 字体)
plt.rcParams['axes.unicode_minus'] = False   #  步骤二(解决坐标轴负数的负号显示问题)
conn = hive.Connection(host='121.36.94.37',port=10000,auth='NOSASL',username='root')
cursor = conn.cursor()
cursor.execute('select keyword,count(*) as cnt from sogou_100w.sogou_ext_20111230 group by keyword order by cnt desc limit 10')
keywords = []
frequency = []
for result in cursor.fetchall():
    keywords.append(result[0])
    frequency.append(result[1])
cursor.close()
conn.close()
plt.barh(keywords, frequency)
plt.title('频度排名-学号') #学号替换为你的学号
plt.xlabel('频度')
plt.ylabel('关键词')
plt.show()
```

#### 4.4.2 基于华为云 DLV 的数据可视化

* 开启 DLV 数据可视化平台
  1. 打开 <https://www.huaweicloud.com/product/dlv.html>
  2. 点击进入控制台
  3. 新建大屏
  4. 空白模板,使用模板:数据可视化;创建大屏
  5. 插入适合分析数据的图组件，这里使用柱状图组件
  6. 点击统计图
  7. 设置 y 轴坐标名称: 点击量
  8. 将前 6 个热度排名的词填入左侧的静态数据
  注意：这里每个 x、y 的取值，使用  在"4.3 Hive SQL 数据分析"中"步骤5"的中完成的结果，来进行可视化 "2)查询频度排名(频度最高的前 50 词)对关键词做 groupby，然后对每组进行 count()，再按照 count()的结果进行倒序排序。" 因此这里面的 x、y、s 的取值，同学们之前应有所不同。

```json
[
    { 
        "x": "百度",
        "y": 7564, 
        "s": "替换为你的学号" 
    }, 
    { 
        "x": "baidu", 
        "y": 3652, 
        "s": "替换为你的学号" 
    }, 
    { 
        "x": "人体艺术", 
        "y": 2786, 
        "s": "替换为你的学号" 
    }, 
    { 
        "x": "4399 小游戏", 
        "y": 2119, 
        "s": "替换为你的学号" 
    }, 
    { 
        "x": "优酷", 
        "y": 1948, 
        "s": "替换为你的学号" 
    }, 
    { 
        "x": "新亮剑", 
        "y": 1946, 
        "s": "替换为你的学号" 
    } 
]
```

9. 点击预览

## 实验考核方法

注意：sougo.100w.utf8 文件原始是 100 万条数据，要求按照学号作为种子，随机抽取 50 万条，所以不同同学的统计结果应有所区别。提交的实验结构，截图必须在指定位置包含学号，不包含学号不能得分。
* Part 1：共 14 分
  + 实验截图 1:方式一及方式二，任选一个完成即可，对应截图 2 分
    - 方式一：元数据库 Mysql 安装的步骤7 中查看 mysql 编码的截图和 Hive 安装部署中的步骤7 中 hive 启动成功的截图；(2 分，不与方式二的 2 分累加)
    - 方式二：HIVE 启动成功的截图(2 分，不与方式二的 2 分累加)
  + 实验截图 3-6：Hive SQL 查询的截图；(8 分，每图 2 分)
  + 实验截图 7：python 可视化截图；(2 分)
  + 实验截图 8：华为 DLV 可视化截图；(2 分)
* Part2：分析 rank 与用户点击次数之间的关系(2 分，结合数据集的内容、查询统计值，给出细节分析说明和论述);
* Part3：还能从数据中挖掘哪些有价值的信息？定义 1 种不同挖掘目标，完成自定义实验，每一种完成以下要求的四个步骤给 4 分。
  1. 设计挖掘目标；
  2. 完成 HiveSQL 代码并运行 HiveSQL 代码，形成执行结果；
  3. 完成基于结果的 Python 可视化代码的编写；
  4. 对结果进行分析。(每个步骤1 分);

除上述采分点外，报告中还需出现部分关键步骤截图以证明独立完成(例如需要使用学号的步骤)

## 概念解释及参考

### grep

[参考](https://www.runoob.com/linux/linux-comm-grep.html)
grep 命令用于查找文件里符合条件的字符串。

grep 指令用于查找内容包含指定的范本样式的文件，如果发现某文件的内容符合所指定的范本样式，预设 grep 指令会把含有范本样式的那一列显示出来。若不指定任何文件名称，或是所给予的文件名为 -，则 grep 指令会从标准输入设备读取数据。

### head

[参考](https://www.runoob.com/linux/linux-comm-head.html)

head 命令可用于查看文件的开头部分的内容，有一个常用的参数 -n 用于显示行数，默认为 10，即显示 10 行的内容。

### wc

Linux wc命令用于计算字数。

利用wc指令我们可以计算文件的Byte数、字数、或是列数，若不指定文件名称、或是所给予的文件名为"-"，则wc指令会从标准输入设备读取数据。
