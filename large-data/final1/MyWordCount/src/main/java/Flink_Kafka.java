import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Properties;

/**
 * Flink_Kafka - 一个分词计数程序
 *
 * @author 陈朴炎
 * @version 1.0
 */
public class Flink_Kafka {
    public static void main(String[] args) throws Exception {

        // 获取Flink运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 配置Kafka连接属性
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "node1:9092"); // Kafka服务器地址
        properties.setProperty("zookeeper.connect", "node1:2181"); // Zookeeper地址（可选，通常用于旧版Kafka）
        properties.setProperty("group.id", "1"); // 消费者组ID

        // 创建FlinkKafkaConsumer来读取Kafka中的数据
        FlinkKafkaConsumer<String> myConsumer = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), properties);
        myConsumer.setStartFromGroupOffsets(); // 从消费者组的偏移量开始消费
        DataStream<String> dataStream = env.addSource(myConsumer); // 将Kafka消费者添加为数据源

        // 处理数据流：将每行文本拆分成单词，并计算每个单词的出现次数
        DataStream<Tuple2<String, Integer>> result = dataStream
                .flatMap(new MyFlatMapper()) // 将每行文本拆分成单词
                .keyBy(0) // 按照单词分组
                .sum(1); // 计算每个单词的出现次数

        // 打印结果到控制台，设置并行度为1
        result.print().setParallelism(1);

        // 执行Flink作业
        env.execute();
    }

    /**
     * MyFlatMapper - A FlatMapFunction that splits lines of text into words.
     * 每行文本拆分成单词，并将每个单词作为Tuple2<单词, 1>输出
     */
    public static class MyFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
            // 将行文本拆分成单词
            String[] words = s.split(" ");
            // 输出每个单词，附带初始计数1
            for (String word : words) {
                collector.collect(new Tuple2<>(word, 1));
            }
        }
    }
}
