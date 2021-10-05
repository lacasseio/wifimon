package dev.nokee.sample

import org.gradle.api.Plugin
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.initialization.Settings

class ConsoleKitBuildAdapterPlugin implements Plugin<Settings> {
	void apply(Settings settings) {
		settings.rootProject.name = 'ConsoleKit'
		settings.gradle.rootProject {
			group = 'ConsoleKit'

			// We compile using the Xcode project as SwiftPM doesn't allow to build the end target directly
			def xcodeProjectTask = tasks.register('generateXcode', SwiftPMGenerateXcodeProject) {
				swiftPmBuildFile = project.file('Package.swift')
				projectLocation = file('console-kit.xcodeproj')
			}

			def buildTask = tasks.register('build', Xcodebuild) {
				it.projectLocation = xcodeProjectTask.flatMap { it.projectLocation }
				it.configuration = 'Release'
				it.target = 'ConsoleKit'
				it.buildDirectory = file('build/Release')
			}

			// Declare what is needed for compilation
			configurations.create('apiElements') {
				canBeConsumed = true
				canBeResolved = false
				attributes {
					attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage, Usage.SWIFT_API))
					attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements, 'framework-bundle'))
				}
				outgoing.artifact(buildTask.flatMap { it.buildDirectory.dir('ConsoleKit.framework') }) {
					type = 'framework'
				}
			}

			// Declare what is needed for linking
			configurations.create('linkElements') {
				canBeConsumed = true
				canBeResolved = false
				attributes {
					attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage, Usage.NATIVE_LINK))
					attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements, 'framework-bundle'))
				}
				outgoing.artifact(buildTask.flatMap { it.buildDirectory.dir('ConsoleKit.framework') }) {
					type = 'framework'
				}
			}

			// Required for runtime dependencies but not currently in use by Nokee
			configurations.create('runtimeElements') {
				canBeConsumed = true
				canBeResolved = false
				attributes {
					attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage, Usage.NATIVE_RUNTIME))
				}
			}
		}
	}
}
