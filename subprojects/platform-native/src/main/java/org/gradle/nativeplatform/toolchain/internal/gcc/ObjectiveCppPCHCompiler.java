/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.nativeplatform.toolchain.internal.gcc;

import org.gradle.internal.Transformers;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.nativeplatform.internal.CompilerOutputFileNamingSchemeFactory;
import org.gradle.nativeplatform.toolchain.internal.CommandLineToolContext;
import org.gradle.nativeplatform.toolchain.internal.CommandLineToolInvocationWorker;
import org.gradle.nativeplatform.toolchain.internal.compilespec.ObjectiveCppPCHCompileSpec;

public class ObjectiveCppPCHCompiler extends GccCompatibleNativeCompiler<ObjectiveCppPCHCompileSpec> {
    public ObjectiveCppPCHCompiler(BuildOperationExecutor buildOperationExecutor, CompilerOutputFileNamingSchemeFactory compilerOutputFileNamingSchemeFactory, CommandLineToolInvocationWorker commandLineTool, CommandLineToolContext invocationContext, String objectFileExtension, boolean useCommandFile) {
        super(buildOperationExecutor, compilerOutputFileNamingSchemeFactory, commandLineTool, invocationContext, new ObjectiveCppPCHCompileArgsTransformer(), Transformers.<ObjectiveCppPCHCompileSpec>noOpTransformer(), objectFileExtension, useCommandFile);
    }

    private static class ObjectiveCppPCHCompileArgsTransformer extends GccCompilerArgsTransformer<ObjectiveCppPCHCompileSpec> {
        @Override
        protected String getLanguage() {
            return "objective-c++-header";
        }
    }
}
