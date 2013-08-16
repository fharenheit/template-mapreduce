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
package com.yourcompany.hadoop.mapreduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * 한글 형태소 분석기를 기반으로 동작하는 Reducer
 *
 * @author Edward KIM
 * @version 0.1
 */
public class KoreanWordcountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    /**
     *
     */
    private int maxSupport = 0;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        this.maxSupport = context.getConfiguration().getInt("maxSupport", 10);
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable count : values) {
            sum += count.get();
        }

        context.getCounter("COUNT", "UNIQUE_WORD").increment(1);

        if (sum > maxSupport) {
            context.getCounter("COUNT", "OVER_THRESHOLD").increment(1);
            context.write(key, new IntWritable(sum));
        } else {
            context.getCounter("COUNT", "UNDER_THRESHOLD").increment(1);
        }
    }
}










