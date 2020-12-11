package it.forgottenworld.fwparties.controller;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatController {

    private final Map<UUID, Boolean> chattingPlayers = new HashMap<>();

    public void sendMessageToPartyMembers(UUID partyLeader, String message){
        PartyController partyController = FWParties.getInstance().getPartyController();
        if(partyController.doesPartyExist(partyLeader)){
            Party party = partyController.getParty(partyLeader);
            for(UUID uuid : party.getPlayerList()){
                Player player = Bukkit.getPlayer(uuid);
                if(player != null){
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
                }
            }
        }
    }

    public void addChattingPlayer(UUID player) {
        chattingPlayers.replace(player, true);
    }

    public void removeChattingPlayer(UUID player) {
        chattingPlayers.replace(player, false);
    }

    public Boolean isPlayerChatting(UUID player) {
        return chattingPlayers.get(player);
    }

    public void registerPlayer(UUID player){
        chattingPlayers.put(player, false);
    }
}