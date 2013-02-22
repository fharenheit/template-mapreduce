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
package org.openflamingo.mapreduce.etl.groupby;

import junit.framework.Assert;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * WordCount Mapper와 Reducer에 대한 통합 테스트 케이스.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class ClusterWordCountMapReduceTest {

    /**
     * Hadoop HDFS for Test
     */
    private MiniDFSCluster dfsCluster = null;

    /**
     * Hadoop Cluster for Test
     */
    private MiniMRCluster miniCluster = null;

    /**
     * Input Path
     */
    private final Path input = new Path("input");

    /**
     * Output Path
     */
    private final Path output = new Path("output");

    /**
     * Datanode의 수
     */
    private int numDataNodes = 1;

    /**
     * Task Ta
     */
    private int numTaskTracker = 1;

    @Before
    public void setUp() throws IOException {
        new File("target", "test-logs").mkdirs();
        System.setProperty("hadoop.log.dir", "target/test-logs");
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

        Configuration conf = new Configuration();

        dfsCluster = new MiniDFSCluster(conf, numDataNodes, true, null);
        dfsCluster.getFileSystem().makeQualified(input);
        dfsCluster.getFileSystem().makeQualified(output);
        miniCluster = new MiniMRCluster(numTaskTracker, getFileSystem().getUri().toString(), 1);
    }

    protected FileSystem getFileSystem() throws IOException {
        return dfsCluster.getFileSystem();
    }

    private void createTextInputFile() throws IOException {
        OutputStream os = getFileSystem().create(new Path(input, "wordcount"));
        Writer wr = new OutputStreamWriter(os);
        wr.write("b a a\n");
        wr.close();
    }

    public JobConf createJobConf() {
        JobConf conf = miniCluster.createJobConf();
        conf.setJobName("wordcount test");

        conf.setMapperClass(WordCountMapper.class);
        conf.setReducerClass(SumReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(IntWritable.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);
        conf.setNumMapTasks(1);
        conf.setNumReduceTasks(1);
        FileInputFormat.setInputPaths(conf, input);
        FileOutputFormat.setOutputPath(conf, output);
        return conf;
    }

    @Test
    public void wordcount() throws IOException {
        createTextInputFile();

        JobClient.runJob(createJobConf());

        Path[] outputFiles = FileUtil.stat2Paths(getFileSystem().listStatus(output, new OutputLogFilter()));

        Assert.assertEquals(1, outputFiles.length);
        InputStream is = getFileSystem().open(outputFiles[0]);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Assert.assertEquals("a\t2", reader.readLine());
        Assert.assertEquals("b\t1", reader.readLine());
        Assert.assertNull(reader.readLine());
        reader.close();
    }

    @After
    public void tearDown() throws Exception {
        if (dfsCluster != null) {
            dfsCluster.shutdown();
            dfsCluster = null;
        }
        if (miniCluster != null) {
            miniCluster.shutdown();
            miniCluster = null;
        }
    }

}
