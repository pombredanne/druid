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

package io.druid.collections;

import java.util.Iterator;

/**
*/
public class IteratorShell<T> implements Iterator<T>
{
  private final Iterator<T> base;

  IteratorShell(
      Iterator<T> base
  )
  {
    this.base = base;
  }

  @Override
  public boolean hasNext()
  {
    return base.hasNext();
  }

  @Override
  public T next()
  {
    return base.next();
  }

  @Override
  public void remove()
  {
    base.remove();
  }
}
