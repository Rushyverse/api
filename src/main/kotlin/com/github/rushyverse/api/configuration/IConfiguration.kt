package com.github.rushyverse.api.configuration

import kotlinx.serialization.Serializable

/**
 * Configuration of the application.
 * @property server Configuration of server.
 */
public interface IConfiguration {

    public val server: IServerConfiguration

}

/**
 * Configuration about the minestom server.
 * @property port Port of the server.
 * @property world Path of the world to load.
 * @property onlineMode `true` to enable the Mojang authentication.
 * @property velocity Velocity configuration.
 * @property bungeeCord BungeeCord configuration.
 */
public interface IServerConfiguration {

    public val port: Int

    public val world: String

    public val onlineMode: Boolean

    public val velocity: IVelocityConfiguration

    public val bungeeCord: IBungeeCordConfiguration
}

/**
 * Configuration to connect the server to the velocity proxy.
 * @property enabled Whether the velocity support is enabled.
 * @property secret Secret to verify if the client comes from the proxy.
 */
public interface IVelocityConfiguration {

    public val enabled: Boolean

    public val secret: String

}

/**
 * Configuration to connect the server to the velocity proxy.
 */
@Serializable
public data class VelocityConfiguration(
    override val enabled: Boolean,
    override val secret: String
) : IVelocityConfiguration


/**
 * Configuration to connect the server to the bungeeCord proxy.
 * @property enabled Whether the server should connect to the bungeeCord proxy.
 * @property secrets Secrets to verify if the client comes from the proxy.
 */
public interface IBungeeCordConfiguration {

    public val enabled: Boolean

    public val secrets: Set<String>

}

/**
 * Configuration to connect the server to the bungeeCord proxy.
 */
@Serializable
public data class BungeeCordConfiguration(
    override val enabled: Boolean,
    override val secrets: Set<String>
) : IBungeeCordConfiguration