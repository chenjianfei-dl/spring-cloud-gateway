/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.cloud.gateway.handler.predicate;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.web.server.ServerWebExchange;

import static org.springframework.cloud.gateway.handler.predicate.BetweenRoutePredicateFactory.getZonedDateTime;

/**
 * @author Spencer Gibb
 */
//声明了范型，即使用到的配置类为 AfterRoutePredicateFactory 中定义的内部类 Config
public class AfterRoutePredicateFactory extends AbstractRoutePredicateFactory<AfterRoutePredicateFactory.Config> {

	public static final String DATETIME_KEY = "datetime";

	public AfterRoutePredicateFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Collections.singletonList(DATETIME_KEY);
	}

	/**
	 * 生产 Predicate 对象，逻辑是判断当前时间（执行时）是否在 Config 中指定的 datetime之后
	 *
	 * @param config
	 * @return
	 */
	@Override
	public Predicate<ServerWebExchange> apply(Config config) {
		ZonedDateTime datetime = getZonedDateTime(config.getDatetime());
		return exchange -> {
			final ZonedDateTime now = ZonedDateTime.now();
			return now.isAfter(datetime);
		};
	}

	/**
	 * 该配置类只包含一个datetime时间字符串属性
	 */
	public static class Config {
		private String datetime;

		public String getDatetime() {
			return datetime;
		}

		public void setDatetime(String datetime) {
			this.datetime = datetime;
		}
	}

}
