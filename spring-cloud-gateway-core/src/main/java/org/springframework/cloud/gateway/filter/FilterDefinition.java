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

package org.springframework.cloud.gateway.filter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.validation.annotation.Validated;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * Filter 定义
 * <p>
 * <p>
 * <p>
 * spring:
 * cloud:
 * gateway:
 * routes:
 * - id: add_request_header_route
 * uri: http://example.org
 * filters:
 * - AddRequestHeader=X-Request-Foo, Bar # ①
 * <p>
 * AddRequestHeader，对应 FilterDefinition 中的 name 属性。
 * AddRequestHeader为AddRequestHeaderGatewayFilterFactory 的类名前缀。
 * <p>
 * <p>
 * X-Request-Foo, Bar ，会被解析成 FilterDefinition 中的 Map 类型属性 args。
 * 此处会被解析成两组键值对，以英文逗号将=后面的字符串分隔成数组，key是固定字符串 _genkey_ + 数组元素下标，
 * value为数组元素自身。
 *
 * @author Spencer Gibb
 */
@Validated
public class FilterDefinition {

	/**
	 * 定义了 Filter 的名称，符合特定的命名规范，为对应的工厂名前缀
	 */
	@NotNull
	private String name;

	/**
	 * 一个键值对参数用于构造 Filter 对象
	 */
	private Map<String, String> args = new LinkedHashMap<>();

	public FilterDefinition() {
	}

	/**
	 * @param text 参数，格式为 ${name}=${args[0]},${args[1]}...${args[n]} 。举个例子,
	 *             "AddRequestParameter=foo, bar"
	 */
	public FilterDefinition(String text) {
		int eqIdx = text.indexOf('=');
		if (eqIdx <= 0) {
			setName(text);
			return;
		}
		setName(text.substring(0, eqIdx));

		String[] args = tokenizeToStringArray(text.substring(eqIdx + 1), ",");

		for (int i = 0; i < args.length; i++) {
			this.args.put(NameUtils.generateName(i), args[i]);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getArgs() {
		return args;
	}

	public void setArgs(Map<String, String> args) {
		this.args = args;
	}

	public void addArg(String key, String value) {
		this.args.put(key, value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FilterDefinition that = (FilterDefinition) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(args, that.args);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, args);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("FilterDefinition{");
		sb.append("name='").append(name).append('\'');
		sb.append(", args=").append(args);
		sb.append('}');
		return sb.toString();
	}
}
