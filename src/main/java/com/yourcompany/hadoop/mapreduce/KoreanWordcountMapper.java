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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.openflamingo.mapreduce.util.Lucene4Utils;

import java.io.IOException;
import java.util.List;

/**
 * 한글 형태소 분서기를 기반으로 동작하는 Mapper
 *
 * @author Edward KIM
 * @version 0.1
 */
public class KoreanWordcountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    /**
     * Lucene 4 기반 한글 형태소 분석기
     */
    private KoreanAnalyzer analyzer = null;

    /**
     * Integer Counter
     */
    private static IntWritable one = new IntWritable(1);

    /**
     * Word
     */
    private static Text wordText = new Text();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration configuration = context.getConfiguration();
        boolean exactMatch = configuration.getBoolean("exactMatch", false);
        boolean bigrammable = configuration.getBoolean("bigrammable", false);
        boolean hasOrigin = configuration.getBoolean("hasOrigin", false);
        boolean originCNoun = configuration.getBoolean("originCNoun", false);

        analyzer = new KoreanAnalyzer();
        analyzer.setBigrammable(bigrammable);
        analyzer.setExactMatch(exactMatch);
        analyzer.setHasOrigin(hasOrigin);
        analyzer.setOriginCNoun(originCNoun);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String row = value.toString();
        List<String> words = Lucene4Utils.tokenizeString(analyzer, row);
        for (String word : words) {
            wordText.set(word);
            context.write(wordText, one);
        }
    }
}









