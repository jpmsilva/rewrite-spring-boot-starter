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

import static com.github.jpmsilva.groundlevel.utilities.SpringUtilities.getSortedBeansOfType;
import static org.ocpsoft.rewrite.annotation.config.AnnotationConfigProvider.CONFIG_BASE_PACKAGES;

import java.util.List;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.ConfigurationRuleBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.proxy.Proxy;
import org.ocpsoft.rewrite.servlet.impl.RewriteServletContextListener;
import org.ocpsoft.rewrite.servlet.impl.RewriteServletRequestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration class for the <a href="https://www.ocpsoft.org/rewrite/">OCPSoft Rewrite</a> API.
 */
@Configuration
@EnableConfigurationProperties(UrlRewriteProperties.class)
public class UrlRewriteAutoConfiguration {

  private final UrlRewriteProperties urlRewriteProperties;

  /**
   * Creates a new auto-configuration class with the specified properties.
   *
   * @param urlRewriteProperties the properties to use during configuration.
   */
  @Autowired
  public UrlRewriteAutoConfiguration(UrlRewriteProperties urlRewriteProperties) {
    this.urlRewriteProperties = urlRewriteProperties;
  }

  /**
   * Rule provider for rules specified in the application configuration file.
   *
   * @return the {@link ConfigurationBuilder} containing the rules.
   */
  @Bean
  ConfigurationBuilder propertyRules() {
    ConfigurationBuilder builder = ConfigurationRuleBuilder.begin();
    urlRewriteProperties.getForwards().forEach((key, value) ->
        builder
            .addRule()
            .when(Direction.isInbound().and(Path.matches(key)))
            .perform(Forward.to(value))
            .addRule()
            .when(Direction.isInbound().and(Path.matches(key + "/{path}")))
            .perform(Forward.to(value + "/{path}"))
            .where("path").matches(".*"));

    urlRewriteProperties.getProxies().forEach((key, value) ->
        builder
            .addRule()
            .when(Direction.isInbound().and(Path.matches(key)))
            .perform(Proxy.to(value))
            .addRule()
            .when(Direction.isInbound().and(Path.matches(key + "/{path}")))
            .perform(Proxy.to(value + "/{path}"))
            .where("path").matches(".*"));

    urlRewriteProperties.getPermanentRedirects().forEach((key, value) ->
        builder
            .addRule()
            .when(Direction.isInbound().and(Path.matches(key)))
            .perform(Redirect.permanent(value))
            .addRule()
            .when(Direction.isInbound().and(Path.matches(key + "/{path}")))
            .perform(Redirect.permanent(value + "/{path}"))
            .where("path").matches(".*"));

    urlRewriteProperties.getTemporaryRedirects().forEach((key, value) ->
        builder
            .addRule()
            .when(Direction.isInbound().and(Path.matches(key)))
            .perform(Redirect.temporary(value))
            .addRule()
            .when(Direction.isInbound().and(Path.matches(key + "/{path}")))
            .perform(Redirect.temporary(value + "/{path}"))
            .where("path").matches(".*"));

    return builder;
  }

  /**
   * The {@link RewriteServletRequestListener} provider.
   *
   * @return a new RewriteServletRequestListener.
   */
  @Bean
  ServletListenerRegistrationBean<RewriteServletRequestListener> rewriteServletRequestListener() {
    return new ServletListenerRegistrationBean<>(new RewriteServletRequestListener());
  }

  /**
   * The {@link RewriteServletContextListener} provider.
   *
   * @return a new RewriteServletContextListener.
   */
  @Bean
  ServletListenerRegistrationBean<RewriteServletContextListener> rewriteServletContextListener() {
    return new ServletListenerRegistrationBean<>(new RewriteServletContextListener());
  }

  /**
   * The {@link RewriteFilter} provider.
   *
   * @param servletContext the current {@link ServletContext}, provided by Spring Boot.
   * @param applicationContext the current {@link ApplicationContext}, provided by Spring Boot.
   * @return a new RewriteFilter.
   */
  @Bean
  FilterRegistrationBean<RewriteFilter> rewriteFilter(ServletContext servletContext, ApplicationContext applicationContext) {
    List<ConfigurationBuilder> builders = getSortedBeansOfType(applicationContext, ConfigurationBuilder.class);
    servletContext.setInitParameter(CONFIG_BASE_PACKAGES, "none");
    servletContext.setAttribute(SpringConfigurationProvider.SERVLET_CONFIG_ATTRIBUTE, builders);

    FilterRegistrationBean<RewriteFilter> rewriteFilter = new FilterRegistrationBean<>();
    rewriteFilter.setFilter(new RewriteFilter());
    rewriteFilter.addUrlPatterns("/*");
    rewriteFilter.setOrder(urlRewriteProperties.getFilterOrder());

    return rewriteFilter;
  }
}
