package com.github.rushyverse.api.entity

import com.extollit.gaming.ai.path.HydrazinePathFinder
import com.github.rushyverse.api.extension.HEIGHT_EYES_PLAYER
import com.github.rushyverse.api.extension.sync
import com.github.rushyverse.api.position.IAreaLocatable
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.pathfinding.NavigableEntity
import net.minestom.server.entity.pathfinding.Navigator
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.instance.Instance
import java.util.*
import java.util.concurrent.CompletableFuture

public open class NPCEntity(
    type: EntityType,
    public var areaTrigger: IAreaLocatable<Player>? = null,
    uuid: UUID = UUID.randomUUID()
) : LivingEntity(type, uuid), NavigableEntity {

    private val navigator: Navigator = Navigator(this)

    override fun getNavigator(): Navigator = navigator

    override fun update(time: Long) {
        sync {
            super.update(time)
            navigator.tick()
            areaTrigger?.let {
                it.position = position
                it.instance = instance
                val (enter, quit) = it.updateEntitiesInArea()
                enter.forEach { player -> onEnterArea(player) }
                quit.forEach { player -> onLeaveArea(player) }
            }
        }
    }

    override fun setInstance(instance: Instance): CompletableFuture<Void> {
        navigator.setPathFinder(HydrazinePathFinder(navigator.pathingEntity, instance.instanceSpace))
        return super.setInstance(instance)
    }

    public open fun lookNearbyPlayer() {
        val area = areaTrigger ?: throw IllegalStateException("An area detector must be set to use this method.")
        val nearbyEntity = area.entitiesInArea.firstOrNull() ?: return
        lookAt(nearbyEntity.position.withY { it + HEIGHT_EYES_PLAYER })
    }

    public open fun onInteract(event: PlayerEntityInteractEvent) {
    }

    public open fun onEnterArea(player: Player) {
    }

    public open fun onLeaveArea(player: Player) {
    }
}