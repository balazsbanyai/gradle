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

package org.gradle.buildinit.plugins

import org.gradle.buildinit.plugins.fixtures.WrapperTestFixture
import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.DefaultTestExecutionResult
import org.gradle.integtests.fixtures.TestExecutionResult;

class GradlePluginIntegrationTest extends AbstractIntegrationSpec {

    public static final String SAMPLE_PLUGIN_CLASS = "src/main/groovy/org/gradle/GreetingPlugin.groovy"
    public static final String SAMPLE_PLUGIN_TEST_CLASS = "src/test/groovy/org/gradle/GreetingPluginTest.groovy"
    public static final String SAMPLE_PLUGIN_DESCRIPTOR = "src/main/resources/META-INF/gradle-plugins/org.gradle.greetingplugin.properties"
    final wrapper = new WrapperTestFixture(testDirectory)

    def "creates sample source if no source present"() {
        when:
        succeeds('init', '--type', 'gradle-plugin')

        then:
        file(SAMPLE_PLUGIN_CLASS).exists()
        file(SAMPLE_PLUGIN_TEST_CLASS).exists()
        file(SAMPLE_PLUGIN_DESCRIPTOR).exists()
        buildFile.exists()
        settingsFile.exists()
        wrapper.generated()

        when:
        succeeds("build")

        then:
        TestExecutionResult testResult = new DefaultTestExecutionResult(testDirectory)
        testResult.assertTestClassesExecuted("org.gradle.GreetingPluginTest")
        testResult.testClass("org.gradle.GreetingPluginTest").assertTestPassed("plugin can be applied to project")
    }


}
