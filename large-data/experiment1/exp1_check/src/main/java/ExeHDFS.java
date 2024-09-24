/**
 * @author ������
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

    // �鿴HDFS�ļ�ϵͳ
    public void testView() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("View file:");
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", "hdfs://1.92.114.12:8020"); // TODO: �� "node1ip" �޸�Ϊ�Լ����ڵ�Ĺ���ip��ַ
        FileSystem hdfs = FileSystem.get(new URI("hdfs://1.92.114.12"), conf, "root"); // TODO: �� "node1ip" �޸�Ϊ�Լ����ڵ�Ĺ���ip��ַ
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

    // �ϴ������ļ���HDFS
    public void testUpload() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Upload file:");
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", "hdfs://1.92.114.12:8020"); // TODO: �� "node1ip" �޸�Ϊ�Լ����ڵ�Ĺ���ip��ַ
        FileSystem hdfs = FileSystem.get(new URI("hdfs://1.92.114.12"), conf, "root"); // TODO: �� "node1ip" �޸�Ϊ�Լ����ڵ�Ĺ���ip��ַ
        InputStream in = new FileInputStream("./upload.txt"); // TODO: fix, ����Ҫ�ϴ����ļ�(upload.txt)��·��
        OutputStream out = hdfs.create(new Path(hdfsPath + "upload_2021211138.txt")); // TODO: �� "studentID" �޸�Ϊ�Լ���ѧ��
        IOUtils.copyBytes(in, out, conf);
        System.out.println("Upload successfully!");
    }

    // ����HDFS�ļ�
    public void testCreate() throws Exception {
        System.out.println("Write file:");
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", "hdfs://1.92.114.12:8020"); // TODO: �� "node1ip" �޸�Ϊ�Լ����ڵ�Ĺ���ip��ַ
        // ��д���ļ�����
        // д���Լ�������ѧ��
        byte[] buff = "Hello world! Myname is ������, my student id is 2021211138.".getBytes(); // TODO: ����������ѧ��
        // FileSystem Ϊ HDFS �� API, ͨ���˵��� HDFS
        FileSystem hdfs = FileSystem.get(new URI("hdfs://1.92.114.12"), conf, "root"); // TODO: �� "node1ip" �޸�Ϊ�Լ����ڵ�Ĺ���ip��ַ
        // �ļ�Ŀ��·����Ӧ��д hdfs �ļ�·��
        Path dst = new Path(hdfsPath + "gby_2021211138.txt"); // TODO: �� "studentID" �޸�Ϊ�Լ���ѧ��
        FSDataOutputStream outputStream = null;
        try {
            // д���ļ�
            outputStream = hdfs.create(dst);
            outputStream.write(buff, 0, buff.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        // ����ļ�д�����
        FileStatus files[] = hdfs.listStatus(dst);
        for (FileStatus file : files) {
            // ��ӡд���ļ�·��������
            System.out.println(file.getPath());
        }
    }

    // ��HDFS�����ļ�������
    public void testDownload() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Download file:");
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", "hdfs://1.92.114.12:8020"); // TODO: �� "node1ip" �޸�Ϊ�Լ����ڵ�Ĺ���ip��ַ
        FileSystem hdfs = FileSystem.get(new URI("hdfs://1.92.114.12"), conf, "root"); // TODO: �� "node1ip" �޸�Ϊ�Լ����ڵ�Ĺ���ip��ַ
        InputStream in = hdfs.open(new Path(hdfsPath + "gby_2021211138.txt")); // TODO: �� "studentID" �޸�Ϊ�Լ���ѧ��
        OutputStream out = new FileOutputStream("./download_studentID.txt"); // TODO: fix, �������ص��ļ�(download_studentID.txt)�Ĵ��·�������Ƿ��Ķ�
        IOUtils.copyBytes(in, out, conf);
        System.out.println("Download successfully!");
    }
}
