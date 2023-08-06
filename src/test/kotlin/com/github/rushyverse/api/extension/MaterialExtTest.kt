package com.github.rushyverse.api.extension

import be.seeseemelk.mockbukkit.MockBukkit
import io.kotest.matchers.shouldBe
import org.bukkit.Material
import org.junit.jupiter.api.Nested
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MaterialExtTest {

    @BeforeTest
    fun onBefore() {
        MockBukkit.mock()
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Nested
    inner class IsWool {

        @Test
        fun `should be wool`() {
            Material.BLACK_WOOL.isWool() shouldBe true
            Material.BLUE_WOOL.isWool() shouldBe true
            Material.BROWN_WOOL.isWool() shouldBe true
            Material.CYAN_WOOL.isWool() shouldBe true
            Material.GRAY_WOOL.isWool() shouldBe true
            Material.GREEN_WOOL.isWool() shouldBe true
            Material.LIGHT_BLUE_WOOL.isWool() shouldBe true
            Material.LIGHT_GRAY_WOOL.isWool() shouldBe true
            Material.LIME_WOOL.isWool() shouldBe true
            Material.MAGENTA_WOOL.isWool() shouldBe true
            Material.ORANGE_WOOL.isWool() shouldBe true
            Material.PINK_WOOL.isWool() shouldBe true
            Material.PURPLE_WOOL.isWool() shouldBe true
            Material.RED_WOOL.isWool() shouldBe true
            Material.WHITE_WOOL.isWool() shouldBe true
            Material.YELLOW_WOOL.isWool() shouldBe true
        }

        @Test
        fun `should not be wool`() {
            Material.AIR.isWool() shouldBe false
            Material.BLACK_CARPET.isWool() shouldBe false
            Material.BLUE_CARPET.isWool() shouldBe false
            Material.ACACIA_FENCE.isWool() shouldBe false
        }

    }

    @Nested
    inner class IsWoolCarpet {

        @Test
        fun `should be wool carpet`() {
            Material.BLACK_CARPET.isWoolCarpet() shouldBe true
            Material.BLUE_CARPET.isWoolCarpet() shouldBe true
            Material.BROWN_CARPET.isWoolCarpet() shouldBe true
            Material.CYAN_CARPET.isWoolCarpet() shouldBe true
            Material.GRAY_CARPET.isWoolCarpet() shouldBe true
            Material.GREEN_CARPET.isWoolCarpet() shouldBe true
            Material.LIGHT_BLUE_CARPET.isWoolCarpet() shouldBe true
            Material.LIGHT_GRAY_CARPET.isWoolCarpet() shouldBe true
            Material.LIME_CARPET.isWoolCarpet() shouldBe true
            Material.MAGENTA_CARPET.isWoolCarpet() shouldBe true
            Material.ORANGE_CARPET.isWoolCarpet() shouldBe true
            Material.PINK_CARPET.isWoolCarpet() shouldBe true
            Material.PURPLE_CARPET.isWoolCarpet() shouldBe true
            Material.RED_CARPET.isWoolCarpet() shouldBe true
            Material.WHITE_CARPET.isWoolCarpet() shouldBe true
            Material.YELLOW_CARPET.isWoolCarpet() shouldBe true
        }

        @Test
        fun `should not be wool carpet`() {
            Material.AIR.isWoolCarpet() shouldBe false
            Material.BLACK_WOOL.isWoolCarpet() shouldBe false
            Material.BLUE_WOOL.isWoolCarpet() shouldBe false
            Material.ACACIA_FENCE.isWoolCarpet() shouldBe false
        }

    }
}
