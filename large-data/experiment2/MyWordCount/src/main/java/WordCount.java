import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author 陈朴炎
 * @version 1.0
 */
public class WordCount {

    /**
     * 这个类的作用是将输入的文本数据切分成单词，并为每个单词输出一个键值对
     *
     * 其中键是单词本身，值是1，表示该单词出现了一次。
     */
    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    /**
     * 将 Mapper 部分输出的中间结果进行合并和汇总
     */
    public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        /**
         * 将 Mapper 部分输出的中间结果进行合并和汇总。
         *
         * @param key   当前要计算的键
         * @param values    输入值的迭代器
         * @param context   上下文对象，用于将结果输出
         *
         * <p>在 reduce 方法内部，首先定义了一个整型变量 sum，用于保存当前键对应的值的总和
         * <p>然后，通过一个 for 循环遍历输入的值的迭代器，将每个值取出并累加到 sum 变量中。</p>
         * <p>最后，将 sum 设置到 IntWritable 对象 result 中，
         *    并通过上下文对象 context 输出该键以及对应的总和。</p>
         */
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main (String [] args) throws Exception{

        Configuration conf = new Configuration();
        // 限定输出参数必须为2个
        String []otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        if(otherArgs.length!=2){
            System.err.println("Please usage: word-count <in> <out>");
            System.exit(2);
        }

        // 创建job对象
        Job job = new Job(conf, "word count");

        // 初始化job对象
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);

        // 设置输出格式
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 设置输入输出
        FileInputFormat.addInputPath(job,new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true)?0:1);
    }

}
