package fr.distractic.bukkit.api.listener

import fr.distractic.bukkit.api.extension.keepProfession
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerCareerChangeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Listener to manage villager state.
 * @property plugin Plugin.
 */
public class VillagerListener : Listener, KoinComponent {

    private val plugin: JavaPlugin by inject()

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