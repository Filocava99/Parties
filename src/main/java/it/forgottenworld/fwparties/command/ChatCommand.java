package it.forgottenworld.fwparties.command;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.controller.ChatController;
import it.forgottenworld.fwparties.controller.PartyController;
import it.forgottenworld.fwparties.util.TextUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PartyController partyController = FWParties.getInstance().getPartyController();
            ChatController chatController = FWParties.getInstance().getChatController();
            if (partyController.isPlayerInParty(player.getUniqueId())) {
                if (args.length > 0) {
                    String message = String.join(" ", Arrays.asList(Arrays.copyOfRange(args, 0, args.length)));
                    chatController.sendMessageToPartyMembers(partyController.getPlayerParty(player.getUniqueId()).getLeader(), "&2[PARTY] &a" + player.getName() + ": " + message);
                } else {
                    if (chatController.isPlayerChatting(player.getUniqueId())) {
                        player.sendMessage(TextUtility.parseColors("&eParty chat disabilitata!"));
                        chatController.removeChattingPlayer(player.getUniqueId());
                    } else {
                        player.sendMessage(TextUtility.parseColors("&eParty chat abilitata!"));
                        chatController.addChattingPlayer(player.getUniqueId());
                    }
                }
            } else {
                player.sendMessage(TextUtility.parseColors(FWParties.getInstance().getPluginConfig().getConfig().getString("not_on_party")));
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
        return true;
    }
}
