/*
 * Copyright 2015 NEC Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.o3project.odenos.core.component.network.topology;

import org.o3project.odenos.core.component.network.BaseObjectQuery;

/**
 * Prepares a query for Link.
 *
 */
public class LinkQuery extends BaseObjectQuery<Link> {

  /**
   * Constructor.
   * @param queriesString query string.
   */
  public LinkQuery(String queriesString) {
    super(queriesString);
  }

  /*
   * (non-Javadoc)
   * @see org.o3project.odenos.core.component.network.BaseObjectQuery#parse()
   */
  @Override
  public boolean parse() {
    if (!super.parse()) {
      return false;
    }
    if (this.queries.size() != 0) {
      return false;
    }
    if (this.attributes == null) {
      return false;
    }
    return true;
  }
}
