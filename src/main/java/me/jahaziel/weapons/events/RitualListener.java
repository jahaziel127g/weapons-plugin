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
                if (me.jahaziel.weapons.managers.RitualManager.startRitual(p, "scythe_of_light")) {
                    e.setCurrentItem(null); // Clear result so they don't get the item immediately
                } else {
                    e.setCancelled(true); // Don't let them craft if ritual can't start (already running etc)
                }
            }
        }
    }
}
