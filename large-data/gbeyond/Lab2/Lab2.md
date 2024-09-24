# 文件预操作及目录

cloudshell: 所有 vim 文本操作可以在 CloudShell 中进行，也可以在类似 WinSCP 等的远程工具中修改，方便也不容易出错
名称根据实际情况替换
noden = 节点n名称; nodenip = 节点n IP 地址; nodeniip = 节点n内网IP;
node1 = 节点1名称; node1ip = 节点1 IP 地址; node1iip = 节点1内网IP;
node2 = 节点2名称; node2ip = 节点2 IP 地址; node2iip = 节点2内网IP;
node3 = 节点3名称; node3ip = 节点3 IP 地址; node3iip = 节点3内网IP;
node4 = 节点4名称; node4ip = 节点4 IP 地址; node4iip = 节点4内网IP;

目录
注：***建议直接看 Lab02 easy 版***

- [目录及文件预操作](#目录及文件预操作)
- [Lab02原版](#lab02原版)
  - [实验操作](#实验操作)
    - [文件上传](#文件上传)
    - [配置节点互信](#配置节点互信)
      - [配置防火墙](#配置防火墙)
      - [配置服务器间免密访问](#配置服务器间免密访问)
      - [设置 IP 映射](#设置-ip-映射)
      - [验证与首次连接](#验证与首次连接)
    - [安装 OpenJDK](#安装-openjdk)
      - [预处理](#预处理)
      - [分发安装包](#分发安装包)
      - [安装 jdk (解压 tar 包)](#安装-jdk-解压-tar-包)
      - [配置环境变量](#配置环境变量)
    - [安装 Hadoop](#安装-hadoop)
      - [预处理](#预处理-1)
      - [配置 hadoop 设置](#配置-hadoop-设置)
      - [分发 Hadoop 到其他节点](#分发-hadoop-到其他节点)
      - [配置环境变量](#配置环境变量-1)
      - [格式化 namenode](#格式化-namenode)
      - [启动与验证](#启动与验证)
      - [检查 hdfs 运行情况](#检查-hdfs-运行情况)
    - [创建 maven 工程](#创建-maven-工程)
    - [java 实现 HDFS 文件读写](#java-实现-hdfs-文件读写)
      - [华为云开放端口](#华为云开放端口)
      - [检查端口](#检查端口)
      - [设置本机电脑hosts](#设置本机电脑hosts)
    - [代码](#代码)
  - [其他操作](#其他操作)
- [Lab02 easy 版](#lab02-easy-版)
  - [实验操作](#实验操作-1)
    - [修改文件](#修改文件)
      - [配置 hadoop 设置](#配置-hadoop-设置-1)
      - [修改本地 hosts 文件](#修改本地-hosts-文件)
    - [华为云开放端口](#华为云开放端口-1)
    - [修改 hosts](#修改-hosts)
      - [设置 IP 映射](#设置-ip-映射-1)
    - [配置节点互信](#配置节点互信-1)
      - [配置防火墙](#配置防火墙-1)
      - [配置服务器间免密访问](#配置服务器间免密访问-1)
      - [验证与首次连接](#验证与首次连接-1)
    - [上传，分发](#上传分发)
      - [上传](#上传)
      - [分发与安装](#分发与安装)
        - [Java](#java)
        - [Hadoop](#hadoop)
    - [修改 profile](#修改-profile)
      - [使配置生效](#使配置生效)
      - [启动与验证](#启动与验证-1)
      - [检查 hdfs 运行情况](#检查-hdfs-运行情况-1)
    - [创建 maven 工程](#创建-maven-工程-1)
    - [java 实现 HDFS 文件读写](#java-实现-hdfs-文件读写-1)
      - [检查端口](#检查端口-1)
    - [代码](#代码-1)
  - [其他操作](#其他操作-1)
- [补充内容](#补充内容)
  - [实验结果](#实验结果)
  - [概念解释](#概念解释)
    - [关闭防火墙](#关闭防火墙)
    - [格式化namenode](#格式化namenode)
  - [参考](#参考)

# Lab02原版

## 实验操作

### 文件上传

```shell
ssh root@node1ip #登录指令
# On my computer
# sftp 版
sftp root@node1ip
lcd Filepath
lpwd # 检查本机路径是否正确
put -r hadoop-2.7.7 hadoop-2.7.7 #不加后面会把文件内容散开传到root，第二个参数用/hadoop-2.7.7会传到根目录，用~/hadoop-2.7.7会不识别，如果目录下没有这个文件夹，会自动创建
put OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar
exit
# scp 版
scp -r Filepath/hadoop-2.7.7 root@node1ip:~/ #上传Hadoop安装包(此指令已失效)
scp -r Filepath/OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar root@node1ip:~/ #上传 OpenJDK
```

TODO: scp指令会少上传share文件夹: hadoop-2.7.7/share/hadoop

```shell
# 传错了，删除
# On node1
rm -rf hadoop-2.7.7 #注意路径是否正确
rm OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar
```

解决方案：

* [x] 方案一：用sftp
* [x] 方案二：使用 WinSCP, xftp, FinalShell, Termius, MobaXterm 等软件

### 配置节点互信

#### 配置防火墙

```shell
# Only on node1
systemctl status firewalld.service #查看防火墙状态
systemctl stop firewalld.service #关闭防火墙 .service
systemctl disable firewalld.service #禁止firewall开机启动 .service
```

#### 配置服务器间免密访问

id_rsa.pub 与 authorized_keys[^rsa]

```shell
# On node1~node4 respectively
ssh-keygen -t rsa #生成rsa类型的ssh密钥,用于远程免密登录，结果：生成/root/.ssh/id_rsa.pub
cat /root/.ssh/id_rsa.pub #输出文件内容，便于汇总
# 将4个节点的 id_rsa.pub 文件内容复制汇总到一个文件里，再分别拷到4个节点的 authorized_keys 中
vim /root/.ssh/authorized_keys #编辑 authorized_keys 文件，拷贝汇总的rsa密钥
```

#### 设置 IP 映射

```shell
# On node1~node4 respectively
vim /etc/hosts #编辑hosts文件，加入 node1~node4 对应 IP 及 node 节点名
```

hosts[^host]添加的内容

```text
node1iip  node1
node2iip  node2
node3iip  node3
node4iip  node4
```

#### 验证与首次连接

完成以上操作后，在4个节点分别执行 `ssh node1~node4` 并选择 yes 后，确保可以无密码跳转到目标节点。若出现连接被拒绝的情况，检查目标终端的 `/etc/hosts.deny` 文件，可能包含了被拒绝的终端IP

```shell
# On node1~node4 respectively, 共16遍
ssh node1
ssh node2
ssh node3
ssh node4
# 参考顺序
# 参考顺序，从 node1 开始
ssh node1 #1->1
ssh node2 #1->2
ssh node2 #2->2
ssh node3 #2->3
ssh node3 #3->3
ssh node4 #3->4
ssh node4 #4->4
ssh node3 #4->3
ssh node2 #3->2
ssh node1 #2->1
ssh node3 #1->3
ssh node1 #3->1
ssh node4 #1->4
ssh node2 #4->2
ssh node4 #2->4
ssh node1 #4->1
```

### 安装 OpenJDK

可以自行安装jdk，结果未知

#### 预处理

```shell
# On node1
cp OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar /usr/lib/jvm/ #将 jdk 安装包拷贝到/usr/lib/jvm 目录下
```

#### 分发安装包

```shell
# On node1
scp /usr/lib/jvm/OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar root@node2:/usr/lib/jvm/ #分发安装包到节点2
scp /usr/lib/jvm/OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar root@node3:/usr/lib/jvm/ #分发安装包到节点3
scp /usr/lib/jvm/OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar root@node4:/usr/lib/jvm/ #分发安装包到节点4
```

#### 安装 jdk (解压 tar 包)

```shell
# On node1~node4 respectively
cd /usr/lib/jvm/
tar -vxf OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar # 安装tar包
```

#### 配置环境变量

```shell
# On node1~node4 respectively
vim /etc/profile #添加配置文本：export JAVA_HOME=/usr/lib/jvm/jdk8u292-b10
source /etc/profile #读取和执行 /etc/profile 文件。使配置生效
java -version #查看java版本
```

如果自行安装的java, 添加配置文本前先运行以下指令查看路径

```shell
# On node1
which java #查看java位置，记录 /bin/java 之前的路径，为JAVA_PATH指示的路径
```

### 安装 Hadoop

#### 预处理

```shell
# On node1
mkdir /home/modules/ # TODO
cp -r hadoop-2.7.7 /home/modules/ #复制 hadoop 安装包，一定要确认复制完成后有没有hadoop-2.7.7文件夹
# 华为CloudShell使用cp -r hadoop-2.7.7 /home/modules/hadoop-2.7.7
cd /home/modules/
```

#### 配置 hadoop 设置

```shell
# 配置环境变量(所有vim文本操作可以在CloudShell中进行，方便)
vim /home/modules/hadoop-2.7.7/etc/hadoop/hadoop-env.sh #添加配置文本：export JAVA_HOME=/usr/lib/jvm/jdk8u292-b10

# 配置 hadoop core-site.xml 配置文件
vim /home/modules/hadoop-2.7.7/etc/hadoop/core-site.xml #配置内容见 ./HDFSfils/core-site.xml 
# 注：fs.defaultFS、fs.obs.access.key、fs.obs.secret.key、fs.obs.endpoint 需根据实际情况修改。

# 配置 hdfs-site.xml; 注：node 名称使用自己实际的 node 名
vim /home/modules/hadoop-2.7.7/etc/hadoop/hdfs-site.xml #配置内容见 ./HDFSfils/hdfs-site.xml 

# 配置 yarn-site.xml
vim /home/modules/hadoop-2.7.7/etc/hadoop/yarn-site.xml #配置内容见 ./HDFSfils/yarn-site.xml 

# 配置 mapred-sit.xml; 注：node 名称使用自己实际的 node 名
cd /home/modules/hadoop-2.7.7/etc/hadoop/
mv mapred-site.xml.template mapred-site.xml
vim /home/modules/hadoop-2.7.7/etc/hadoop/mapred-site.xml #配置内容见 ./HDFSfils/mapred-site.xml 

# 配置 slaves
vim /home/modules/hadoop-2.7.7/etc/hadoop/slaves #配置内容为其余三个节点的名称(不是IP)，每行一个，共3行(删掉原有的部分)
```

slaves 内容[^slaves]

```text
node2
node3
node4
```

#### 分发 Hadoop 到其他节点

```shell
mkdir /home/modules/
# 确保他节点的/home/modules下没有hadoop-2.7.7文件夹，如有，rm -rf /home/modules/hadoop-2.7.7, 注意传过去别散开
# scp 不行了用 sftp
scp -r /home/modules/hadoop-2.7.7 root@node2:/home/modules/ #分发Hadoop到节点2
scp -r /home/modules/hadoop-2.7.7 root@node3:/home/modules/ #分发Hadoop到节点3
scp -r /home/modules/hadoop-2.7.7 root@node4:/home/modules/ #分发Hadoop到节点4
```

#### 配置环境变量

```shell
# On node1~node4 respectively
vim /etc/profile #内容如下
```

```text
export HADOOP_HOME=/home/modules/hadoop-2.7.7
export PATH=$JAVA_HOME/bin:$PATH
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH
export HADOOP_CLASSPATH=/home/modules/hadoop-2.7.7/share/hadoop/tools/lib/*:$HADOOP_CLASSPATH
```

```shell
# On node1~node4 respectively
source /etc/profile #读取和执行 /etc/profile 文件。使配置生效
chmod -R 777 /home/modules/hadoop-2.7.7
```

#### 格式化 namenode

```shell
# On node1
hadoop namenode -format #格式化指令
```

如果已经运行过 `start-all.sh` 指令，要先进行以下操作，再运行上面的指令：

1. 再node1上，运行 `stop-all.sh` 指令
2. 清空各个节点配置的hadoop tmp目录、name目录、data目录、以及hadoop logs目录，tmp目录即core-site.xml下配置的<name>hadoop.tmp.dir</name>所指向的目录(hadoop-2.7.7/tmp)的所有子目录，其余同理

#### 启动与验证

```shell
# On node1
start-all.sh #启动 Hadoop
jps #验证,结果如下则正确
# On node2~node4 respectively
jps
```

```text
主节点运行结果：
Jps
ResourceManager
SecondaryNameNode
NameNode

子节点运行结果：
NodeManager
Jps
DataNode
```

#### 检查 hdfs 运行情况

```shell
hadoop fs -ls / #浏览hadoop的文件系统，若目录下无文件则只会有一个 WARN 的提示后显示结束
hadoop dfsadmin -report #快速定位出哪些节点 down 掉了，HDFS 的容量以及使用了多少，以及每个节点的硬盘使用情况。
```

指令基本与 Linux 一致

### 创建 maven 工程

没有安装 maven 先安装 maven

1. 新建工程
2. 修改pom.xml
3. 重载项目或更新项目

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
      <!--需要在java/main/resources路径下配置log4jproperties文件-->
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
      <version>1.2.17</version>
    </dependency>
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
```

如使用 IDEA 则按实验报告要求做

### java 实现 HDFS 文件读写

#### 华为云开放端口

弹性云服务器 -> 安全组 -> 选择default -> 选择入向方向规则 -> 添加规则(共两个端口，8020和50010，优先级为1)

开8020端口是为了让hadoop可以被访问，50010端口是为了可以写数据

#### 检查端口

```shell
# On node1
netstat -ltpn #查看端口
```

确保 Hadoop 集群(4个节点) 8020 端口监听的不是本地 IP(不是本地 IP可跳过 hosts 文件修改步骤， 若为 127.0.0.1:8020 则需要修改 hosts 文件)

修改 hosts 文件：

```shell
# On node1~node4 respectively
vim /etc/hosts #修改 hosts 文件
```

将 127.0.0.1 的部分注释掉(加#)，然后将四台服务器 ip 修改为局域网 ip (可在华为云上查看)

```text
node1iip    node1
node2iip    node2
node3iip    node3
node4iip    node4
```

#### 设置本机电脑hosts

Windows hosts文件位置: `C:\Windows\System32\drivers\etc` 。
修改方法: 将hosts文件复制到桌面上，修改桌面上的hosts文件，之后复制回原目录，此时提示需要管理员权限，继续并赋予权限即可(也可用vscode打开修改并保存，会提示你保存失败，以管理员模式重试，之后点以管理员身份，然后赋予权限即可)
生效方法：dos命令行下执行 `ipconfig /flushdns` 刷新dns
补充：可以先运行 `ipconfig /displaydns` 查看dns

mac电脑修改本地 hosts 文件，在本地终端输入：
vim /etc/hosts

文件末尾添加四台服务器局域网 ip 以及服务器名称：

```text
node1ip  node1
node2ip  node2
node3ip  node3
node4ip  node4
```

```shell
# On node1
# 启动 hadoop
start-dfs.sh
start-yarn.sh
# 或用
start-all.sh
```

### 代码

hdfsPath = "/"
log4j打印日志
正常运行不会等待很长时间。反之说明出现了问题，可以耐心等待报出异常，或登录到服务器，查看/home/modules/hadoop-2.7.7/logs/hadoop-root-namenode-主节点名.log以及/home/modules/hadoop-2.7.7/logs/hadoop-root-datanode-从节点名.log这些日志文件，同样请关注java异常栈 (日志文件内容越靠后就越新) 。
执行java代码时有红字log4j或类似日志记录一样的东西不用管，只有java异常栈才说明问题，其余部分跟原报告执行效果一样即说明实验成功。

## 其他操作

查看日志：
CloudShell 中，home/modules/hadoop-2.7.7/logs

# Lab02 easy 版

## 实验操作

### 修改文件

#### 配置 hadoop 设置

修改文件名： `mapred-site.xml.template` 改为 `mapred-site.xml`

修改文件内容：
文件均在 `hadoop-2.7.7/etc/hadoop/` 目录下
`hadoop-env.sh` : 添加配置文本 `export JAVA_HOME=/usr/lib/jvm/jdk8u292-b10` 到文件中
`core-site.xml` : 替换掉原文件中的 `<configuration></configuration>` 部分， 主要改fs.defaultFS、fs.obs.access.key、fs.obs.secret.key、fs.obs.endpoint
`hdfs-site.xml` : 替换掉原文件中的 `<configuration></configuration>` 部分，主要将node 名称改成自己实际的 node 名
`yarn-site.xml` : 替换掉原文件中的 `<configuration></configuration>` 部分，主要将node 名称改成自己实际的 node 名
`mapred-site.xml` : 替换掉原文件中的 `<configuration></configuration>` 部分，主要将node 名称改成自己实际的 node 名
`slaves` :原内容删除，改为子节点名称，共三行，文件内容如下

slaves 内容[^slaves]

```text
node2
node3
node4
```

#### 修改本地 hosts 文件

Windows hosts文件位置: `C:\Windows\System32\drivers\etc` 。
修改方法: 将hosts文件复制到桌面上，修改桌面上的hosts文件，之后复制回原目录，此时提示需要管理员权限，继续并赋予权限即可(也可用vscode打开修改并保存，会提示你保存失败，以管理员模式重试，之后点以管理员身份，然后赋予权限即可)
生效方法：dos命令行下执行 `ipconfig /flushdns` 刷新dns
补充：可以先运行 `ipconfig /displaydns` 查看dns

mac电脑修改本地 hosts 文件，在本地终端输入：
vim /etc/hosts

文件末尾添加四台服务器局域网 ip 以及服务器名称：

```text
node1ip  node1
node2ip  node2
node3ip  node3
node4ip  node4
```

### 华为云开放端口

弹性云服务器 -> 安全组 -> 选择default -> 选择入向方向规则 -> 添加规则(共两个端口，8020和50010，优先级为1)

开8020端口是为了让hadoop可以被访问，50010端口是为了可以写数据

### 修改 hosts

#### 设置 IP 映射

```shell
ssh root@node1ip #登录指令
# On node1~node4 respectively
vim /etc/hosts #编辑hosts文件，加入 node1~node4 对应 IP 及 node 节点名
```

hosts[^host]添加的内容, 注释掉127的地址

```text
node1iip  node1
node2iip  node2
node3iip  node3
node4iip  node4

node1iip  node1 node1
```

### 配置节点互信

#### 配置防火墙

```shell
# Only on node1
systemctl status firewalld.service #查看防火墙状态
systemctl stop firewalld.service #关闭防火墙 .service
systemctl disable firewalld.service #禁止firewall开机启动 .service
```

#### 配置服务器间免密访问

id_rsa.pub 与 authorized_keys[^rsa]

```shell
# On node1~node4 respectively
ssh-keygen -t rsa #生成rsa类型的ssh密钥,用于远程免密登录，结果：生成/root/.ssh/id_rsa.pub
cat /root/.ssh/id_rsa.pub #输出文件内容，便于汇总
# 将4个节点的 id_rsa.pub 文件内容复制汇总到一个文件里，再分别拷到4个节点的 authorized_keys 中
vim /root/.ssh/authorized_keys #编辑 authorized_keys 文件，拷贝汇总的rsa密钥, 带前面的 "ssh-rsa "
```

#### 验证与首次连接

完成以上操作后，在4个节点分别执行 `ssh node1~node4` 并选择 yes 后，确保可以无密码跳转到目标节点。若出现连接被拒绝的情况，检查目标终端的 `/etc/hosts.deny` 文件，可能包含了被拒绝的终端IP

```shell
# On node1~node4 respectively, 共16遍TODO
ssh node1
ssh node2
ssh node3
ssh node4
# 参考顺序，从 node1 开始
ssh node1 #1->1
ssh node2 #1->2
ssh node2 #2->2
ssh node3 #2->3
ssh node3 #3->3
ssh node4 #3->4
ssh node4 #4->4
ssh node3 #4->3
ssh node2 #3->2
ssh node1 #2->1
ssh node3 #1->3
ssh node1 #3->1
ssh node4 #1->4
ssh node2 #4->2
ssh node4 #2->4
ssh node1 #4->1
```

### 上传，分发

#### 上传

```shell
# On my computer
# sftp 版
sftp root@node1ip
lcd Filepath
lpwd # 检查本机路径是否正确
put -r hadoop-2.7.7 hadoop-2.7.7 #不加后面会把文件内容散开传到root，第二个参数用/hadoop-2.7.7会传到根目录，用~/hadoop-2.7.7会不识别，如果目录下没有这个文件夹，会自动创建
put OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar
exit
# scp 版
scp -r Filepath/hadoop-2.7.7 root@node1ip:~/ #上传Hadoop安装包(此指令已失效)
scp -r Filepath/OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar root@node1ip:~/ #上传 OpenJDK
```

TODO: scp指令会少上传share文件夹: hadoop-2.7.7/share/hadoop

```shell
# 传错了，删除
# On node1
rm -rf hadoop-2.7.7 #注意路径是否正确
rm OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar
```

#### 分发与安装

##### Java

```shell
# On node1
cp OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar /usr/lib/jvm/ #将 jdk 安装包拷贝到/usr/lib/jvm 目录下
scp /usr/lib/jvm/OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar root@node2:/usr/lib/jvm/ #分发安装包到节点2
scp /usr/lib/jvm/OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar root@node3:/usr/lib/jvm/ #分发安装包到节点3
scp /usr/lib/jvm/OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar root@node4:/usr/lib/jvm/ #分发安装包到节点4
```

```shell
# On node1~node4 respectively
cd /usr/lib/jvm/
tar -vxf OpenJDK8U-jdk_aarch64_linux_openj9_8u292b10_openj9-0.26.0.tar # 安装tar包
```

如果自行安装的java, 添加配置文本前先运行以下指令查看路径

```shell
# On node1
which java #查看java位置，记录 /bin/java 之前的路径，为JAVA_PATH指示的路径
```

##### Hadoop

```shell
# On node1~node4 respectively
mkdir /home/modules/
# 确保他节点的/home/modules下没有hadoop-2.7.7文件夹，如有，rm -rf /home/modules/hadoop-2.7.7, 注意传过去别散开
cp -r hadoop-2.7.7 /home/modules/ #复制 hadoop 安装包，一定要确认复制完成后有没有hadoop-2.7.7文件夹
# scp 不行了用 sftp
scp -r /home/modules/hadoop-2.7.7 root@node2:/home/modules/hadoop-2.7.7 #分发Hadoop到节点2
scp -r /home/modules/hadoop-2.7.7 root@node3:/home/modules/hadoop-2.7.7 #分发Hadoop到节点3
scp -r /home/modules/hadoop-2.7.7 root@node4:/home/modules/hadoop-2.7.7 #分发Hadoop到节点4
```

### 修改 profile

```shell
# On node1~node4 respectively
vim /etc/profile #添加配置文本,内容如下
```

```text
export JAVA_HOME=/usr/lib/jvm/jdk8u292-b10
export HADOOP_HOME=/home/modules/hadoop-2.7.7
export PATH=$JAVA_HOME/bin:$PATH
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH
export HADOOP_CLASSPATH=/home/modules/hadoop-2.7.7/share/hadoop/tools/lib/*:$HADOOP_CLASSPATH
```

#### 使配置生效

```shell
# On node1~node4 respectively
source /etc/profile #读取和执行 /etc/profile 文件。使配置生效
java -version #查看java版本
chmod -R 777 /home/modules/hadoop-2.7.7 #修改文件权限，一定要做

# On node1
hadoop namenode -format #格式化指令，使hadoop配置文件生效，如果有Permission Denied，解决问题后要重新格式化
```

对于格式化 namenode ，如果已经运行过 `start-all.sh` 指令，要先进行以下操作，再运行上面的指令：

1. 再node1上，运行 `stop-all.sh` 指令
2. 清空各个节点配置的hadoop tmp目录、name目录、data目录、以及hadoop logs目录，tmp目录即core-site.xml下配置的<name>hadoop.tmp.dir</name>所指向的目录(hadoop-2.7.7/tmp)的所有子目录，其余同理

#### 启动与验证

```shell
# On node1
start-all.sh #启动 Hadoop
jps #验证,结果如下则正确
# On node2~node4 respectively
jps
```

```text
主节点运行结果(截图)：
Jps
ResourceManager
SecondaryNameNode
NameNode

子节点运行结果(截图)：
NodeManager
Jps
DataNode
```

```shell
# On node1
netstat -ltpn #查看端口
```

确保 Hadoop 集群(4个节点) 8020 端口监听的不是本地 IP(不是本地 IP可跳过 hosts 文件修改步骤， 若为 127.0.0.1:8020 则需要修改 hosts 文件)

修改 hosts 文件：

```shell
# On node1~node4 respectively
vim /etc/hosts #修改 hosts 文件
```

将 127.0.0.1 的部分注释掉(加#)，确保四台服务器 ip 为局域网 ip (可在华为云上查看)

#### 检查 hdfs 运行情况

```shell
# On node1
hadoop fs -ls / #浏览hadoop的文件系统，若目录下无文件则只会有一个 WARN 的提示后显示结束
hadoop dfsadmin -report #应该有三个子节点的状态信息
```

指令基本与 Linux 一致

### 创建 maven 工程

没有安装 maven 先安装 maven

1. 新建工程
2. 修改pom.xml
3. 重载项目或更新项目

```xml
<!-- pom.xml 修改内容-->
  <properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <hadoop.version>2.7.7</hadoop.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
      <version>1.2.17</version>
    </dependency>
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
```

如使用 IDEA 则按实验报告要求做

### java 实现 HDFS 文件读写

#### 检查端口

```shell
# On node1
netstat -ltpn #查看端口
```

确保 Hadoop 集群(4个节点) 8020 端口监听的不是本地 IP(不是本地 IP可跳过 hosts 文件修改步骤， 若为 127.0.0.1:8020 则需要修改 hosts 文件)

修改 hosts 文件：

```shell
# On node1~node4 respectively
vim /etc/hosts #修改 hosts 文件
```

将 127.0.0.1 的部分注释掉(加#)

```shell
# On node1
# 启动 hadoop
start-dfs.sh
start-yarn.sh
```

### 代码

hdfsPath = "/"
log4j打印日志
正常运行不会等待很长时间。反之说明出现了问题，可以耐心等待报出异常，或登录到服务器，查看/home/modules/hadoop-2.7.7/logs/hadoop-root-namenode-主节点名.log以及/home/modules/hadoop-2.7.7/logs/hadoop-root-datanode-从节点名.log这些日志文件，同样请关注java异常栈 (日志文件内容越靠后就越新) 。
执行java代码时有红字log4j或类似日志记录一样的东西不用管，只有java异常栈才说明问题，其余部分跟原报告执行效果一样即说明实验成功。

## 其他操作

查看日志：
CloudShell 中，home/modules/hadoop-2.7.7/logs

# 补充内容

## 实验结果

**实验结束后应得到：**
一个 Hadoop 集群，其中 1 个主节点，3 个子节点。
一个 maven 工程
完成 HDFS 文件读写实践

**需提交：**

* maven 打压缩包(3分)
* 实验报告：
图一：启动 Hadoop 后，主节点输入 jps 后的输出(1分)
图二：启动 Hadoop 后，任意子节点输入 jps 后的输出(1分)
图三：java 代码运行结果(必须按要求包含学号信息，否则不得分)(3分)
图四：HDFS 下载文件截图(必须按要求包含学号信息，否则不得分)(3分)
实验报告应包含对截图的文字介绍，以证明理解截图含义(1分)

## 概念解释

### 关闭防火墙

* hadoop 关闭防火墙原因：不关闭可能出现节点间无法通信的情况，会妨碍Hadoop集群间的相互通信。如果内网内开启防火墙，内网集群通讯会出现很多问题。而且集群式现在没什么安全性考虑的。
* 安全性：集群一般是处于局域网中的，都是内网搭建的，对外还有一个服务器的，那个服务器有防火墙，由它来访问内网集群，因此关闭内网防火墙一般也不会存在安全隐患。
* 防火墙配置方式：假设有100台服务器，99台肯定是在内网中，1台连接外网的。
    - 那个1台：需要配置防火墙。需要哪些端口就开哪些端口，不需要就关了。如果需要防火墙对其他的连接依旧起作用的话，需要将 hadoop 需要监听的那些端口配置到防火墙接受规则中。如果自己搭建：不用管，可以直接关，无所谓；如果公司用: 防火墙要开着，先把端口全部关闭，然后搭建的时候需要开发哪些端口就开放哪些，主要考虑到安全。有运维的话，运维会解决的防火墙问题。
    - 其余99台：不需要配置防火墙。

### 格式化namenode

在 Hadoop 的 HDFS 部署好了之后并不能马上使用，而是先要对配置的文件系统[^文件系统]进行格式化[^格式化]。在 NameNode 节点上，有两个最重要的路径，分别被用来存储元数据信息和操作日志，而这两个路径来自于配置文件，它们对应的属性分别是 `dfs.name.dir` 和 `dfs.name.edits.dir` ，同时，它们默认的路径均是 `/tmp/hadoop/dfs/name` 。

对于第一次使用 HDFS，在启动 NameNode 时，需要先执行 `hadoop namenode -format` 命令，然后才能正常启动 NameNode 节点的服务。

格式化namenode都发生了什么：

1. 清空dfs.name.dir和dfs.name.edits.dir两个目录下的所有文件
2. 在目录dfs.name.dir下创建文件：
    > [plain] view plaincopy
    > {dfs.name.dir}/current/fsimage
    > {dfs.name.dir}/current/fstime
    > {dfs.name.dir}/current/VERSION
    > {dfs.name.dir}/image/fsimage

3. 在目录dfs.name.edits.dir下创建文件：
    > [plain] view plaincopy
    > {dfs.name.edits.dir}/current/edits
    > {dfs.name.edits.dir}/current/fstime
    > {dfs.name.edits.dir}/current/VERSION
    > {dfs.name.edits.dir}/image/fsimage

## 参考

[sftp常用命令介绍](https://blog.csdn.net/qq_24309787/article/details/80117269)
[hadoop：分布式集群参数master节点的配置！](https://blog.csdn.net/yoggieCDA/article/details/106373933)
[ssh-keygen的使用方法及配置authorized_keys两台linux机器相互认证](https://blog.csdn.net/xiaoyida11/article/details/51557174)
[一文读懂authorized_keys和known_hosts](https://blog.csdn.net/qq_26400953/article/details/105145103)
[hosts文件的作用说明](https://blog.csdn.net/zsjwenrou/article/details/81134595)
[hosts文件作用](https://blog.csdn.net/bbj12345678/article/details/109120910)
[hadoop之slaves文件详细分析](https://blog.csdn.net/qq_40128682/article/details/79937325)
[NameNode的format操作做了什么](https://blog.csdn.net/xhh198781/article/details/6904615)
[为什么要格式化namenode以及注意点](https://blog.csdn.net/qq_36770189/article/details/97422158)
[Hadoop中重新格式化namenode](https://my.oschina.net/HIJAY/blog/220816)
[Hadoop namenode重新格式化需注意问题](https://blog.csdn.net/gis_101/article/details/52821946)

[^rsa]: rsa: 用于远程登录的密钥，记录着一个想通过ssh远程登录其它设备的设备的信息，当被加入到被登录设备的authorized_keys里时，可以实现对其的免密远程登录, 即 A 的 id_rsa 存到 B 的 authorized_keys 中后，A 可以免密登录 B
[^host]: host 构建名称和 ip 的映射，使用名称就是使用 ip。
[^slaves]: slaves 文件指明哪些节点运行 DateNode 进程，需将节点保存到slaves文件中。
[^文件系统]: 此时的文件系统在物理上还不存在，或许是网络磁盘来描述会更加合适
[^格式化]: 此处的格式化并不是指传统意义上的本地磁盘格式化，而是一些清除与准备工作
