# API

This project allows to create a Minestom server easily in Kotlin. It provides a lot of features to create a Minecraft server.

## Tools

The server is built using [Kotlin](https://kotlinlang.org/) and [Minestom](https://github.com/Minestom/Minestom).
The version of tools used is specified in the [build.gradle.kts](build.gradle.kts) file.

The API doesn't have an entrypoint.

## Installation

You can find the artifact on [Jitpack](https://jitpack.io/#UniverseProject/data-service).
Use the version you prefer by following the tutorial on jitpack and replacing `{version}` bellow.

### Gradle (groovy)

```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
  api("com.github.Rushyverse:api:{version}")
}
```

### Gradle (kotlin)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
  api("com.github.Rushyverse:api:{version}")
}
```

### Maven

```xml
<project>
    <repositories>
        <repository>
            <id>jitpack</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.Rushyverse</groupId>
            <artifactId>api</artifactId>
            <version>{version}</version>
        </dependency>
    </dependencies>
</project>
```

### Local modification

Firstly, you should modify the version of the project in [gradle.properties](gradle.properties) file by changing the `version` property.

If you want to modify the API locally and use it in your project, you need to publish it in local repository with the following command:

```bash
gradle publishApiPublicationToMavenLocal
# or
gradlew publishApiPublicationToMavenLocal
```

When the publication is done, you can use the API in your project using the following configuration :

**Replace `{version}` by the version in the [gradle.properties](gradle.properties) file used during publish.**

```groovy
repositories {
    mavenLocal()
}

dependencies {
  api("com.github.Rushyverse:api:{version}")
}
```

### Gradle (kotlin)

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    api("com.github.Rushyverse:api:{version}")
}
```

### Maven

```xml
<project>
    <repositories>
        <repository>
            <id>mavenLocal</id>
            <url>file://${user.home}/.m2/repository</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.Rushyverse</groupId>
            <artifactId>api</artifactId>
            <version>{version}</version>
        </dependency>
    </dependencies>
</project>
```

## Usage

### Create a server

To create a server, you need to create a class that extends `RushyServer` and override the `start` method 
and configuration classes to load the configuration of the server.

***Configuration classes***

The interfaces for configuration are located in this [package](src/main/kotlin/io/github/rushyverse/api/configuration/IConfiguration.kt).

```kotlin
import io.github.rushyverse.api.configuration.IConfiguration
import io.github.rushyverse.api.configuration.IServerConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyConfiguration(
    @SerialName("server")
    override val server: MyServerConfiguration
) : IConfiguration

@Serializable
data class MyServerConfiguration(
    override val port: Int,
    override val world: String
) : IServerConfiguration
```

If when you start your application, you have an error like this:

**Your current kotlinx.serialization core version is too low, while current Kotlin compiler plugin 1.7.22 requires at least 1.0-M1-SNAPSHOT. Please update your kotlinx.serialization runtime dependency.**

You need to suppress the warning by adding `@Suppress("PROVIDED_RUNTIME_TOO_LOW")` on the class.

```kotlin
@Suppress("PROVIDED_RUNTIME_TOO_LOW") // https://github.com/Kotlin/kotlinx.serialization/issues/993
@Serializable
data class MyConfiguration(
    @SerialName("server")
    override val server: MyServerConfiguration
) : IConfiguration
```

***Server class***
```kotlin
import io.github.rushyverse.api.RushyServer

class MyServer(private val configurationPath: String?) : RushyServer() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            MyServer(args.firstOrNull()).start()
        }
    }
    
    override fun start() {
        start<MyConfiguration>(configurationPath) { 
            // this = MyConfiguration
            // it = InstanceContainer
            // Configure your server here
        }
    }
}
```

### Configuration

The function `start` takes a configuration class as parameter. So you need to define a configuration file in the `resources` folder.
The configuration file should be named `server.conf`.

According to the configuration class above (`MyConfiguration`), the configuration file should have the following content:

```hocon
server {
  port = 25565
  world = "world"
}
```

If in the working directory (where you launch the server) the configuration file will be created if it doesn't exist.
However, the world folder must be added manually.

_The method `start` takes a string as parameter. This string is the path to the configuration file. If the string is null, the configuration file will be searched in the working directory._

### Commands

In `RushyServer` class, the method `registerCommands` can be used to register the [implemented commands](src/main/kotlin/io/github/rushyverse/api/command) from API.

```kotlin
import io.github.rushyverse.api.RushyServer

class MyServer : RushyServer() {

    override fun start() {
        start<MyConfiguration>(configurationPath) {
            registerCommands()
        }
    }

    override fun registerCommands(manager: CommandManager) {
        super.registerCommands(manager) // register API commands
        manager.register(MyCommand())
    }
}
```

### Translation

The `RushyServer` class offers a method to create [TranslationsProvider](src/main/kotlin/io/github/rushyverse/api/translation/TranslationsProvider.kt) instance using [resource bundle files](src/main/resources/api.properties).

If you want, you can override the method `createTranslationsProvider` to create your own instance of `TranslationsProvider`.

```kotlin
import io.github.rushyverse.api.RushyServer

class MyServer : RushyServer() {

    override fun start() {
        start<MyConfiguration>(configurationPath) {
            // Register "myBundle" resource bundle and the API resource bundle
            val translationsProvider = createTranslationsProvider(listOf("myBundle"))
            // Get the value of "myKey" for english language
            println(translationsProvider.translate("myKey", SupportedLanguage.ENGLISH.locale, "myBundle"))
            // Get the value of "myKey2" for english language with parameters
            println(translationsProvider.translate("myKey2", SupportedLanguage.ENGLISH.locale, "myBundle", arrayOf("myValue")))
        }
    }
}
```

## Build

To build the project, you need to use the gradle app ([gradlew.bat](gradlew.bat) for windows
and [gradlew](gradlew) for linux).
`gradlew` is a wrapper to run gradle command without install it on our computer.

```shell
gradlew shadowJar
```

The jar will be created in the [dedicated folder](build/libs).

### Test

The code is tested using JUnit and can be executed using the following command:

```bash
gradlew test
```