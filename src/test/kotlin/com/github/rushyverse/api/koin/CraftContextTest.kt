package com.github.rushyverse.api.koin

import com.github.rushyverse.api.utils.randomString
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koin.core.KoinApplication
import org.koin.core.error.ClosedScopeException
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.dsl.module
import kotlin.test.*

class CraftContextTest {

    @BeforeTest
    fun onBefore() {
        CraftContext.koins.toMap().forEach { (id, _) ->
            CraftContext.stopKoin(id)
        }
    }

    @Nested
    @DisplayName("Get koin context")
    inner class Get {

        @Test
        fun `when no instance exists for the id`() {
            assertThrows<IllegalStateException> {
                CraftContext.get(randomString())
            }
        }

        @Test
        fun `when instance is registered for the id`() {
            val id = randomString()
            CraftContext.startKoin(id)
            val koin = CraftContext.get(id)
            assertNotNull(koin)
        }
    }

    @Nested
    @DisplayName("Get koin context or null")
    inner class GetOrNull {

        @Test
        fun `when no instance exists for the id`() {
            assertNull(CraftContext.getOrNull(randomString()))
        }

        @Test
        fun `when instance is registered for the id`() {
            val id = randomString()
            CraftContext.startKoin(id)
            val koin = CraftContext.get(id)
            assertNotNull(koin)
        }
    }

    @Nested
    @DisplayName("Stop koin context")
    inner class StopKoin {

        @Test
        fun `when no instance exists for the id`() {
            CraftContext.stopKoin(randomString())
        }

        @Test
        fun `when instance is registered for the id`() {
            assertEquals(0, CraftContext.koins.size)
            val id = randomString()
            CraftContext.startKoin(id)
            val module = module {
                single { 1.2 }
            }
            assertEquals(1, CraftContext.koins.size)

            val koin = CraftContext.get(id)
            koin.loadModules(listOf(module))
            assertEquals(1.2, koin.get())

            CraftContext.stopKoin(id)
            assertEquals(0, CraftContext.koins.size)
            assertNull(CraftContext.getOrNull(id))
            assertThrows<ClosedScopeException> {
                assertEquals(1.2, koin.get())
            }
        }
    }

    @Nested
    @DisplayName("Start koin context")
    inner class StartKoin {

        @Test
        fun `when no instance exists for the id`() {
            assertEquals(0, CraftContext.koins.size)
            CraftContext.startKoin(randomString()) {}
            assertEquals(1, CraftContext.koins.size)
        }

        @Test
        fun `when instance already exists for the id`() {
            assertEquals(0, CraftContext.koins.size)
            val id = randomString()
            CraftContext.startKoin(id) {}
            assertEquals(1, CraftContext.koins.size)
            assertThrows<KoinAppAlreadyStartedException> {
                CraftContext.startKoin(id) {}
            }
            assertEquals(1, CraftContext.koins.size)
        }

        @Test
        fun `define module in declaration during starting`() {
            assertEquals(0, CraftContext.koins.size)
            val id = randomString()
            CraftContext.startKoin(id) {
                this.modules(module {
                    single { "hello" }
                })
            }
            val injectedString = CraftContext.get(id).get<String>()
            assertEquals("hello", injectedString)
            assertEquals(1, CraftContext.koins.size)
        }
    }

    @Nested
    @DisplayName("Start with koin application")
    inner class StartKoinApplication {

        @Test
        fun `when no instance exists for the id`() {
            assertEquals(0, CraftContext.koins.size)
            val koinApplication = KoinApplication.init()
            CraftContext.startKoin(randomString(), koinApplication)
            assertEquals(1, CraftContext.koins.size)
        }

        @Test
        fun `when instance already exists for the id`() {
            assertEquals(0, CraftContext.koins.size)
            val id = randomString()
            val koinApplication = KoinApplication.init()
            CraftContext.startKoin(id, koinApplication)
            assertEquals(1, CraftContext.koins.size)
            assertThrows<KoinAppAlreadyStartedException> {
                CraftContext.startKoin(id, koinApplication)
            }
            assertEquals(1, CraftContext.koins.size)
        }

        @Test
        fun `define module in declaration during starting`() {
            assertEquals(0, CraftContext.koins.size)
            val koinApplication = KoinApplication.init()
            koinApplication.modules(module {
                single { "hello" }
            })
            val id = randomString()
            CraftContext.startKoin(id, koinApplication)
            val injectedString = CraftContext.get(id).get<String>()
            assertEquals("hello", injectedString)
            assertEquals(1, CraftContext.koins.size)
        }
    }

