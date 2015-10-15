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

package io.druid.query;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.metamx.common.ISE;
import com.metamx.common.guava.Sequence;
import com.metamx.common.guava.Sequences;
import io.druid.query.aggregation.MetricManipulationFn;
import io.druid.query.aggregation.MetricManipulatorFns;

import java.util.Map;

/**
 */
public class FinalizeResultsQueryRunner<T> implements QueryRunner<T>
{
  private final QueryRunner<T> baseRunner;
  private final QueryToolChest<T, Query<T>> toolChest;

  public FinalizeResultsQueryRunner(
      QueryRunner<T> baseRunner,
      QueryToolChest<T, Query<T>> toolChest
  )
  {
    this.baseRunner = baseRunner;
    this.toolChest = toolChest;
  }

  @Override
  public Sequence<T> run(final Query<T> query, Map<String, Object> responseContext)
  {
    final boolean isBySegment = query.getContextBySegment(false);
    final boolean shouldFinalize = query.getContextFinalize(true);

    final Query<T> queryToRun;
    final Function<T, T> finalizerFn;
    final MetricManipulationFn metricManipulationFn;

    if (shouldFinalize) {
      queryToRun = query.withOverriddenContext(ImmutableMap.<String, Object>of("finalize", false));
      metricManipulationFn = MetricManipulatorFns.finalizing();

    } else {
      queryToRun = query;
      metricManipulationFn = MetricManipulatorFns.identity();
    }
    if (isBySegment) {
      finalizerFn = new Function<T, T>()
      {
        final Function<T, T> baseFinalizer = toolChest.makePostComputeManipulatorFn(
            query,
            metricManipulationFn
        );

        @Override
        @SuppressWarnings("unchecked")
        public T apply(T input)
        {
          Result<BySegmentResultValueClass<T>> result = (Result<BySegmentResultValueClass<T>>) input;

          if (input == null) {
            throw new ISE("Cannot have a null result!");
          }

          BySegmentResultValue<T> resultsClass = result.getValue();

          return (T) new Result<BySegmentResultValueClass>(
              result.getTimestamp(),
              new BySegmentResultValueClass(
                  Lists.transform(resultsClass.getResults(), baseFinalizer),
                  resultsClass.getSegmentId(),
                  resultsClass.getInterval()
              )
          );
        }
      };
    } else {
      finalizerFn = toolChest.makePostComputeManipulatorFn(query, metricManipulationFn);
    }


    return Sequences.map(
        baseRunner.run(queryToRun, responseContext),
        finalizerFn
    );

  }
}
