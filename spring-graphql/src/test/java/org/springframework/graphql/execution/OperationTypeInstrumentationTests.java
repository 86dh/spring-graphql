/*
 * Copyright 2026-present the original author or authors.
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
 */

package org.springframework.graphql.execution;


import java.util.Set;

import graphql.language.OperationDefinition;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import org.springframework.graphql.BookSource;
import org.springframework.graphql.ExecutionGraphQlRequest;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.GraphQlSetup;
import org.springframework.graphql.ResponseHelper;
import org.springframework.graphql.TestExecutionRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OperationTypeInstrumentation}.
 * @author Brian Clozel
 */
class OperationTypeInstrumentationTests {

	private final OperationTypeInstrumentation instrumentation = new OperationTypeInstrumentation();

	private final GraphQlSetup graphQlSetup = GraphQlSetup.schemaResource(BookSource.schema).instrumentation(this.instrumentation);

	@Test
	void allOperationTypesAllowedByDefault() {
		String document = """
				{
					bookById(id: 1) {
						name
					}
				}
				""";
		ExecutionGraphQlRequest request = TestExecutionRequest.forDocument(document);
		Mono<ExecutionGraphQlResponse> responseMono = graphQlSetup
				.queryFetcher("bookById", env -> BookSource.getBookWithoutAuthor(1L))
				.toGraphQlService()
				.execute(request);
		ResponseHelper response = ResponseHelper.forResponse(responseMono);
		assertThat(response.errorCount()).isZero();
	}

	@Test
	void rejectsWhenOperationNotAllowed() {
		String document = """
				{
					bookById(id: 1) {
						name
					}
				}
				""";
		ExecutionGraphQlRequest request = TestExecutionRequest.forDocument(document);
		request.allowedOperations(Set.of(OperationDefinition.Operation.MUTATION, OperationDefinition.Operation.SUBSCRIPTION));
		Mono<ExecutionGraphQlResponse> responseMono = graphQlSetup
				.queryFetcher("bookById", env -> BookSource.getBookWithoutAuthor(1L))
				.toGraphQlService()
				.execute(request);
		ResponseHelper response = ResponseHelper.forResponse(responseMono);
		assertThat(response.errorCount()).isEqualTo(1);
		ResponseHelper.Error error = response.error(0);
		assertThat(error.errorType()).isEqualTo("OperationNotSupported");
		assertThat(error.message()).isEqualTo("Operation type 'QUERY' is not allowed for this request");
	}

}
