package com.github.rushyverse.api.entity

import com.github.rushyverse.api.extension.AddPlayerTextureProperty
import com.github.rushyverse.api.position.IAreaLocatable
import com.github.rushyverse.api.utils.randomPos
import com.github.rushyverse.api.utils.randomString
import net.kyori.adventure.text.Component
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.PlayerInfoPacket
import net.minestom.server.network.packet.server.play.PlayerInfoPacket.AddPlayer
import net.minestom.testing.Env
import net.minestom.testing.EnvTest
import org.junit.jupiter.api.Nested
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull


class PlayerNPCEntityTest : CommonNPCEntityTest() {

    @Nested
    inner class Instantiation {

        @Test
        fun `should set properties as empty list if not defined`() {
            val npc = PlayerNPCEntity(randomString())
            assertEquals(emptyList(), npc.properties)
        }

        @Test
        fun `should set area trigger as null if not defined`() {
            val npc = PlayerNPCEntity(randomString())
            assertNull(npc.areaTrigger)
        }

        @Test
        fun `should set inTabList as false if not defined`() {
            val npc = PlayerNPCEntity(randomString())
            assertFalse { npc.inTabList }
        }
    }

    @Nested
    @EnvTest
    inner class UpdateNewViewer {

        @Nested
        inner class AddPlayerPacket {

            @Test
            fun `should send add player packet to new viewer with default values`(env: Env) {
                assertPacketSent(
                    env,
                    UUID.randomUUID(),
                    randomString(),
                    emptyList(),
                    null
                )
            }

            @Test
            fun `should send add player packet to new viewer with textures property`(env: Env) {
                assertPacketSent(
                    env,
                    UUID.randomUUID(),
                    randomString(),
                    listOf(
                        AddPlayerTextureProperty(randomString(), randomString())
                    ),
                    null
                )
            }

            @Test
            fun `should send add player packet to new viewer with custom name`(env: Env) {
                assertPacketSent(
                    env,
                    UUID.randomUUID(),
                    randomString(),
                    emptyList(),
                    Component.text(randomString())
                )
            }

            private fun assertPacketSent(
                env: Env,
                npcUUID: UUID,
                npcName: String,
                npcProperties: List<AddPlayer.Property>,
                npcCustomName: Component?
            ) {
                val instance = env.createFlatInstance()
                val connection = env.createConnection()
                val player = connection.connect(instance, randomPos()).join()

                val npc = PlayerNPCEntity(npcName, npcProperties, null, npcUUID, false)
                npc.customName = npcCustomName

                val packetTracker = connection.trackIncoming(PlayerInfoPacket::class.java)
                npc.updateNewViewer(player)

                packetTracker.assertSingle {
                    assertEquals(
                        PlayerInfoPacket(
                            PlayerInfoPacket.Action.ADD_PLAYER,
                            listOf(
                                AddPlayer(
                                    npcUUID,
                                    npcName,
                                    npcProperties,
                                    GameMode.CREATIVE,
                                    0,
                                    npcCustomName ?: Component.text(npcName),
                                    null
                                )
                            )
                        ), it
                    )
                }
            }

        }

        @Nested
        inner class RemovePlayerPacket {

            @Test
            fun `should send remove packet to new viewer`(env: Env) {
                assertPacketSent(
                    env,
                    UUID.randomUUID(),
                    false
                )
            }

            @Test
            fun `should not send remove packet to new viewer`(env: Env) {
                assertPacketSent(
                    env,
                    UUID.randomUUID(),
                    true
                )
            }

            private fun assertPacketSent(
                env: Env,
                npcUUID: UUID,
                inTabList: Boolean
            ) {
                val instance = env.createFlatInstance()
                val connection = env.createConnection()
                val player = connection.connect(instance, randomPos()).join()

                val npc = PlayerNPCEntity(randomString(), inTabList = inTabList, uuid = npcUUID)

                npc.updateNewViewer(player)

                val packetTracker = connection.trackIncoming(PlayerInfoPacket::class.java)
                npc.scheduler().processTick()

                if (inTabList) {
                    packetTracker.assertEmpty()
                } else {
                    packetTracker.assertSingle {
                        assertEquals(
                            PlayerInfoPacket(
                                PlayerInfoPacket.Action.REMOVE_PLAYER,
                                listOf(PlayerInfoPacket.RemovePlayer(npcUUID))
                            ), it
                        )
                    }
                }
            }

        }

    }

    override fun createEntity(area: IAreaLocatable<Player>?): NPCEntity {
        return PlayerNPCEntity(randomString(), areaTrigger = area)
    }
}