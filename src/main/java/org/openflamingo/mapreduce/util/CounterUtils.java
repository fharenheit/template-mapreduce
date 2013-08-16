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
package org.openflamingo.mapreduce.util;

import org.apache.hadoop.mapreduce.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Hadoop MapReduce Counter Utility.
 *
 * @author Edward KIM
 * @author Seo Ji Hye
 * @since 0.1
 */
public class CounterUtils {

    /**
     * SLF4J Logging
     */
    private static Logger logger = LoggerFactory.getLogger(CounterUtils.class);

    /**
     * Mapper의 Counter를 기록한다.
     *
     * @param mapper  Mapper
     * @param name    Counter Name
     * @param context Mapper의 Context
     */
    public static void writerMapperCounter(Mapper mapper, String name, Mapper.Context context) {
        context.getCounter(mapper.getClass().getName(), name).increment(1);
    }

    /**
     * Reducer의 Counter를 기록한다.
     *
     * @param reducer Reducer
     * @param name    Counter Name
     * @param context Reducer의 Context
     */
    public static void writerReducerCounter(Reducer reducer, String name, Reducer.Context context) {
        context.getCounter(reducer.getClass().getName(), name).increment(1);
    }

    /**
     * Job에 정의되어 있는 모든 Counter를 Map 형식으로 변환한다.
     * Key값은 <tt>GROUP_COUNTER</tt> 형식으로 예를 들면 Group Name이
     * <tt>CLEAN</tt>이고, Counter가 <tt>VALID</tt>라면 실제 Key는
     * <tt>CLEAN_VALID</tt>가 된다.
     *
     * @param job Hadoop Job
     * @return Counter와 값을 포함하는 Map
     */
    public static Map<String, String> getCounters(Job job) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            Counters counters = job.getCounters();
            Collection<String> groupNames = counters.getGroupNames();
            Iterator<String> groupIterator = groupNames.iterator();
            while (groupIterator.hasNext()) {
                String groupName = groupIterator.next();
                CounterGroup group = counters.getGroup(groupName);
                Iterator<Counter> counterIterator = group.iterator();
                while (counterIterator.hasNext()) {
                    Counter counter = counterIterator.next();
                    logger.info("[{}] {} = {}", new Object[]{
                            group.getName(), counter.getName(), counter.getValue()
                    });
                    String realName = HadoopMetrics.getMetricName(group.getName() + "_" + counter.getName());
                    if (!StringUtils.isEmpty(realName)) {
                        resultMap.put(realName, String.valueOf(counter.getValue()));
                    }
                }
            }
        } catch (Exception ex) {
        }
        return resultMap;
    }
}
