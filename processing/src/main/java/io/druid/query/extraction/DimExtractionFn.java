/*
 * Druid - a distributed column store.
 * Copyright (C) 2012, 2013  Metamarkets Group Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.druid.query.extraction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
    @JsonSubTypes.Type(name = "time", value = TimeDimExtractionFn.class),
    @JsonSubTypes.Type(name = "regex", value = RegexDimExtractionFn.class),
    @JsonSubTypes.Type(name = "partial", value = MatchingDimExtractionFn.class),
    @JsonSubTypes.Type(name = "searchQuery", value = SearchQuerySpecDimExtractionFn.class),
    @JsonSubTypes.Type(name = "javascript", value = JavascriptDimExtractionFn.class)
})
/**
 * A DimExtractionFn is a function that can be used to modify the values of a dimension column.
 *
 * A simple example of the type of operation this enables is the RegexDimExtractionFn which applies a
 * regular expression with a capture group.  When the regular expression matches the value of a dimension,
 * the value captured by the group is used for grouping operations instead of the dimension value.
 */
public interface DimExtractionFn
{
  /**
   * Returns a byte[] unique to all concrete implementations of DimExtractionFn.  This byte[] is used to
   * generate a cache key for the specific query.
   *
   * @return a byte[] unit to all concrete implements of DimExtractionFn
   */
  public byte[] getCacheKey();

  /**
   * The "extraction" function.  This should map a dimension value into some other value.
   *
   * In order to maintain the "null and empty string are equivalent" semantics that Druid provides, the
   * empty string is considered invalid output for this method and should instead return null.  This is
   * a contract on the method rather than enforced at a lower level in order to eliminate a global check
   * for extraction functions that do not already need one.
   *
   *
   * @param dimValue the original value of the dimension
   * @return a value that should be used instead of the original
   */
  public String apply(String dimValue);

  /**
   * Offers information on whether the extraction will preserve the original ordering of the values.
   *
   * Some optimizations of queries is possible if ordering is preserved.  Null values *do* count towards
   * ordering.
   *
   * @return true if ordering is preserved, false otherwise
   */
  public boolean preservesOrdering();
}
