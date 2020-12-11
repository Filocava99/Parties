package it.forgottenworld.fwparties.controller;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.exception.InvalidPartyException;
import it.forgottenworld.fwparties.party.Party;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyController implements Serializable {

    private final Map<UUID, UUID> inviteMap = new HashMap<>();
    private final Map<UUID, Party> partyMap = new HashMap<>();
    private final Map<UUID, Party> playerMap = new HashMap<>();

    public void addInvite(UUID playerInvited, UUID partyToJoin){
        inviteMap.put(playerInvited, partyToJoin);
    }

    public boolean hasPendingInvite(UUID player){
        return inviteMap.containsKey(player);
    }

    public UUID removeInvite(UUID playerInvited){
        return inviteMap.remove(playerInvited);
    }

    public void addPlayerToParty(UUID playerToAdd, UUID partyLeader) throws InvalidPartyException {
        if(partyMap.containsKey(partyLeader)){
            Party party = partyMap.get(partyLeader);
            party.addPlayer(playerToAdd);
            playerMap.put(playerToAdd, party);
        }else{
            throw new InvalidPartyException();
        }
    }

    public void removePlayerFromParty(UUID player, UUID partyLeader){
        if(partyMap.containsKey(partyLeader)){
            Party party = partyMap.get(partyLeader);
            party.removePlayer(player);
            playerMap.remove(player);
            FWParties.getInstance().getChatController().removeChattingPlayer(player);
        }
    }

    public void removePlayerFromParty(UUID player){
        if(playerMap.containsKey(player)) {
            Party party = playerMap.remove(player);
            party.removePlayer(player);
            playerMap.remove(player);
            FWParties.getInstance().getChatController().removeChattingPlayer(player);
        }
    }

    public boolean doesPartyExist(UUID partyLeader){
        return partyMap.containsKey(partyLeader);
    }

    public boolean isPartyLeader(UUID partyLeader){
        return doesPartyExist(partyLeader);
    }

    public Party getParty(UUID partyLeader){
        return partyMap.get(partyLeader);
    }

    public void deleteParty(UUID partyLeader){
        if(partyMap.containsKey(partyLeader)){
            Party party = partyMap.remove(partyLeader);
            party.getPlayerList().forEach(uuid -> {
                if(inviteMap.containsKey(uuid)){
                    inviteMap.remove(uuid);
                    playerMap.remove(uuid);
                }
            });
        }
    }

    public Party getPlayerParty(UUID player){
        return playerMap.get(player);
    }

    public boolean isPlayerInParty(UUID player){
        return playerMap.containsKey(player);
    }

    public void createParty(UUID partyLeader, String password){
        Party party = new Party(partyLeader, password);
        partyMap.put(partyLeader, party);
        playerMap.put(partyLeader, party);
    }

    public void createParty(UUID partyLeader){
        createParty(partyLeader, null);
    }

    public int getPartySize(UUID partyLeader) throws InvalidPartyException {
        try {
            return partyMap.get(partyLeader).getPlayersNumber();
        }catch (Exception e){
            throw new InvalidPartyException();
        }
    }

    public boolean areInSameParty(Player player1, Player player2){
        Party targetParty = getPlayerParty(player1.getUniqueId());
        Party playerParty = getPlayerParty(player2.getUniqueId());
        if (targetParty != null && playerParty != null) {
            return playerParty.equals(targetParty);
        }
        return false;
    }

}