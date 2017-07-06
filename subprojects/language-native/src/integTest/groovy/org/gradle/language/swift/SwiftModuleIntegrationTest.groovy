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

package org.gradle.language.swift

import org.gradle.nativeplatform.fixtures.AbstractInstalledToolChainIntegrationSpec
import org.gradle.nativeplatform.fixtures.app.ExeWithLibraryUsingSwiftLibraryHelloWorldApp
import org.gradle.nativeplatform.fixtures.app.SwiftHelloWorldApp
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition

import static org.gradle.util.Matchers.containsText

@Requires(TestPrecondition.SWIFT_SUPPORT)
class SwiftModuleIntegrationTest extends AbstractInstalledToolChainIntegrationSpec {
    def helloWorldApp = new SwiftHelloWorldApp()

    def "build fails when compilation fails"() {
        given:
        buildFile << """
            apply plugin: 'swift-module'
         """

        and:
        helloWorldApp.brokenFile.writeToDir(file("src/main"))

        expect:
        fails "assemble"
        failure.assertHasDescription("Execution failed for task ':compileSwift'.");
        failure.assertHasCause("A build operation failed.")
        failure.assertThatCause(containsText("Swift compiler failed while compiling swift file(s)"))
    }

    def "sources are compiled with Swift compiler"() {
        settingsFile << "rootProject.name = 'hello'"

        given:
        helloWorldApp.librarySources*.writeToDir(file('src/main'))

        and:
        buildFile << """
            apply plugin: 'swift-module'
         """

        expect:
        succeeds "assemble"
        result.assertTasksExecuted(":compileSwift", ":linkMain", ":assemble")
        sharedLibrary("build/lib/hello").assertExists()
    }

    def "can define public module"() {
        settingsFile << "rootProject.name = 'hello'"
        given:
        def app = new SwiftHelloWorldApp()
        app.library.sourceFiles.each { it.writeToFile(file("src/main/swift/$it.name")) }

        and:
        buildFile << """
            apply plugin: 'swift-module'
         """

        expect:
        succeeds "assemble"
        result.assertTasksExecuted(":compileSwift", ":linkMain", ":assemble")
        sharedLibrary("build/lib/hello").assertExists()
        file("build/main/objs/hello.swiftmodule").assertExists()
    }

    def "can compile and link against another library"() {
        settingsFile << "include 'Hello', 'Greeting'"
        def app = new ExeWithLibraryUsingSwiftLibraryHelloWorldApp()

        given:
        buildFile << """
            project(':Hello') {
                apply plugin: 'swift-module'
                dependencies {
                    implementation project(':Greeting')
                }
            }
            project(':Greeting') {
                apply plugin: 'swift-module'
            }
"""
        app.library.sourceFiles.each { it.writeToFile(file("Hello/src/main/swift/$it.name")) }
        app.greetingsLibrary.sourceFiles.each { it.writeToFile(file("Greeting/src/main/swift/$it.name")) }

        expect:
        succeeds ":Hello:assemble"
        result.assertTasksExecuted(":Greeting:compileSwift", ":Greeting:linkMain", ":Hello:compileSwift", ":Hello:linkMain", ":Hello:assemble")
        sharedLibrary("Hello/build/lib/Hello").assertExists()
        sharedLibrary("Greeting/build/lib/Greeting").assertExists()
    }

    def "can change default module name and successfully link against library library"() {
        settingsFile << "include 'lib1', 'lib2'"
        def app = new ExeWithLibraryUsingSwiftLibraryHelloWorldApp()

        given:
        buildFile << """
            project(':lib1') {
                apply plugin: 'swift-module'
                dependencies {
                    implementation project(':lib2')
                }
                tasks.withType(SwiftCompile)*.moduleName = 'Hello'
            }
            project(':lib2') {
                apply plugin: 'swift-module'
                tasks.withType(SwiftCompile)*.moduleName = 'Greeting'
            }
"""
        app.library.sourceFiles.each { it.writeToFile(file("lib1/src/main/swift/$it.name")) }
        app.greetingsLibrary.sourceFiles.each { it.writeToFile(file("lib2/src/main/swift/$it.name")) }

        expect:
        succeeds ":lib1:assemble"
        result.assertTasksExecuted(":lib2:compileSwift", ":lib2:linkMain", ":lib1:compileSwift", ":lib1:linkMain", ":lib1:assemble")
        sharedLibrary("lib1/build/lib/lib1").assertExists()
        sharedLibrary("lib2/build/lib/lib2").assertExists()
    }
}
