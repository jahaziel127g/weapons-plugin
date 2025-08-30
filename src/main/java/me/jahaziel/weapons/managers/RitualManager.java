package me.jahaziel.weapons.managers;

import me.jahaziel.weapons.WeaponsPlugin;
import me.jahaziel.weapons.items.CustomItems;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RitualManager {

    private static final Map<UUID, RitualData> rituals = new HashMap<>();
    private static final Map<UUID, Set<String>> craftedItems = new HashMap<>();
    private static final int RITUAL_SECONDS = 60;

    public static void init() {
        rituals.clear();
        craftedItems.clear();
    }

    public static boolean hasCrafted(Player player, String itemId) {
        return craftedItems.getOrDefault(player.getUniqueId(), Collections.emptySet()).contains(itemId);
    }

    public static void startRitual(Player player, Location center, String itemId) {
        UUID uuid = player.getUniqueId();
        if (rituals.containsKey(uuid)) {
            player.sendMessage(ChatColor.RED + "You already have a ritual in progress!");
            return;
        }

        World world = center.getWorld();
        List<BlockSnapshot> original = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = center.clone().add(x, 0, z);
                original.add(new BlockSnapshot(loc, loc.getBlock().getType(), loc.getBlock().getBlockData()));
                loc.getBlock().setType(Material.OBSIDIAN, false);
            }
        }

        BossBar bossBar = Bukkit.createBossBar("Ritual: " + itemId, BarColor.PURPLE, BarStyle.SOLID);
        bossBar.addPlayer(player);

        final int[] remaining = {RITUAL_SECONDS};

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                remaining[0]--;
                bossBar.setTitle("Ritual: " + itemId + " (" + remaining[0] + "s)");
                bossBar.setProgress(Math.max(0.0, remaining[0] / (double) RITUAL_SECONDS));

                if (remaining[0] <= 0) {
                    cancel();
                    bossBar.removePlayer(player);

                    // Restore blocks
                    for (BlockSnapshot snap : original) {
                        snap.loc.getBlock().setType(snap.material, false);
                        try { snap.loc.getBlock().setBlockData(snap.blockData); } catch (Exception ignored) {}
                    }

                    // Spawn item
                    ItemStack itemStack = CustomItems.getItem(itemId);
                    if (itemStack != null) {
                        Item dropped = world.dropItem(center.clone().add(0,5,0), itemStack);
                        dropped.setPickupDelay(20);
                        player.sendMessage(ChatColor.GREEN + "Ritual complete! Crafted " + itemId);
                        craftedItems.computeIfAbsent(uuid, k -> new HashSet<>()).add(itemId);
                    }

                    rituals.remove(uuid);
                }
            }
        };

        task.runTaskTimer(WeaponsPlugin.getInstance(), 20L, 20L);
        rituals.put(uuid, new RitualData(bossBar, task));
    }

    public static void resetCrafted(Player player) {
        craftedItems.remove(player.getUniqueId());
    }

    private record RitualData(BossBar bossBar, BukkitRunnable task) {}
    private record BlockSnapshot(Location loc, Material material, org.bukkit.block.data.BlockData blockData) {}
}
