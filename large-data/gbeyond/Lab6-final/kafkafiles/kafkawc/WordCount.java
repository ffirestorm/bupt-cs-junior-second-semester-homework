package kafkawc;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer08;

//import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
//import scala.Tuple2;

public class WordCount {
    public static void main(String[] args) throws Exception {
        // 获取Flink 运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 配置Kafka连接属性
        Properties properties = new Properties();
        // TODO: node1换成主节点名称
        properties.setProperty("bootstrap.servers", "node1:9092"); // Kafka连接ip端口
        properties.setProperty("zookeeper.connect", "node1:2181"); // ZooKeeper连接ip端口
        properties.setProperty("group.id", "1"); // Kafka消费组

        FlinkKafkaConsumer08<String> myconsumer = new FlinkKafkaConsumer08<>("test", new SimpleStringSchema(),
                properties);

        // 默认消费策略
        myconsumer.setStartFromGroupOffsets();

        DataStream<String> dataStream = env.addSource(myconsumer);

        DataStream<Tuple2<String, Integer>> result = dataStream.flatMap(new MyFlatMapper()).keyBy(0).sum(1);

        result.print().setParallelism(1);

        env.execute();
    }
}
