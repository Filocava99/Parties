package it.filippocavallari.listener

import it.filippocavallari.Parties
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.potion.PotionEffectType

class PlayerListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamagedByEntity(event: EntityDamageByEntityEvent) {
        val damagerEntity = event.damager
        val damagedEntity = event.entity
        if (damagedEntity is Player) {
            val damager: Player = if (damagerEntity is Projectile) {
                val projectileSource = damagerEntity.shooter
                if (projectileSource is Player) {
                    projectileSource
                } else {
                    return
                }
            } else if (damagerEntity is Player) {
                damagerEntity
            } else {
                return
            }
            if (Parties.INSTANCE.config.config.getStringList("always_on_friendly_fire_worlds")
                    .contains(damagedEntity.location.world?.name)
            ) {
                return
            }
            if (Parties.INSTANCE.partyManager.arePlayersInSameParty(damager.uniqueId, damagedEntity.uniqueId)) {
                event.isCancelled = true
            }
        }
    }

//    @EventHandler
//    fun onEntityDamaged(event: EntityDamageEvent) {
//        if (event.entity is Player) {
//            updatePlayerHealthInScoreboard(event.entity as Player)
//        }
//    }

//    @EventHandler
//    fun onRegen(event: EntityRegainHealthEvent) {
//        if (event.entity is Player) {
//            updatePlayerHealthInScoreboard(event.entity as Player)
//        }
//    }

    @EventHandler
    fun onPotionSplash(event: PotionSplashEvent) {
        if (event.entity.effects.stream().map { it -> it.type }
                .anyMatch { potionEffect -> potionEffect === PotionEffectType.BLINDNESS || potionEffect === PotionEffectType.HARM || potionEffect === PotionEffectType.POISON || potionEffect === PotionEffectType.SLOW }){
            if(event.entity.shooter is Player){
                val shooter = event.entity.shooter as Player
                val affectedEntities = event.affectedEntities
                affectedEntities.forEach { livingEntity ->
                    run{
                        if(livingEntity is Player){
                            val damaged = livingEntity as Player
                            if(Parties.INSTANCE.config.config.getStringList("always_on_friendly_fire_worlds").contains(damaged.location.world?.name)){
                                return;
                            }
                            if(Parties.INSTANCE.partyManager.arePlayersInSameParty(shooter.uniqueId, damaged.uniqueId)){
                                affectedEntities.remove(livingEntity)
                            }
                        }
                    }
                 }
                try{
                    val affectedEntitiesField = event::class.java.getDeclaredField("affectedEntities")
                    affectedEntitiesField.isAccessible = true
                    affectedEntitiesField.set(event, affectedEntities)
                }catch (ignored: Exception){ }
            }
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val message = event.message
        val partyManager = Parties.INSTANCE.partyManager
        val chatManager = Parties.INSTANCE.chatManager
        if (partyManager.isPlayerInParty(player.uniqueId)) {
            if (chatManager.isPlayerChatting(player.uniqueId)) {
                event.isCancelled = true
                partyManager.getPlayerParty(player.uniqueId)?.let {
                    chatManager.sendMessageToPartyMembers(
                        it.leader,
                    "&2[PARTY] &a" + player.name + ": " + message
                    )
                }
            }
        }
    }

//    private fun updatePlayerHealthInScoreboard(player: Player) {
//        val partyController: PartyController = plugin.getPartyController()
//        if (partyController.isPlayerInParty(player.uniqueId)) {
//            partyController.updatePlayerHealthInScoreboard(player)
//        }
//    }

}