package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.randomString
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

            val content = randomString()
            sender.sendMessageError(content)
            val component = slotComponent.captured
            assertEquals(NamedTextColor.RED, component.color())
            assertEquals(content, component.content())
        }
    }
}
