package me.jahaziel.weapons.events;

import me.jahaziel.weapons.items.CustomItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class RitualListener implements Listener {
    @EventHandler
    public void onCraft(org.bukkit.event.inventory.CraftItemEvent e) {
        ItemStack result = e.getRecipe().getResult();
        if (CustomItems.isCustomItem(result, "scythe_of_light")) {
            if (e.getWhoClicked() instanceof org.bukkit.entity.Player p) {
                e.setCancelled(true);

                if (me.jahaziel.weapons.managers.RitualManager.startRitual(p, "scythe_of_light")) {
                    e.getInventory().setMatrix(new ItemStack[9]);
                    p.closeInventory();
                }
            }
        }
    }
}
