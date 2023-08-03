package com.github.rushyverse.api.serializer

import com.destroystokyo.paper.Namespaced
import com.github.rushyverse.api.extension.ItemStack
import com.github.rushyverse.api.extension.asMiniString
import com.github.rushyverse.api.extension.getTexturesProperty
import com.github.rushyverse.api.extension.setTextures
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Damageable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

/**
 * Serializer for [ItemStack].
 */
public object ItemStackSerializer : KSerializer<ItemStack> {

    private val intSerializer: KSerializer<Int> get() = Int.serializer()

    private val doubleSerializer: KSerializer<Double> get() = Double.serializer()

    private val booleanSerializer: KSerializer<Boolean> get() = Boolean.serializer()

    private val stringSerializer: KSerializer<String> get() = String.serializer()

    private val materialSerializer: MaterialSerializer get() = MaterialSerializer

    private val namespacedKeySerializer: NamespacedSerializer get() = NamespacedSerializer

    private val componentSerializer: ComponentSerializer get() = ComponentSerializer

    private val mapEnchantmentSerializer: KSerializer<Map<Enchantment, Int>>
        get() = MapSerializer(
            EnchantmentSerializer,
            intSerializer
        )

    private val listNamespacedKeySerializer: KSerializer<List<Namespaced>>
        get() = ListSerializer(
            namespacedKeySerializer
        )

    private val listComponentSerializer: KSerializer<List<Component>> get() = ListSerializer(componentSerializer)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("itemstack") {
        element("material", materialSerializer.descriptor)
        element("amount", intSerializer.descriptor)
        element("enchantments", mapEnchantmentSerializer.descriptor)
        element("unbreakable", booleanSerializer.descriptor)
        element("customMetaModel", intSerializer.descriptor)
        element("destroyableKeys", listNamespacedKeySerializer.descriptor)
        element("placeableKeys", listNamespacedKeySerializer.descriptor)
        element("displayName", componentSerializer.descriptor)
        element("lore", listComponentSerializer.descriptor)
        // For item
        element("durability", doubleSerializer.descriptor)
        // For Skull item
        element("texture", stringSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: ItemStack) {
        val itemMeta = if (value.hasItemMeta()) value.itemMeta else null

        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, materialSerializer, value.type)
            encodeSerializableElement(descriptor, 1, intSerializer, value.amount)
            encodeSerializableElement(descriptor, 2, mapEnchantmentSerializer, value.enchantments)
            encodeSerializableElement(descriptor, 3, booleanSerializer.nullable, itemMeta?.isUnbreakable)
            encodeSerializableElement(descriptor, 4, intSerializer.nullable, itemMeta?.customModelData)
            encodeSerializableElement(
                descriptor,
                5,
                listNamespacedKeySerializer,
                itemMeta?.destroyableKeys?.toList() ?: emptyList()
            )
            encodeSerializableElement(
                descriptor,
                6,
                listNamespacedKeySerializer.nullable,
                itemMeta?.placeableKeys?.toList()
            )
            encodeSerializableElement(descriptor, 7, stringSerializer.nullable, itemMeta?.displayName()?.asMiniString())
            encodeSerializableElement(descriptor, 8, listComponentSerializer.nullable, itemMeta?.lore())
            // For item
            encodeSerializableElement(
                descriptor,
                9,
                doubleSerializer.nullable,
                itemMeta?.let { it as? Damageable }?.health
            )
            // For Skull item
            encodeSerializableElement(
                descriptor,
                10,
                stringSerializer.nullable,
                itemMeta?.let { it as? SkullMeta }?.playerProfile?.getTexturesProperty()?.value
            )
        }
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        return decoder.decodeStructure(descriptor) {
            var material: Material? = null
            var amount = 1
            var enchantments: Map<Enchantment, Int>? = null
            var unbreakable: Boolean? = null
            var customMetaModel: Int? = null
            var destroyableKeys: Collection<Namespaced>? = null
            var placeableKeys: Collection<Namespaced>? = null
            var displayName: Component? = null
            var lore: MutableList<Component>? = null
            // For item
            var durability: Double? = null
            // For Skull item
            var texture: String? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> material = decodeSerializableElement(descriptor, index, materialSerializer)
                    1 -> amount = decodeSerializableElement(descriptor, index, intSerializer)
                    2 -> enchantments = decodeSerializableElement(
                        descriptor,
                        index,
                        mapEnchantmentSerializer
                    )

                    3 -> unbreakable = decodeSerializableElement(descriptor, index, booleanSerializer)
                    4 -> customMetaModel = decodeSerializableElement(descriptor, index, intSerializer.nullable)
                    5 -> destroyableKeys = decodeSerializableElement(descriptor, index, listNamespacedKeySerializer)
                    6 -> placeableKeys = decodeSerializableElement(descriptor, index, listNamespacedKeySerializer)
                    7 -> displayName = decodeSerializableElement(descriptor, index, componentSerializer)
                    8 -> lore = decodeSerializableElement(descriptor, index, listComponentSerializer).toMutableList()
                    9 -> durability = decodeSerializableElement(descriptor, index, doubleSerializer)
                    10 -> texture = decodeSerializableElement(descriptor, index, stringSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            material ?: throw SerializationException("The field material is missing")

            ItemStack(material) {
                this.amount = amount
                this.editMeta {
                    enchantments?.forEach { (enchant, level) ->
                        it.addEnchant(enchant, level, true)
                    }
                    unbreakable?.also(it::setUnbreakable)
                    customMetaModel?.also(it::setCustomModelData)
                    destroyableKeys?.also(it::setDestroyableKeys)
                    placeableKeys?.also(it::setPlaceableKeys)
                    displayName?.also(it::displayName)
                    lore?.also(it::lore)

                    if (it is Damageable) {
                        durability?.also(it::damage)
                    }

                    if (it is SkullMeta) {
                        texture?.let { texture ->
                            val profile = Bukkit.createProfile(UUID.randomUUID())
                            profile.setTextures(texture)
                            it.playerProfile = profile
                        }
                    }
                }
            }
        }
    }
}
