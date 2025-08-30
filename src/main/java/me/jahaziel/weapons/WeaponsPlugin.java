package me.jahaziel.weapons;

import me.jahaziel.weapons.commands.BountyCommand;
import me.jahaziel.weapons.commands.ResetCommand;
import me.jahaziel.weapons.commands.RitualCommand;
import me.jahaziel.weapons.events.WeaponsListener;
import me.jahaziel.weapons.managers.RitualManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WeaponsPlugin extends JavaPlugin {

    private static WeaponsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("WeaponsPlugin enabled!");

        getCommand("bounty").setExecutor(new BountyCommand());
        getCommand("ritual").setExecutor(new RitualCommand());
        getCommand("weapans").setExecutor(new ResetCommand());

        getServer().getPluginManager().registerEvents(new WeaponsListener(), this);

        RitualManager.init();
    }

    @Override
    public void onDisable() {
        getLogger().info("WeaponsPlugin disabled!");
    }

    public static WeaponsPlugin getInstance() {
        return instance;
    }
}
