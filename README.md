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
```

## Usage

### Create a server

To create a server, you need to create a class that extends `RushyServer` and override the `start` method 
and configuration classes to load the configuration of the server.

***Configuration classes***
```kotlin
import fr.rushy.api.configuration.IConfiguration
import fr.rushy.api.configuration.IServerConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW") // https://github.com/Kotlin/kotlinx.serialization/issues/993
@Serializable
data class MyConfiguration(
    @SerialName("server")
    override val server: MyServerConfiguration
) : IConfiguration

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class MyServerConfiguration(
    override val port: Int,
    override val world: String
) : IServerConfiguration
```

***Server class***
```kotlin
import fr.rushy.api.RushyServer

class MyServer(private val configurationPath: String?) : RushyServer() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            MyServer(args.firstOrNull()).start()
        }
    }
    
    override fun start() {
        start<MyConfiguration>(configurationPath) {
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


