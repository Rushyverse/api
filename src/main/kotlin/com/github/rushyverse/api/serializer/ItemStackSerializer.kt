package com.github.rushyverse.api.serializer

import com.destroystokyo.paper.Namespaced
import com.github.rushyverse.api.extension.ItemStack
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

    private val materialSerializer: KSerializer<Material> get() = MaterialSerializer

    private val amountSerializer: KSerializer<Int> get() = Int.serializer()

    private val enchantmentsSerializer: KSerializer<Map<Enchantment, Int>?> = MapSerializer(EnchantmentSerializer, Int.serializer()).nullable

    private val unbreakableSerializer: KSerializer<Boolean?> = Boolean.serializer().nullable

    private val customMetaModelSerializer: KSerializer<Int?> = Int.serializer().nullable

    private val destroyableKeysSerializer: KSerializer<List<Namespaced>?> = ListSerializer(NamespacedSerializer).nullable

    private val placeableKeysSerializer: KSerializer<List<Namespaced>?> get() = destroyableKeysSerializer

    private val displayNameSerializer: KSerializer<Component?> = ComponentSerializer.nullable

    private val loreSerializer: KSerializer<List<Component>?> = ListSerializer(ComponentSerializer).nullable

    private val durabilitySerializer: KSerializer<Double?> = Double.serializer().nullable

    private val textureSerializer: KSerializer<String?> = String.serializer().nullable

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("itemstack") {
        element("material", materialSerializer.descriptor)
        element("amount", amountSerializer.descriptor)
        element("enchantments", enchantmentsSerializer.descriptor)
        element("unbreakable", unbreakableSerializer.descriptor)
        element("customMetaModel", customMetaModelSerializer.descriptor)
        element("destroyableKeys", destroyableKeysSerializer.descriptor)
        element("placeableKeys", placeableKeysSerializer.descriptor)
        element("displayName", displayNameSerializer.descriptor)
        element("lore", loreSerializer.descriptor)
        // For item
        element("durability", durabilitySerializer.descriptor)
        // For Skull item
        element("texture", textureSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: ItemStack) {
        val itemMeta = if (value.hasItemMeta()) value.itemMeta else null

        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, materialSerializer, value.type)
            encodeSerializableElement(descriptor, 1, amountSerializer, value.amount)
            encodeSerializableElement(descriptor, 2, enchantmentsSerializer, value.enchantments)
            encodeSerializableElement(descriptor, 3, unbreakableSerializer, itemMeta?.isUnbreakable)
            encodeSerializableElement(descriptor, 4, customMetaModelSerializer, itemMeta?.customModelData)
            encodeSerializableElement(
                descriptor,
                5,
                destroyableKeysSerializer,
                itemMeta?.destroyableKeys?.toList() ?: emptyList()
            )
            encodeSerializableElement(
                descriptor,
                6,
                placeableKeysSerializer,
                itemMeta?.placeableKeys?.toList()
            )
            encodeSerializableElement(descriptor, 7, displayNameSerializer, itemMeta?.displayName())
            encodeSerializableElement(descriptor, 8, loreSerializer, itemMeta?.lore())
            // For item
            encodeSerializableElement(
                descriptor,
                9,
                durabilitySerializer,
                itemMeta?.let { it as? Damageable }?.health
            )
            // For Skull item
            encodeSerializableElement(
                descriptor,
                10,
                textureSerializer,
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
            var lore: Collection<Component>? = null
            // For item
            var durability: Double? = null
            // For Skull item
            var texture: String? = null

            if (decodeSequentially()) {
                material = decodeSerializableElement(descriptor, 0, materialSerializer)
                amount = decodeSerializableElement(descriptor, 1, amountSerializer)
                enchantments = decodeSerializableElement(descriptor, 2, enchantmentsSerializer)
                unbreakable = decodeSerializableElement(descriptor, 3, unbreakableSerializer)
                customMetaModel = decodeSerializableElement(descriptor, 4, customMetaModelSerializer)
                destroyableKeys = decodeSerializableElement(descriptor, 5, destroyableKeysSerializer)
                placeableKeys = decodeSerializableElement(descriptor, 6, placeableKeysSerializer)
                displayName = decodeSerializableElement(descriptor, 7, displayNameSerializer)
                lore = decodeSerializableElement(descriptor, 8, loreSerializer)
                durability = decodeSerializableElement(descriptor, 9, durabilitySerializer)
                texture = decodeSerializableElement(descriptor, 10, textureSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> material = decodeSerializableElement(descriptor, index, materialSerializer)
                        1 -> amount = decodeSerializableElement(descriptor, index, amountSerializer)
                        2 -> enchantments = decodeSerializableElement(
                            descriptor,
                            index,
                            enchantmentsSerializer
                        )
                        3 -> unbreakable = decodeSerializableElement(descriptor, index, unbreakableSerializer)
                        4 -> customMetaModel = decodeSerializableElement(descriptor, index, customMetaModelSerializer)
                        5 -> destroyableKeys = decodeSerializableElement(descriptor, index, destroyableKeysSerializer)
                        6 -> placeableKeys = decodeSerializableElement(descriptor, index, placeableKeysSerializer)
                        7 -> displayName = decodeSerializableElement(descriptor, index, displayNameSerializer)
                        8 -> lore = decodeSerializableElement(descriptor, index, loreSerializer)
                        9 -> durability = decodeSerializableElement(descriptor, index, durabilitySerializer)
                        10 -> texture = decodeSerializableElement(descriptor, index, textureSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
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
                    lore?.toList()?.also(it::lore)

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
