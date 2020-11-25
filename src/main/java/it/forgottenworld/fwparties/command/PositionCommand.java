package it.forgottenworld.fwparties.command;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.controller.PartyController;
import it.forgottenworld.fwparties.util.TextUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PositionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PartyController partyController = FWParties.getInstance().getPartyController();
            if (partyController.isPlayerInParty(player.getUniqueId())) {
                String message = ChatColor.GREEN +  "X: " + ChatColor.GOLD + player.getLocation().getBlockX() + ChatColor.GREEN + "  Y: " + ChatColor.GOLD + player.getLocation().getBlockY() + ChatColor.GREEN + "  Z: " + ChatColor.GOLD + player.getLocation().getBlockZ();
                partyController.sendMessageToPartyMembers(partyController.getPlayerParty(player.getUniqueId()).getLeader(), "&2[PARTY] &a" + player.getName() + ": " + message);
            } else {
                player.sendMessage(TextUtility.parseColors(FWParties.getInstance().getPluginConfig().getConfig().getString("not_on_party")));
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
        return true;
    }
}