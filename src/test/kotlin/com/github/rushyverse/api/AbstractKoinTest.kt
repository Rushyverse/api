package com.github.rushyverse.api

import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.koin.loadModule
import com.github.rushyverse.api.utils.randomString
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import org.bukkit.Server
import org.koin.core.module.Module
import org.koin.dsl.ModuleDeclaration

open class AbstractKoinTest {

    lateinit var plugin: Plugin

    lateinit var server: Server

    private lateinit var pluginId: String

    @BeforeTest
    open fun onBefore() {
        pluginId = randomString()
        CraftContext.startKoin(pluginId) { }
        CraftContext.startKoin(APIPlugin.ID_API) { }

        server = mockk {
            every { pluginManager } returns mockk()
        }

        loadTestModule {
            plugin = mockk {
                every { id } returns pluginId
                every { name } returns randomString()
                every { server } returns this@AbstractKoinTest.server
            }
            single { plugin }
        }

        loadApiTestModule {
            single { server }
        }
    }

    @AfterTest
    open fun onAfter() {
        CraftContext.stopKoin(pluginId)
        CraftContext.stopKoin(APIPlugin.ID_API)
        unmockkAll()
    }

    fun loadTestModule(moduleDeclaration: ModuleDeclaration): Module =
        loadModule(pluginId, moduleDeclaration = moduleDeclaration)

    fun loadApiTestModule(moduleDeclaration: ModuleDeclaration): Module =
        loadModule(APIPlugin.ID_API, moduleDeclaration = moduleDeclaration)

}
