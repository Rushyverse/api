package com.github.rushyverse.api

import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.koin.loadModule
import com.github.rushyverse.api.utils.randomString
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Server
import org.koin.core.module.Module
import org.koin.dsl.ModuleDeclaration
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AbstractKoinTest {

    lateinit var plugin: Plugin

    lateinit var server: Server

    lateinit var pluginId: String

    @BeforeTest
    open fun onBefore() {
        pluginId = randomString()
        CraftContext.startKoin(pluginId) { }
        CraftContext.startKoin(APIPlugin.ID_API) { }

        loadTestModule {
            plugin = mockk(randomString()) {
                every { id } returns pluginId
                every { name } returns randomString()
            }
            single { plugin }
        }

        server = mockk(randomString())
        loadApiTestModule {
            single { server }
        }
    }

    @AfterTest
    open fun onAfter() {
        CraftContext.stopKoin(pluginId)
        CraftContext.stopKoin(APIPlugin.ID_API)
    }

    inline fun <reified T : Any> testInject(): T = CraftContext.get(pluginId).inject<T>().value

    fun loadTestModule(moduleDeclaration: ModuleDeclaration): Module =
        loadModule(pluginId, false, moduleDeclaration)

    fun loadApiTestModule(moduleDeclaration: ModuleDeclaration): Module =
        loadModule(APIPlugin.ID_API, true, moduleDeclaration)

}
