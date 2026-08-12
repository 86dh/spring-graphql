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

import graphql.ErrorType;
import graphql.execution.AbortExecutionException;
import graphql.language.OperationDefinition;


/**
 * Indicates that the operation for the current request is not allowed by the semantics of the transport layer.
 * @author Brian Clozel
 * @since 2.1.0
 * @see org.springframework.graphql.support.DefaultExecutionGraphQlRequest#allowedOperations(Set)
 */
@SuppressWarnings("serial")
public class OperationNotAllowedException extends AbortExecutionException {

	private final OperationDefinition.Operation operation;

	private final Set<OperationDefinition.Operation> allowedOperations;

	OperationNotAllowedException(OperationDefinition.Operation operation, Set<OperationDefinition.Operation> allowedOperations) {
		super("Operation type '" + operation + "' is not allowed for this request");
		this.operation = operation;
		this.allowedOperations = allowedOperations;
	}

	/**
	 * Return the operation to perform for the current request.
	 */
	public OperationDefinition.Operation getOperation() {
		return this.operation;
	}

	/**
	 * Return the set of operations that may be executed for this request.
	 */
	public Set<OperationDefinition.Operation> getAllowedOperations() {
		return this.allowedOperations;
	}

	@Override
	public graphql.ErrorType getErrorType() {
		return ErrorType.OperationNotSupported;
	}

}
