package it.forgottenworld.fwparties.listener;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.controller.PartyController;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class PlayerListener implements Listener {

    private final FWParties plugin;

    public PlayerListener() {
        this.plugin = FWParties.getInstance();
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        Entity damagerEntity = event.getDamager();
        Entity damagedEntity = event.getEntity();
        if (damagedEntity instanceof Player) {
            Player damager;
            if (damagerEntity instanceof Projectile) {
                ProjectileSource projectileSource = ((Projectile) damagerEntity).getShooter();
                if (projectileSource instanceof Player) {
                    damager = (Player) projectileSource;
                } else {
                    return;
                }
            } else if (damagerEntity instanceof Player) {
                damager = (Player) damagerEntity;
            } else {
                return;
            }
            Player target = (Player) damagedEntity;
            if (plugin.getPartyController().areInSameParty(damager, target)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getEffects().stream().map(PotionEffect::getType).anyMatch(potionEffect -> potionEffect == PotionEffectType.BLINDNESS || potionEffect == PotionEffectType.HARM || potionEffect == PotionEffectType.POISON || potionEffect == PotionEffectType.SLOW)) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player shooter = (Player) event.getEntity().getShooter();
                event.getAffectedEntities().forEach(livingEntity -> {
                    if (livingEntity instanceof Player) {
                        Player damaged = (Player) livingEntity;
                        if (plugin.getPartyController().areInSameParty(shooter, damaged)) {
                            event.setIntensity(livingEntity, 0);
                        }
                    }
                });
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        PartyController partyController = plugin.getPartyController();
        if (partyController.isPlayerChatting(player.getUniqueId())) {
            event.setCancelled(true);
            partyController.sendMessageToPartyMembers(partyController.getPlayerParty(player.getUniqueId()).getLeader(), "&2[PARTY] &a" + player.getName() + ": " + message);
        }
    }
}
