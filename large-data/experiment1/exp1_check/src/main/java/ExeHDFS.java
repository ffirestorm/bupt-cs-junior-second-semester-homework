/**
 * @author 陈朴炎
 * @version 1.0
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class ExeHDFS {
    String hdfsPath = "/";

    public static void main(String[] args) {
        ExeHDFS testHDFS = new ExeHDFS();
        try {
            testHDFS.testView();
            testHDFS.testUpload();
            testHDFS.testCreate();
            testHDFS.testDownload();
            testHDFS.testView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 查看HDFS文件系统
    public void testView() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("View file:");
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", "hdfs://1.92.114.12:8020"); // TODO: 将 "node1ip" 修改为自己主节点的公网ip地址
        FileSystem hdfs = FileSystem.get(new URI("hdfs://1.92.114.12"), conf, "root"); // TODO: 将 "node1ip" 修改为自己主节点的公网ip地址
        Path path = new Path(hdfsPath);
        FileStatus[] list = hdfs.listStatus(path);
        if (list.length == 0) {
            System.out.println("HDFS is empty.");
        } else {
            for (FileStatus f : list) {
                System.out.printf("name: %s, folder: %s, size: %d\n", f.getPath(), f.isDirectory(), f.getLen());
            }
        }
    }

    // 上传本地文件到HDFS
    public void testUpload() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Upload file:");
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", "hdfs://1.92.114.12:8020"); // TODO: 将 "node1ip" 修改为自己主节点的公网ip地址
        FileSystem hdfs = FileSystem.get(new URI("hdfs://1.92.114.12"), conf, "root"); // TODO: 将 "node1ip" 修改为自己主节点的公网ip地址
        InputStream in = new FileInputStream("./upload.txt"); // TODO: fix, 完善要上传的文件(upload.txt)的路径
        OutputStream out = hdfs.create(new Path(hdfsPath + "upload_2021211138.txt")); // TODO: 将 "studentID" 修改为自己的学号
        IOUtils.copyBytes(in, out, conf);
        System.out.println("Upload successfully!");
    }

    // 创建HDFS文件
    public void testCreate() throws Exception {
        System.out.println("Write file:");
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", "hdfs://1.92.114.12:8020"); // TODO: 将 "node1ip" 修改为自己主节点的公网ip地址
        // 待写入文件内容
        // 写入自己姓名与学号
        byte[] buff = "Hello world! Myname is 陈朴炎, my student id is 2021211138.".getBytes(); // TODO: 完善姓名与学号
        // FileSystem 为 HDFS 的 API, 通过此调用 HDFS
        FileSystem hdfs = FileSystem.get(new URI("hdfs://1.92.114.12"), conf, "root"); // TODO: 将 "node1ip" 修改为自己主节点的公网ip地址
        // 文件目标路径，应填写 hdfs 文件路径
        Path dst = new Path(hdfsPath + "gby_2021211138.txt"); // TODO: 将 "studentID" 修改为自己的学号
        FSDataOutputStream outputStream = null;
        try {
            // 写入文件
            outputStream = hdfs.create(dst);
            outputStream.write(buff, 0, buff.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        // 检查文件写入情况
        FileStatus files[] = hdfs.listStatus(dst);
        for (FileStatus file : files) {
            // 打印写入文件路径及名称
            System.out.println(file.getPath());
        }
    }

    // 从HDFS下载文件到本地
    public void testDownload() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Download file:");
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", "hdfs://1.92.114.12:8020"); // TODO: 将 "node1ip" 修改为自己主节点的公网ip地址
        FileSystem hdfs = FileSystem.get(new URI("hdfs://1.92.114.12"), conf, "root"); // TODO: 将 "node1ip" 修改为自己主节点的公网ip地址
        InputStream in = hdfs.open(new Path(hdfsPath + "gby_2021211138.txt")); // TODO: 将 "studentID" 修改为自己的学号
        OutputStream out = new FileOutputStream("./download_studentID.txt"); // TODO: fix, 完善下载的文件(download_studentID.txt)的存放路径，就是放哪儿
        IOUtils.copyBytes(in, out, conf);
        System.out.println("Download successfully!");
    }
}
