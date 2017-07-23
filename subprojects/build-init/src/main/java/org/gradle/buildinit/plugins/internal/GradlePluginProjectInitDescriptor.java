/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.buildinit.plugins.internal;

import org.gradle.api.internal.DocumentationRegistry;
import org.gradle.api.internal.file.FileResolver;

public class GradlePluginProjectInitDescriptor extends GroovyProjectInitDescriptor {

    public GradlePluginProjectInitDescriptor(TemplateOperationFactory templateOperationFactory, FileResolver fileResolver, TemplateLibraryVersionProvider libraryVersionProvider, ProjectInitDescriptor globalSettingsDescriptor, DocumentationRegistry documentationRegistry) {
        super(templateOperationFactory, fileResolver, libraryVersionProvider, globalSettingsDescriptor, documentationRegistry);
    }

    @Override
    public void generate(BuildInitTestFramework testFramework) {
        super.generate(testFramework);

        generatePluginDescriptorResourceFile();
    }

    private void generatePluginDescriptorResourceFile() {
        templateOperationFactory.newTemplateOperation()
            .withTemplate("gradleplugin/org.gradle.greetingplugin.properties.template")
            .withTarget("src/main/resources/META-INF/gradle-plugins/org.gradle.greetingplugin.properties")
            .create()
            .generate();
    }

    @Override
    protected TemplateOperation sourceTemplateOperation() {
        return fromClazzTemplate("gradleplugin/org/gradle/GreetingPlugin.groovy.template", "main");
    }

    @Override
    protected TemplateOperation testTemplateOperation(BuildInitTestFramework testFramework) {
        return fromClazzTemplate("gradleplugin/org/gradle/GreetingPluginTest.groovy.template", "test");
    }

    @Override
    protected void configureBuildScript(BuildScriptBuilder buildScriptBuilder) {
        super.configureBuildScript(buildScriptBuilder);

        buildScriptBuilder.plugin("Apply the gradle plugin development plugin", "java-gradle-plugin");
    }
}
