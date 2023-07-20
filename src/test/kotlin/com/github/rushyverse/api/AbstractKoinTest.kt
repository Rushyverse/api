package com.github.rushyverse.api

import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.koin.loadModule
import com.github.rushyverse.api.utils.getRandomString
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Server
import org.koin.core.module.Module
import org.koin.dsl.ModuleDeclaration
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AbstractKoinTest {

    lateinit var plugin: com.github.rushyverse.api.Plugin

    lateinit var server: Server

    lateinit var pluginId: String

    @BeforeTest
    open fun onBefore() {
        pluginId = getRandomString()
        CraftContext.startKoin(pluginId) { }

        loadTestModule {
            plugin = mockk(getRandomString())
            server = mockk(getRandomString())

            every { plugin.server } returns server
            every { plugin.id } returns pluginId
            every { plugin.name } returns getRandomString()

            single { plugin }
            single { server }
        }
    }

    @AfterTest
    open fun onAfter() {
        CraftContext.stopKoin(pluginId)
    }

    inline fun <reified T : Any> testInject(): T = CraftContext.get(pluginId).inject<T>().value

    fun loadTestModule(moduleDeclaration: ModuleDeclaration): Module =
        loadModule(pluginId, false, moduleDeclaration)

}