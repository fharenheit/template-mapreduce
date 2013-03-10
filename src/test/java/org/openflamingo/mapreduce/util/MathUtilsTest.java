/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openflamingo.mapreduce.util;

import org.junit.Before;
import org.junit.Test;

/**
 * Math Utility의 단위 테스트 케이스.
 *
 * @author Edward KIM
 * @since 0.2
 */
public class MathUtilsTest {

    double[] quantiles = new double[100];

    double[] values = {12, 34, 2, 4, 5, 6, 2, 34, 5, 234, 23, 234, 234, 234, 23, 26, 68, 75, 64, 53, 2123, 74, 56, 35, 345, 352, 4, 234};

    @Before
    public void before() {
        for (int i = 0; i < 100; i++) {
            quantiles[i] = i + 1;
        }
    }

    @Test
    public void percentile() throws Exception {
        System.out.println(MathUtils.percentile(values, quantiles, ","));
    }

}
