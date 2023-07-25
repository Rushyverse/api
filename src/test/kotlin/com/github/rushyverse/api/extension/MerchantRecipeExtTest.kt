package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.getRandomString
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MerchantRecipeExtTest {

    @Test
    fun `constructor utils function`() {
        val result = mockk<ItemStack>(getRandomString())
        val maxUses = 1
        val uses = 2
        val experienceReward = false
        val villagerExperience = 3
        val priceMultiplier = 1.1F
        val demand = 4
        val specialPrice = 5
        val ignoreDiscounts = true

        val item1 = ItemStack(Material.COOKED_BEEF)
        val item2 = ItemStack(Material.ACACIA_DOOR)
        val ingredients = listOf(item1, item2)

        var recipe = MerchantRecipe(
            result = result,
            maxUses = maxUses,
            uses = uses,
            experienceReward = experienceReward,
            villagerExperience = villagerExperience,
            priceMultiplier = priceMultiplier,
            demand = demand,
            specialPrice = specialPrice,
            ignoreDiscounts = ignoreDiscounts,
            ingredients = ingredients
        )

        assertEquals(result, recipe.result)
        assertEquals(maxUses, recipe.maxUses)
        assertEquals(uses, recipe.uses)
        assertEquals(experienceReward, recipe.hasExperienceReward())
        assertEquals(villagerExperience, recipe.villagerExperience)
        assertEquals(priceMultiplier, recipe.priceMultiplier)
        assertEquals(demand, recipe.demand)
        assertEquals(specialPrice, recipe.specialPrice)
        assertEquals(ignoreDiscounts, recipe.shouldIgnoreDiscounts())
        // Problem with Bukkit server
        //        assertEquals(ingredients, recipe.ingredients)

        recipe = MerchantRecipe(
            result = result,
            maxUses = 0,
            experienceReward = true,
            ignoreDiscounts = false,
        )

        assertTrue { recipe.hasExperienceReward() }
        assertFalse { recipe.shouldIgnoreDiscounts() }
    }

    @Test
    fun `default constructor utils function`() {
        val result = mockk<ItemStack>(getRandomString())
        val maxUses = 10
        val recipe = MerchantRecipe(
            result = result,
            maxUses = maxUses
        )

        assertEquals(result, recipe.result)
        assertEquals(maxUses, recipe.maxUses)
        assertEquals(0, recipe.uses)
        assertEquals(false, recipe.hasExperienceReward())
        assertEquals(0, recipe.villagerExperience)
        assertEquals(0.0f, recipe.priceMultiplier)
        assertEquals(0, recipe.demand)
        assertEquals(0, recipe.specialPrice)
        assertEquals(false, recipe.shouldIgnoreDiscounts())
        assertEquals(emptyList(), recipe.ingredients)
    }
}
