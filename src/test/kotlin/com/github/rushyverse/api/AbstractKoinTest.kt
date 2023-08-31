package com.github.rushyverse.api

import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.koin.loadModule
import com.github.rushyverse.api.utils.randomString
import io.mockk.every
import io.mockk.mockk
import org.koin.core.module.Module
import org.koin.dsl.ModuleDeclaration
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class AbstractKoinTest {

    lateinit var plugin: Plugin

    private lateinit var pluginId: String

    @BeforeTest
    open fun onBefore() {
        pluginId = randomString()
        CraftContext.startKoin(pluginId) { }
        CraftContext.startKoin(APIPlugin.ID_API) { }

        loadTestModule {
            plugin = mockk {
                every { id } returns pluginId
                every { name } returns randomString()
            }
            single { plugin }
        }
    }

    @AfterTest
    open fun onAfter() {
        CraftContext.stopKoin(pluginId)
        CraftContext.stopKoin(APIPlugin.ID_API)
    }

    fun loadTestModule(moduleDeclaration: ModuleDeclaration): Module =
        loadModule(pluginId, moduleDeclaration = moduleDeclaration)

    fun loadApiTestModule(moduleDeclaration: ModuleDeclaration): Module =
        loadModule(APIPlugin.ID_API, moduleDeclaration = moduleDeclaration)

}
