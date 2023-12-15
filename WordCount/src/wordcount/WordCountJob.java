package wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import wordcount.WordCountMapper.WordCountCombiner;



public class WordCountJob extends Configured implements Tool {
	public static class WordCountPartitioner extends Partitioner<Text, IntWritable>{
		public int getPartition(Text key, IntWritable value, int numReduceTasks){
			if(numReduceTasks == 1){
				return 0;
			}
			return (key.toString().length() * value.get()) % numReduceTasks;
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), "WordCountJob");
		Configuration conf = job.getConfiguration();
		job.setJarByClass(getClass());
		
		Path in = new Path(args[0]);
		Path out = new Path(args[1]);
		out.getFileSystem(conf).delete(out, true); //Code added by Amit
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setMapperClass(WordCountMapper.class);
		job.setPartitionerClass(WordCountPartitioner.class);
		job.setCombinerClass(WordCountCombiner.class);
		job.setReducerClass(WordCountReducer.class);
		job.setNumReduceTasks(3);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		return job.waitForCompletion(true)?0:1;
	}

	public static void main(String[] args) {
		int result = 0;
		try {
			result = ToolRunner.run(new Configuration(), 
							new WordCountJob(),
							args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(result);
	}

}
