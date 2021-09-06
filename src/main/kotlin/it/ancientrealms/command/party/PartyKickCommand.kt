package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartyKickCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.kick"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        try {
            if (sender is Player) {
                val toBeKicked = Bukkit.getOfflinePlayer(args[1])
                val partyManager = Parties.INSTANCE.partyManager
                val config = Parties.INSTANCE.config.config
                if (partyManager.isPartyLeader(sender.uniqueId)) {
                    if (partyManager.getPlayerParty(sender.uniqueId) == partyManager.getPlayerParty(toBeKicked.uniqueId)) {
                        if (partyManager.isPartyLeader(toBeKicked.uniqueId)) {
                            sender.sendMessage(TextUtils.parseColors("party_leader_left"))
                        } else {
                            partyManager.removePlayerFromParty(toBeKicked.uniqueId, sender.uniqueId)
                            Parties.INSTANCE.chatManager.sendMessageToPartyMembers(
                                sender.uniqueId,
                                config.getString("player_kicked")?.replace("%player%", toBeKicked.name!!) ?: ""
                            )
                            if (toBeKicked.isOnline) {
                                Bukkit.getPlayer(args[1])
                                    ?.sendMessage(TextUtils.parseColors(config.getString("kicked")))
                            }
                        }
                    } else {
                        sender.sendMessage(
                            TextUtils.parseColors(config.getString("player_not_in_party"))
                                .replace("%player%", toBeKicked.name ?: "")
                        )
                    }
                } else {
                    sender.sendMessage(
                        TextUtils.parseColors(config.getString("not_leader")).replace("%player%", sender.name)
                    )
                }
            } else {
                sender?.sendMessage("Only players can run that command")
            }
        } catch (e: Exception) {
            sender?.sendMessage("Missing or invalid parameters. Use /party to see the list of available commands")
        }
    }
}