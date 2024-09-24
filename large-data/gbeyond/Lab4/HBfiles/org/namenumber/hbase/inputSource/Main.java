package org.namenumber.hbase.inputSource; // TODO: namenumber 改成姓名缩写+学号

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/*
 * HBase作为输入源，从HBase表中读取数据，使用MapReduce计算完成之后，将数据储存到HDFS中
 */
public class Main {
    public final Log LOG = LogFactory.getLog(Main.class);
    // job name
    public static final String NAME = "Member Test1";
    // 输出目录
    public static final String TEMP_INDEX_PATH = "hdfs://node1:8020/tmp/member_user"; // TODO:node1该成节点1名称，member_user改成学号+姓名即表名
    // HBase 作为输入源的HBase中的表member_user
    public static String inputTable = "member_user"; // TODO:member_user改成学号+姓名即表名

    public static void main(String[] args) throws Exception {
        // 1. 获得HBase的配置信息
        Configuration conf = HBaseConfiguration.create();
        // 2. 创建全表扫描器对象
        Scan scan = new Scan();
        scan.setBatch(0);
        scan.setCaching(10000);
        scan.setMaxVersions();
        scan.setTimeRange(System.currentTimeMillis() - 3 * 24 * 3600 * 1000L, System.currentTimeMillis());
        // 添加扫描的条件，列族和列族名
        scan.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("keyword"));
        // 设置HDFS的存储执行为fasle
        conf.setBoolean("mapred.map.tasks.speculative.execution", false);
        conf.setBoolean("mapred.reduce.tasks.speculative.execution", false);
        Path tmpIndexPath = new Path(TEMP_INDEX_PATH);
        FileSystem fs = FileSystem.get(conf);
        // 判断该路径是否存在，如果存在则首先进行删除
        if (fs.exists(tmpIndexPath)) {
            fs.delete(tmpIndexPath, true);
        }
        // 创建job对象
        Job job = new Job(conf, NAME);
        job.setJarByClass(Main.class);
        // 设置TableMapper类的相关信息，即对准mapper类的初始化设置
        // (hbase输入源对应的表，扫描器，负责计算的逻辑，输出的类型，输出value的类型，job)
        TableMapReduceUtil.initTableMapperJob(inputTable, scan, MemberMapper.class, Text.class, Text.class, job);
        job.setNumReduceTasks(0);
        // 设置从HBase表中经过MapReduce 计算后的结果以文本格式输出
        job.setOutputFormatClass(TextOutputFormat.class);
        // 设置作业输出结果保存到HDFS的文件路径
        FileOutputFormat.setOutputPath(job, tmpIndexPath);
        // 开始运行作业
        boolean success = job.waitForCompletion(true);
        System.exit(success ? 0 : 1);
    }
}
