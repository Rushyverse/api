package io.github.distractic.bukkit.api.extension

import io.github.distractic.bukkit.api.utils.getRandomString
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandSenderExtTest {

    @Nested
    inner class Message {

        @Test
        fun `send error message`() {
            val sender = mockk<CommandSender>()
            val slotComponent = slot<TextComponent>()
            justRun { sender.sendMessage(capture(slotComponent)) }

            val content = getRandomString()
            sender.sendMessageError(content)
            val component = slotComponent.captured
            assertEquals(NamedTextColor.RED, component.color())
            assertEquals(content, component.content())
        }
    }

    @Nested
    inner class Permissions {

    }
}