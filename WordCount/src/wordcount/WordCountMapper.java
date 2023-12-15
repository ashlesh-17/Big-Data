package wordcount;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

	private static final IntWritable ONE = new IntWritable(1);
	private Text outputKey = new Text();
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String currentLine = value.toString();
		String [] words = StringUtils.split(currentLine, ' ');
		for(String word : words) {
			outputKey.set(word);
			context.write(outputKey, ONE);
		}
	}
	
	
	public static class WordCountCombiner extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable outputValue = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for(IntWritable count : values) {
				sum += count.get();
			}
			outputValue.set(sum);
			context.write(key, outputValue);
		}
		
	}
}
