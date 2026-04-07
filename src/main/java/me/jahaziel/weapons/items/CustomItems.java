package me.jahaziel.weapons.items;

import me.jahaziel.weapons.WeaponsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlotGroup;
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

        // Scythe of Darkness
        org.bukkit.inventory.ShapedRecipe sdRecipe = new org.bukkit.inventory.ShapedRecipe(
                new NamespacedKey(pl, "scythe_of_darkness_recipe"), getItem("scythe_of_darkness"));
        sdRecipe.shape("SES", "SMS", "SPS");
        sdRecipe.setIngredient('S', Material.SHULKER_SHELL);
        sdRecipe.setIngredient('E', Material.ECHO_SHARD);
        sdRecipe.setIngredient('M', Material.NETHERITE_AXE);
        sdRecipe.setIngredient('P', Material.PHANTOM_MEMBRANE);
        pl.getServer().addRecipe(sdRecipe);

        // Wither Launcher
        org.bukkit.inventory.ShapedRecipe wlRecipe = new org.bukkit.inventory.ShapedRecipe(
                new NamespacedKey(pl, "wither_launcher_recipe"), getItem("wither_launcher"));
        wlRecipe.shape("WSW", "CBC", "WCW");
        wlRecipe.setIngredient('W', Material.WITHER_SKELETON_SKULL);
        wlRecipe.setIngredient('S', Material.ECHO_SHARD);
        wlRecipe.setIngredient('C', Material.CROSSBOW);
        wlRecipe.setIngredient('B', Material.BLAZE_POWDER);
        pl.getServer().addRecipe(wlRecipe);

        // Lifestealer
        org.bukkit.inventory.ShapedRecipe lsRecipe = new org.bukkit.inventory.ShapedRecipe(
                new NamespacedKey(pl, "lifestealer_recipe"), getItem("lifestealer"));
        lsRecipe.shape("HHH", "HSH", "HNH");
        lsRecipe.setIngredient('H', Material.HEART_OF_THE_SEA);
        lsRecipe.setIngredient('S', Material.NETHERITE_SWORD);
        lsRecipe.setIngredient('N', Material.NETHER_STAR);
        pl.getServer().addRecipe(lsRecipe);

        // King's Crown
        org.bukkit.inventory.ShapedRecipe kcRecipe = new org.bukkit.inventory.ShapedRecipe(
                new NamespacedKey(pl, "kings_crown_recipe"), getItem("kings_crown"));
        kcRecipe.shape("GDG", "GHG", "NGN");
        kcRecipe.setIngredient('G', Material.GOLD_INGOT);
        kcRecipe.setIngredient('D', Material.DIAMOND);
        kcRecipe.setIngredient('H', Material.NETHERITE_HELMET);
        kcRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        pl.getServer().addRecipe(kcRecipe);
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
        ItemStack it = new ItemStack(Material.MACE);
        ItemMeta m = it.getItemMeta();
        if (m != null) {
            m.setDisplayName("§fScythe of Light");
            m.addEnchant(Enchantment.SHARPNESS, 4, true);
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            m.setLore(
                    java.util.Arrays.asList("§7A heavy blade that shines with pure light.", "§eCooldown: sword-like"));

            m.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new org.bukkit.attribute.AttributeModifier(
                    new NamespacedKey(plugin, "scythe_damage"), 8.0,
                    org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
            m.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new org.bukkit.attribute.AttributeModifier(
                    new NamespacedKey(plugin, "scythe_speed"), -2.4,
                    org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));

            it.setItemMeta(m);
        }
        return withId(it, "scythe_of_light");
    }

    private static ItemStack createScytheOfDarkness() {
        ItemStack it = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta m = it.getItemMeta();
        if (m != null) {
            m.setDisplayName("§0Scythe of Darkness");
            m.addEnchant(Enchantment.SHARPNESS, 5, true);
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            m.setLore(java.util.Arrays.asList(
                    "§7A cruel scythe swathed in shadow.",
                    "§7Reaping I",
                    "§7Spewing I",
                    "",
                    "§eRight-click: Spewing (Wave of Darkness)",
                    "§ePassive: Reaping (Pull entities)"));

            m.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new org.bukkit.attribute.AttributeModifier(
                    new NamespacedKey(plugin, "scythe_dark_damage"), 9.0,
                    org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
            m.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new org.bukkit.attribute.AttributeModifier(
                    new NamespacedKey(plugin, "scythe_dark_speed"), -3.0,
                    org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));

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
            m.setLore(java.util.Arrays.asList(
                    "§7Fires wither heads that do not damage terrain.",
                    "§7Targets: 20s Wither",
                    "§7User: 10s Wither",
                    "§eCooldown: 30s"));
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
            m.setLore(java.util.Arrays.asList(
                    "§7Steals life from foes.",
                    "§7Passive: Recover 50% damage dealt as HP.",
                    "§7Ability: 30s Absorption-steal buff (25%).",
                    "§eCooldown: 1 minute"));
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
            m.setLore(java.util.Arrays.asList(
                    "§7A crown that grants regal might.",
                    "",
                    "§6Passives (While Worn):",
                    "§7 - Strength II",
                    "§7 - Speed II",
                    "§7 - Fire Resistance",
                    "",
                    "§eAbility: /bounty <player>"));
            it.setItemMeta(m);
        }
        return withId(it, "kings_crown");
    }

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

    public static boolean isValidItemId(String id) {
        return registry.containsKey(id);
    }
}
