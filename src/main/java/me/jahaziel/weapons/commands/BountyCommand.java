package me.jahaziel.weapons.commands;

import me.jahaziel.weapons.items.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Only players can run this."); return true; }
        if (p.getInventory().getHelmet() == null || !CustomItems.isCustomItem(p.getInventory().getHelmet(), "kings_crown")) {
            p.sendMessage("Only the King's Crown wearer can place a bounty.");
            return true;
        }
        if (args.length != 1) { p.sendMessage("Usage: /bounty <player>"); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { p.sendMessage("Player not found."); return true; }
        target.setGlowing(true);
        p.sendMessage("Bounty placed on " + target.getName());
        return true;
    }
}
