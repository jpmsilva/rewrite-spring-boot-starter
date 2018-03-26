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

import com.github.jpmsilva.groundlevel.utilities.GenericsUtilities;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;

/**
 * Implementation of a {@link HttpConfigurationProvider} that is designed to load rewrite rules from a Spring application. The rules themselves are passed as an
 * attribute of the servlet context.
 *
 * @see UrlRewriteAutoConfiguration
 */
public class SpringConfigurationProvider extends HttpConfigurationProvider {

  /**
   * The named attribute where the rule configuration is stored within the servlet context.
   */
  public static final String SERVLET_CONFIG_ATTRIBUTE = SpringConfigurationProvider.class.getName() + ".builders";

  /**
   * {@inheritDoc}
   */
  @Override
  public Configuration getConfiguration(ServletContext context) {
    List<ConfigurationBuilder> rules = GenericsUtilities.cast(context.getAttribute(SERVLET_CONFIG_ATTRIBUTE));
    ConfigurationBuilder configuration = ConfigurationBuilder.begin();
    rules.stream()
        .map(ConfigurationBuilder::getRules)
        .flatMap(Collection::stream)
        .forEach(configuration::addRule);
    return configuration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int priority() {
    return 0;
  }
}
