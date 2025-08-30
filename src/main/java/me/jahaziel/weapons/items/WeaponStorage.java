package me.jahaziel.weapons.items;

import me.jahaziel.weapons.WeaponsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeaponStorage {
    private static WeaponsPlugin plugin;
    private static File file;
    private static FileConfiguration cfg;
    private static final Set<String> crafted = new HashSet<>();

    public static void init(WeaponsPlugin pl) {
        plugin = pl;
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        file = new File(plugin.getDataFolder(), "crafted.yml");
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) { e.printStackTrace(); }
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public static void load() {
        crafted.clear();
        if (cfg.contains("crafted")) {
            List<String> list = cfg.getStringList("crafted");
            crafted.addAll(list);
        }
    }

    public static void save() {
        cfg.set("crafted", crafted.stream().toList());
        try { cfg.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public static boolean isCrafted(String id) { return crafted.contains(id); }
    public static void markCrafted(String id) { crafted.add(id); save(); }
    public static void resetAll() { crafted.clear(); save(); }
}
