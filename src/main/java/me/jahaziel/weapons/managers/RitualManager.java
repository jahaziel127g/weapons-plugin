package me.jahaziel.weapons.managers;

import me.jahaziel.weapons.WeaponsPlugin;
import me.jahaziel.weapons.items.CustomItems;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class RitualManager {

    private static final Map<UUID, RitualData> rituals = new HashMap<>();
    private static final int RITUAL_SECONDS = 15 * 60; // 15 minutes

    public static void init() {
        rituals.clear();
    }

    public static boolean startRitual(org.bukkit.entity.Player player, String itemId) {
        UUID uuid = player.getUniqueId();
        if (rituals.containsKey(uuid)) {
            player.sendMessage(ChatColor.RED + "You already have a ritual in progress.");
            return false;
        }

        Location center = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        World world = center.getWorld();
        if (world == null) {
            player.sendMessage(ChatColor.RED + "Unable to start ritual: world is null.");
            return false;
        }

        // Save original 3x3 at center y
        List<BlockSnapshot> original = new ArrayList<>();
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block b = world.getBlockAt(cx + dx, cy, cz + dz);
                original.add(new BlockSnapshot(b.getLocation(), b.getType(), b.getBlockData().clone()));
            }
        }

        // Build: center BEACON, surround SPRUCE_PLANKS
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block b = world.getBlockAt(cx + dx, cy, cz + dz);
                if (dx == 0 && dz == 0) b.setType(Material.BEACON, false);
                else b.setType(Material.SPRUCE_PLANKS, false);
            }
        }

        BossBar bar = Bukkit.createBossBar("Ritual: " + itemId + " (15:00)", BarColor.WHITE, BarStyle.SOLID);
        bar.addPlayer(player);
        bar.setProgress(1.0);
        bar.setVisible(true);

        player.sendMessage(ChatColor.GREEN + "Ritual started for " + ChatColor.WHITE + itemId +
                ChatColor.GREEN + " at coordinates: " + ChatColor.GOLD +
                center.getBlockX() + " " + center.getBlockY() + " " + center.getBlockZ());

        final int[] remaining = {RITUAL_SECONDS};

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                remaining[0]--;

                int mins = remaining[0] / 60;
                int secs = remaining[0] % 60;
                String mmss = String.format("%02d:%02d", mins, secs);
                bar.setTitle("Ritual: " + itemId + " (" + mmss + ")");
                bar.setProgress(Math.max(0.0, remaining[0] / (double) RITUAL_SECONDS));

                if (remaining[0] <= 0) {
                    // finish
                    cancel();

                    bar.removePlayer(player);
                    bar.setVisible(false);

                    // restore original blocks
                    for (BlockSnapshot snap : original) {
                        Block b = world.getBlockAt(snap.loc);
                        b.setType(snap.material, false);
                        try { b.setBlockData(snap.blockData); } catch (Exception ignored) {}
                    }

                    // spawn the proper custom item 5 blocks above center so it falls
                    Location spawn = center.clone().add(0, 5, 0);
                    ItemStack item = switch (itemId) {
                        case "scythe_of_light" -> CustomItems.createScytheOfLight();
                        case "scythe_of_darkness" -> CustomItems.createScytheOfDarkness();
                        case "wither_launcher" -> CustomItems.createWitherLauncher();
                        case "lifestealer" -> CustomItems.createLifestealer();
                        case "kings_crown" -> CustomItems.createKingsCrown();
                        default -> null;
                    };

                    if (item != null) {
                        Item dropped = world.dropItem(spawn, item);
                        dropped.setPickupDelay(20);
                    }

                    player.sendMessage(ChatColor.GREEN + "Ritual complete! The item has been crafted at " +
                            ChatColor.GOLD + center.getBlockX() + " " + center.getBlockY() + " " + center.getBlockZ());

                    rituals.remove(uuid);
                }
            }
        }.runTaskTimer(WeaponsPlugin.getInstance(), 20L, 20L);

        rituals.put(uuid, new RitualData(center, original, bar, task, itemId));
        return true;
    }

    public static void cancelRitual(UUID uuid) {
        RitualData data = rituals.remove(uuid);
        if (data == null) return;
        data.task.cancel();
        data.bossBar.removeAll();
        data.bossBar.setVisible(false);
        World world = data.center.getWorld();
        if (world == null) return;
        for (BlockSnapshot snap : data.originalBlocks) {
            Block b = world.getBlockAt(snap.loc);
            b.setType(snap.material, false);
            try { b.setBlockData(snap.blockData); } catch (Exception ignored) {}
        }
    }

    public static boolean hasRitual(UUID uuid) {
        return rituals.containsKey(uuid);
    }

    private static class RitualData {
        final Location center;
        final List<BlockSnapshot> originalBlocks;
        final BossBar bossBar;
        final BukkitTask task;
        final String itemId;
        RitualData(Location center, List<BlockSnapshot> originalBlocks, BossBar bossBar, BukkitTask task, String itemId) {
            this.center = center; this.originalBlocks = originalBlocks; this.bossBar = bossBar; this.task = task; this.itemId = itemId;
        }
    }

    private static class BlockSnapshot {
        final Location loc; final Material material; final BlockData blockData;
        BlockSnapshot(Location loc, Material material, BlockData blockData) { this.loc = loc; this.material = material; this.blockData = blockData; }
    }
}
