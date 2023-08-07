package com.github.rushyverse.api

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.github.rushyverse.api.APIPlugin.Companion.BUNDLE_API
import com.github.rushyverse.api.configuration.reader.IFileReader
import com.github.rushyverse.api.configuration.reader.YamlFileReader
import com.github.rushyverse.api.extension.registerListener
import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.koin.loadModule
import com.github.rushyverse.api.listener.PlayerListener
import com.github.rushyverse.api.listener.VillagerListener
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import com.github.rushyverse.api.serializer.*
import com.github.rushyverse.api.translation.ResourceBundleTranslator
import com.github.rushyverse.api.translation.registerResourceBundleForSupportedLocales
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.contextual
import org.bukkit.entity.Player
import org.koin.core.module.Module
import org.koin.dsl.bind
import java.util.*

/**
 * Represents the base functionality required to create a plugin.
 * This abstract class provides necessary tools and life-cycle methods to facilitate the creation
 * and management of a plugin that utilizes asynchronous operations, dependency injection, and
 * other utility functions.
 */
public abstract class Plugin : SuspendingJavaPlugin() {

    /**
     * A unique identifier for this plugin. This ID is used for tasks like identifying
     * the Koin application, loading Koin modules, etc.
     */
    public abstract val id: String

    override suspend fun onEnableAsync() {
        super.onEnableAsync()

        CraftContext.startKoin(id)
        moduleBukkit()
        moduleClients()

        registerListener { PlayerListener(this) }
        registerListener { VillagerListener(this) }
    }

    /**
     * Creates and loads a Koin module that stores instances of this plugin and of his child.
     *
     * @return The Koin module containing the plugin instances.
     */
    protected inline fun <reified T : Plugin> modulePlugin(): Module = loadModule(id) {
        single { this@Plugin }
        single { this@Plugin as T }
    }

    /**
     * Creates and loads a Koin module containing client management components.
     *
     * @return The Koin module for client management.
     */
    protected fun moduleClients(): Module = loadModule(id) {
        single { ClientManagerImpl() } bind ClientManager::class
    }

    /**
     * Creates and loads a Koin module with Bukkit-specific components.
     * Can be overridden by derived classes to provide additional or customized components.
     *
     * @return The Koin module for Bukkit components.
     */
    protected open fun moduleBukkit(): Module = loadModule(id) {
        single { getLogger() }
    }

    override suspend fun onDisableAsync() {
        CraftContext.stopKoin(id)
        super.onDisableAsync()
    }

    /**
     * Creates a new YAML reader instance to handle YAML configurations.
     * Allows for customization of serializers and configurations.
     *
     * @param configuration Configuration options for the YAML reader.
     * @param serializerModuleBuilder Provides additional serializers.
     * @return A configured YAML reader instance.
     */
    protected open fun createYamlReader(
        configuration: YamlConfiguration = YamlConfiguration(),
        serializerModuleBuilder: SerializersModuleBuilder.() -> Unit = {},
    ): IFileReader {
        val yaml = Yaml(
            serializersModule = SerializersModule {
                contextual(ComponentSerializer)
                contextual(DyeColorSerializer)
                contextual(EnchantmentSerializer)
                contextual(ItemStackSerializer)
                contextual(LocationSerializer)
                contextual(MaterialSerializer)
                contextual(NamespacedSerializer)
                contextual(PatternSerializer)
                contextual(PatternTypeSerializer)
                contextual(RangeDoubleSerializer)
                serializerModuleBuilder()
            },
            configuration = configuration,
        )
        return YamlFileReader(this, yaml)
    }

    /**
     * Abstract function to create a new client instance associated with a given player.
     *
     * @param player The player for whom the client instance should be created.
     * @return The created client instance.
     */
    public abstract fun createClient(player: Player): Client

    /**
     * Creates a new translator to fetch translations for the supported languages.
     * Can be overridden by derived classes to provide custom translation providers.
     *
     * @return A translator configured for the supported languages.
     */
    protected open suspend fun createTranslator(): ResourceBundleTranslator =
        ResourceBundleTranslator().apply {
            registerResourceBundleForSupportedLocales(BUNDLE_API, ResourceBundle::getBundle)
        }
}
