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

package io.druid.indexing.common.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Throwables;
import io.druid.indexing.common.TaskLock;
import io.druid.indexing.common.task.Task;
import org.joda.time.Interval;

public class LockAcquireAction implements TaskAction<TaskLock>
{
  @JsonIgnore
  private final Interval interval;

  @JsonCreator
  public LockAcquireAction(
      @JsonProperty("interval") Interval interval
  )
  {
    this.interval = interval;
  }

  @JsonProperty
  public Interval getInterval()
  {
    return interval;
  }

  public TypeReference<TaskLock> getReturnTypeReference()
  {
    return new TypeReference<TaskLock>()
    {
    };
  }

  @Override
  public TaskLock perform(Task task, TaskActionToolbox toolbox)
  {
    try {
      return toolbox.getTaskLockbox().lock(task, interval);
    }
    catch (InterruptedException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public boolean isAudited()
  {
    return false;
  }

  @Override
  public String toString()
  {
    return "LockAcquireAction{" +
           "interval=" + interval +
           '}';
  }
}
