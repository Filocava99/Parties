package it.forgottenworld.fwparties.controller;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.exception.InvalidPartyException;
import it.forgottenworld.fwparties.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PartyController implements Serializable {

    private final Map<UUID, UUID> inviteMap = new HashMap<>();
    private final Map<UUID, Party> partyMap = new HashMap<>();
    private final Map<UUID, Party> playerMap = new HashMap<>();
    private transient Map<UUID, Scoreboard> scoreboardMap = new HashMap<>();

    public void addInvite(UUID playerInvited, UUID partyToJoin) {
        inviteMap.put(playerInvited, partyToJoin);
    }

    public boolean hasPendingInvite(UUID player) {
        return inviteMap.containsKey(player);
    }

    public UUID removeInvite(UUID playerInvited) {
        return inviteMap.remove(playerInvited);
    }

    public void addPlayerToParty(UUID playerToAdd, UUID partyLeader) throws InvalidPartyException {
        if (partyMap.containsKey(partyLeader)) {
            Party party = partyMap.get(partyLeader);
            party.addPlayer(playerToAdd);
            playerMap.put(playerToAdd, party);
            addPlayerToScoreboard(playerToAdd);
        } else {
            throw new InvalidPartyException();
        }
    }

    public void removePlayerFromParty(UUID player, UUID partyLeader) {
        if (partyMap.containsKey(partyLeader)) {
            Party party = partyMap.get(partyLeader);
            party.removePlayer(player);
            removePlayerFromScoreboard(player);
            playerMap.remove(player);
            FWParties.getInstance().getChatController().removeChattingPlayer(player);
            removePlayerFromParty(player);
        }
    }

    public void removePlayerFromParty(UUID player) {
        if (playerMap.containsKey(player)) {
            removePlayerFromScoreboard(player);
            Party party = playerMap.remove(player);
            party.removePlayer(player);
            playerMap.remove(player);
            FWParties.getInstance().getChatController().removeChattingPlayer(player);
        }
    }

    public boolean doesPartyExist(UUID partyLeader) {
        return partyMap.containsKey(partyLeader);
    }

    public boolean isPartyLeader(UUID partyLeader) {
        return doesPartyExist(partyLeader);
    }

    public Party getParty(UUID partyLeader) {
        return partyMap.get(partyLeader);
    }

    public void deleteParty(UUID partyLeader) {
        if (partyMap.containsKey(partyLeader)) {
            Party party = partyMap.remove(partyLeader);
            party.getPlayerList().forEach(uuid -> {
                removePlayerFromScoreboard(uuid);
                inviteMap.remove(uuid);
                playerMap.remove(uuid);
            });
        }
    }

    public Party getPlayerParty(UUID player) {
        return playerMap.get(player);
    }

    public boolean isPlayerInParty(UUID player) {
        return playerMap.containsKey(player);
    }

    public void createParty(UUID partyLeader, String password) {
        Party party = new Party(partyLeader, password);
        partyMap.put(partyLeader, party);
        playerMap.put(partyLeader, party);
        initializeScoreboard(partyLeader);
        addPlayerToScoreboard(partyLeader);
    }

    public void createParty(UUID partyLeader) {
        createParty(partyLeader, null);
    }

    public int getPartySize(UUID partyLeader) throws InvalidPartyException {
        try {
            return partyMap.get(partyLeader).getPlayersNumber();
        } catch (Exception e) {
            throw new InvalidPartyException();
        }
    }

    public boolean areInSameParty(Player player1, Player player2) {
        Party targetParty = getPlayerParty(player1.getUniqueId());
        Party playerParty = getPlayerParty(player2.getUniqueId());
        if (targetParty != null && playerParty != null) {
            return playerParty.equals(targetParty);
        }
        return false;
    }

    public void setPartyColor(UUID partyLeader, char color) {
        Scoreboard scoreboard = scoreboardMap.get(partyLeader);
        Team team = scoreboard.getTeam(partyLeader.toString().substring(0, 16));
        team.setColor(Objects.requireNonNull(ChatColor.getByChar(color)));
        team.setPrefix(ChatColor.translateAlternateColorCodes('&', "&" + color) + "");
        partyMap.get(partyLeader).getPlayerList().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setScoreboard(scoreboard);
            }
        });
    }

    public void initializeScoreboardMap(){
        scoreboardMap = new HashMap<>();
    }

    private void initializeScoreboard(UUID partyLeader) {
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        Objective obj = board.registerNewObjective(partyLeader.toString().substring(0, 16), "dummy", "Party");
        Score score = obj.getScore(ChatColor.GREEN + ">> Players: " + ChatColor.BOLD + "1");
        score.setScore(21);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team team = board.registerNewTeam(partyLeader.toString().substring(0,16));
        String partyLeaderName = Objects.requireNonNull(Bukkit.getOfflinePlayer(partyLeader).getName());
        team.addEntry(partyLeaderName);
        team.setPrefix(ChatColor.RED + "");
        team.setColor(Objects.requireNonNull(ChatColor.getByChar('c')));
        obj.getScore(partyLeaderName).setScore(1);
        scoreboardMap.put(partyLeader, board);
    }

    public void addPlayerToScoreboard(UUID player) {
        UUID partyLeader = playerMap.get(player).getLeader();
        if (!scoreboardMap.containsKey(partyLeader)) {
            initializeScoreboard(partyLeader);
        }
        Scoreboard scoreboard = scoreboardMap.get(partyLeader);
        Team team = scoreboard.getTeam(partyLeader.toString().substring(0, 16));
        Player onlinePlayer = Bukkit.getPlayer(player);
        team.addEntry(onlinePlayer.getName());
        scoreboard.getObjective(partyLeader.toString().substring(0,16)).getScore(onlinePlayer.getName()).setScore((int)onlinePlayer.getHealth());
        onlinePlayer.setScoreboard(scoreboard);
    }

    public void removePlayerFromScoreboard(UUID player) {
        UUID partyLeader = playerMap.get(player).getLeader();
        if (scoreboardMap.containsKey(partyLeader)) {
            Scoreboard scoreboard = scoreboardMap.get(partyLeader);
            Team team = scoreboard.getTeam(partyLeader.toString().substring(0, 16));
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
            team.removeEntry(offlinePlayer.getName());
            scoreboard.resetScores(offlinePlayer.getName());
            Player onlinePlayer = Bukkit.getPlayer(player);
            if(onlinePlayer != null){
                onlinePlayer.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
            }
        }
    }

    public void updatePlayerHealthInScoreboard(Player player){

    }
}