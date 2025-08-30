package me.jahaziel.weapons.commands;

import me.jahaziel.weapons.managers.RitualManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RitualCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players can run this.");
            return true;
        }
        if (!p.isOp()) {
            p.sendMessage("Only OPs can start rituals.");
            return true;
        }
        if (args.length != 1) {
            p.sendMessage("Usage: /ritual <itemId>");
            return true;
        }

        // FIX: call startRitual with (Player, String) as RitualManager expects
        boolean ok = RitualManager.startRitual(p, args[0]);
        if (!ok) p.sendMessage("Ritual failed or already running.");
        return true;
    }
}
