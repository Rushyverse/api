package com.github.rushyverse.api.listener

import com.github.rushyverse.api.extension.keepProfession
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerCareerChangeEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * Listener to manage villager state.
 * @property plugin Plugin.
 */
public class VillagerListener(private val plugin: JavaPlugin) : Listener {


    /**
     * When a villager will have his profession changed, check if a specific tag is present into the entity.
     * If the tag is present, the event will be cancelled, otherwise the career will be changed.
     * @param event Event when a villager will lose his job.
     */
    @EventHandler
    public fun onChangeCareer(event: VillagerCareerChangeEvent) {
        event.isCancelled = event.entity.keepProfession(plugin)
    }
}