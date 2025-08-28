package me.jahaziel.weapons;

import me.jahaziel.weapons.commands.BountyCommand;
import me.jahaziel.weapons.commands.RitualCommand;
import me.jahaziel.weapons.events.RitualListener;
import me.jahaziel.weapons.events.WeaponsListener;
import me.jahaziel.weapons.managers.RitualManager;
import me.jahaziel.weapons.managers.CooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WeaponsPlugin extends JavaPlugin {

    private static WeaponsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("WeaponsPlugin enabled!");

        // Commands
        getCommand("bounty").setExecutor(new BountyCommand());
        getCommand("ritual").setExecutor(new RitualCommand());

        // Events
        getServer().getPluginManager().registerEvents(new WeaponsListener(), this);

        // If you included RitualListener (pickup announcements), register it:
        try {
            getServer().getPluginManager().registerEvents(new RitualListener(), this);
        } catch (NoClassDefFoundError ignored) {
            // If you don't have RitualListener class, ignore (you may already have one)
        }

        // Managers init
        CooldownManager.init();
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
