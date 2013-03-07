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
package org.openflamingo.mapreduce.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AprioriCombinator {

    private double[] items;

    private int combination;

    private int minSupport;

    public AprioriCombinator(double[] items, int combination, int minSupport) {
        this.items = combination == 1 ? items : init(items, minSupport);
        this.minSupport = minSupport;
        this.combination = combination;
    }

    private double[] init(double[] items, int minSupport) {
        double[] k1Items = new double[items.length];
        int index = 0;
        for (double item : items) {
            k1Items[index++] = item;
        }
        return Arrays.copyOfRange(k1Items, 0, index);
    }

    public Iterator<double[]> iterator() {
        return new Combinator();
    }

    private class Combinator implements Iterator<double[]> {

        private List<double[]> list;

        private int itemNo;

        private int index;

        private int bound;

        private double[] result;

        public Combinator() {
            list = new ArrayList<double[]>();
            list.add(new double[0]);
            bound = list.size();
        }

        public boolean hasNext() {
            return (result = getNext()) != null;
        }

        public double[] next() {
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private double[] getNext() {
            while (itemNo < items.length) {
                double head = items[itemNo];
                double[] body = list.get(index);
                double[] merged = merge(head, body);
                if (merged.length < combination) {
                    list.add(merged);
                }
                if (++index == bound) {
                    itemNo++;
                    index = 0;
                    bound = list.size();
                }
                if (merged.length == combination) {
                    return merged;
                }
            }
            return null;
        }

        private double[] merge(double head, double[] body) {
            if (body.length == 0) {
                return new double[]{head};
            }
            double[] merged = new double[body.length + 1];
            System.arraycopy(body, 0, merged, 0, body.length);
            merged[body.length] = head;
            return merged;
        }
    }

    public static void main(String[] args) {
        AprioriCombinator combinator = new AprioriCombinator(new double[]{1, 2, 3, 4, 5}, 2, 0);
        Iterator<double[]> iterator = combinator.iterator();
        while (iterator.hasNext()) {
            double[] next = iterator.next();
            System.out.println(next[0] + "/" + next[1]);
        }
    }
}