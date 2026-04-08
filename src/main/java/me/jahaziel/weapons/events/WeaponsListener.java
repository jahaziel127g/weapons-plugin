package me.jahaziel.weapons.events;

import me.jahaziel.weapons.WeaponsPlugin;
import me.jahaziel.weapons.items.CustomItems;
import me.jahaziel.weapons.managers.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class WeaponsListener implements Listener {
    private final WeaponsPlugin plugin;
    private static NamespacedKey launcherKey;
    private NamespacedKey crownTouchKey;
    private final java.util.Map<UUID, ItemStack> recentPickupMap = new java.util.HashMap<>();

    public WeaponsListener(WeaponsPlugin plugin) {
        this.plugin = plugin;
        launcherKey = new NamespacedKey(plugin, "is_wither_launcher_projectile");
        crownTouchKey = new NamespacedKey(plugin, "kings_crown_last_touch");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    removeCrownDuplicates(p, null);
                    
                    ItemStack helmet = p.getInventory().getHelmet();
                    if (helmet != null && CustomItems.isCustomItem(helmet, "kings_crown")) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 100, 1, true, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, true, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0, true, false));
                    }
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);
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

        if (id.equals("scythe_of_light")) {
            if (e.getEntity() instanceof LivingEntity tgt) {
                Location p = tgt.getLocation().add(0, 1, 0);
                tgt.getWorld().spawnParticle(Particle.END_ROD, p, 12, 0.4, 0.4, 0.4, 0.0);
                tgt.getWorld().playSound(p, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1f);
            }
        }

        if (id.equals("scythe_of_darkness")) {
            if (e.getEntity() instanceof LivingEntity tgt) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Vector pull = player.getLocation().toVector().subtract(tgt.getLocation().toVector()).normalize()
                            .multiply(1.2);
                    pull.setY(0.2);
                    tgt.setVelocity(pull);
                }, 1L);

                tgt.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, tgt.getLocation().add(0, 1, 0), 8, 0.3, 0.3,
                        0.3, 0.02);
            }
        }

        if (id.equals("lifestealer")) {
            double damage = e.getFinalDamage();

            double heal = damage * 0.5;
            double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            player.setHealth(Math.min(player.getHealth() + heal, max));

            if (CooldownManager.isOnCooldown("lifestealer_buff_active", player.getUniqueId())) {
                double absorption = damage * 0.25;
                player.setAbsorptionAmount(player.getAbsorptionAmount() + absorption);
                player.getWorld().spawnParticle(Particle.GLOW, player.getLocation().add(0, 1, 0), 4, 0.2, 0.2, 0.2,
                        0.0);
            }

            player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 6, 0.3, 0.3, 0.3, 0.0);
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 8, 0.2, 0.2, 0.2, 0.0);
        }

        if (id.equals("wither_launcher")) {
            if (!CooldownManager.isOnCooldown("wither_launcher", player.getUniqueId())) {
                if (e.getEntity() instanceof LivingEntity tgt) {
                    tgt.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0));
                    CooldownManager.setCooldown("wither_launcher", player.getUniqueId(), 30L);
                }
            }
        }

        if (id.equals("kusanagi")) {
            if (e.getEntity() instanceof LivingEntity tgt) {
                double damage = e.getFinalDamage();
                double heal = damage * 1.0;
                double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                player.setHealth(Math.min(player.getHealth() + heal, max));

                player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 8, 0.3, 0.3, 0.3, 0.0);
                player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.0);

                if (Math.random() < 0.05) {
                    Location tgtLoc = tgt.getLocation();
                    tgt.getWorld().strikeLightning(tgtLoc);
                    tgt.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2));
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

        if (id.equals("scythe_of_darkness")) {
            if (CooldownManager.isOnCooldown("scythe_dark_wave", uuid)) {
                player.sendActionBar(Component.text(
                        "§cScythe wave on cooldown (" + CooldownManager.getRemaining("scythe_dark_wave", uuid) + "s)"));
                return;
            }
            Location eye = player.getEyeLocation();
            Vector dir = eye.getDirection().normalize();

            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1f);
            player.sendTitle("§5Dark Wave", "§7You feel the shadows consume you", 5, 40, 5);

            java.util.Set<LivingEntity> targets = new java.util.HashSet<>();
            for (int i = 1; i <= 8; i++) {
                Location p = eye.clone().add(dir.clone().multiply(i));
                p.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, p, 10, 0.4, 0.4, 0.4, 0.02);
                p.getWorld().spawnParticle(Particle.END_ROD, p, 6, 0.2, 0.2, 0.2, 0.0);

                for (Entity ent : p.getWorld().getNearbyEntities(p, 1.5, 1.5, 1.5)) {
                    if (ent instanceof LivingEntity tgt && !tgt.equals(player)) {
                        targets.add(tgt);
                    }
                }
            }

            for (LivingEntity tgt : targets) {
                tgt.damage(6, player);
                for (PotionEffect eff : player.getActivePotionEffects()) {
                    tgt.addPotionEffect(new PotionEffect(eff.getType(), eff.getDuration(), eff.getAmplifier()));
                }
            }

            double cost = Math.max(1.0, player.getHealth() * 0.25);
            player.damage(cost);
            CooldownManager.setCooldown("scythe_dark_wave", uuid, 10L);
            player.setCooldown(Material.NETHERITE_SWORD, 20 * 10);
            return;
        }

        if (id.equals("lifestealer")) {
            if (CooldownManager.isOnCooldown("lifestealer_m2", uuid)) {
                player.sendActionBar(Component.text(
                        "§cLifestealer on cooldown (" + CooldownManager.getRemaining("lifestealer_m2", uuid) + "s)"));
                return;
            }

            CooldownManager.setCooldown("lifestealer_buff_active", uuid, 30L);

            player.sendActionBar(Component.text("§c§lSoul Feast Active! (30s)"));
            player.playSound(player.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1f, 0.8f);
            player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.05);

            CooldownManager.setCooldown("lifestealer_m2", uuid, 60L);
            player.setCooldown(Material.NETHERITE_SWORD, 20 * 60);
            return;
        }

        if (id.equals("wither_launcher")) {
            if (CooldownManager.isOnCooldown("wither_launcher", uuid)) {
                player.sendActionBar(Component.text("§cWither Launcher on cooldown ("
                        + CooldownManager.getRemaining("wither_launcher", uuid) + "s)"));
                return;
            }
            Location spawn = player.getEyeLocation().add(player.getLocation().getDirection());
            WitherSkull skull = player.getWorld().spawn(spawn, WitherSkull.class);
            skull.setShooter(player);
            skull.setVelocity(player.getLocation().getDirection().multiply(1.6));
            skull.setIsIncendiary(false);
            skull.getPersistentDataContainer().set(launcherKey, PersistentDataType.BYTE, (byte) 1);

            player.getWorld().spawnParticle(Particle.END_ROD, spawn, 12, 0.5, 0.5, 0.5, 0.0);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 0.9f);

            CooldownManager.setCooldown("wither_launcher", uuid, 30L);
            player.setCooldown(Material.CROSSBOW, 20 * 30);
            return;
        }

        if (id.equals("kings_crown")) {
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1.2, 0), 6, 0.2, 0.4, 0.2,
                    0.0);
        }

        if (id.equals("kusanagi")) {
            if (CooldownManager.isOnCooldown("kusanagi_thunder", uuid)) {
                player.sendActionBar(Component.text("§cKusanagi Thunder Storm on cooldown ("
                        + CooldownManager.getRemaining("kusanagi_thunder", uuid) + "s)"));
                return;
            }

            player.sendTitle("§b§lThunder Storm", "§7The heavens answer!", 5, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.8f);

            Location playerLoc = player.getLocation();
            for (Entity ent : player.getWorld().getNearbyEntities(playerLoc, 30, 30, 30)) {
                if (ent instanceof LivingEntity target && !target.equals(player)) {
                    Location targetLoc = target.getLocation();
                    player.getWorld().strikeLightning(targetLoc);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 3));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
                }
            }

            player.getWorld().spawnParticle(Particle.LARGE_EXPLOSION, playerLoc.add(0, 5, 0), 30, 15, 5, 15, 0.1);

            CooldownManager.setCooldown("kusanagi_thunder", uuid, 120L);
            player.setCooldown(Material.NETHERITE_SWORD, 20 * 120);
            return;
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof WitherSkull skull))
            return;
        if (!skull.getPersistentDataContainer().has(launcherKey, PersistentDataType.BYTE))
            return;

        Location loc = skull.getLocation();
        loc.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc, 30, 0.5, 0.5, 0.5, 0.1);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.2f);

        for (Entity ent : skull.getNearbyEntities(4, 4, 4)) {
            if (ent instanceof LivingEntity target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 1));
            }
        }

        if (skull.getShooter() instanceof Player shooter) {
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 1));
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof WitherSkull skull) {
            if (skull.getPersistentDataContainer().has(launcherKey, PersistentDataType.BYTE)) {
                e.blockList().clear();
            }
        }
    }

    @EventHandler
    public void onArmorChange(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!e.getInventory().equals(player.getInventory())) {
            return;
        }

        if (e.getRawSlot() != 39) {
            return;
        }

        ItemStack cursor = e.getCursor();

        boolean cursorHasCrown = CustomItems.isCustomItem(cursor, "kings_crown");

        if (cursorHasCrown || (e.isShiftClick() && cursorHasCrown)) {
            ItemStack offhand = player.getInventory().getItemInOffHand();
            if (CustomItems.isCustomItem(offhand, "kings_crown")) {
                player.getInventory().setItemInOffHand(null);
            }
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (CustomItems.isCustomItem(mainHand, "kings_crown")) {
                player.getInventory().setItemInMainHand(null);
            }
            final int raw = e.getRawSlot();
            Bukkit.getScheduler().runTaskLater(plugin, () -> tagSlotLastTouch(player, raw), 1L);
        }
    }

    @EventHandler
    public void onInventoryClickCleanup(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!e.getInventory().equals(player.getInventory())) {
            return;
        }
        final Integer preserve = Integer.valueOf(e.getRawSlot());
        Bukkit.getScheduler().runTaskLater(plugin, () -> removeCrownDuplicates(player, preserve), 1L);
    }

    @EventHandler
    public void onEntityPickupItem(org.bukkit.event.entity.EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        ItemStack picked = e.getItem().getItemStack();
        if (!CustomItems.isCustomItem(picked, "kings_crown")) return;

        recentPickupMap.put(player.getUniqueId(), picked.clone());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ItemStack snapshot = recentPickupMap.remove(player.getUniqueId());
            if (snapshot == null) return;
            for (int i = 0; i < 36; i++) {
                ItemStack it = player.getInventory().getItem(i);
                if (it != null && CustomItems.isCustomItem(it, "kings_crown") && matchSnapshot(it, snapshot)) {
                    tagSlotLastTouch(player, i);
                    break;
                }
            }
            ItemStack helm = player.getInventory().getHelmet();
            if (helm != null && CustomItems.isCustomItem(helm, "kings_crown") && matchSnapshot(helm, snapshot)) {
                tagSlotLastTouch(player, 39);
            }
            ItemStack off = player.getInventory().getItemInOffHand();
            if (off != null && CustomItems.isCustomItem(off, "kings_crown") && matchSnapshot(off, snapshot)) {
                tagSlotLastTouch(player, 40);
            }
            removeCrownDuplicates(player, null);
        }, 1L);
    }

    private boolean matchSnapshot(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        String ida = CustomItems.getId(a);
        String idb = CustomItems.getId(b);
        if (ida == null || idb == null) return false;
        if (!ida.equals(idb)) return false;
        ItemMeta ma = a.getItemMeta();
        ItemMeta mb = b.getItemMeta();
        if (ma == null || mb == null) return false;
        String da = ma.hasDisplayName() ? ma.getDisplayName() : null;
        String db = mb.hasDisplayName() ? mb.getDisplayName() : null;
        return (da == null ? db == null : da.equals(db));
    }

    private void removeCrownDuplicates(Player player, Integer preserveRawSlot) {
        class CrownLoc { int slot; ItemStack item; long touch; }
        java.util.List<CrownLoc> crowns = new java.util.ArrayList<>();

        ItemStack helmet = player.getInventory().getHelmet();
        if (CustomItems.isCustomItem(helmet, "kings_crown")) {
            CrownLoc c = new CrownLoc(); c.slot = 39; c.item = helmet; c.touch = getLastTouch(helmet); crowns.add(c);
        }

        for (int i = 0; i < 36; i++) {
            ItemStack it = player.getInventory().getItem(i);
            if (CustomItems.isCustomItem(it, "kings_crown")) {
                CrownLoc c = new CrownLoc(); c.slot = i; c.item = it; c.touch = getLastTouch(it); crowns.add(c);
            }
        }

        ItemStack off = player.getInventory().getItemInOffHand();
        if (CustomItems.isCustomItem(off, "kings_crown")) {
            CrownLoc c = new CrownLoc(); c.slot = 40; c.item = off; c.touch = getLastTouch(off); crowns.add(c);
        }

        ItemStack main = player.getInventory().getItemInMainHand();
        if (CustomItems.isCustomItem(main, "kings_crown")) {
            CrownLoc c = new CrownLoc(); c.slot = 36; c.item = main; c.touch = getLastTouch(main); crowns.add(c);
        }

        if (crowns.size() <= 1) return;

        CrownLoc preserve = null;
        if (preserveRawSlot != null) {
            for (CrownLoc c : crowns) if (c.slot == preserveRawSlot) preserve = c;
        }
        if (preserve == null) {
            long best = Long.MIN_VALUE;
            for (CrownLoc c : crowns) { if (c.touch > best) { best = c.touch; preserve = c; } }
        }
        if (preserve == null) {
            for (CrownLoc c : crowns) if (c.slot == 39) preserve = c;
        }

        for (CrownLoc c : crowns) {
            if (c == preserve) continue;
            if (c.slot == 39) player.getInventory().setHelmet(null);
            else if (c.slot == 40) player.getInventory().setItemInOffHand(null);
            else if (c.slot >= 0 && c.slot < 36) player.getInventory().setItem(c.slot, null);
            else if (c.slot == 36) player.getInventory().setItemInMainHand(null);
        }
    }

    private long getLastTouch(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0L;
        ItemMeta m = item.getItemMeta(); if (m == null) return 0L;
        java.lang.Long v = m.getPersistentDataContainer().get(crownTouchKey, PersistentDataType.LONG);
        return v == null ? 0L : v.longValue();
    }

    private void tagSlotLastTouch(Player player, int rawSlot) {
        ItemStack target = null;
        if (rawSlot == 39) target = player.getInventory().getHelmet();
        else if (rawSlot >= 0 && rawSlot < 36) target = player.getInventory().getItem(rawSlot);
        else if (rawSlot == 40) target = player.getInventory().getItemInOffHand();
        else target = player.getInventory().getItemInMainHand();

        if (CustomItems.isCustomItem(target, "kings_crown")) {
            ItemMeta meta = target.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(crownTouchKey, PersistentDataType.LONG, System.currentTimeMillis());
                target.setItemMeta(meta);
            }
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
            for (ItemStack drop : e.getDrops()) {
                String id = CustomItems.getId(drop);
                if (id != null) {
                    me.jahaziel.weapons.items.WeaponStorage.unmarkCrafted(id);
                    Bukkit.broadcastMessage("§cThe ancient " + id.replace("_", " ") + " was lost in the void with "
                            + e.getEntity().getName() + ".");
                }
            }
        }
    }
}
