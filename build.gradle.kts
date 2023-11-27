import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    embeddedKotlin("jvm")
    embeddedKotlin("plugin.serialization")
    id("org.jetbrains.dokka") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    `maven-publish`
    `java-library`
    jacoco
}

detekt {
    // Allows having different behavior for CI.
    // When building a branch, we want to fail the build if detekt fails.
    // When building a PR, we want to ignore failures to report them in sonar.
    val envIgnoreFailures = System.getenv("DETEKT_IGNORE_FAILURES")?.toBooleanStrictOrNull() ?: false
    ignoreFailures = envIgnoreFailures

    config.from(file("config/detekt/detekt.yml"))
}

jacoco {
    reportsDirectory.set(file("${layout.buildDirectory.get()}/reports/jacoco"))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    val kotlinSerializableVersion = "1.6.0"
    val kamlVersion = "0.55.0"
    val coroutineVersion = "1.6.4"
    val loggingVersion = "3.0.5"
    val koinVersion = "3.4.3"
    val mccoroutineVersion = "2.13.0"
    val paperVersion = "1.20-R0.1-SNAPSHOT"
    val mockBukkitVersion = "3.19.1"
    val junitVersion = "5.10.0"
    val mockkVersion = "1.12.5"
    val fastboardVersion = "2.0.0"
    val kotestVersion = "5.6.2"
    val icu4jVersion = "73.2"

    api(kotlin("stdlib"))
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinSerializableVersion")
    api("com.charleskorn.kaml:kaml:$kamlVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutineVersion")
    api("io.github.microutils:kotlin-logging:$loggingVersion")

    // Plural translation
    api("com.ibm.icu:icu4j:$icu4jVersion")

    // Injection framework
    api("io.insert-koin:koin-core:$koinVersion")
    api("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // MC coroutine framework
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:$mccoroutineVersion")
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccoroutineVersion")

    // Minecraft server framework
    "io.papermc.paper:paper-api:$paperVersion".let {
        compileOnly(it)
        testImplementation(it)
    }

    // Scoreboard framework
    api("fr.mrmicky:fastboard:$fastboardVersion")

    api("com.github.Rushyverse:core:6ae31a9250")

    // Tests
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:$mockBukkitVersion")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion") {
        exclude("org.jetbrains.kotlin", "kotlin-test-junit")
    }
    testImplementation("io.mockk:mockk:$mockkVersion")
}

val javaVersion get() = JavaVersion.VERSION_17
val javaVersionString get() = javaVersion.toString()
val javaVersionInt get() = javaVersionString.toInt()

kotlin {
    explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
    jvmToolchain(javaVersionInt)

    sourceSets {
        compilerOptions {
            freeCompilerArgs = listOf("-Xcontext-receivers")
        }

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
        kotlinOptions.jvmTarget = javaVersionString
    }

    withType<JavaCompile> {
        sourceCompatibility = javaVersionString
        targetCompatibility = javaVersionString
    }

    test {
        useJUnitPlatform()
    }

    clean {
        delete(dokkaOutputDir)
    }

    dokkaHtml.configure {
        // CompileJava should be executed to build library in Jitpack
        dependsOn(deleteDokkaOutputDir, compileJava.get())
        outputDirectory.set(file(dokkaOutputDir))
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    group = "documentation"
    delete(dokkaOutputDir)
}

val sourcesJar by tasks.registering(Jar::class) {
    group = "build"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    group = "documentation"
    dependsOn(tasks.dokkaHtml)
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
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
                        url.set("https://mit-license.org/")
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
