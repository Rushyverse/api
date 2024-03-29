package com.github.rushyverse.api.koin

import com.github.rushyverse.api.APIPlugin
import com.github.rushyverse.api.APIPlugin.Companion.ID_API
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.KoinContext
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import kotlin.collections.set

/**
 * Wrapper for [org.koin.dsl.module] that immediately loads the module for the current [Koin] instance.
 * @param id App id to find the dedicated koin instance.
 * @param createdAtStart `true` to execute declaration directly, or `false` to load with lazy way.
 * @param moduleDeclaration Declaration of the module
 * @return The new module created.
 */
public fun loadModule(
    id: String,
    createdAtStart: Boolean = false,
    moduleDeclaration: ModuleDeclaration
): Module {
    return module(createdAtStart, moduleDeclaration).also { CraftContext.loadKoinModules(id, it) }
}

/**
 * Injects an instance of the specified type T from the Koin context of the [APIPlugin]. Allows to retrieve
 * shared instances between plugins.
 * @returnA lazy delegate of type T representing the injected instance.
 */
public inline fun <reified T : Any> inject(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
): Lazy<T> = lazy(mode) { CraftContext.get(ID_API).get(qualifier) }

/**
 * Injects an instance of the specified type T from the Koin context defined for the [id].
 * The [id] can be the id of the plugin to retrieve instance linked to the plugin.
 * If the instance is not found, the [idFallback] will be used to retrieve the instance.
 *
 * @param id The id of the memory container to retrieve the instance from.
 * @param idFallback The id of the memory container to retrieve the instance from if the first one is not found.
 * @return A lazy delegate of type T representing the injected instance.
 */
public inline fun <reified T : Any> inject(
    id: String,
    idFallback: String = ID_API,
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
): Lazy<T> = lazy(mode) {
    CraftContext.get(id).getOrNull<T>(qualifier) ?: CraftContext.get(idFallback).get<T>(qualifier)
}

/**
 * A copy of [KoinContext] to retrieve koin instance for each application.
 * This contains the [KoinApplication] and its [Koin] instance for dependency injection.
 *
 * @see org.koin.core.context.GlobalContext
 */
public object CraftContext {

    /**
     * [Koin] instanced linked to an app id.
     */
    private val _koins: MutableMap<String, Pair<KoinApplication, Koin>> = mutableMapOf()

    /**
     * [Koin] instanced linked to an app id.
     */
    public val koins: Map<String, Pair<KoinApplication, Koin>> = _koins

    /**
     * Gets the [Koin] instance for an app.
     * @param id App id to find the dedicated koin instance.
     * @return The koin instance.
     * @throws IllegalStateException [KoinApplication] has not yet been started.
     */
    public fun get(id: String): Koin = getOrNull(id) ?: error("KoinApplication has not been started for the id [$id]")

    /**
     * Gets the [Koin] instance or null if the [KoinApplication] has not yet been started.
     * @param id App id to find the dedicated koin instance.
     * @return Koin?
     */
    public fun getOrNull(id: String): Koin? = _koins[id]?.second

    /** Closes and removes the current [Koin] instance. */
    public fun stopKoin(id: String) {
        synchronized(this) {
            val koinInstance = _koins[id] ?: return@synchronized
            koinInstance.second.close()
            _koins -= id
        }
    }

    /**
     * Starts using the provided [KoinAppDeclaration] to create the [KoinApplication] for this context.
     *
     * @param appDeclaration The application declaration to start with.
     *
     * @throws KoinAppAlreadyStartedException The [KoinApplication] has already been instantiated.
     */
    public fun startKoin(id: String, appDeclaration: KoinAppDeclaration = {}): KoinApplication = synchronized(this) {
        val koinApplication = KoinApplication.init().apply(appDeclaration)
        return@synchronized startKoin(id, koinApplication)
    }

    /**
     * Starts using the provided [KoinApplication] as the current one for this context.
     *
     * @param koinApplication The application to start with.
     *
     * @throws KoinAppAlreadyStartedException The [KoinApplication] has already been instantiated.
     */
    public fun startKoin(id: String, koinApplication: KoinApplication): KoinApplication = synchronized(this) {
        register(id, koinApplication)
        koinApplication.createEagerInstances()

        return koinApplication
    }

    /**
     * Registers a [KoinApplication] to as the current one for this context.
     *
     * @param koinApplication The application to registers.
     *
     * @throws KoinAppAlreadyStartedException The [KoinApplication] has already been instantiated.
     */
    private fun register(id: String, koinApplication: KoinApplication) {
        if (getOrNull(id) != null) {
            throw KoinAppAlreadyStartedException("Koin Application has already been started for id [$id]")
        }

        _koins[id] = koinApplication to koinApplication.koin
    }

    /**
     * Loads a module into the [Koin] instance.
     *
     * @param module The module to load.
     */
    public fun loadKoinModules(id: String, module: Module) {
        synchronized(this) {
            loadKoinModules(id, listOf(module))
        }
    }

    /**
     * Loads modules into the [Koin] instance.
     *
     * @param modules The modules to load.
     */
    public fun loadKoinModules(id: String, modules: List<Module>) {
        synchronized(this) {
            get(id).loadModules(modules)
        }
    }

    /**
     * Unloads a module from the [Koin] instance.
     *
     * @param module The module to unload.
     */
    public fun unloadKoinModules(id: String, module: Module) {
        synchronized(this) {
            unloadKoinModules(id, listOf(module))
        }
    }

    /**
     * Unloads modules from the [Koin] instance.
     *
     * @param modules The modules to unload.
     */
    public fun unloadKoinModules(id: String, modules: List<Module>) {
        synchronized(this) {
            get(id).unloadModules(modules)
        }
    }
}
