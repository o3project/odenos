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

package org.o3project.odenos.component.federator;

/**
 * Exception of Federator.
 *
 */
@SuppressWarnings("serial")
public class FederatorException extends Exception {

  /**
   * Constructors.
   */
  public FederatorException() {
    super();
  }

  /**
   * Constructors with messages.
   * @param message detail message
   */
  public FederatorException(String message) {
    super(message);
  }

  /**
   * Constructors with messages and cause.
   * @param message detail message
   * @param cause cause
   */
  public FederatorException(String message, Throwable cause) {
    super(message, cause);
  }

}
