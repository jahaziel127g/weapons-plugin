package me.jahaziel.weapons.commands;

import me.jahaziel.weapons.items.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BountyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Console cannot use this.");
            return true;
        }
        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "Usage: /bounty <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        ItemStack helm = p.getInventory().getHelmet();
        if (!CustomItems.isCustomItem(helm, "kings_crown")) {
            p.sendMessage(ChatColor.RED + "You must wear the King's Crown to place a bounty.");
            return true;
        }
        target.setGlowing(true);
        Bukkit.broadcastMessage(ChatColor.YELLOW + p.getName() + " has placed a bounty on " + ChatColor.GOLD + target.getName());
        return true;
    }
}
