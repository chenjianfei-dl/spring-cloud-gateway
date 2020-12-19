/*
 * Copyright 2013-2018 the original author or authors.
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

package org.springframework.cloud.gateway.support;

import java.util.function.Function;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

/**
 * Mock implementation of {@link ClientHttpRequest}.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class CachedBodyOutputMessage implements ReactiveHttpOutputMessage {

	private final DataBufferFactory bufferFactory;
	private final HttpHeaders httpHeaders;

	private Flux<DataBuffer> body = Flux.error(
			new IllegalStateException("The body is not set. " +
					"Did handling complete with success? Is a custom \"writeHandler\" configured?"));

	private Function<Flux<DataBuffer>, Mono<Void>> writeHandler = initDefaultWriteHandler();

	public CachedBodyOutputMessage(ServerWebExchange exchange, HttpHeaders httpHeaders) {
		this.bufferFactory = exchange.getResponse().bufferFactory();
		this.httpHeaders = httpHeaders;
	}

	@Override
	public void beforeCommit(Supplier<? extends Mono<Void>> action) {

	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.httpHeaders;
	}

	private Function<Flux<DataBuffer>, Mono<Void>> initDefaultWriteHandler() {
		return body -> {
			this.body = body.cache();
			return this.body.then();
		};
	}

	@Override
	public DataBufferFactory bufferFactory() {
		return this.bufferFactory;
	}

	/**
	 * Return the request body, or an error stream if the body was never set
	 * or when {@link #setWriteHandler} is configured.
	 */
	public Flux<DataBuffer> getBody() {
		return this.body;
	}

	/**
	 * Configure a custom handler for writing the request body.
	 *
	 * <p>The default write handler consumes and caches the request body so it
	 * may be accessed subsequently, e.g. in test assertions. Use this property
	 * when the request body is an infinite stream.
	 *
	 * @param writeHandler the write handler to use returning {@code Mono<Void>}
	 *                     when the body has been "written" (i.e. consumed).
	 */
	public void setWriteHandler(Function<Flux<DataBuffer>, Mono<Void>> writeHandler) {
		Assert.notNull(writeHandler, "'writeHandler' is required");
		this.writeHandler = writeHandler;
	}

	@Override
	public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
		return Mono.defer(() -> this.writeHandler.apply(Flux.from(body)));
	}

	@Override
	public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
		return writeWith(Flux.from(body).flatMap(p -> p));
	}

	@Override
	public Mono<Void> setComplete() {
		return writeWith(Flux.empty());
	}

}
