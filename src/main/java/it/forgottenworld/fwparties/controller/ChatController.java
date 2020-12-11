package it.forgottenworld.fwparties.controller;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class ChatController {

    private final Map<UUID, Boolean> chattingPlayers = new HashMap<>();
    private final Set<UUID> spyingChatPlayers = new HashSet<>();

    public boolean isPlayerSpyingChat(UUID player){
        return spyingChatPlayers.contains(player);
    }

    public void addSpyingChatPlayer(UUID player){
        spyingChatPlayers.add(player);
    }

    public void removeSpyingChatPlayer(UUID player){
        spyingChatPlayers.remove(player);
    }

    public void sendMessageToPartyMembers(UUID partyLeader, String message){
        PartyController partyController = FWParties.getInstance().getPartyController();
        if(partyController.doesPartyExist(partyLeader)){
            Party party = partyController.getParty(partyLeader);
            Stream.concat(party.getPlayerList().stream(), spyingChatPlayers.stream()).forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if(player != null){
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
                }
            });
        }
    }

    public void addChattingPlayer(UUID player) {
        chattingPlayers.replace(player, true);
    }

    public void removeChattingPlayer(UUID player) {
        chattingPlayers.replace(player, false);
    }

    public Boolean isPlayerChatting(UUID player) {
        return chattingPlayers.containsKey(player) && chattingPlayers.get(player);
    }
}