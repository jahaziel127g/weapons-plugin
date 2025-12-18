package me.jahaziel.weapons.events;

import me.jahaziel.weapons.WeaponsPlugin;
import me.jahaziel.weapons.items.CustomItems;
import me.jahaziel.weapons.managers.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class WeaponsListener implements Listener {
    private final WeaponsPlugin plugin;

    public WeaponsListener(WeaponsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player))
            return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon == null)
            return;

        String id = CustomItems.getId(weapon);
        if (id == null)
            return;

        // SCYTHE OF LIGHT — mace-like hits but sword cooldown
        if (id.equals("scythe_of_light")) {
            // e.setDamage(e.getDamage() + 3.0); // handled by attributes now matches Sword
            // (8)
            if (e.getEntity() instanceof LivingEntity tgt) {
                Location p = tgt.getLocation().add(0, 1, 0);
                // replaced removed ITEM_CRACK with END_ROD for sparkle
                tgt.getWorld().spawnParticle(Particle.END_ROD, p, 12, 0.4, 0.4, 0.4, 0.0);
                tgt.getWorld().playSound(p, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1f);
            }
        }

        // SCYTHE OF DARKNESS — reaping (pull) on hit
        if (id.equals("scythe_of_darkness")) {
            if (e.getEntity() instanceof LivingEntity tgt) {
                Vector pull = player.getLocation().toVector().subtract(tgt.getLocation().toVector()).normalize()
                        .multiply(1.6);
                tgt.setVelocity(pull);
                // shadow particle using CAMPFIRE_COSY_SMOKE (stable)
                tgt.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, tgt.getLocation().add(0, 1, 0), 8, 0.3, 0.3,
                        0.3, 0.02);
            }
        }

        // LIFESTEALER — heal 50% of damage dealt
        if (id.equals("lifestealer")) {
            double heal = e.getFinalDamage() * 0.5;
            double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            player.setHealth(Math.min(player.getHealth() + heal, max));
            // use END_ROD + HEART instead of ITEM_CRACK
            player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 6, 0.3, 0.3, 0.3, 0.0);
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 8, 0.2, 0.2, 0.2, 0.0);
        }

        // WITHER LAUNCHER — backup on-hit effect
        if (id.equals("wither_launcher")) {
            if (!CooldownManager.isOnCooldown("wither_launcher", player.getUniqueId())) {
                if (e.getEntity() instanceof LivingEntity tgt) {
                    tgt.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0));
                    CooldownManager.setCooldown("wither_launcher", player.getUniqueId(), 30L);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND)
            return;
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null)
            return;
        String id = CustomItems.getId(item);
        if (id == null)
            return;

        UUID uuid = player.getUniqueId();

        // SCYTHE OF DARKNESS — right-click wave that transfers potion effects & costs
        // 25% health
        if (id.equals("scythe_of_darkness")) {
            if (CooldownManager.isOnCooldown("scythe_dark_wave", uuid)) {
                player.sendMessage(
                        "§cScythe wave on cooldown (" + CooldownManager.getRemaining("scythe_dark_wave", uuid) + "s)");
                return;
            }
            Location eye = player.getEyeLocation();
            Vector dir = eye.getDirection().normalize();

            // visual: feather trail replaced with END_ROD + CAMPFIRE_COSY_SMOKE
            for (int i = 1; i <= 6; i++) {
                Location p = eye.clone().add(dir.clone().multiply(i));
                p.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, p, 10, 0.4, 0.4, 0.4, 0.02);
                p.getWorld().spawnParticle(Particle.END_ROD, p, 6, 0.2, 0.2, 0.2, 0.0);
            }

            // affect targets
            for (Entity ent : player.getNearbyEntities(5, 2, 5)) {
                if (ent instanceof LivingEntity tgt && !tgt.equals(player)) {
                    tgt.damage(6, player);
                    // transfer potion effects
                    for (PotionEffect eff : player.getActivePotionEffects()) {
                        tgt.addPotionEffect(new PotionEffect(eff.getType(), eff.getDuration(), eff.getAmplifier()));
                    }
                }
            }

            // cost player 25% health
            double cost = Math.max(1.0, player.getHealth() * 0.25);
            player.damage(cost);
            CooldownManager.setCooldown("scythe_dark_wave", uuid, 10L);

            // cosmetic: sound and title
            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1f);
            player.sendTitle("§5Dark Wave", "§7You feel the shadows consume you", 5, 40, 5);
            return;
        }

        // LIFESTEALER right-click: temporary absorption hearts
        if (id.equals("lifestealer")) {
            if (CooldownManager.isOnCooldown("lifestealer_m2", uuid)) {
                player.sendMessage("§cLifestealer on cooldown");
                return;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 1)); // stronger absorption
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3,
                    0.0);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
            CooldownManager.setCooldown("lifestealer_m2", uuid, 60L);
            return;
        }

        // WITHER LAUNCHER right-click: spawn wither skull (non-destructive)
        if (id.equals("wither_launcher")) {
            if (CooldownManager.isOnCooldown("wither_launcher", uuid)) {
                player.sendMessage("§cWither Launcher on cooldown");
                return;
            }
            Location spawn = player.getEyeLocation().add(player.getLocation().getDirection());
            WitherSkull skull = player.getWorld().spawn(spawn, WitherSkull.class);
            skull.setShooter(player);
            skull.setVelocity(player.getLocation().getDirection().multiply(1.6));
            skull.setIsIncendiary(false);

            // visual trail around launch (END_ROD)
            player.getWorld().spawnParticle(Particle.END_ROD, spawn, 12, 0.5, 0.5, 0.5, 0.0);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 0.9f);

            // when skull 'hits' apply wither effects and remove skull (delayed simulate)
            Bukkit.getScheduler().runTaskLater(WeaponsPlugin.getInstance(), () -> {
                for (Entity ent : skull.getNearbyEntities(3, 3, 3)) {
                    if (ent instanceof LivingEntity t)
                        t.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 1));
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 1));
                skull.remove();
            }, 12L);

            CooldownManager.setCooldown("wither_launcher", uuid, 30L);
            return;
        }

        if (id.equals("kings_crown")) {
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1.2, 0), 6, 0.2, 0.4, 0.2,
                    0.0);
        }
    }

    @EventHandler
    public void onItemDespawn(org.bukkit.event.entity.ItemDespawnEvent e) {
        ItemStack item = e.getEntity().getItemStack();
        String id = CustomItems.getId(item);
        if (id != null) {
            me.jahaziel.weapons.items.WeaponStorage.unmarkCrafted(id);
            Bukkit.broadcastMessage("§cThe ancient " + id.replace("_", " ") + " has returned to the void (Despawned).");
        }
    }

    @EventHandler
    public void onItemDamage(org.bukkit.event.entity.EntityDamageEvent e) {
        if (e.getEntity() instanceof Item itemEntity) {
            // Items do not implement Damageable in this version, so we check the cause.
            // These causes usually destroy items instantly.
            switch (e.getCause()) {
                case LAVA:
                case VOID:
                case FIRE:
                case FIRE_TICK:
                case ENTITY_EXPLOSION:
                case BLOCK_EXPLOSION:
                case CONTACT:
                    ItemStack item = itemEntity.getItemStack();
                    String id = CustomItems.getId(item);
                    if (id != null) {
                        me.jahaziel.weapons.items.WeaponStorage.unmarkCrafted(id);
                        Bukkit.broadcastMessage("§cThe ancient " + id.replace("_", " ") + " has been destroyed by "
                                + e.getCause().name().toLowerCase() + ".");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
        if (e.getEntity().getLastDamageCause() != null &&
                e.getEntity().getLastDamageCause()
                        .getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.VOID) {
            // Logic handled by potential EntityDamageEvent/despawn if items drop in void
            // But if we want to catch it specifically:
            for (ItemStack drop : e.getDrops()) {
                String id = CustomItems.getId(drop);
                if (id != null) {
                    // If we unmark here, we should probably remove it from drops so it doesn't
                    // duplicate-destroy
                    // or we accept multiple messages.
                    // A cleaner way is just rely on the item entity falling into void if it drops.
                    // But if keepInventory is on, or it's cleared, we might miss it.
                    // Implementation plan said: "Iterate drops. If custom item found..."
                    me.jahaziel.weapons.items.WeaponStorage.unmarkCrafted(id);
                    Bukkit.broadcastMessage("§cThe ancient " + id.replace("_", " ") + " was lost in the void with "
                            + e.getEntity().getName() + ".");
                }
            }
        }
    }
}
