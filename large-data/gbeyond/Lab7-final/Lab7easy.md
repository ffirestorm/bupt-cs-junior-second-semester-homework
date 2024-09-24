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
    * [本机python准备及可视化程序编写](#本机python准备及可视化程序编写)
    * [文件上传](#文件上传)
    * [元数据库Mysql 安装](#元数据库mysql-安装)
    * [Hive 安装部署](#hive-安装部署)
      * [方式一](#方式一)
      * [方式二：使用华为云MRS服务直接购买HIVE集群](#方式二使用华为云mrs服务直接购买hive集群)
    * [Hive SQL 数据分析](#hive-sql-数据分析)
    * [数据可视化](#数据可视化)
      * [基于 Python 的数据可视化](#基于-python-的数据可视化)
      * [基于华为云 DLV 的数据可视化](#基于华为云-dlv-的数据可视化)
  * [实验考核方法](#实验考核方法)
  * [概念解释及参考](#概念解释及参考)
    * [MySQL安装及启动失败解决](#mysql安装及启动失败解决)
    * [grep](#grep)
    * [head](#head)
    * [wc](#wc)
    * [mysql语句](#mysql语句)
    * [sasl 安装失败](#sasl-安装失败)
    * [python代码运行失败](#python代码运行失败)

## TODO

* [ ] 安装jdk8
* [ ] 开放端口10000
* [ ] Part2自行设计
* [ ] Part3自行设计

## 实验描述

利用 hive 命令行完成搜狗日志各项数据分析，使用 Python 进行数据可视化。主要步骤包括：安装部署 Hive、启动 Hadoop 集群、进入 Hive 命令行、创建数据库和数据表、加载或导入数据、用 Hive SQL 完成需求、使用 Python 实现数据可视化。

## 实验目的

01. 掌握安装 Hive 的方法
02. 掌握 Hive 创建数据库、导入数据的方法
03. 学会使用 Hive SQL 分析数据
04. 学会数据可视化的方法

## 实验步骤

实验开始前，请确保 Hadoop 集群已经安装成功(可参考前面的实验)。接下来的步骤主要是：元数据库 Mysql 安装、Hive 安装部署、Hive SQL 数据分析、数据可视化。

### 本机python准备及可视化程序编写

<!--
* 安装 Anaconda(没必要)
  + 登陆 Anaconda 官网下载安装包 <https://www.anaconda.com/download/>，选择 Python3.6 version，点击 64-Bit 下载(32 位电脑请下载 32-Bit)。
  + 双击下载好的  Anaconda3-x.x.x-Windows-x86_64.exe 文件，出现如下界面，点击 Next;
  + Install for: Just me  还是  All Users,这里直接  Just Me,继续点击 Next 。
  + 选择软件安装地址，继续 Next;
  + 两个都不勾选，第一个是加入环境变量，第二个是默认使用 Python 3.6，我们使用Anaconda 自带的环境，点击 Install 开始安装;
  + 等待完成安装，点击 finish
* Anaconda下环境准备(尽量用以下顺序安装)
  + pip install matplotlib
  + pip install pyhive
  + pip install thrift
  + pip install thrift_sasl
  + conda install sasl
  + 输入：`jupyter notebook`，回车，浏览器会启动 Jupyter notebook,此对话框不要关闭。
  + 拷贝 url 到浏览器
  + 新建 python 文件并运行
-->

在自己的电脑上安装Python。安装python 可参考这个视频:<https://www.bilibili.com/video/BV17A411T73u/>(我后面会重置这个视频，修正一些错误)

```shell
在本机上以管理员身份打开命令窗口
# 用以下顺序安装不会出问题
# 安装whl包的工具
pip install wheel
# pip 安装 matplotlib 包
pip install matplotlib
# pip 安装 pyhive(python 远程连接 hive)
pip install pyhive
# pip 安装 thrift(python 远程连接 hive)
pip install thrift
# pip 安装 thrift-sasl(必要的包)
pip install thrift-sasl
# pip 安装 sasl(python 远程连接 hive)
# pip install sasl

去官网下载支持的版本: https://www.lfd.uci.edu/~gohlke/pythonlibs/#sasl
# 我的是Python3.9.6,所以下载的是sasl-0.3.1-cp39-cp39-win_amd64.whl

# 查看pip包所在
pip show pip
# 找到这一行(后面的路径是我的安装路径，具体路径根据你自己的情况来)：Location: c:\program files\ide_env\python39\lib\site-packages
将下载好的sasl包移动到以上目录
# 进入pip包所在文件(上面找到的路径)
cd c:\Program Files\IDE_Env\Python39\Lib\site-packages # 该路径为我的路径，具体路径根据实际情况来
pip install sasl.whl # <!--TODO: 将 sasl.whl 替换为sasl包名完整名称-->
# 我的是Python3.9.6,下载的是sasl-0.3.1-cp39-cp39-win_amd64.whl
# 所以我的指令是 pip install sasl-0.3.1-cp39-cp39-win_amd64.whl
```

[官网安装包](https://www.lfd.uci.edu/~gohlke/pythonlibs/#sasl)对应关系
| 包名                                      | 对应版本           |
| ----------------------------------------- | ------------------ |
| sasl‑0.3.1‑pp38‑pypy38_pp73‑win_amd64.whl | python未知 win64位 |
| sasl‑0.3.1‑cp310‑cp310‑win_amd64.whl      | python3.10 win64位 |
| sasl‑0.3.1‑cp310‑cp310‑win32.whl          | python3.10 win32位 |
| sasl‑0.3.1‑cp39‑cp39‑win_amd64.whl        | python3.9 win64位  |
| sasl‑0.3.1‑cp39‑cp39‑win32.whl            | python3.9 win32位  |
| sasl‑0.3.1‑cp38‑cp38‑win_amd64.whl        | python3.8 win64位  |
| sasl‑0.3.1‑cp38‑cp38‑win32.whl            | python3.8 win32位  |
| sasl‑0.3.1‑cp37‑cp37m‑win_amd64.whl       | python3.7 win64位  |
| sasl‑0.3.1‑cp37‑cp37m‑win32.whl           | python3.7 win32位  |
| sasl‑0.2.1‑cp36‑cp36m‑win_amd64.whl       | python3.6 win64位  |
| sasl‑0.2.1‑cp36‑cp36m‑win32.whl           | python3.6 win32位  |
| sasl‑0.2.1‑cp35‑cp35m‑win_amd64.whl       | python3.5 win64位  |
| sasl‑0.2.1‑cp35‑cp35m‑win32.whl           | python3.5 win32位  |
| sasl‑0.2.1‑cp34‑cp34m‑win_amd64.whl       | python3.4 win64位  |
| sasl‑0.2.1‑cp34‑cp34m‑win32.whl           | python3.4 win32位  |
| sasl‑0.2.1‑cp27‑cp27m‑win_amd64.whl       | python2.7 win64位  |
| sasl‑0.2.1‑cp27‑cp27m‑win32.whl           | python2.7 win32位  |
| sasl‑0.1.3‑cp27‑none‑win_amd64.whl        | python2.7 win64位  |
| sasl‑0.1.3‑cp27‑none‑win32.whl            | python2.7 win32位  |

创建KeywordTop10.py文件:
KeywordTop10.py:使用以下内容，完成关键词搜索前 10 的可视化

```python
from pyhive import hive
import matplotlib.pyplot as plt

plt.rcParams['font.sans-serif'] = ['SimHei']  # 步骤一(替换 sans-serif 字体)
plt.rcParams['axes.unicode_minus'] = False  # 步骤二(解决坐标轴负数的负号显示问题)
conn = hive.Connection(host='node1ip',
                       port=10000,
                       auth='NOSASL',
                       username='root')  # TODO: host值改为node1公网ip
cursor = conn.cursor()
cursor.execute(
    'SELECT keyword,count(*) as cnt FROM sogou_100w.sogou_ext_20111230 GROUP BY keyword order by cnt desc limit 10'
)
keywords = []
frequency = []
for result in cursor.fetchall():
    keywords.append(result[0])
    frequency.append(result[1])
cursor.close()
conn.close()
plt.barh(keywords, frequency)
plt.title('频度排名-学号')  # TODO: 学号替换为你的学号
plt.xlabel('频度')
plt.ylabel('关键词')
plt.show()

```

### 文件上传

在华为云安全组里开放10000端口
启动服务器，注释掉127开头的行

```shell
# 使用 WinScp 上传或使用 wget 下载
# mysql-5.7.30.tar.gz
# apache-hive-2.1.1-bin.tar.gz
# mysql-connector-java-5.1.28.jar
# sogou.100w.utf8
# extract.py
# 上传文件

# sftp 版
sftp root@node1ip
lcd Filepath
lpwd # 检查本机路径是否正确
put mysql-5.7.30.tar.gz
put apache-hive-2.1.1-bin.tar.gz
put mysql-connector-java-5.1.28.jar
put sogou.100w.utf8
put extract.py
exit

# scp 版
scp mysql-5.7.30.tar.gz root@node1ip:~/
scp apache-hive-2.1.1-bin.tar.gz root@node1ip:~/
scp mysql-connector-java-5.1.28.jar root@node1ip:~/
scp sogou.100w.utf8 root@node1ip:~/
scp extract.py root@node1ip:~/

# wget下载版
# mysql:
wget https://obs-mirror-ftp4.obs.cn-north-4.myhuaweicloud.com/database/mysql-5.7.30.tar.gz
# apache-hive-2.1.1-bin.tar.gz:
wget http://archive.apache.org/dist/hive/hive-2.1.1/apache-hive-2.1.1-bin.tar.gz
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
# end
yum -y remove mariadb-*

# 添加 MySQL yum 源(首先确保能访问网络)；
# 安装 mysql 所需依赖：
yum install -y perl openssl openssl-devel libaio perl-JSON autoconf
# 解压 mysql 安装包：
tar -zxvf mysql-5.7.30.tar.gz
# 进入 aarch64 目录，对 rpm 包进行安装:
cd aarch64
yum install *.rpm

# 启动 MySQL
systemctl start mysqld
# 查看 MySQL 状态
systemctl status mysqld
# 如果安装失败，用 `yum remove mysql*` 指令卸载，然后删除aarch64文件夹和`/var/lib/mysql`文件夹，重启服务器，改hosts，从解压安装包那步重新运行再不行可以尝试 `chown -R mysql /var/lib/mysql` 和 `chown -R mysql /var/lib/mysql` 指令，然后再次重新安装，详情看笔记末尾概念解释与参考

# 修改 root 默认密码：
# 查看 mysql 安装生成的随机默认密码(/var/log/mysqld.log 文件中)
grep 'temporary password' /var/log/mysqld.log
# 登录 mysql(密码为上图红框标注部分)
mysql -uroot -p
# 修改 mysql 密码为:MyNewPass4!
ALTER USER 'root'@'localhost' IDENTIFIED BY 'MyNewPass4!';
# 注意：mysql5.7 默认安装了密码安全检查插件(validate_password)，默认密码检查策略要求密码必须包含：大小写字母、数字和特殊符号，并且长度不能少于 8 位。否则会提示 ERROR

# 修改 mysql 密码策略
# 查看 msyql 密码策略的相关信息:
SHOW variables like '%password%';
exit;

## {输出结果注意部分

| validate_password_dictionary_file      |                 |
| validate_password_length               | 8               |
| validate_password_mixed_case_count     | 1               |
| validate_password_number_count         | 1               |
| validate_password_policy               | MEDIUM          |
| validate_password_special_char_count   | 1               |
+----------------------------------------+-----------------+

## end}

# 向 my.cnf 文件中[mysqld]下添加如下配置(/etc/my.cnf)：
vim /etc/my.cnf
```

my.cnf 配置内容:对比修改以下内容

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
MyNewPass4!
# 查看编码
SHOW variables like '%character%';
exit;
ifconfig
```

<!--TODO: 截图1.1, MySQL表格及ip-->

### Hive 安装部署

本节内容是 Hive 安装部署，主要内容包括：启动 hadoop 集群、解压并安装 Hive、创建 Hive 的元数据库、修改配置文件、添加并生效环境变量、初始化元数据

#### 方式一

基于前面实验搭建的环境搭建HIVE 集群(后面的1.3.1 到 1.3.2 的步骤，依据此方式进行讲解)
(后面附了：方式二：使用华为云MRS服务直接购买HIVE集群，也可以选择用方式二来完成Hive 集群环境的搭建工作)

```shell
# [root@master ~]#
# 启动 Hadoop
start-all.sh
# hadoop dfsadmin -report
# 解压并安装 Hive
cd ~
tar -zxvf /root/apache-hive-2.1.1-bin.tar.gz

# 向 MySQL 中添加 hadoop 用户和创建名为(hive)的数据库；
# 登录 mysql
mysql -uroot -p
MyNewPass4!
# 创建 hadoop 用户(密码:hadoop):
grant all on *.* to hadoop@'%' identified by 'hadoop';
grant all on *.* to hadoop@'localhost' identified by 'hadoop';
grant all on *.* to hadoop@'master' identified by 'hadoop';
flush privileges;
# 创建数据库连接
CREATE database hive;
exit;

# 配置 Hive ([root@master ~]#)
# 进入 hive 安装目录下的配置目录:
cd /root/apache-hive-2.1.1-bin/conf/
# 创建 hive 配置文件并添加如下内容:
touch hive-site.xml
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
        <value>jdbc:mysql://master:3306/hive?characterEncoding=UTF-8</value><!--TODO: master换成主节点名称,然后把注释删了-->
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
cd /root/apache-hive-2.1.1-bin/lib/
ll | grep mysql-connector-java-5.1.28.jar
# 配置系统用户环境变量
# 打开配置文件：
cd ~
vim /root/.bash_profile
```

.bash_profile:添加以下内容

```shell
#HIVE
export HIVE_HOME=/root/apache-hive-2.1.1-bin
export PATH=$PATH:$HIVE_HOME/bin
```

```shell

# 使环境变量生效
source /root/.bash_profile
# 初始化 Hive 元数据库。把 hive 的元数据都同步到 mysql 中
schematool -dbType mysql -initSchema
# 启动 hive 客户端
hive
# 退出:exit、quit、ctrl+c
exit;
ifconfig
```

<!--TODO: 截图1.2, hive启动及ip-->

#### 方式二：使用华为云MRS服务直接购买HIVE集群

* 购买HIVE集群
  01. 最新活动
  02. 免费使用专区
  03. AI与大数据
  04. MapReduce服务
  05. 集群名改为姓名-学号，如：张三-2019211000
  06. 分析组件：Hive
  07. 输入密码
  08. 确认付款
* 绑定动态IP:
  01. 弹性云服务器
  02. 更多
  03. 网络设置
  04. 绑定弹性公网IP
  05. 购买弹性公网IP
  06. 计费模式：按需计费
  07. 弹性公网IP名称
  08. 购买完后返回页面绑定动态IP。
* 修改安全组
  01. 点开任一服务器
  02. 安全组
  03. 配置规则
  04. 优先级:1;协议端口:全部放通;源地址:0.0.0.0/0;
购买完成后即可通过ssh进入服务器
进入后执行下面两条命令

```shell
cd /opt/client
source bigdata_env
```

执行完成后即可正常启动HIVE

### Hive SQL 数据分析

利用 hive 命令行完成搜狗日志各项数据分析，本节内容包括：进入 Hive 命令行、创建数据库和数据表、加载或导入数据、用 Hive SQL 完成需求。

> hive使用前提要MySQL和Hadoop都启动
> 启动
> systemctl start mysqld
> start-all.sh
> 查看状态
> systemctl status mysqld
> hadoop dfsadmin -report

```shell
# 数据预处理 ([root@master ~]#)
cd ~
# 对 sogou.100w.utf8 文件，用学号做种子，随机抽取 50 万条，构成新的数据集。执行以下指令(BUPT-ID 需替换为学号):
python extract.py BUPT-ID
# 查看数据内容
head -1 sogou.100w.utf8
# 查看总行数
wc -l sogou.100w.utf8
# 500000
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
SHOW databases;
# 创建数据库表
CREATE database sogou_100w;
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
INSERT OVERWRITE TABLE sogou_partition PARTITION(year,month,day,hour) SELECT * FROM sogou_ext_20111230;
# (如果此处报错 Number of reduce tasks is set to 0 since there's no reduce operator 需要启动 yarn)
# 查询分区表的结果：
SELECT * FROM sogou_partition limit 3;

# 数据分析需求
# 计总条数
SELECT count(*) FROM sogou_ext_20111230;
# 统计非空查询条数，即关键词不为空或者 null,用 WHERE 语句排除关键词为 null 和空的字段，在此基础上用 count()函数统计.
SELECT count(*) FROM sogou_ext_20111230 WHERE keyword is not null and keyword !='';
# 无重复总条数(根据 ts、uid、keyword、url)先按照以上四个字段分组，再用 having 语句选出 count()数为 1 的记录为表 a，再用 count()函数统计表 a 中的条数。
SELECT count(*) FROM (SELECT ts,uid,keyword,url FROM sogou_ext_20111230 GROUP BY ts,uid,keyword,url having count(*) =1) a;
exit;
ifconfig
# <!--TODO: 截图1.3, 该指令结果及ip-->
# 独立 UID 条数使用distinct()函数对 uid 字段去重，再用 count()函数统计出条数:
# 重新进入 hive 并切换数据库：
hive
use sogou_100w;
# <!--TODO: 自行设计查询语句-->
SELECT count(DISTINCT uid) FROM sogou_ext_20111230;
exit;
ifconfig
# <!--TODO: 截图1.4, 该指令结果及ip-->

# 实际需求分析：
# 查询关键词平均长度统计。提示:先使用 split 函数对关键词进行切分，然后用 size()函数统计关键词的大小，然后再用 avg 函数获取长度的平均值：
# 重新进入 hive 并切换数据库：
hive
use sogou_100w;
# <!--TODO: 自行设计查询语句-->
# 方式一: 结果由点问题，用方式二吧
SELECT AVG(LENGTH(keyword)) FROM sogou_ext_20111230;
# 方式二
SELECT avg(a.cnt) FROM (SELECT size(split(keyword,'s+')) AS cnt FROM sogou_ext_20111230) a;
exit;
ifconfig
# <!--TODO: 截图1.5, 该指令结果及ip-->

# 重新进入 hive 并切换数据库：
hive
use sogou_100w;
# 查询频度排名(频度最高的前 50 词)对关键词做 groupby，然后对每组进行 count()，再按照count()的结果进行倒序排序。
SELECT keyword,count(*) AS cnt FROM sogou_ext_20111230 GROUP BY keyword order by cnt desc limit 50;
记住，后面要用

# UID 分析
# UID 的查询次数分布(查询 1 次的 UID 个数，2 次的，3 次的，大于 3 次的 UID 个数)先按照 uid 分组，并用 count()函数对每组进行统计，然后再用 sum、if 函数对查询次数为 1，查询次数为 2，查询次数为3，查询次数大于 3 的 uid 进行统计
SELECT SUM(IF(uids.cnt=1,1,0)),SUM(IF(uids.cnt=2,1,0)),SUM(IF(uids.cnt=3,1,0)),SUM(IF(uids.cnt>3,1,0)) FROM (SELECT uid,count(*) as cnt FROM sogou_ext_20111230 GROUP BY uid) uids;
记住，后面可能要用
# 查询次数大于 2 次的用户总数。提示：先对 uid 进行 groupby，并用 having 函数过滤出查询次数大于2 的用户，然后再用 count 函数统计用户的总数。
# <!--TODO: 自行设计查询语句(结果中需有 ip)-->
# 方式一
SELECT count(userNum1.uid) FROM ( SELECT uid,count(*) AS count FROM sogou_ext_20111230 GROUP BY uid having count > 2) userNum1;
# 方式二
SELECT SUM(IF(userNum2.cnt > 2, 1, 0)) FROM ( SELECT uid,count(*) AS cnt FROM sogou_ext_20111230 GROUP BY uid) userNum2;
exit;
ifconfig
# <!--TODO: 截图1.6, 该指令结果及ip-->

# 用户行为分析
# 重新进入 hive 并切换数据库：
hive
use sogou_100w;
# 点击次数与 Rank 之间的关系分析: Rank 在 10 以内的点击次数
SELECT count(*) FROM sogou_ext_20111230 WHERE rank < 11;
# 直接输入 url 查询的点击次数。通过 WHERE 条件选出直接通过 url 查询的记录再用 count 函数进行统计
SELECT count(*) FROM sogou_ext_20111230 WHERE keyword like '%www%';
# 截图备用，part2分析要用
exit;
```

### 数据可视化

#### 基于 Python 的数据可视化

```shell
# 修改 Hadoop 集群配置
cd /home/modules/hadoop-2.7.7/etc/hadoop/
vim core-site.xml
```

core-site.xml:添加以下内容

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
cd /home/modules/hadoop-2.7.7/etc/hadoop/
# 分发配置好的core-site.xml
scp core-site.xml root@node2:$PWD
scp core-site.xml root@node3:$PWD
scp core-site.xml root@node4:$PWD

# 重启hadoop
stop-all.sh
start-all.sh
hadoop dfsadmin -report

# 开启 Hive 远程模式
hive --service metastore &
hive --service hiveserver2 &
```

```shell
# 在本机上
# 进入KeywordTop10.py所在目录
cd KeywordTop10.py所在目录
python KeywordTop10.py
# 如果安装了Anacoda打开之前jupyter网页，运行代码
```

<!--TODO: 截图1.7, 输出的图-->

#### 基于华为云 DLV 的数据可视化

* 开启 DLV 数据可视化平台
  01. 打开 <https://www.huaweicloud.com/product/dlv.html>
  02. 点击进入控制台
  03. 新建大屏
  04. 空白模板,使用模板:数据可视化;创建大屏
  05. 插入适合分析数据的图组件，这里使用柱状图组件
  06. 点击统计图
  07. 设置 y 轴坐标名称
      - y轴->轴单位->单位->输入:点击量
  08. 将前 6 个热度排名的词填入静态数据
      - 数据->将数据填入数据源类型下方的数据框内
      注意：这里每个 x、y 的取值，使用  在"4.3 Hive SQL 数据分析"中"步骤5"的中完成的结果，来进行可视化 "2.查询频度排名(频度最高的前 50 词)对关键词做 groupby，然后对每组进行 count()，再按照 count()的结果进行倒序排序。" 因此这里面的 x、y、s 的取值，同学们之前应有所不同。见588行左右

  09. 点击预览
<!--TODO: 截图1.8-->

热度排名数据:根据实际情况更改

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

## 实验考核方法

注意：sogou.100w.utf8 文件原始是 100 万条数据，要求按照学号作为种子，随机抽取 50 万条，所以不同同学的统计结果应有所区别。提交的实验结构，截图必须在指定位置包含学号，不包含学号不能得分。
* Part 1：共 14 分
  + 实验截图 1:方式一及方式二，任选一个完成即可，对应截图 2 分
    - 方式一：元数据库 Mysql 安装的步骤7 中查看 mysql 编码的截图和 Hive 安装部署中的步骤7 中 hive 启动成功的截图；(2 分，不与方式二的 2 分累加)
    - 方式二：HIVE 启动成功的截图(2 分，不与方式二的 2 分累加)
  + 实验截图 3-6：Hive SQL 查询的截图；(8 分，每图 2 分)
  + 实验截图 7：python 可视化截图；(2 分)
  + 实验截图 8：华为 DLV 可视化截图；(2 分)
* Part2：分析 rank 与用户点击次数之间的关系(2 分，结合数据集的内容、查询统计值，给出细节分析说明和论述);
* Part3：还能从数据中挖掘哪些有价值的信息？定义 1 种不同挖掘目标，完成自定义实验，每一种完成以下要求的四个步骤给 4 分。
  01. 设计挖掘目标；
  02. 完成 HiveSQL 代码并运行 HiveSQL 代码，形成执行结果；
  03. 完成基于结果的 Python 可视化代码的编写；
  04. 对结果进行分析。(每个步骤1 分);

除上述采分点外，报告中还需出现部分关键步骤截图以证明独立完成(例如需要使用学号的步骤)

## 概念解释及参考

### MySQL安装及启动失败解决

[参考方案](https://stackoverflow.com/questions/9083408/fatal-error-cant-open-and-lock-privilege-tables-table-mysql-host-doesnt-ex)

01. Uninstall mysql using `yum remove mysql*`
02. Recursively delete `/usr/bin/mysql` and `/var/lib/mysql`
03. Delete the file `/etc/my.cnf.rmp`
04. Use `ps -e` to check the processes to make sure mysql isn't still running.
05. Reboot server with `reboot`
06. install MySQL
07. Give mysql ownership and group priveleges with:
    - `chown -R mysql /var/lib/mysql`
    - `chgrp -R mysql /var/lib/mysql`

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

### mysql语句

[参考一](https://blog.csdn.net/zhu_9527/article/details/18233053)

### sasl 安装失败

[sasl 包官网](https://www.lfd.uci.edu/~gohlke/pythonlibs/#sasl)
[参考一](https://www.jianshu.com/p/c67657db5a93)
[参考一辅助参考](https://blog.csdn.net/weixin_44420419/article/details/106845166)

### python代码运行失败

[参考一](https://blog.csdn.net/weixin_43142260/article/details/115198097)
[参考二](https://www.cnblogs.com/free-easy0000/p/9638982.html)
