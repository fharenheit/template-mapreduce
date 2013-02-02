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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.junit.Before;
import org.junit.Test;

/**
 * GroupBy Mapper와 Reducer에 대한 단위 테스트 케이스.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class GroupByMapReduceTest {

    private Mapper mapper;
    private Reducer reducer;
    private MapReduceDriver driver;

    @Before
    public void setUp() {
        mapper = new GroupByMapper();
        reducer = new GroupByReducer();
        driver = new MapReduceDriver(mapper, reducer);
    }

    @Test
    public void test1() {
        Configuration conf = new Configuration();
        conf.set("inputDelimiter", ",");
        conf.set("keyValueDelimiter", ",");
        conf.set("valueDelimiter", ",");
        conf.set("allowDuplicate", "false");
        conf.set("allowSort", "false");
        conf.set("groupByKey", "0");

        driver.setConfiguration(conf);

        driver.withInput(new LongWritable(1), new Text("홍길동,a,b"));
        driver.withInput(new LongWritable(2), new Text("홍길동,b"));
        driver.withOutput(NullWritable.get(), new Text("홍길동,a,b"));
        driver.runTest();
    }

    @Test
    public void testWithDuplicationTrue() {
        Configuration conf = new Configuration();
        conf.set("inputDelimiter", ",");
        conf.set("keyValueDelimiter", ",");
        conf.set("valueDelimiter", ",");
        conf.set("allowDuplicate", "true");
        conf.set("allowSort", "false");
        conf.set("groupByKey", "0");
        driver.setConfiguration(conf);

        driver.withInput(new LongWritable(1), new Text("홍길동,a,b"));
        driver.withInput(new LongWritable(2), new Text("홍길동,b"));
        driver.withOutput(NullWritable.get(), new Text("홍길동,a,b,b"));
        driver.runTest();
    }

    @Test
    public void testWithSort() {
        Configuration conf = new Configuration();
        conf.set("inputDelimiter", ",");
        conf.set("keyValueDelimiter", ",");
        conf.set("valueDelimiter", ",");
        conf.set("allowDuplicate", "true");
        conf.set("allowSort", "true");
        conf.set("groupByKey", "0");
        driver.setConfiguration(conf);

        driver.withInput(new LongWritable(1), new Text("홍길동,1,c,b"));
        driver.withInput(new LongWritable(2), new Text("홍길동,2,a,b"));
        driver.withOutput(NullWritable.get(), new Text("홍길동,1,2,a,b,b,c"));
        driver.runTest();
    }

    @Test
    public void test2() {
        Configuration conf = new Configuration();
        conf.set("inputDelimiter", ",");
        conf.set("keyValueDelimiter", "\t");
        conf.set("valueDelimiter", ",");
        conf.set("allowDuplicate", "false");
        conf.set("allowSort", "false");
        conf.set("groupByKey", "1");
        driver.setConfiguration(conf);

        driver.withInput(new LongWritable(1), new Text("1,홍길동,a"));
        driver.withInput(new LongWritable(2), new Text("2,홍길동,b"));
        driver.withOutput(NullWritable.get(), new Text("홍길동\t1,a,2,b"));
        driver.runTest();
    }
}
