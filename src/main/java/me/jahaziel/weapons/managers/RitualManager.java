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

public class RitualManager {
    private static WeaponsPlugin plugin;
    private static final Map<UUID, RitualData> active = new HashMap<>();
    private static final int RITUAL_SECONDS = 15 * 60; // 15 minutes

    public static void init(WeaponsPlugin pl) { plugin = pl; }

    public static boolean startRitual(Player player, String itemId) {
        if (!player.isOp()) return false;
        UUID uuid = player.getUniqueId();
        if (active.containsKey(uuid)) { player.sendMessage("§cYou already have a ritual running."); return false; }

        Location center = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        World world = center.getWorld();

        List<BlockSnapshot> original = new ArrayList<>();
        int cx = center.getBlockX(), cy = center.getBlockY(), cz = center.getBlockZ();
        for (int dx=-1; dx<=1; dx++) {
            for (int dz=-1; dz<=1; dz++) {
                org.bukkit.block.Block b = world.getBlockAt(cx + dx, cy, cz + dz);
                original.add(new BlockSnapshot(b.getLocation(), b.getType(), b.getBlockData()));
                if (dx==0 && dz==0) b.setType(Material.BEACON);
                else b.setType(Material.SPRUCE_PLANKS);
            }
        }

        BossBar bar = Bukkit.createBossBar("Ritual: " + itemId + " (15:00)", BarColor.PURPLE, BarStyle.SEGMENTED_20);
        bar.addPlayer(player);

        player.sendMessage("§aRitual started for §f" + itemId + " §aat coordinates: §6" + center.getBlockX() + " " + center.getBlockY() + " " + center.getBlockZ());

        final int[] remaining = {RITUAL_SECONDS};
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                remaining[0]--;
                bar.setTitle("Ritual: " + itemId + " (" + (remaining[0]/60) + ":" + String.format("%02d", remaining[0]%60) + ")");
                bar.setProgress(Math.max(0.0, remaining[0] / (double) RITUAL_SECONDS));
                if (remaining[0] <= 0) {
                    // restore blocks
                    for (BlockSnapshot s : original) {
                        org.bukkit.block.Block b = world.getBlockAt(s.loc);
                        b.setType(s.material, false);
                        try { b.setBlockData(s.blockData); } catch (Exception ignored) {}
                    }
                    ItemStack created = CustomItems.getItem(itemId);
                    if (created != null) {
                        Item dropped = world.dropItem(center.clone().add(0,5,0), created);
                        dropped.setPickupDelay(20);
                        WeaponStorage.markCrafted(itemId);

                        // spectacle
                        world.spawn(center, Firework.class);
                        world.playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    }
                    bar.removePlayer(player);
                    bar.setVisible(false);
                    player.sendTitle("§aRitual Complete", "§6" + itemId, 10, 60, 10);
                    active.remove(uuid);
                    cancel();
                }
            }
        };

        task.runTaskTimer(plugin, 20L, 20L);
        active.put(uuid, new RitualData(center, original, bar, task));
        return true;
    }

    public static void cancelRitual(UUID uuid) {
        RitualData d = active.remove(uuid);
        if (d == null) return;
        d.task.cancel();
        d.bossBar.removeAll();
    }

    private record RitualData(Location center, List<BlockSnapshot> original, BossBar bossBar, BukkitRunnable task) {}
    private record BlockSnapshot(Location loc, Material material, org.bukkit.block.data.BlockData blockData) {}
}
