import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("org.jetbrains.dokka") version "1.8.10"
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    val minestomVersion = "aebf72de90"
    val loggingVersion = "3.0.5"
    val mockkVersion = "1.13.4"
    val coroutinesVersion = "1.6.4"
    val kotlinSerializationVersion = "1.5.0"
    val commonsNetVersion = "3.9.0"
    val icu4jVersion = "72.1"
    val minimessageVersion = "4.14.0"

    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinSerializationVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-hocon:$kotlinSerializationVersion")

    api("com.github.Minestom.Minestom:Minestom:$minestomVersion")
    api("commons-net:commons-net:$commonsNetVersion")
    api("com.ibm.icu:icu4j:$icu4jVersion")
    api("net.kyori:adventure-text-minimessage:$minimessageVersion")

    // Logging information
    api("io.github.microutils:kotlin-logging:$loggingVersion")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.github.Minestom.Minestom:testing:$minestomVersion")
}

kotlin {
    explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }
}

val dokkaOutputDir = "${rootProject.projectDir}/dokka"

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

    test {
        useJUnitPlatform()
    }

    clean {
        delete(dokkaOutputDir)
    }

    val deleteDokkaOutputDir by register<Delete>("deleteDokkaOutputDirectory") {
        group = "documentation"
        delete(dokkaOutputDir)
    }

    dokkaHtml.configure {
        dependsOn(deleteDokkaOutputDir)
        outputDirectory.set(file(dokkaOutputDir))
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    group = "build"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    group = "documentation"
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

publishing {
    val projectName = project.name

    publications {
        val projectOrganizationPath = "Rushyverse/$projectName"
        val projectGitUrl = "https://github.com/$projectOrganizationPath"

        create<MavenPublication>(projectName) {
            from(components["kotlin"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name.set(projectName)
                description.set(project.description)
                url.set(projectGitUrl)

                issueManagement {
                    system.set("GitHub")
                    url.set("$projectGitUrl/issues")
                }

                ciManagement {
                    system.set("GitHub Actions")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://mit-license.org")
                    }
                }

                developers {
                    developer {
                        name.set("Quentixx")
                        email.set("Quentixx@outlook.fr")
                        url.set("https://github.com/Quentixx")
                    }
                    developer {
                        name.set("Distractic")
                        email.set("Distractic@outlook.fr")
                        url.set("https://github.com/Distractic")
                    }
                }

                scm {
                    connection.set("scm:git:$projectGitUrl.git")
                    developerConnection.set("scm:git:git@github.com:$projectOrganizationPath.git")
                    url.set(projectGitUrl)
                }

                distributionManagement {
                    downloadUrl.set("$projectGitUrl/releases")
                }
            }
        }
    }
}
