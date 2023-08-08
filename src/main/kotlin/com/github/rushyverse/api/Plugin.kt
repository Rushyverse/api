package com.github.rushyverse.api

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.github.rushyverse.api.APIPlugin.Companion.BUNDLE_API
import com.github.rushyverse.api.configuration.reader.IFileReader
import com.github.rushyverse.api.configuration.reader.YamlFileReader
import com.github.rushyverse.api.extension.asComponent
import com.github.rushyverse.api.extension.registerListener
import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.koin.loadModule
import com.github.rushyverse.api.listener.PlayerListener
import com.github.rushyverse.api.listener.VillagerListener
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import com.github.rushyverse.api.player.language.LanguageManager
import com.github.rushyverse.api.serializer.*
import com.github.rushyverse.api.translation.ResourceBundleTranslator
import com.github.rushyverse.api.translation.Translator
import com.github.rushyverse.api.translation.registerResourceBundleForSupportedLocales
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.contextual
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.jetbrains.annotations.Blocking
import org.koin.core.module.Module
import org.koin.dsl.bind
import java.util.*

/**
 * Represents the base functionality required to create a plugin.
 * This abstract class provides necessary tools and life-cycle methods to facilitate the creation
 * and management of a plugin that utilizes asynchronous operations, dependency injection, and
 * other utility functions.
 * @property id A unique identifier for this plugin.
 * @property bundle The name of the resource bundle to use for this plugin.
 * This ID is used for tasks like identifying the Koin application and loading Koin modules.
 */
public abstract class Plugin(
    public val id: String,
    public val bundle: String
) : SuspendingJavaPlugin() {

    /**
     * Client manager linked to this plugin.
     */
    public val clientManager: ClientManager by inject(id)

    /**
     * Translator linked to this plugin.
     */
    public val translator: Translator by inject(id)

    /**
     * Common language manager for all plugins.
     */
    public val languageManager: LanguageManager by inject()

    override suspend fun onEnableAsync() {
        super.onEnableAsync()

        CraftContext.startKoin(id)
        moduleBukkit()
        moduleClients()
        moduleTranslation()

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
     * Creates and loads a Koin module containing translation components.
     *
     * @return The Koin module for translation.
     */
    protected fun moduleTranslation(): Module = loadModule(id) {
        single { createTranslator() } bind Translator::class
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
    @Blocking
    protected open fun createTranslator(): ResourceBundleTranslator =
        ResourceBundleTranslator(bundle).apply {
            registerResourceBundleForSupportedLocales(BUNDLE_API, ResourceBundle::getBundle)
        }

    /**
     * Broadcasts a localized message to all players.
     *
     * This function groups players by their language preferences, translates the message once per language,
     * and then sends the appropriate localized message to each player.
     *
     * @param players The players to whom the message should be sent.
     * @param key The key used to look up the translation in the resource bundle.
     * @param bundle The resource bundle to use for the translation.
     * @param argumentBuilder A function that builds the arguments for the translation.
     * @param messageModifier A function that modifies the translated message before it is sent.
     * The modification must be chained.
     */
    public suspend inline fun broadcast(
        players: Collection<Player>,
        key: String,
        bundle: String = this.bundle,
        messageModifier: (Component) -> Component = { it },
        argumentBuilder: Translator.(Locale) -> Array<Any> = { emptyArray() },
    ) {
        players.groupBy { languageManager.get(it).locale }
            .forEach { (lang, receiver) ->
                val translatedComponent = translator
                    .get(key, lang, translator.argumentBuilder(lang), bundle)
                    .asComponent().let(messageModifier)

                receiver.forEach { it.sendMessage(translatedComponent) }
            }
    }
}
