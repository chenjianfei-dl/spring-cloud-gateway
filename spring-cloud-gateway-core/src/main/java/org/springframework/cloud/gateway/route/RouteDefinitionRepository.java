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

package org.springframework.cloud.gateway.route;

/**
 * 实现从存储器( 例如，内存 / Redis / MySQL 等 )读取、保存、删除路由配置
 *
 * 目前 Spring Cloud Gateway 实现了基于内存为存储器的 InMemoryRouteDefinitionRepository
 *
 * @author Spencer Gibb
 */
public interface RouteDefinitionRepository extends RouteDefinitionLocator, RouteDefinitionWriter {
}
