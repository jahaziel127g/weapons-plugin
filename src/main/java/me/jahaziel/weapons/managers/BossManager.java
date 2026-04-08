package me.jahaziel.weapons.managers;

import me.jahaziel.weapons.WeaponsPlugin;
import me.jahaziel.weapons.items.CustomItems;
import me.jahaziel.weapons.items.WeaponStorage;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BossManager implements Listener {
    private static WeaponsPlugin plugin;
    private static final Set<UUID> ultraBosses = new HashSet<>();
    private static boolean bossActive = false;
    private static Location bossSpawnLocation = null;

    public static void init(WeaponsPlugin pl) {
        plugin = pl;
        plugin.getServer().getPluginManager().registerEvents(new BossManager(), pl);
    }

    public static void spawnUltraBoss() {
        if (bossActive) {
            plugin.getLogger().info("Ultra boss already active, skipping spawn");
            return;
        }

        World world = plugin.getServer().getWorlds().get(0);
        Location spawnLoc = findSpawnLocation(world);

        if (spawnLoc == null) {
            plugin.getLogger().warning("Could not find valid spawn location for ultra boss");
            return;
        }

        bossSpawnLocation = spawnLoc;
        spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);
        spawnLoc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, spawnLoc, 5, 1, 1, 1, 0);

        Entity entity = spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.WITHER);
        if (!(entity instanceof Wither wither)) return;

        wither.setCustomName("§4§lUltra Guardian");
        wither.setCustomNameVisible(true);
        wither.setHealth(500);
        wither.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
        wither.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25);
        wither.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
        wither.setInvulnerable(false);
        wither.setCollidable(true);

        ultraBosses.add(wither.getUniqueId());
        bossActive = true;

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "§4§lA Legendary Guardian has spawned! §cFind the Kusanagi!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "§eLocation: §c" + spawnLoc.getBlockX() + ", " + spawnLoc.getBlockY() + ", " + spawnLoc.getBlockZ()));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!bossActive) {
                    cancel();
                    return;
                }
                wither.getWorld().spawnParticle(Particle.LAVA, wither.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0);
            }
        }.runTaskTimer(plugin, 0, 40);
    }

    private static Location findSpawnLocation(World world) {
        int maxAttempts = 100;
        int minDistance = 700;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = (int) ((Math.random() * 4000) - 2000);
            int z = (int) ((Math.random() * 4000) - 2000);
            int y = world.getHighestBlockYAt(x, z);

            if (y < 0) y = 64;

            Location testLoc = new Location(world, x, y, z);

            if (isValidSpawnLocation(testLoc, minDistance)) {
                return testLoc;
            }
        }

        for (int x = -2000; x <= 2000; x += 200) {
            for (int z = -2000; z <= 2000; z += 200) {
                int y = world.getHighestBlockYAt(x, z);
                if (y < 0) y = 64;
                Location testLoc = new Location(world, x, y, z);
                if (isValidSpawnLocation(testLoc, minDistance)) {
                    return testLoc;
                }
            }
        }

        return null;
    }

    private static boolean isValidSpawnLocation(Location loc, int minDistance) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(loc.getWorld())) {
                double dist = player.getLocation().distance(loc);
                if (dist < minDistance) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isBossActive() {
        return bossActive;
    }

    public static Location getBossSpawnLocation() {
        return bossSpawnLocation;
    }

    @EventHandler
    public void onUltraBossDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Wither)) return;
        if (!ultraBosses.contains(entity.getUniqueId())) return;

        ultraBosses.remove(entity.getUniqueId());
        bossActive = false;
        bossSpawnLocation = null;

        Location deathLoc = entity.getLocation();
        ItemStack kusanagi = CustomItems.getItem("kusanagi");
        if (kusanagi != null) {
            Item drop = deathLoc.getWorld().dropItemNaturally(deathLoc.add(0, 1, 0), kusanagi);
            drop.setPickupDelay(40);
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "§b§lThe Ultra Guardian has fallen! §eThe Kusanagi has been dropped!"));
    }

    public static void resetBoss() {
        bossActive = false;
        bossSpawnLocation = null;
        ultraBosses.clear();
    }
}