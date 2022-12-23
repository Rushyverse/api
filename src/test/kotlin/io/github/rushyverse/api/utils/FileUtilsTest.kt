package io.github.rushyverse.api.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals

class FileUtilsTest {

    @Test
    fun `should get the current directory where is executed the program`(@TempDir tmpDirectory: File) {
        assertEquals(File(System.getProperty("user.dir")), workingDirectory)

        val initCurrentDirectory = System.getProperty("user.dir")

        System.setProperty("user.dir", tmpDirectory.absolutePath)
        assertEquals(tmpDirectory, workingDirectory)

        System.setProperty("user.dir", initCurrentDirectory)
    }
}