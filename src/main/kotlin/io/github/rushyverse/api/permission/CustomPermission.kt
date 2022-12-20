package io.github.rushyverse.api.permission

/**
 * Custom permission of the server.
 */
public interface CustomPermission {
    public companion object {
        public const val GAMEMODE: String = "custom.gamemode"
        public const val KICK: String = "custom.kick"
        public const val STOP_SERVER: String = "custom.stop"
        public const val GIVE: String = "custom.give"
    }
}