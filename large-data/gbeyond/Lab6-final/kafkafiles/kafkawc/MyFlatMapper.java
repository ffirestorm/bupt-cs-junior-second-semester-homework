package kafkawc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

//import scala.Tuple2;

public class MyFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>> {

    @Override
    public void flatMap(String s, Collector<Tuple2<String, Integer>> out) throws Exception {
        // 按空格分词
        String[] words = s.split(" ");
        for (String word : words) {
            out.collect(new Tuple2<>(word, 1));
        }
    }
}
