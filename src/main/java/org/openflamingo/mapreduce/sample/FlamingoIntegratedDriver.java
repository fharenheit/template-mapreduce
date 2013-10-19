/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openflamingo.mapreduce.sample;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class FlamingoIntegratedDriver extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.util.Tool {

    public static void main(String[] args) throws Exception {
        Properties properties = System.getProperties();
        Set<Object> objects = properties.keySet();
        for (Iterator<Object> iterator = objects.iterator(); iterator.hasNext(); ) {
            Object key = iterator.next();
            System.out.println(key + "=" + properties.get(key));
        }

        int res = ToolRunner.run(new FlamingoIntegratedDriver(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        Job job = new Job(super.getConf());

        parseArguements(args, job);

        job.setJarByClass(FlamingoIntegratedDriver.class);

        job.setMapperClass(FlamingoIntegratedMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setNumReduceTasks(0);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    private void parseArguements(String[] args, Job job) throws IOException {
        for (int i = 0; i < args.length; ++i) {
            if ("-input".equals(args[i])) {
                FileInputFormat.addInputPaths(job, args[++i]);
            } else if ("-output".equals(args[i])) {
                FileOutputFormat.setOutputPath(job, new Path(args[++i]));
            }
        }
    }
}