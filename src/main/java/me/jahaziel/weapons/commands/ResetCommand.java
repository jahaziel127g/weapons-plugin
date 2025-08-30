package me.jahaziel.weapons.commands;

import me.jahaziel.weapons.managers.RitualManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (args.length > 0 && args[0].equalsIgnoreCase("reset")) {
            RitualManager.resetCrafted(player);
            player.sendMessage("§aYour crafted weapons have been reset!");
            return true;
        }

        player.sendMessage("§cUsage: /weapans reset");
        return true;
    }
}
