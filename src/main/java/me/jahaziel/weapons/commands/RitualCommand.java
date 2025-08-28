package me.jahaziel.weapons.commands;

import me.jahaziel.weapons.managers.RitualManager;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class RitualCommand implements CommandExecutor {

    private static final List<String> VALID = Arrays.asList(
            "scythe_of_light","scythe_of_darkness","wither_launcher","lifestealer","kings_crown");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players can start rituals.");
            return true;
        }
        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "Usage: /ritual <item_id>");
            p.sendMessage(ChatColor.GRAY + "Valid: " + String.join(", ", VALID));
            return true;
        }
        String id = args[0].toLowerCase();
        if (!VALID.contains(id)) {
            p.sendMessage(ChatColor.RED + "Invalid item id.");
            return true;
        }
        RitualManager.startRitual(p, id);
        return true;
    }
}
