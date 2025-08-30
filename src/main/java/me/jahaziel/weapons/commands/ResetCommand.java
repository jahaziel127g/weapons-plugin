package me.jahaziel.weapons.commands;

import me.jahaziel.weapons.items.WeaponStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Only players can run this."); return true; }
        if (!p.isOp()) { p.sendMessage("You must be OP to reset."); return true; }
        WeaponStorage.resetAll();
        p.sendMessage("All crafted weapons have been reset.");
        return true;
    }
}

