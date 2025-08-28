package me.jahaziel.weapons.events;

import me.jahaziel.weapons.items.CustomItems;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class RitualListener implements Listener {

    // When any entity picks up an item (player picks up the crafted item)
    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.getItem() == null || event.getItem().getItemStack() == null) return;

        // Check if this item is one of our custom items via persistent data (same method as CustomItems.isCustomItem)
        // We'll check each known ID so we can announce item display name
        var isLight = CustomItems.isCustomItem(event.getItem().getItemStack(), "scythe_of_light");
        var isDark = CustomItems.isCustomItem(event.getItem().getItemStack(), "scythe_of_darkness");
        var isWither = CustomItems.isCustomItem(event.getItem().getItemStack(), "wither_launcher");
        var isLife = CustomItems.isCustomItem(event.getItem().getItemStack(), "lifestealer");
        var isCrown = CustomItems.isCustomItem(event.getItem().getItemStack(), "kings_crown");

        if (isLight || isDark || isWither || isLife || isCrown) {
            ItemMeta meta = event.getItem().getItemStack().getItemMeta();
            String display = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : event.getItem().getItemStack().getType().name();

            // Announce to the server
            String msg = ChatColor.GOLD + "The player \"" + player.getName() + "\" has got the \"" + ChatColor.RESET + display + ChatColor.GOLD + "\"";
            player.getServer().broadcastMessage(msg);
        }
    }
}
