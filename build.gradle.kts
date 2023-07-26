plugins {
    embeddedKotlin("jvm")
    embeddedKotlin("plugin.serialization")
    id("org.jetbrains.dokka") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    val kotlinSerializableVersion = "1.5.1"
    val kamlVersion = "0.53.0"
    val coroutineVersion = "1.6.4"
    val loggingVersion = "2.1.23"
    val koinVersion = "3.2.0"
    val mccoroutineVersion = "2.4.0"
    val paperVersion = "1.19-R0.1-SNAPSHOT"
    val junitVersion = "5.9.0"
    val mockkVersion = "1.12.5"
    val slf4jVersion = "2.0.0-alpha6"
    val fastboardVersion = "2.0.0"
    val commandApiVersion = "9.0.3"

    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinSerializableVersion")
    implementation("com.charleskorn.kaml:kaml:$kamlVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutineVersion")
    implementation("io.github.microutils:kotlin-logging:$loggingVersion")

    // Injection framework
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // MC coroutine framework
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:$mccoroutineVersion")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccoroutineVersion")

    // Minecraft server framework
    compileOnly("io.papermc.paper:paper-api:$paperVersion")

    // Scoreboard framework
    implementation("fr.mrmicky:fastboard:$fastboardVersion")

    // CommandAPI framework
    // The CommandAPI dependency used for Bukkit and it's forks
    api("dev.jorel:commandapi-bukkit-core:$commandApiVersion")
    // Due to all functions available in the kotlindsl being inlined, we only need this dependency at compile-time
    api("dev.jorel:commandapi-bukkit-kotlin:$commandApiVersion")

    api("com.github.Rushyverse:core:6ae31a9250")

    // Tests
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    testImplementation("io.papermc.paper:paper-api:$paperVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion") {
        exclude("org.jetbrains.kotlin", "kotlin-test-junit")
    }
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.slf4j:slf4j-api:$slf4jVersion")
    testImplementation("org.slf4j:slf4j-simple:$slf4jVersion")
}

kotlin {
    explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }
}

val dokkaOutputDir = "${rootProject.projectDir}/dokka"

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }

    clean {
        delete(dokkaOutputDir)
    }

    dokkaHtml.configure {
        outputDirectory.set(file(dokkaOutputDir))
    }

    shadowJar {
        archiveClassifier.set("")
    }
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

publishing {
    publications {
        val projectOrganizationPath = "Rushyverse/${project.name}"
        val projectGitUrl = "https://github.com/$projectOrganizationPath"

        create<MavenPublication>(project.name) {
            shadow.component(this)
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name.set(project.name)
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
