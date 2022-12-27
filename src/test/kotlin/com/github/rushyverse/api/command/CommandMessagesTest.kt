package com.github.rushyverse.api.command

import io.mockk.every
import io.mockk.mockk
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandMessagesTest {

    @Nested
    inner class SendPlayerNotFoundMessage {

        @Test
        fun `should send component translatable if sender is player`() {
            val sender = mockk<Player>()
            var component: Component? = null
            every { sender.sendMessage(any<Component>()) } answers {
                component = arg(0)
            }
            CommandMessages.sendPlayerNotFoundMessage(sender)

            assertEquals(Component.translatable("argument.entity.notfound.player", NamedTextColor.RED), component)
        }

        @Test
        fun `should send component text if sender is not player`() {
            val sender = mockk<CommandSender>()
            var component: Component? = null
            every { sender.sendMessage(any<Component>()) } answers {
                component = arg(0)
            }
            CommandMessages.sendPlayerNotFoundMessage(sender)

            assertEquals(Component.text("No player was found", NamedTextColor.RED), component)
        }

    }

    @Test
    fun `should send missing permission message`() {
        val sender = mockk<CommandSender>()
        var component: Component? = null
        every { sender.sendMessage(any<Component>()) } answers {
            component = arg(0)
        }
        CommandMessages.sendMissingPermissionMessage(sender)

        assertEquals(Component.translatable("commands.help.failed", NamedTextColor.RED), component)
    }
}