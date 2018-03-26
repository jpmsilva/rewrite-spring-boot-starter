/*
 * Copyright 2018 Joao Silva
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

package com.github.jpmsilva.rewritespringbootstarter;

import java.util.Map;
import java.util.TreeMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@ConfigurationProperties(prefix = "rewrite")
public class UrlRewriteProperties {

  /**
   * Somewhere between the default (0) and the security filter (-100).
   */
  public static final int DEFAULT_FILTER_ORDER = FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER
      - 50;

  private int filterOrder = DEFAULT_FILTER_ORDER;

  private Map<String, String> forwards = new TreeMap<>();

  private Map<String, String> permanentRedirects = new TreeMap<>();

  private Map<String, String> temporaryRedirects = new TreeMap<>();

  private Map<String, String> proxies = new TreeMap<>();

  /**
   * URL Rewrite filter chain order, in relation to other servlet filters deployed.
   *
   * @return the filter order.
   */
  public int getFilterOrder() {
    return filterOrder;
  }

  public void setFilterOrder(int filterOrder) {
    this.filterOrder = filterOrder;
  }

  /**
   * Rules for forwarding requests (that means that URL changes will not be visible in the client's browser.
   *
   * @return a map containing as keys the source contexts and as values the destination contexts.
   */
  public Map<String, String> getForwards() {
    return forwards;
  }

  /**
   * Rules for redirects with code 301.
   *
   * @return a map containing as keys the source contexts and as values the destination contexts.
   */
  public Map<String, String> getPermanentRedirects() {
    return permanentRedirects;
  }

  /**
   * Rules for redirects with code 300.
   *
   * @return a map containing as keys the source contexts and as values the destination contexts.
   */
  public Map<String, String> getTemporaryRedirects() {
    return temporaryRedirects;
  }

  /**
   * Rules for serving requests from a remote HTTP/HTTPS server.
   *
   * @return a map containing as keys the source contexts and as values the destination URLs.
   */
  public Map<String, String> getProxies() {
    return proxies;
  }
}
