# API

This project allows to create a Minestom server easily in Kotlin. It provides a lot of features to create a Minecraft
server.

## Tools

The server is built using [Kotlin](https://kotlinlang.org/) and [Minestom](https://github.com/Minestom/Minestom).
The version of tools used is specified in the [build.gradle.kts](build.gradle.kts) file.

The API doesn't have an entrypoint.

## Installation

You can find the artifact on [Jitpack](https://jitpack.io/#UniverseProject/data-service).
Use the version you prefer by following the tutorial on jitpack and replacing `{version}` bellow.

The newest version can be found [here: ![](https://jitpack.io/v/Rushyverse/api.svg)](https://jitpack.io/#Rushyverse/api)

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

## Usage

### Create a server

To create a server, you need to create a class that extends `RushyServer` and override the `start` method
and configuration classes to load the configuration of the server.

***Configuration classes***

The interfaces for configuration are located in
this [package](src/main/kotlin/com/github/rushyverse/api/configuration/IConfiguration.kt).

```kotlin
import com.github.rushyverse.api.configuration.BungeeCordConfiguration
import com.github.rushyverse.api.configuration.IConfiguration
import com.github.rushyverse.api.configuration.IServerConfiguration
import com.github.rushyverse.api.configuration.VelocityConfiguration
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
    override val world: String,
    override val onlineMode: Boolean,
    override val bungeeCord: BungeeCordConfiguration,
    override val velocity: VelocityConfiguration
) : IServerConfiguration
```

If when you start your application, you have an error like this:

**Your current kotlinx.serialization core version is too low, while current Kotlin compiler plugin 1.7.22 requires at
least 1.0-M1-SNAPSHOT. Please update your kotlinx.serialization runtime dependency.**

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
import com.github.rushyverse.api.RushyServer

suspend fun main(args: Array<String>) {
    MyServer(args.firstOrNull()).start()
}

class MyServer(private val configurationPath: String?) : RushyServer() {

    override suspend fun start() {
        start<MyConfiguration>(configurationPath) {
            // this = MyConfiguration
            // it = InstanceContainer
            // Configure your server here
        }
    }
}
```

### Configuration

The function `start` takes a configuration class as parameter. So you need to define a configuration file in
the `resources` folder.
The configuration file should be named `server.conf` and use this [template](src/test/resources/server.conf).

If in the working directory (where you launch the server) the configuration file will be created if it doesn't exist.
However, the world folder must be added manually.

_The method `start` takes a string as parameter. This string is the path to the configuration file. If the string is
null, the configuration file will be searched in the working directory._

### Commands

In `API` class, the method `registerCommands` can be used to register
the [implemented commands](src/main/kotlin/com/github/rushyverse/api/command) from API.

```kotlin
import com.github.rushyverse.api.RushyServer

class MyServer : RushyServer() {

    override suspend fun start() {
        start<MyConfiguration>(configurationPath) {
            API.registerCommands()
        }
    }
}
```

### Translation

The `RushyServer` class offers a method to
create [TranslationsProvider](src/main/kotlin/com/github/rushyverse/api/translation/TranslationsProvider.kt) instance
using [resource bundle files](src/main/resources/api.properties).

If you want, you can override the method `createTranslationsProvider` to create your own instance
of `TranslationsProvider`.

```kotlin
import com.github.rushyverse.api.RushyServer

class MyServer : RushyServer() {

    override suspend fun start() {
        start<MyConfiguration>(configurationPath) {
            // Register "myBundle" resource bundle and the API resource bundle
            val translationsProvider = createTranslationsProvider(listOf(API.BUNDLE_API, "myBundle"))
            // Get the value of "myKey" for english language
            println(translationsProvider.translate("myKey", SupportedLanguage.ENGLISH.locale, "myBundle"))
            // Get the value of "myKey2" for english language with parameters
            println(
                translationsProvider.translate(
                    "myKey2",
                    SupportedLanguage.ENGLISH.locale,
                    "myBundle",
                    arrayOf("myValue")
                )
            )
        }
    }
}
```

### Suspend Command

The API provides functions to execute command in coroutine context.

```kotlin
import com.github.rushyverse.api.extension.addConditionalSyntaxSuspend
import com.github.rushyverse.api.extension.addSyntaxSuspend
import com.github.rushyverse.api.extension.setDefaultExecutorSuspend
import kotlinx.coroutines.delay
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player

class MyCommand : Command("mycommand") {

    init {
        setDefaultExecutorSuspend { sender, context ->
            sender.sendMessage("Hello initial thread ${Thread.currentThread().name}")
            delay(1000)
            sender.sendMessage("The thread is changed to ${Thread.currentThread().name}")
        }

        val argument = ArgumentType.String("argument")

        addSyntaxSuspend({ sender, context ->
            sender.sendMessage("Hello initial thread ${Thread.currentThread().name}")
            delay(1000)
            sender.sendMessage("The thread is changed to ${Thread.currentThread().name}")
        }, argument)

        val argumentInt = ArgumentType.Integer("argumentInt")

        addConditionalSyntaxSuspend({ sender, commandString ->
            sender is Player
        }, { sender, context ->
            sender.sendMessage("Hello initial thread ${Thread.currentThread().name}")
            delay(1000)
            sender.sendMessage("The thread is changed to ${Thread.currentThread().name}")
        }, argument, argumentInt)
    }
}
```

As you can see, the `setDefaultExecutorSuspend`, `addSyntaxSuspend` and `addConditionalSyntaxSuspend` methods are
extensions of `Command` class.
These methods allow you to execute the command in coroutine context if needed.

In the example above, the first `sender.sendMessage(...)` is executed in the thread used by Minestom to execute the
command.
However, when a suspend function is called, the next part of the code is executed in another thread.
You can define
the [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/)
of the coroutine using the `coroutineScope`.
So, after the [delay](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/delay.html)
function, the thread is changed to the thread defined by the `coroutineScope`.

### Suspend Listener

The API provides functions to handle events in coroutine context through the class [EventListenerSuspend](src/main/kotlin/com/github/rushyverse/api/listener/EventListenerSuspend.kt).

```kotlin
import com.github.rushyverse.api.listener.EventListenerSuspend
import kotlinx.coroutines.delay
import net.minestom.server.event.player.PlayerSpawnEvent

class PlayerSpawnListener : EventListenerSuspend<PlayerSpawnEvent>() {

    override fun eventType(): Class<PlayerSpawnEvent> {
        return PlayerSpawnEvent::class.java
    }

    override suspend fun runSuspend(event: PlayerSpawnEvent) {
        val player = event.player
        player.sendMessage("Hello initial thread ${Thread.currentThread().name}")
        delay(1000)
        player.sendMessage("The thread is changed to ${Thread.currentThread().name}")
    }
}
```

Like suspend command, the first `sender.sendMessage(...)` is executed in the thread used by Minestom to handle the
event.
After the [delay](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/delay.html)
function,
the thread is changed to the thread defined by the `coroutineScope` in constructor of [EventListenerSuspend](src/main/kotlin/com/github/rushyverse/api/listener/EventListenerSuspend.kt).

**Note: The method `runSuspend` doesn't return value because when the current thread change, the result of the initial `run` method is expected,
so the class always returns EventListener.Result.SUCCESS.**

You can also add a new listener to the event bus using the `addSuspendListener` method.

```kotlin
MinecraftServer.getGlobalEventHandler().addListenerSuspend<PlayerSpawnEvent> {
    val player = event.player
    player.sendMessage("Hello initial thread ${Thread.currentThread().name}")
    delay(1000)
    player.sendMessage("The thread is changed to ${Thread.currentThread().name}")
}
```

### Acquirable

Minestom provides the [Acquirable API](https://wiki.minestom.net/thread-architecture/acquirable-api).
In order to simplify the use of these components, we provide some extensions.

```kotlin
import com.github.rushyverse.api.extension.acquirable

val player: Player = ...
player.acquirable // to retrieve the Acquirable instance
```

When you need to synchronize the access to an entity, you can use `sync` and `async` methods.

```kotlin
import com.github.rushyverse.api.extension.sync
import com.github.rushyverse.api.extension.async

val player: Player = ...
player.sync {
    // The acquirable instance is locked here
    // This code is executed in the current thread with the entity locked
    player.sendMessage("In sync ${Thread.currentThread().name}")
}

// The acquirable instance is unlocked here

player.async {
    // The acquirable instance is locked here
    // This code is executed in the thread defined by the scope sent in parameter (optional)
    player.sendMessage("In async ${Thread.currentThread().name}")
}
```

Sometimes, you work with a list of entities and you need to synchronize the access to all of them.

```kotlin
import com.github.rushyverse.api.extension.toAcquirables

val players: List<Player> = ...
val acquirables: AcquirableCollection<Player> = players.toAcquirables()
```

With the method `toAcquirables`, you can convert a list of entities to [AcquirableCollection](https://wiki.minestom.net/thread-architecture/acquirable-api#acquirable-collections).

## Modification

Firstly, you should modify the version of the project in [gradle.properties](gradle.properties) file by changing
the `version` property.

If you want to modify the API locally and use it in your project, you need to publish it in local repository with the
following command:

```bash
gradlew publishToMavenLocal
```

When the publication is done, you can use the API in your project using the following configuration :

**Replace `{version}` by the version in the [gradle.properties](gradle.properties) file used during publish.**

### Gradle (groovy)

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