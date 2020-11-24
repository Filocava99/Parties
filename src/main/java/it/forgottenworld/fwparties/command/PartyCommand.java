package it.forgottenworld.fwparties.command;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.controller.PartyController;
import it.forgottenworld.fwparties.exception.InvalidPartyException;
import it.forgottenworld.fwparties.party.Party;
import it.forgottenworld.fwparties.util.TextUtility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class PartyCommand implements CommandExecutor {

    private final FWParties plugin;
    private final FileConfiguration config;

    public PartyCommand() {
        this.plugin = FWParties.getInstance();
        config = plugin.getPluginConfig().getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            printHelp(sender);
        } else if (args[0].equalsIgnoreCase("create")) {
            create(sender, args);
        } else if (args[0].equalsIgnoreCase("disband")) {
            disband(sender, args);
        }else if(args[0].equalsIgnoreCase("setPassword")){
            setPassword(sender, args);
        }else if(args[0].equalsIgnoreCase("join")){
            join(sender, args);
        }else if(args[0].equalsIgnoreCase("invite")){
            invite(sender, args);
        }else if(args[0].equalsIgnoreCase("accept")){
            accept(sender);
        }else if(args[0].equalsIgnoreCase("decline")){
            decline(sender, args);
        }else if(args[0].equalsIgnoreCase("leave")){
            leave(sender, args);
        }else if(args[0].equalsIgnoreCase("kick")){
            kick(sender, args);
        }else if(args[0].equalsIgnoreCase("chat")){
            chat(sender, args);
        }else if(args[0].equalsIgnoreCase("info")){
            info(sender, args);
        }else{
            printHelp(sender);
        }
        return true;
    }

    private void printHelp(CommandSender sender) {
        ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&6-------{ FWParty }-------\n" +
                "&a/party create <password>\n" +
                "&a/party disband\n" +
                "&a/party setPassword <password>\n" +
                "&a/party join <partyLeaderName> <password>\n" +
                "&a/party invite <playerName>\n" +
                "&a/party accept\n" +
                "&a/party decline\n" +
                "&a/party leave\n" +
                "&a/party kick <player>\n" +
                "&a/party chat [message]> oppure /pc [message]\n" +
                "&a/party info"));
        sender.spigot().sendMessage(componentBuilder.create());
    }

    private void create(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String password;
            if (args.length > 1) {
                password = args[1];
            } else {
                password = null;
            }
            PartyController partyController = plugin.getPartyController();
            //TODO Il secondo controllo e' ridondante ma lo lascio per chiarezza
            if (partyController.isPlayerInParty(player.getUniqueId()) || partyController.doesPartyExist(player.getUniqueId())) {
                player.sendMessage(TextUtility.parseColors(config.getString("already_in_party")));
            } else {
                partyController.createParty(player.getUniqueId(), password);
                player.sendMessage(TextUtility.parseColors(config.getString("party_created")));
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
    }

    private void disband(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PartyController partyController = plugin.getPartyController();
            if (partyController.isPartyLeader(player.getUniqueId())) {
                partyController.sendMessageToPartyMembers(player.getUniqueId(), Objects.requireNonNull(config.getString("disband_message")).replace("%player%", player.getName()));
                partyController.deleteParty(player.getUniqueId());
            } else {
                player.sendMessage(TextUtility.parseColors(config.getString("error_message")));
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
    }

    private void setPassword(CommandSender sender, String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                try {
                    String password = args[1];
                    PartyController partyController = plugin.getPartyController();
                    if (partyController.isPartyLeader(player.getUniqueId())) {
                        partyController.getParty(player.getUniqueId()).setPassword(password);
                        player.sendMessage(TextUtility.parseColors(config.getString("password_changed")));
                    } else {
                        player.sendMessage(TextUtility.parseColors(config.getString("error_message")));
                    }
                } catch (IllegalArgumentException e) {
                    player.sendMessage(TextUtility.parseColors(e.getMessage()));
                }
            } else {
                sender.sendMessage("Only players can run that command");
            }
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage("You must specify the password");
        }
    }

    private void join(CommandSender sender, String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                PartyController partyController = plugin.getPartyController();
                try {
                    String partyLeaderName = args[1];
                    String password = args[2];
                    Player partyLeader = Bukkit.getPlayer(partyLeaderName);
                    UUID partyLeaderUUID = partyLeader.getUniqueId();
                    if (partyController.isPlayerInParty(player.getUniqueId())) {
                        player.sendMessage(TextUtility.parseColors(config.getString("already_in_party")));
                    } else if (partyController.doesPartyExist(partyLeaderUUID)) {
                        Party party = partyController.getParty(partyLeaderUUID);
                        if (party.getPassword().equals(password)) {
                            try {
                                if (partyController.getPartySize(partyLeaderUUID) >= config.getInt("party_limit")) {
                                    player.sendMessage(TextUtility.parseColors(config.getString("party_limit_reached")));
                                } else {
                                    partyController.addPlayerToParty(player.getUniqueId(), partyLeaderUUID);
                                    player.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("invite_accepted")).replace("%player%", partyLeaderName)));
                                    partyLeader.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("accept_party_notification")).replace("%player%", player.getName())));
                                }
                            } catch (InvalidPartyException e) {
                                player.sendMessage(TextUtility.parseColors(config.getString("invalid_party")));
                            }
                        } else {
                            player.sendMessage(TextUtility.parseColors(config.getString("wrong_password")));
                        }
                    } else {
                        player.sendMessage(TextUtility.parseColors(config.getString("invalid_party")));
                    }
                } catch (IllegalArgumentException e) {
                    player.sendMessage(TextUtility.parseColors(config.getString(e.getMessage())));
                }
            } else {
                sender.sendMessage("Only players can run that command");
            }
        } catch (
                IndexOutOfBoundsException e) {
            sender.sendMessage("Missing parameters. Use /party to see the list of available commands");
        }

    }

    private void invite(CommandSender sender, String[] args) {
        try {
            if (sender instanceof Player) {
                Player partyLeader = (Player) sender;
                Player playerInvited = Bukkit.getPlayer(args[1]);
                PartyController partyManager = plugin.getPartyController();
                if (partyLeader.equals(playerInvited)) {
                    sender.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("cannot_invite_yourself"))));
                } else if (partyManager.doesPartyExist(partyLeader.getUniqueId()) && partyManager.getParty(partyLeader.getUniqueId()).getPlayerList().contains(Objects.requireNonNull(playerInvited).getUniqueId())) {
                    sender.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("player_already_in_party")).replace("%player%", playerInvited.getName())));
                } else {
                    plugin.getPartyController().createParty(partyLeader.getUniqueId());
                    assert playerInvited != null;
                    plugin.getPartyController().addInvite(playerInvited.getUniqueId(), partyLeader.getUniqueId());
                    partyLeader.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("invite_message")).replace("%player%", playerInvited.getName())));
                    playerInvited.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("invite_received_message")).replace("%player%", partyLeader.getName())));
                }
            } else {
                sender.sendMessage("Only players can run that command");
            }
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage("Missing parameters. Use /party to see the list of available commands");
        }
    }

    private void accept(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PartyController partyController = plugin.getPartyController();
            if (partyController.hasPendingInvite(player.getUniqueId())) {
                if (!partyController.isPartyLeader(player.getUniqueId())) {
                    UUID partyLeaderUUID = partyController.removeInvite(player.getUniqueId());
                    try {
                        if (partyController.getPartySize(partyLeaderUUID) >= config.getInt("party_limit")) {
                            player.sendMessage(Objects.requireNonNull(config.getString("party_limit_reached")));
                        } else {
                            partyController.addPlayerToParty(player.getUniqueId(), partyLeaderUUID);
                            Player partyLeader = Bukkit.getPlayer(partyLeaderUUID);
                            assert partyLeader != null;
                            player.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("invite_accepted")).replace("%player%", partyLeader.getName())));
                            partyLeader.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("accept_party_notification")).replace("%player%", player.getName())));
                        }
                    } catch (InvalidPartyException e) {
                        player.sendMessage(TextUtility.parseColors(config.getString("invalid_party")));
                    }
                } else {
                    player.sendMessage(TextUtility.parseColors(config.getString("already_in_party")));
                }
            } else {
                player.sendMessage(TextUtility.parseColors(config.getString("no_invites")));
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
    }

    private void decline(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PartyController partyController = plugin.getPartyController();
            if (partyController.hasPendingInvite(player.getUniqueId())) {
                UUID partyLeaderUUID = partyController.removeInvite(player.getUniqueId());
                Player partyLeader = Bukkit.getPlayer(partyLeaderUUID);
                assert partyLeader != null;
                player.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("invite_refused")).replace("%player%", partyLeader.getName())));
                partyLeader.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("decline_party_notification")).replace("%player%", player.getName())));
            } else {
                player.sendMessage(TextUtility.parseColors(config.getString("no_invites")));
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
    }

    private void leave(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PartyController partyController = plugin.getPartyController();
            if (partyController.isPlayerInParty(player.getUniqueId())) {
                if (partyController.isPartyLeader(player.getUniqueId())) {
                    player.sendMessage(TextUtility.parseColors(config.getString("party_leader_left")));
                } else {
                    UUID partyLeader = partyController.getPlayerParty(player.getUniqueId()).getLeader();
                    partyController.removePlayerFromParty(player.getUniqueId());
                    Bukkit.getPlayer(partyLeader).sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("player_left_party")).replace("%player%", player.getName())));
                    player.sendMessage(TextUtility.parseColors(config.getString("player_left")));
                }
            } else {
                player.sendMessage(TextUtility.parseColors("not_on_party"));
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
    }

    private void kick(CommandSender sender, String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Player toBeKicked = Bukkit.getPlayer(args[1]);
                PartyController partyController = plugin.getPartyController();
                if (partyController.isPartyLeader(player.getUniqueId())) {
                    assert toBeKicked != null;
                    if (partyController.getPlayerParty(player.getUniqueId()).equals(partyController.getPlayerParty(toBeKicked.getUniqueId()))) {
                        if (partyController.isPartyLeader(toBeKicked.getUniqueId())) {
                            player.sendMessage(TextUtility.parseColors("party_leader_left"));
                        } else {
                            partyController.removePlayerFromParty(toBeKicked.getUniqueId(), player.getUniqueId());
                            partyController.sendMessageToPartyMembers(player.getUniqueId(), Objects.requireNonNull(config.getString("player_kicked")).replace("%player%", toBeKicked.getName()));
                            toBeKicked.sendMessage(TextUtility.parseColors(config.getString("kicked")));
                        }
                    } else {
                        player.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("player_not_in_party")).replace("%player%", toBeKicked.getName())));
                    }
                } else {
                    player.sendMessage(TextUtility.parseColors(Objects.requireNonNull(config.getString("not_leader")).replace("%player%", player.getName())));
                }
            } else {
                sender.sendMessage("Only players can run that command");
            }
        } catch (Exception e) {
            sender.sendMessage("Missing or invalid parameters. Use /party to see the list of available commands");
        }
    }

    private void chat(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PartyController partyController = plugin.getPartyController();
            if (args.length > 1) {
                if (partyController.isPlayerInParty(player.getUniqueId()) || partyController.doesPartyExist(player.getUniqueId())) {
                    String message = String.join(" ", Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
                    partyController.sendMessageToPartyMembers(partyController.getPlayerParty(player.getUniqueId()).getLeader(), "&2[PARTY] &a" + player.getName() + ": " + message);
                } else {
                    player.sendMessage(TextUtility.parseColors(config.getString("not_on_party")));
                }
            } else {
                if (partyController.isPlayerChatting(player.getUniqueId())) {
                    player.sendMessage(TextUtility.parseColors("&eParty chat disabilitata!"));
                    partyController.removeChattingPlayer(player.getUniqueId());
                } else {
                    player.sendMessage(TextUtility.parseColors("&eParty chat abilitata!"));
                    partyController.addChattingPlayer(player.getUniqueId());
                }
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
    }

    private void info(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            PartyController partyManager = plugin.getPartyController();
            Player player = (Player) sender;
            if (partyManager.isPlayerInParty(player.getUniqueId())) {
                player.sendMessage(TextUtility.parseColors(partyManager.getPlayerParty(player.getUniqueId()).getPartyInfo()));
            } else {
                player.sendMessage(TextUtility.parseColors(config.getString("not_on_party")));
            }
        } else {
            sender.sendMessage("Only players can run that command");
        }
    }
}
