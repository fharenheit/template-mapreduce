package org.openflamingo.mapreduce.sample;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;

public class PdfTextExtractionDriver {

    public static void main(String[] args) throws Exception {
        JobConf job = new JobConf(PdfTextExtractionDriver.class);
        parseArguements(args, job);

        job.setJarByClass(PdfTextExtractionDriver.class);

        job.setMapperClass(PdfTextExtractionMapper.class);

        job.setInputFormat(SequenceFileAsBinaryInputFormat.class);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setNumReduceTasks(0);

        JobClient client = new JobClient();
        RunningJob runningJob = client.submitJob(job);

        while (true) {
            Thread.sleep(1000);
            System.out.println("Map : " + runningJob.mapProgress() + ", Reducer : " + runningJob.reduceProgress() + "");
            if (runningJob.isComplete()) {
                System.exit(0);
            } else {
                System.exit(-1);
            }
        }
    }

    private static void parseArguements(String[] args, JobConf job) throws IOException {
        for (int i = 0; i < args.length; ++i) {
            if ("-input".equals(args[i])) {
                FileInputFormat.addInputPaths(job, args[++i]);
            } else if ("-output".equals(args[i])) {
                FileOutputFormat.setOutputPath(job, new Path(args[++i]));
            } else if ("-line.delimiter".equals(args[i])) {
                job.set("line.delimiter", args[++i]);
            } else if ("-kv.delimiter".equals(args[i])) {
                job.set("kv.delimiter", args[++i]);
            }
        }
    }
}