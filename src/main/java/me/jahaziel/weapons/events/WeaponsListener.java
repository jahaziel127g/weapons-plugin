package me.jahaziel.weapons.events;

import me.jahaziel.weapons.WeaponsPlugin;
import me.jahaziel.weapons.items.CustomItems;
import me.jahaziel.weapons.managers.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class WeaponsListener implements Listener {

    private static final String CD_SCYTHE_DARK_WAVE = "scythe_dark_wave";
    private static final String CD_WITHER_LAUNCH = "wither_launch";
    private static final String CD_LIFESTEAL_RIGHT = "lifesteal_right";
    private static final String CD_SCYTHE_LIGHT = "scythe_light_attack";

    private final Set<UUID> ourSkulls = new HashSet<>();
    private final Map<UUID, Long> lifestealAbsorbUntil = new HashMap<>();

    public WeaponsListener() {
        // Reapply crown passives periodically (every 5s)
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    applyOrRemoveCrownEffects(p);
                }
            }
        }.runTaskTimer(WeaponsPlugin.getInstance(), 0L, 20L * 5);
    }

    private boolean hasItem(ItemStack item, String id) {
        return CustomItems.isCustomItem(item, id);
    }

    private void applyOrRemoveCrownEffects(Player p) {
        ItemStack helm = p.getInventory().getHelmet();
        boolean wearing = hasItem(helm, "kings_crown");
        if (wearing) {
            // Strength II, Speed II, Fire Resistance while worn (reapplied)
            p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1, true, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1, true, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0, true, false, false));
        }
    }

    // Right-click abilities
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) return;

        // Scythe of Darkness
        if (hasItem(item, "scythe_of_darkness")) {
            UUID uuid = player.getUniqueId();
            if (CooldownManager.isOnCooldown(CD_SCYTHE_DARK_WAVE, uuid)) {
                long rem = CooldownManager.getRemaining(CD_SCYTHE_DARK_WAVE, uuid) / 1000;
                player.sendMessage(ChatColor.RED + "Scythe wave on cooldown (" + rem + "s).");
                return;
            }
            event.setCancelled(true);
            shootParticleWave(player, 10, 0.5);
            double newHealth = Math.max(1.0, player.getHealth() - (player.getHealth() * 0.25));
            player.setHealth(newHealth);
            CooldownManager.setCooldown(CD_SCYTHE_DARK_WAVE, uuid, 30_000);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 1f);
            return;
        }

        // Wither Launcher
        if (hasItem(item, "wither_launcher")) {
            UUID uuid = player.getUniqueId();
            if (CooldownManager.isOnCooldown(CD_WITHER_LAUNCH, uuid)) {
                long rem = CooldownManager.getRemaining(CD_WITHER_LAUNCH, uuid) / 1000;
                player.sendMessage(ChatColor.RED + "Wither Launcher on cooldown (" + rem + "s).");
                return;
            }
            event.setCancelled(true);
            launchWitherSkull(player);
            CooldownManager.setCooldown(CD_WITHER_LAUNCH, uuid, 30_000);
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0)); // user takes 10s wither
            return;
        }

        // Lifestealer right-click
        if (hasItem(item, "lifestealer")) {
            UUID uuid = player.getUniqueId();
            if (CooldownManager.isOnCooldown(CD_LIFESTEAL_RIGHT, uuid)) {
                long rem = CooldownManager.getRemaining(CD_LIFESTEAL_RIGHT, uuid) / 1000;
                player.sendMessage(ChatColor.RED + "Lifestealer ability on cooldown (" + rem + "s).");
                return;
            }
            lifestealAbsorbUntil.put(uuid, System.currentTimeMillis() + (30 * 1000));
            CooldownManager.setCooldown(CD_LIFESTEAL_RIGHT, uuid, 60_000);
            player.sendMessage(ChatColor.GREEN + "Lifestealer: 25% of your heals will be applied as absorption for 30s.");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            event.setCancelled(true);
        }
    }

    private void shootParticleWave(Player player, double range, double step) {
        Location origin = player.getEyeLocation();
        Vector dir = origin.getDirection().normalize();
        World w = player.getWorld();

        for (double d = 0; d < range; d += step) {
            Location point = origin.clone().add(dir.clone().multiply(d));
            w.spawnParticle(Particle.SWEEP_ATTACK, point, 6, 0.2, 0.2, 0.2, 0.02);
            for (Entity e : w.getNearbyEntities(point, 1.2, 1.2, 1.2)) {
                if (e instanceof LivingEntity le && le != player) {
                    // transfer player's potion effects
                    for (PotionEffect ef : player.getActivePotionEffects()) {
                        le.addPotionEffect(ef);
                    }
                    le.damage(6.0, player);
                }
            }
        }
    }

    private void launchWitherSkull(Player player) {
        Location eye = player.getEyeLocation();
        World w = player.getWorld();
        Entity ent = w.spawnEntity(eye.add(player.getLocation().getDirection().multiply(1.2)), EntityType.WITHER_SKULL);
        if (!(ent instanceof WitherSkull skull)) return;
        skull.setShooter(player);
        skull.setVelocity(player.getLocation().getDirection().multiply(1.6));
        ourSkulls.add(skull.getUniqueId());
        skull.setPersistent(true);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof WitherSkull skull)) return;
        UUID id = skull.getUniqueId();
        if (!ourSkulls.remove(id)) return;

        Location hit = skull.getLocation();
        World w = hit.getWorld();
        for (Entity e : w.getNearbyEntities(hit, 2, 2, 2)) {
            if (e instanceof LivingEntity le && !(le instanceof ArmorStand)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 0)); // 20s wither
            }
        }
        skull.remove(); // no terrain damage
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        ItemStack hand = attacker.getInventory().getItemInMainHand();
        if (hand == null) return;

        UUID uuid = attacker.getUniqueId();

        // Scythe of Light: mace-like knockback + sword cooldown
        if (hasItem(hand, "scythe_of_light")) {
            if (CooldownManager.isOnCooldown(CD_SCYTHE_LIGHT, uuid)) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "Weapon cooldown!");
                return;
            }
            CooldownManager.setCooldown(CD_SCYTHE_LIGHT, uuid, 600);
            if (event.getEntity() instanceof LivingEntity target) {
                Vector knock = target.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize().multiply(1.8);
                knock.setY(0.5);
                target.setVelocity(knock);
            }
        }

        // Lifestealer heal (50% of damage dealt)
        if (hasItem(hand, "lifestealer")) {
            double damage = event.getFinalDamage();
            double heal = damage * 0.5;
            double max = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double newHp = Math.min(max, attacker.getHealth() + heal);
            attacker.setHealth(newHp);

            Long until = lifestealAbsorbUntil.get(attacker.getUniqueId());
            if (until != null && until > System.currentTimeMillis()) {
                double absAdd = heal * 0.25;
                double curAbs = attacker.getAbsorptionAmount();
                attacker.setAbsorptionAmount(Math.min(max, curAbs + absAdd));
            }
        }

        // Scythe of Darkness Reaping (pull toward player)
        if (hasItem(hand, "scythe_of_darkness")) {
            if (event.getEntity() instanceof LivingEntity target) {
                Vector pull = attacker.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.0);
                pull.setY(0.2);
                target.setVelocity(pull);
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = event.getItem().getItemStack();
        if (stack == null) return;

        String id = CustomItems.getId(stack);
        if (id == null) return;

        String display = (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
                ? stack.getItemMeta().getDisplayName()
                : stack.getType().name();
        Bukkit.broadcastMessage(ChatColor.GOLD + "The player \"" + player.getName() + "\" has got the \"" + ChatColor.RESET + display + ChatColor.GOLD + "\"");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        applyOrRemoveCrownEffects(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player p) applyOrRemoveCrownEffects(p);
    }
}
