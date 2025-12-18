package me.jahaziel.weapons.events;

import me.jahaziel.weapons.items.CustomItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RitualListener implements Listener {
    @EventHandler
    public void onRitualUse(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null)
            return;
        String[] ids = { "scythe_of_light", "scythe_of_darkness", "wither_launcher", "lifestealer", "kings_crown" };
        for (String id : ids) {
            if (CustomItems.isCustomItem(item, id)) {
                e.getPlayer().sendMessage("§a[Plugin] Recognized: " + id);
            }
        }
    }

    @EventHandler
    public void onCraft(org.bukkit.event.inventory.CraftItemEvent e) {
        ItemStack result = e.getRecipe().getResult();
        if (CustomItems.isCustomItem(result, "scythe_of_light")) {
            if (e.getWhoClicked() instanceof org.bukkit.entity.Player p) {
                // Check if already crafted (global limit) happens in startRitual,
                // but we need to cancel the vanilla craft regardless.
                e.setCancelled(true);

                if (me.jahaziel.weapons.managers.RitualManager.startRitual(p, "scythe_of_light")) {
                    // Consume items manually
                    e.getInventory().setMatrix(new ItemStack[9]);
                    p.closeInventory();
                }
            }
        }
    }
}
