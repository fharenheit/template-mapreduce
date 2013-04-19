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
package org.openflamingo.mapreduce.hive;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;

import java.util.ArrayList;

/**
 * Group First Item Hive UDAF.
 * 이 UDAF는 Aggregation한 결과 데이터에서 가장 첫번째 항목을 선택하는 기능으로 중복된 데이터가 존재하더라도 모두
 * List에 담아서 처리하게 되므로 JVM Heap 소비가 발생한다. 따라서 Aggregation의 단위 건수가 많고 해당 구분키가 중복된 경우
 * 이 메소드를 사용하기 보다는 중복 제거 기능을 제공하는 Set을 사용할 것을 권장한다.
 * <p/>
 * <ul>
 *   <li>Mapper : iterate - terminatePartial</li>
 *   <li>Reducer : init - merge - terminate</li>
 * </ul>
 *
 * @author Edward KIM
 * @since 0.1
 */
@Description(
        name = "groupFirst",
        value = "_FUNC_(arr)  - Return a first item in aggregation. arr can be a array.",
        extended = "Example:\n"
                + "  > SELECT _FUNC_(groupFirst(contractId)) FROM Log GROUP BY contractId;\n"
                + "  '192837182'"
)
public class UDAFFirst extends UDAF {

    /**
     * 기본 생성자.
     */
    public UDAFFirst() {
    }

    /**
     * Aggregation을 수행하는 실제 클래스. Hive는 UDAFEvaluator를 구현하는 UDAF의 모든 클래스를 자동을 찾는다.
     */
    public static class UDAFGroupFirstEvaluator implements UDAFEvaluator {

        ArrayList<String> data;

        public UDAFGroupFirstEvaluator() {
            data = new ArrayList<String>();
        }

        /**
         * Aggregation의 상태값을 초기화한다.
         */
        public void init() {
            data.clear();
        }

        /**
         * 원본 데이터의 1개의 ROW를 반복한다.
         * 이 UDAF는 문자열로 된 인자를 N개 수용하며 단일 문자열을 처리하고자 하면 단일 문자열 인자를 사용하도록 한다.
         *
         * 이 메소드는 항상 <tt>true</tt>를 반환한다.
         */
        public boolean iterate(String[] elements) {
            if (elements != null) {
                StringBuilder builder = new StringBuilder();
                for (String element : elements) {
                    builder.append(element);
                }
                data.add(builder.toString());
            }
            return true;
        }

        /**
         * 부분 Aggregation을 마무리하고 상태를 반환한다.
         */
        public ArrayList<String> terminatePartial() {
            return data;
        }

        /**
         * 부분 Aggregation을 병합한다.
         * 이 함수는 항상 같은 유형의 값들을 가진 단일 인자를 가지도록 해야 하며 terminatePartial() 메소드의 결과값을 받는다.
         *
         * 이 메소드는 항상 <tt>true</tt>를 반환한다.
         */
        public boolean merge(ArrayList<String> elements) {
            if (elements != null) {
                data.addAll(elements);
            }
            return true;
        }

        /**
         * Aggregation을 마무리하고 최종 결과를 반환한다.
         * 최종 결과를 생성할 때에는 가장 첫번째 아이템만 추출한다.
         *
         * @return Group First UDAF의 최종 결과
         */
        public String terminate() {
            return data.get(0);
        }
    }

}