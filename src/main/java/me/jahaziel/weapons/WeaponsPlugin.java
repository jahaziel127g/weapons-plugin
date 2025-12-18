package me.jahaziel.weapons;

import me.jahaziel.weapons.commands.BountyCommand;
import me.jahaziel.weapons.commands.ResetCommand;
import me.jahaziel.weapons.commands.RitualCommand;
import me.jahaziel.weapons.events.RitualListener;
import me.jahaziel.weapons.events.WeaponsListener;
import me.jahaziel.weapons.items.CustomItems;
import me.jahaziel.weapons.items.WeaponStorage;
import me.jahaziel.weapons.managers.RitualManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WeaponsPlugin extends JavaPlugin {
    private static WeaponsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // load storage & items
        WeaponStorage.init(this);
        WeaponStorage.load();
        CustomItems.init(this);
        CustomItems.registerRecipes(this);

        RitualManager.init(this);
        RitualManager.load();

        // Commands
        getCommand("bounty").setExecutor(new BountyCommand());
        getCommand("ritual").setExecutor(new RitualCommand());
        getCommand("weapon").setExecutor(new ResetCommand());

        // Events
        getServer().getPluginManager().registerEvents(new WeaponsListener(this), this);
        getServer().getPluginManager().registerEvents(new RitualListener(), this);

        getLogger().info("WeaponsPlugin enabled (visuals + effects active)");
    }

    @Override
    public void onDisable() {
        WeaponStorage.save();
        RitualManager.save();
        getLogger().info("WeaponsPlugin disabled");
    }

    public static WeaponsPlugin getInstance() {
        return instance;
    }
}
