package com.github.rushyverse.api.player

import net.kyori.adventure.text.Component
import java.util.concurrent.atomic.AtomicReference

/**
 * Abstract class for handling client events individually for API dependant plugins.
 * This approach is necessary to handle client events individually
 * for each plugin depending on the API.
 *
 * The call event approach is therefore not favorable because
 * it broadcasts the event for all dependent plugins currently running.
 */
public abstract class PluginClientEvents {

    /**
     * Manage when the plugin creates a new client.
     * @param client The ready-to-use client.
     * @param joinMessage The message to display when creating this client,
     *                    it is directly affiliated to the PlayerJoinEvent.
     */
    public abstract suspend fun onJoin(client: Client, joinMessage: AtomicReference<Component?>)

    /**
     * Manage when the plugin removes a client.
     * @param client The client being deleted.
     * @param quitMessage The message to display when deleting this client,
     *                    it is directly affiliated to the PlayerQuitEvent.
     */
    public abstract suspend fun onQuit(client: Client, quitMessage: AtomicReference<Component?>)
}