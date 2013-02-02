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

import org.apache.commons.cli.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.openflamingo.mapreduce.util.HdfsUtils;

/**
 * Command Line Parser를 이용한 Hadoop MapReduce Sample Driver.
 *
 * @author Edward KIM
 * @version 0.1
 */
public class Sample2Driver extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.util.Tool {

    /**
     * 필수 옵션
     */
    private final String[][] requiredOptions =
            {
                    {"i", "입력 경로를 지정해 주십시오. 입력 경로가 존재하지 않으면 MapReduce가 동작할 수 없습니다."},
                    {"o", "출력 경로를 지정해 주십시오."},
                    {"d", "컬럼의 구분자를 지정해주십시오. CSV 파일의 컬럼을 처리할 수 없습니다."},
            };

    /**
     * 사용가능한 옵션 목록을 구성한다.
     *
     * @return 옵션 목록
     */
    private static Options getOptions() {
        Options options = new Options();
        options.addOption("i", "input", true, "입력 경로 (필수)");
        options.addOption("o", "output", true, "출력 경로 (필수)");
        options.addOption("d", "delimiter", true, "컬럼 구분자 (필수)");
        options.addOption("od", "delete", false, "출력 경로가 이미 존재하는 경우 삭제");
        return options;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Sample2Driver(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        Job job = new Job();

        int result = parseArguements(args, job);
        if (result != 0) {
            return result;
        }

        job.setJarByClass(Sample2Driver.class);

        // Mapper Class
        job.setMapperClass(SampleMapper.class);

        // Output Key/Value
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        // Reducer Task
        job.setNumReduceTasks(0);

        // Run a Hadoop Job
        return job.waitForCompletion(true) ? 0 : 1;
    }

    private int parseArguements(String[] args, Job job) throws Exception {
        ////////////////////////////////////////
        // 옵션 목록을 구성하고 검증한다.
        ////////////////////////////////////////

        Options options = getOptions();
        HelpFormatter formatter = new HelpFormatter();
        if (args.length == 0) {
            formatter.printHelp("org.openflamingo.hadoop jar <JAR> " + getClass().getName(), options, true);
            return -1;
        }

        // 커맨드 라인을 파싱한다.
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);

        // 파라미터를 검증한다.
        for (String[] requiredOption : requiredOptions) {
            if (!cmd.hasOption(requiredOption[0])) {
                formatter.printHelp("org.openflamingo.hadoop jar <JAR> " + getClass().getName(), options, true);
                return -1;
            }
        }

        ////////////////////////////////////////
        // 파라미터를 Hadoop Configuration에 추가한다
        ////////////////////////////////////////

        if (cmd.hasOption("i")) {
            FileInputFormat.addInputPaths(job, cmd.getOptionValue("i"));
        }

        if (cmd.hasOption("o")) {
            FileOutputFormat.setOutputPath(job, new Path(cmd.getOptionValue("o")));
        }

        if (cmd.hasOption("d")) {
            job.getConfiguration().set("delimiter", cmd.getOptionValue("d"));
        }

        // 옵션을 지정한 경우 출력 경로를 삭제한다.
        if (cmd.hasOption("od")) {
            if (HdfsUtils.isExist(cmd.getOptionValue("o"))) {
                HdfsUtils.deleteFromHdfs(cmd.getOptionValue("o"));
            }
        }

        return 0;
    }
}