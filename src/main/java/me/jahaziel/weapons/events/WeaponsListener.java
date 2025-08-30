package me.jahaziel.weapons.events;

import me.jahaziel.weapons.items.CustomItems;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import me.jahaziel.weapons.WeaponsPlugin;

public class WeaponsListener implements Listener {

    @EventHandler
    public void onWeaponHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isCustomWeapon(item)) return;

        String name = item.getItemMeta().getDisplayName();

        if (name.contains("Scythe of Darkness")) {
            if (e.getEntity() instanceof LivingEntity le) {
                Location playerLoc = player.getLocation();
                Location targetLoc = le.getLocation();
                double dx = playerLoc.getX() - targetLoc.getX();
                double dy = playerLoc.getY() - targetLoc.getY();
                double dz = playerLoc.getZ() - targetLoc.getZ();
                le.setVelocity(new org.bukkit.util.Vector(dx, dy, dz).normalize().multiply(1.5));
            }
        }

        if (name.contains("Lifestealer")) {
            if (e.getEntity() instanceof LivingEntity le) {
                double heal = e.getFinalDamage() * 0.5;
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + heal));
            }
        }
    }

    @EventHandler
    public void onWeaponUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (!isCustomWeapon(item)) return;
        String name = item.getItemMeta().getDisplayName();

        if (name.contains("Wither Launcher") && e.getAction() == Action.RIGHT_CLICK_AIR) {
            Location loc = player.getEyeLocation().add(player.getLocation().getDirection());
            Wither skull = player.getWorld().spawn(loc, Wither.class);
            skull.setAI(false);
            skull.setSilent(true);
            skull.setInvulnerable(true);
            skull.setCustomName("WitherProjectile");
        }

        if (name.contains("Scythe of Darkness") && e.getAction() == Action.RIGHT_CLICK_AIR) {
            // Particle wave
            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if (count++ > 10) cancel();
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(player.getLocation().getDirection()), 10, 1, 1, 1, 0.1);
                    for (Entity entity : player.getNearbyEntities(5,5,5)) {
                        if (entity instanceof LivingEntity le && le != player) {
                            le.damage(4);
                            le.setVelocity(player.getLocation().getDirection().multiply(1.2));
                        }
                    }
                }
            }.runTaskTimer(WeaponsPlugin.getInstance(), 0L, 2L);
        }
    }

    private boolean isCustomWeapon(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String name = item.getItemMeta().getDisplayName();
        return name.contains("Scythe") || name.contains("Lifestealer") || name.contains("Wither Launcher") || name.contains("King's Crown");
    }
}