    @Nested
    @DisplayName("Load koin module with single module")
    inner class LoadKoinSingleModule {

        @Test
        fun `when no instance exists for the id`() {
            assertThrows<IllegalStateException> {
                CraftContext.loadKoinModules(randomString(), module { })
            }
        }

        @Test
        fun `when instance exists for the id`() {
            val id = randomString()
            CraftContext.startKoin(id)
            CraftContext.loadKoinModules(id, module {
                single { "hello" }
            })
            assertEquals("hello", CraftContext.get(id).get())
        }
    }

    @Nested
    @DisplayName("Load koin module with several modules")
    inner class LoadKoinListModule {

        @Test
        fun `when no instance exists for the id`() {
            assertThrows<IllegalStateException> {
                CraftContext.loadKoinModules(randomString(), emptyList())
            }
        }

        @Test
        fun `when instance exists for the id`() {
            val id = randomString()
            CraftContext.startKoin(id)
            CraftContext.loadKoinModules(id, listOf(
                module {
                    single { "hello" }
                }, module {
                    single { 42 }
                })
            )
            assertEquals("hello", CraftContext.get(id).get())
            assertEquals(42, CraftContext.get(id).get())
        }
    }

    @Nested
    @DisplayName("Unload koin module with single module")
    inner class UnloadKoinSingleModule {

        @Test
        fun `when no instance exists for the id`() {
            assertThrows<IllegalStateException> {
                CraftContext.unloadKoinModules(randomString(), module { })
            }
        }

        @Test
        fun `when instance exists for the id but not module linked`() {
            val id = randomString()
            CraftContext.startKoin(id)
            CraftContext.unloadKoinModules(id, module {
                single { "hello" }
            })
        }

        @Test
        fun `when instance exists for the id`() {
            val id = randomString()
            CraftContext.startKoin(id)
            val module = module {
                single { "hello" }
            }
            CraftContext.loadKoinModules(id, module)
            assertEquals("hello", CraftContext.get(id).get())

            CraftContext.unloadKoinModules(id, module)
            assertThrows<NoBeanDefFoundException> {
                assertEquals("hello", CraftContext.get(id).get())
            }
        }
    }

    @Nested
    @DisplayName("Unload koin module with several modules")
    inner class UnloadKoinListModule {

        @Test
        fun `when no instance exists for the id`() {
            assertThrows<IllegalStateException> {
                CraftContext.unloadKoinModules(randomString(), emptyList())
            }
        }

        @Test
        fun `when instance exists for the id but not module linked`() {
            val id = randomString()
            CraftContext.startKoin(id)
            CraftContext.unloadKoinModules(id, listOf(module {
                single { "hello" }
            }))
        }

        @Test
        fun `when instance exists for the id`() {
            val id = randomString()
            CraftContext.startKoin(id)
            val module1 = module {
                single { "hello" }
            }
            val module2 = module {
                single { 1 }
            }
            val module3 = module {
                single { 1.2 }
            }
            CraftContext.loadKoinModules(id, listOf(module1, module2, module3))
            assertEquals("hello", CraftContext.get(id).get())
            assertEquals(1, CraftContext.get(id).get())
            assertEquals(1.2, CraftContext.get(id).get())

            CraftContext.unloadKoinModules(id, listOf(module1))
            assertThrows<NoBeanDefFoundException> {
                assertEquals("hello", CraftContext.get(id).get())
            }
            assertEquals(1, CraftContext.get(id).get())
            assertEquals(1.2, CraftContext.get(id).get())

            CraftContext.unloadKoinModules(id, listOf(module2, module3))
            assertThrows<NoBeanDefFoundException> {
                assertEquals(1, CraftContext.get(id).get())
            }

            assertThrows<NoBeanDefFoundException> {
                assertEquals(1.2, CraftContext.get(id).get())
            }
        }
    }

    @Nested
    @DisplayName("Load koin module with lazy extension function")
    inner class LoadKoinSingleModuleExtension {

        @Test
        fun `when no instance exists for the id`() {
            assertThrows<IllegalStateException> {
                loadModule(randomString()) {}
            }
        }

        @Test
        fun `when instance exists for the id with lazy module creation`() {
            val id = randomString()
            CraftContext.startKoin(id)
            var isInit = false
            loadModule(id) {
                single {
                    isInit = true
                    "hello"
                }
            }
            assertFalse { isInit }
            assertEquals("hello", CraftContext.get(id).get())
            assertTrue { isInit }
        }
    }
}
