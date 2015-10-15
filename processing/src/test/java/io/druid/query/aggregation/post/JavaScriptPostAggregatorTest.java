/*
 * Druid - a distributed column store.
 * Copyright 2012 - 2015 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.druid.query.aggregation.post;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class JavaScriptPostAggregatorTest
{
  @Test
    public void testCompute()
    {
      JavaScriptPostAggregator javaScriptPostAggregator;

      Map<String, Object> metricValues = Maps.newHashMap();
      metricValues.put("delta", -10.0);
      metricValues.put("total", 100.0);


      String absPercentFunction = "function(delta, total) { return 100 * Math.abs(delta) / total; }";
      javaScriptPostAggregator = new JavaScriptPostAggregator("absPercent", Lists.newArrayList("delta", "total"), absPercentFunction);

      Assert.assertEquals(10.0, javaScriptPostAggregator.compute(metricValues));
    }
}
