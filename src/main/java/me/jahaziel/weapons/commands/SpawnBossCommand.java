package me.jahaziel.weapons.commands;

import me.jahaziel.weapons.managers.BossManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SpawnBossCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou need OP to use this command."));
            return true;
        }

        if (BossManager.isBossActive()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cAn Ultra Guardian is already active!"));
            return true;
        }

        BossManager.spawnUltraBoss();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&aUltra Guardian spawned successfully!"));
        return true;
    }
}