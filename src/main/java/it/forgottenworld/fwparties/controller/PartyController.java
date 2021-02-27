package it.forgottenworld.fwparties.controller;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.exception.InvalidPartyException;
import it.forgottenworld.fwparties.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

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
            removePlayerFromTeam(player);
            addPlayerToScoreboard(player);
            removePlayerFromScoreboard(player);
            party.removePlayer(player);
            playerMap.remove(player);
            FWParties.getInstance().getChatController().removeChattingPlayer(player);
        }
    }

    public void removePlayerFromParty(UUID player) {
        removePlayerFromParty(player, playerMap.get(player).getLeader());
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
            Party party = partyMap.get(partyLeader);
            party.getPlayerList().forEach(uuid -> {
                removePlayerFromTeam(uuid);
                removePlayerFromScoreboard(uuid);
                inviteMap.remove(uuid);
                playerMap.remove(uuid);
            });
            partyMap.remove(partyLeader);
            scoreboardMap.remove(partyLeader);
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
        score.setScore(22);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team team = board.registerNewTeam(partyLeader.toString().substring(0,16));
        String partyLeaderName = Objects.requireNonNull(Bukkit.getOfflinePlayer(partyLeader).getName());
        team.addEntry(partyLeaderName);
        team.setPrefix(ChatColor.RED + "");
        team.setColor(Objects.requireNonNull(ChatColor.getByChar('c')));
        obj.getScore(partyLeaderName.substring(0,Math.min(partyLeaderName.length(),16))).setScore(1);
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
        scoreboard.getObjective(partyLeader.toString().substring(0,16)).getScore(onlinePlayer.getName().substring(0,Math.min(16, onlinePlayer.getName().length()))).setScore((int)onlinePlayer.getHealth());
        scoreboard.resetScores(ChatColor.GREEN + ">> Players: " + ChatColor.BOLD + (partyMap.get(partyLeader).getPlayerList().size()-1));
        scoreboard.getObjective(partyLeader.toString().substring(0, 16)).getScore(ChatColor.GREEN + ">> Players: " + ChatColor.BOLD + (partyMap.get(partyLeader).getPlayerList().size())).setScore(21);
        onlinePlayer.setScoreboard(scoreboard);
    }

    public void removePlayerFromScoreboard(UUID player) {
        UUID partyLeader = playerMap.get(player).getLeader();
        if (scoreboardMap.containsKey(partyLeader)) {
            Scoreboard scoreboard = scoreboardMap.get(partyLeader);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
            scoreboard.resetScores(offlinePlayer.getName().substring(0,Math.min(16, offlinePlayer.getName().length())));
            Player onlinePlayer = Bukkit.getPlayer(player);
            if(onlinePlayer != null){
                onlinePlayer.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
            }
        }
    }

    public void removePlayerFromTeam(UUID player){
        UUID partyLeader = playerMap.get(player).getLeader();
        Scoreboard scoreboard = scoreboardMap.get(partyLeader);
        if(scoreboard == null) System.out.println("samu gay");
        if(partyMap.get(partyLeader) == null) System.out.println("cacca");
        if(partyMap.get(partyLeader).getPlayerList() == null) System.out.println("merda");
        scoreboard.resetScores(ChatColor.GREEN + ">> Players: " + ChatColor.BOLD + partyMap.get(partyLeader).getPlayerList().size());
        scoreboard.getObjective(partyLeader.toString().substring(0, 16)).getScore(ChatColor.GREEN + ">> Players: " + ChatColor.BOLD + (partyMap.get(partyLeader).getPlayerList().size()-1)).setScore(21);
        Team team = scoreboard.getTeam(partyLeader.toString().substring(0, 16));
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        team.removeEntry(offlinePlayer.getName());
    }

    public void updatePlayerHealthInScoreboard(Player player){
        UUID partyLeader = playerMap.get(player.getUniqueId()).getLeader();
        if (scoreboardMap.containsKey(partyLeader)) {
            Scoreboard scoreboard = scoreboardMap.get(partyLeader);
            scoreboard.getObjective(partyLeader.toString().substring(0,16)).getScore(player.getName().substring(0,Math.min(16,player.getName().length()))).setScore((int)Math.floor(player.getHealth()));
        }
    }
}