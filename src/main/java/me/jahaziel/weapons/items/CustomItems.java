package me.jahaziel.weapons.items;

import me.jahaziel.weapons.WeaponsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class CustomItems {
    private static final Map<String, ItemStack> registry = new HashMap<>();
    private static NamespacedKey key;
    private static WeaponsPlugin plugin;

    public static void init(WeaponsPlugin pl) {
        plugin = pl;
        key = new NamespacedKey(pl, "custom_weapon_id");

        // register items
        register("scythe_of_light", createScytheOfLight());
        register("scythe_of_darkness", createScytheOfDarkness());
        register("wither_launcher", createWitherLauncher());
        register("lifestealer", createLifestealer());
        register("kings_crown", createKingsCrown());
    }

    public static void registerRecipes(WeaponsPlugin pl) {
        // Scythe of Light
        org.bukkit.inventory.ShapedRecipe slRecipe = new org.bukkit.inventory.ShapedRecipe(
                new NamespacedKey(pl, "scythe_of_light_recipe"), getItem("scythe_of_light"));
        slRecipe.shape("PEP", "PMP", "SNS");
        slRecipe.setIngredient('P', Material.PLAYER_HEAD);
        slRecipe.setIngredient('E', Material.ENCHANTED_GOLDEN_APPLE);
        slRecipe.setIngredient('M', Material.MACE);
        slRecipe.setIngredient('S', Material.ECHO_SHARD);
        slRecipe.setIngredient('N', Material.NETHERITE_SWORD);
        pl.getServer().addRecipe(slRecipe);
    }

    private static void register(String id, ItemStack item) {
        registry.put(id, item);
    }

    private static ItemStack withId(ItemStack item, String id) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createScytheOfLight() {
        ItemStack it = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta m = it.getItemMeta();
        if (m != null) {
            m.setDisplayName("§fScythe of Light");
            m.addEnchant(Enchantment.SHARPNESS, 4, true);
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            m.setLore(
                    java.util.Arrays.asList("§7A heavy blade that shines with pure light.", "§eCooldown: sword-like"));
            it.setItemMeta(m);
        }
        return withId(it, "scythe_of_light");
    }

    private static ItemStack createScytheOfDarkness() {
        ItemStack it = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta m = it.getItemMeta();
        if (m != null) {
            m.setDisplayName("§0Scythe of Darkness");
            m.addEnchant(Enchantment.SHARPNESS, 5, true);
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            m.setLore(java.util.Arrays.asList("§7A cruel scythe swathed in shadow.",
                    "§eRight-click: dark wave (costs 25% health)"));
            it.setItemMeta(m);
        }
        return withId(it, "scythe_of_darkness");
    }

    private static ItemStack createWitherLauncher() {
        ItemStack it = new ItemStack(Material.CROSSBOW);
        ItemMeta m = it.getItemMeta();
        if (m != null) {
            m.setDisplayName("§5Wither Launcher");
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            m.setLore(java.util.Arrays.asList("§7Fires wither heads that do not damage terrain.", "§eCooldown: 30s"));
            it.setItemMeta(m);
        }
        return withId(it, "wither_launcher");
    }

    private static ItemStack createLifestealer() {
        ItemStack it = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta m = it.getItemMeta();
        if (m != null) {
            m.setDisplayName("§cLifestealer");
            m.addEnchant(Enchantment.SHARPNESS, 4, true);
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            m.setLore(java.util.Arrays.asList("§7Steals life from foes.", "§eRight-click: temporary absorption (60s)"));
            it.setItemMeta(m);
        }
        return withId(it, "lifestealer");
    }

    private static ItemStack createKingsCrown() {
        ItemStack it = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta m = it.getItemMeta();
        if (m != null) {
            m.setDisplayName("§6King's Crown");
            m.setUnbreakable(true);
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            m.setLore(java.util.Arrays.asList("§7Wearer gains regal might."));
            it.setItemMeta(m);
        }
        return withId(it, "kings_crown");
    }

    // API
    public static String getId(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return null;
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static boolean isCustomItem(ItemStack item, String id) {
        String got = getId(item);
        return got != null && got.equals(id);
    }

    public static ItemStack getItem(String id) {
        ItemStack base = registry.get(id);
        return base == null ? null : base.clone();
    }
}
