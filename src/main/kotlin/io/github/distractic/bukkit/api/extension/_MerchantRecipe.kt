package io.github.distractic.bukkit.api.extension

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

/**
 * Constructor helper to create an instance of [MerchantRecipe][org.bukkit.inventory.MerchantRecipe].
 * @param result ItemStack
 * @param maxUses The amount by which the demand influences
 * the amount of the first ingredient is scaled by the recipe's.
 * @param uses Number of times this trade has been used.
 * @param experienceReward Whether to reward experience to the player for the trade.
 * @param villagerExperience Amount of experience the villager earns from this trade.
 * @param priceMultiplier price multiplier, can never be below zero.
 * @param demand This value is periodically updated by the
 * villager that owns this merchant recipe based on how often the recipe has
 * been used since it has been last restocked in relation to its.
 * @param specialPrice This value is dynamically
 * updated whenever a player starts and stops trading with a villager that owns
 * this merchant recipe. It is based on the player's individual reputation with
 * the villager, and the player's currently active status effects (see
 * {@link PotionEffectType#HERO_OF_THE_VILLAGE}). The influence of the player's
 * reputation on the special price is scaled by the recipe's.
 * @param ignoreDiscounts Whether all discounts on this trade should be ignored.
 * @param ingredients List of ingredients necessary to obtain [result] item.
 * @return New instance of [MerchantRecipe][org.bukkit.inventory.MerchantRecipe].
 */
public fun MerchantRecipe(
    result: ItemStack,
    maxUses: Int,
    uses: Int = 0,
    experienceReward: Boolean = false,
    villagerExperience: Int = 0,
    priceMultiplier: Float = 0.0F,
    demand: Int = 0,
    specialPrice: Int = 0,
    ignoreDiscounts: Boolean = false,
    ingredients: List<ItemStack> = emptyList()
): MerchantRecipe = MerchantRecipe(
    result,
    uses,
    maxUses,
    experienceReward,
    villagerExperience,
    priceMultiplier,
    demand,
    specialPrice,
    ignoreDiscounts
).also {
    it.ingredients = ingredients
}