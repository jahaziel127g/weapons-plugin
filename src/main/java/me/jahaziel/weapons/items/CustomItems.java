package me.jahaziel.weapons.items;

import me.jahaziel.weapons.WeaponsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public final class CustomItems {

    private static NamespacedKey getKey() {
        return new NamespacedKey(WeaponsPlugin.getInstance(), "custom_item_id");
    }

    public static ItemStack createScytheOfLight() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(ChatColor.WHITE + "Scythe of Light");
        m.setLore(Arrays.asList(
                ChatColor.GRAY + "Crossover between a mace and sword.",
                ChatColor.GRAY + "Sword cooldown, mace properties."
        ));
        m.addEnchant(Enchantment.SHARPNESS, 5, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        m.setUnbreakable(true);
        m.getPersistentDataContainer().set(getKey(), PersistentDataType.STRING, "scythe_of_light");
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack createScytheOfDarkness() {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(ChatColor.DARK_GRAY + "Scythe of Darkness");
        m.setLore(Arrays.asList(
                ChatColor.GRAY + "Netherite sword + axe hybrid.",
                ChatColor.GRAY + "Right-click: wave. Has Reaping & Spewing."
        ));
        m.addEnchant(Enchantment.SHARPNESS, 6, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        m.setUnbreakable(true);
        m.getPersistentDataContainer().set(getKey(), PersistentDataType.STRING, "scythe_of_darkness");
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack createWitherLauncher() {
        ItemStack item = new ItemStack(Material.CROSSBOW);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(ChatColor.DARK_PURPLE + "Wither Launcher");
        m.setLore(Arrays.asList(
                ChatColor.GRAY + "Shoots wither skulls that apply Wither.",
                ChatColor.GRAY + "User takes 10s wither; targets take 20s."
        ));
        m.addEnchant(Enchantment.QUICK_CHARGE, 3, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        m.setUnbreakable(true);
        m.getPersistentDataContainer().set(getKey(), PersistentDataType.STRING, "wither_launcher");
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack createLifestealer() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(ChatColor.RED + "Lifestealer");
        m.setLore(Arrays.asList(
                ChatColor.GRAY + "Heals 50% of damage you deal.",
                ChatColor.GRAY + "Right-click: 25% of extra hearts applied as absorption for 30s (1m CD)."
        ));
        m.addEnchant(Enchantment.SHARPNESS, 4, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        m.setUnbreakable(true);
        m.getPersistentDataContainer().set(getKey(), PersistentDataType.STRING, "lifestealer");
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack createKingsCrown() {
        ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(ChatColor.GOLD + "King's Crown");
        m.setLore(Arrays.asList(
                ChatColor.GRAY + "Unbreakable crown granting Strength II, Speed II, Fire Resistance when worn.",
                ChatColor.GRAY + "Use /bounty <player> while wearing to place a bounty (glow)."
        ));
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        m.setUnbreakable(true);
        m.getPersistentDataContainer().set(getKey(), PersistentDataType.STRING, "kings_crown");
        item.setItemMeta(m);
        return item;
    }

    /**
     * Returns the stored custom id string (e.g. "scythe_of_light") or null if the item is not a custom item.
     */
    public static String getId(ItemStack item) {
        if (item == null || !item.hasItemMeta() || item.getItemMeta() == null) return null;
        return item.getItemMeta().getPersistentDataContainer().get(getKey(), PersistentDataType.STRING);
    }

    public static boolean isCustomItem(ItemStack item, String id) {
        String value = getId(item);
        return value != null && value.equalsIgnoreCase(id);
    }
}
