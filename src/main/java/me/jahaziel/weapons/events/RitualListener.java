package me.jahaziel.weapons.events;

import me.jahaziel.weapons.items.CustomItems;
import me.jahaziel.weapons.items.WeaponStorage;
import me.jahaziel.weapons.managers.BossManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class RitualListener implements Listener {
    private static final int MIN_DISTANCE = 700;

    @EventHandler
    public void onCraft(org.bukkit.event.inventory.CraftItemEvent e) {
        ItemStack result = e.getRecipe().getResult();
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (CustomItems.isCustomItem(result, "scythe_of_light")) {
            e.setCancelled(true);
            if (me.jahaziel.weapons.managers.RitualManager.startRitual(player, "scythe_of_light")) {
                e.getInventory().setMatrix(new ItemStack[9]);
                player.closeInventory();
            }
            return;
        }

        if (CustomItems.isCustomItem(result, "kusanagi")) {
            e.setCancelled(true);
            handleKusanagiCraft(player, e);
        }
    }

    private void handleKusanagiCraft(Player player, org.bukkit.event.inventory.CraftItemEvent e) {
        if (!BossManager.isBossActive()) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                    "&cThe Ultra Guardian must be alive to forge the Kusanagi!"));
            return;
        }

        Location bossLoc = BossManager.getBossSpawnLocation();
        if (bossLoc == null) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                    "&cCould not find the Ultra Guardian!"));
            return;
        }

        double distance = player.getLocation().distance(bossLoc);
        if (distance > MIN_DISTANCE) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                    "&cYou must be within " + MIN_DISTANCE + " blocks of the Ultra Guardian!"));
            return;
        }

        ItemStack[] matrix = e.getInventory().getMatrix();
        String usedLegendary = null;

        for (ItemStack item : matrix) {
            if (item == null) continue;
            String id = CustomItems.getId(item);
            if (id != null && CustomItems.isLegendaryWeapon(id)) {
                if (WeaponStorage.hasBeenUsedForKusanagi(id)) {
                    player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                            "&cThis legendary weapon has already been used to forge Kusanagi!"));
                    return;
                }
                usedLegendary = id;
                break;
            }
        }

        if (usedLegendary == null) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                    "&cYou need a legendary weapon in the crafting grid!"));
            return;
        }

        WeaponStorage.markUsedForKusanagi(usedLegendary);
        e.getInventory().setMatrix(new ItemStack[9]);
        player.closeInventory();

        ItemStack kusanagi = CustomItems.getItem("kusanagi");
        player.getInventory().addItem(kusanagi);

        player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "§b§lYou have forged the legendary Kusanagi!"));

        Bukkit.broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "§b§l" + player.getName() + " has forged the Kusanagi using " +
                        usedLegendary.replace("_", " ") + "!"));
    }
}