package com.github.rushyverse.api.entity

import com.extollit.gaming.ai.path.HydrazinePathFinder
import com.github.rushyverse.api.extension.sync
import com.github.rushyverse.api.position.IAreaLocatable
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.pathfinding.NavigableEntity
import net.minestom.server.entity.pathfinding.Navigator
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.instance.Instance
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * A non-playable character.
 * @property areaTrigger Area to know which players are near of the entity.
 * @property navigator Navigator of the entity.
 */
public open class NPCEntity(
    type: EntityType,
    public var areaTrigger: IAreaLocatable<Player>? = null,
    uuid: UUID = UUID.randomUUID()
) : LivingEntity(type, uuid), NavigableEntity {

    private val navigator: Navigator = Navigator(this)

    override fun getNavigator(): Navigator = navigator

    override fun update(time: Long) {
        val position: Pos
        val instance: Instance
        sync {
            super.update(time)
            navigator.tick()
            position = this.position
            instance = this.instance
        }

        updateAreaEntities(position, instance)
    }

    /**
     * If the [areaTrigger] is not null, update the entities in the area.
     * @param position Position of the entity.
     * @param instance Instance where is located the entity.
     */
    private fun updateAreaEntities(position: Pos, instance: Instance) {
        areaTrigger?.let {
            it.position = position
            it.instance = instance
            val (enter, quit) = it.updateEntitiesInArea()
            enter.forEach(this::onEnterArea)
            quit.forEach(this::onLeaveArea)
        }
    }

    override fun setInstance(instance: Instance): CompletableFuture<Void> {
        navigator.setPathFinder(HydrazinePathFinder(navigator.pathingEntity, instance.instanceSpace))
        return super.setInstance(instance)
    }

    /**
     * Look at the nearest player.
     */
    public open fun lookNearbyPlayer() {
        val area = areaTrigger ?: throw IllegalStateException("An area detector must be set to use this method.")
        val nearbyEntity = area.entitiesInArea.firstOrNull() ?: return
        lookAt(nearbyEntity)
    }

    /**
     * Called when a player interact with the entity.
     * @param event Event of the interaction.
     */
    public open fun onInteract(event: PlayerEntityInteractEvent) {
    }

    /**
     * Called when a player enter the area of the entity.
     * @param player Player who enter the area.
     */
    public open fun onEnterArea(player: Player) {
    }

    /**
     * Called when a player leave the area of the entity.
     * @param player Player who leave the area.
     */
    public open fun onLeaveArea(player: Player) {
    }
}