package it.forgottenworld.fwparties.command;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.controller.ChatController;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (args[0].equalsIgnoreCase("reload")) {
                FWParties.getInstance().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded");
            } else if (args[0].equalsIgnoreCase("spy")) {
                ChatController chatController = FWParties.getInstance().getChatController();
                UUID uuid = ((Player) sender).getUniqueId();
                if (chatController.isPlayerSpyingChat(uuid)) {
                    chatController.removeSpyingChatPlayer(uuid);
                    sender.sendMessage(ChatColor.YELLOW + "Disabled parties spy chat");
                } else {
                    chatController.addSpyingChatPlayer(uuid);
                    sender.sendMessage(ChatColor.YELLOW + "Enaled parties spy chat");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_GREEN + "\"-------{ FWParty Admin }-------");
                sender.sendMessage(ChatColor.GREEN + "/pa reload");
                sender.sendMessage(ChatColor.GREEN + "/pa spy");
            }
        }
        return true;
    }
}
