package me.jahaziel.weapons.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class CustomItems {

    private static final Map<String, ItemStack> items = new HashMap<>();

    static {
        items.put("scythe_of_light", createScytheOfLight());
        items.put("scythe_of_darkness", createScytheOfDarkness());
        items.put("wither_launcher", createWitherLauncher());
        items.put("lifestealer", createLifestealer());
        items.put("kings_crown", createKingsCrown());
    }

    public static ItemStack getItem(String id) { return items.get(id); }

    public static ItemStack createScytheOfLight() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fScythe of Light");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createScytheOfDarkness() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§0Scythe of Darkness");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWitherLauncher() {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§8Wither Launcher");
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createLifestealer() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cLifestealer");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createKingsCrown() {
        ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6King's Crown");
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
