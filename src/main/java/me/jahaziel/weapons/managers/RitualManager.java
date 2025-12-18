package me.jahaziel.weapons.managers;

import me.jahaziel.weapons.WeaponsPlugin;
import me.jahaziel.weapons.items.CustomItems;
import me.jahaziel.weapons.items.WeaponStorage;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class RitualManager {
    private static WeaponsPlugin plugin;
    private static final Map<UUID, RitualData> active = new HashMap<>();
    private static File file;
    private static FileConfiguration cfg;

    public static void init(WeaponsPlugin pl) {
        plugin = pl;
        file = new File(plugin.getDataFolder(), "rituals.yml");
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public static void load() {
        if (!file.exists())
            return;
        if (!cfg.contains("rituals"))
            return;

        for (String key : cfg.getConfigurationSection("rituals").getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            String path = "rituals." + key;

            String itemId = cfg.getString(path + ".item");
            int remaining = cfg.getInt(path + ".remaining");
            Location center = cfg.getLocation(path + ".center");

            List<BlockSnapshot> original = new ArrayList<>();
            List<?> rawBlocks = cfg.getList(path + ".blocks");
            if (rawBlocks != null) {
                for (Object o : rawBlocks) {
                    if (o instanceof String s) { // manual serialization simplicity
                        // format: world,x,y,z,MATERIAL,dataString
                        String[] parts = s.split(";", 4);
                        if (parts.length >= 3) {
                            // recreating simplistic snapshot
                            // Note: full block data serialization is complex, simplifying for this quick
                            // fix
                            // We will just restore material.
                        }
                    }
                }
            }

            // Full persistence of block restoration is complex due to BlockData
            // serialization.
            // For this iteration, we will implement a simplified robust restore:
            // We'll just clear the ritual blocks (beacon/spruce) if they exist at center.
            // Actually, let's just properly serialize the location and material.

            // Retrying load logic to be robust:
            // If we are restoring, we need to restart the task.
            // However, we lost the "original" block list efficiently.
            // Strategy: scan the area again. If it's spruce/beacon, we assume it's part of
            // the ritual
            // but we won't know what was there BEFORE.

            // BETTER STRATEGY:
            // Since we can't easily serialize generic BlockData without NMS or verbose
            // adapters,
            // we will just save the LIST of locations and their original Materials.

            List<Map<?, ?>> blockList = cfg.getMapList(path + ".blocks");
            for (Map<?, ?> map : blockList) {
                Location loc = (Location) map.get("loc");
                Material mat = Material.valueOf((String) map.get("type"));
                original.add(new BlockSnapshot(loc, mat, null));
            }

            BossBar bar = Bukkit.createBossBar("Ritual: " + itemId, BarColor.PURPLE, BarStyle.SEGMENTED_20);
            bar.addPlayer(Bukkit.getPlayer(uuid)); // might be null if offline, that's fine

            resumeRitual(uuid, center, itemId, remaining, original, bar);
        }
    }

    public static void save() {
        cfg.set("rituals", null); // clear old
        for (Map.Entry<UUID, RitualData> entry : active.entrySet()) {
            String path = "rituals." + entry.getKey();
            RitualData d = entry.getValue();
            cfg.set(path + ".item", d.itemId);
            cfg.set(path + ".remaining", d.remaining[0]);
            cfg.set(path + ".center", d.center);

            List<Map<String, Object>> serializedBlocks = new ArrayList<>();
            for (BlockSnapshot s : d.original) {
                Map<String, Object> map = new HashMap<>();
                map.put("loc", s.loc);
                map.put("type", s.material.name());
                serializedBlocks.add(map);
            }
            cfg.set(path + ".blocks", serializedBlocks);
        }
        try {
            cfg.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean startRitual(Player player, String itemId) {
        if (!player.isOp())
            return false;
        UUID uuid = player.getUniqueId();
        if (active.containsKey(uuid)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.ritual-already-running", "&cAlready running.")));
            return false;
        }

        Location center = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        World world = center.getWorld();

        List<BlockSnapshot> original = new ArrayList<>();
        int cx = center.getBlockX(), cy = center.getBlockY(), cz = center.getBlockZ();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                org.bukkit.block.Block b = world.getBlockAt(cx + dx, cy, cz + dz);
                original.add(new BlockSnapshot(b.getLocation(), b.getType(), b.getBlockData()));
                if (dx == 0 && dz == 0)
                    b.setType(Material.BEACON);
                else
                    b.setType(Material.SPRUCE_PLANKS);
            }
        }

        BossBar bar = Bukkit.createBossBar("Ritual: " + itemId, BarColor.PURPLE, BarStyle.SEGMENTED_20);
        bar.addPlayer(player);

        String startMsg = plugin.getConfig().getString("messages.ritual-start", "Ritual started.");
        startMsg = startMsg.replace("%item%", itemId)
                .replace("%x%", String.valueOf(cx))
                .replace("%y%", String.valueOf(cy))
                .replace("%z%", String.valueOf(cz));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', startMsg));

        int duration = plugin.getConfig().getInt("ritual-time", 900);
        resumeRitual(uuid, center, itemId, duration, original, bar);
        return true;
    }

    private static void resumeRitual(UUID uuid, Location center, String itemId, int initialRemaining,
            List<BlockSnapshot> original, BossBar bar) {
        int totalDuration = plugin.getConfig().getInt("ritual-time", 900);
        // avoid div by zero
        if (totalDuration <= 0)
            totalDuration = 1;

        final int[] remaining = { initialRemaining };
        final int maxTime = totalDuration;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                remaining[0]--;

                // Visuals
                if (remaining[0] % 5 == 0) {
                    center.getWorld().spawnParticle(Particle.WITCH, center.clone().add(0, 1, 0), 10, 0.5, 0.5, 0.5,
                            0.05);
                }

                bar.setTitle("Ritual: " + itemId + " (" + (remaining[0] / 60) + ":"
                        + String.format("%02d", remaining[0] % 60) + ")");
                bar.setProgress(Math.max(0.0, Math.min(1.0, remaining[0] / (double) maxTime)));

                if (remaining[0] <= 0) {
                    completeRitual(uuid, center, itemId, original, bar);
                    cancel();
                }
            }
        };

        task.runTaskTimer(plugin, 20L, 20L);
        active.put(uuid, new RitualData(center, original, bar, task, itemId, remaining));
    }

    private static void completeRitual(UUID uuid, Location center, String itemId, List<BlockSnapshot> original,
            BossBar bar) {
        World world = center.getWorld();
        // restore blocks
        for (BlockSnapshot s : original) {
            org.bukkit.block.Block b = world.getBlockAt(s.loc);
            b.setType(s.material, false);
            try {
                b.setBlockData(s.blockData);
            } catch (Exception ignored) {
            }
        }
        ItemStack created = CustomItems.getItem(itemId);
        if (created != null) {
            Item dropped = world.dropItem(center.clone().add(0, 5, 0), created);
            dropped.setPickupDelay(20);
            WeaponStorage.markCrafted(itemId);

            world.spawn(center, Firework.class);
            world.playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }

        bar.removeAll();

        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            String msg = plugin.getConfig().getString("messages.ritual-complete", "Ritual Complete");
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', msg), "§6" + itemId, 10, 60, 10);
        }
        active.remove(uuid);
    }

    public static void cancelRitual(UUID uuid) {
        RitualData d = active.remove(uuid);
        if (d == null)
            return;
        d.task.cancel();
        d.bossBar.removeAll();
    }

    private record RitualData(Location center, List<BlockSnapshot> original, BossBar bossBar, BukkitRunnable task,
            String itemId, int[] remaining) {
    }

    private record BlockSnapshot(Location loc, Material material, org.bukkit.block.data.BlockData blockData) {
    }
}
