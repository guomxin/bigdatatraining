package com.telchina.bdtutorial.mr;

import java.io.IOException;

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

/**
 * 
 * 将项目导出为jar包，并传到hadoop集群任一节点,然后运行如下命令：
 * hadoop jar wordcount.jar com.telchina.bdtutorial.mr.WordCounter /tmp/wordCountInput/ /tmp/wordCountOutput/
 *
 */
@SuppressWarnings("deprecation")
public class WordCounter {

	public static class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {
		private static final IntWritable one = new IntWritable(1);
		private Text word = new Text();

		/*
		@Override
		protected void setup(
				Mapper<Object, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			super.setup(context);
			String filepath = ((FileSplit)context.getInputSplit()).getPath().toString();
		}
		*/

		protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			String[] words = value.toString().split(" ");

			if (words.length == 0)
				return;

			for (String item : words)
				if ((item != null) && (!item.equals(""))) {
					this.word.set(item.trim().toLowerCase());
					context.write(this.word, one);
				}
		}
	}

	public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		protected void reduce(Text key, Iterable<IntWritable> values,
				Reducer<Text, IntWritable, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			this.result.set(sum);
			context.write(key, this.result);
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		if (otherArgs.length < 2) {
			System.err.println("params:<inputDir> <outputDir>");
			System.exit(1);
		}
		Job job = new Job(conf, "Word count !");
		job.setJarByClass(WordCounter.class);
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);
		job.setCombinerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
