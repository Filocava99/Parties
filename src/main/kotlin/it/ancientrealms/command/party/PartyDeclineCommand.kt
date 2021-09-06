package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartyDeclineCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.decline"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val partyManager = Parties.INSTANCE.partyManager
            val config = Parties.INSTANCE.config.config
            if (partyManager.hasPendingInvite(sender.uniqueId)) {
                val partyLeaderUUID = partyManager.removeInvite(sender.uniqueId)
                partyLeaderUUID?.let {
                    sender.sendMessage(
                        TextUtils.parseColors(config.getString("invite_refused"))
                            .replace("%player%", Bukkit.getOfflinePlayer(it).name ?: "")
                    )
                    if (Bukkit.getOfflinePlayer(it).isOnline) {
                        val partyLeader = Bukkit.getPlayer(it)!!
                        partyLeader.sendMessage(
                            TextUtils.parseColors(
                                config.getString("decline_party_notification")?.replace("%player%", sender.name)
                            )
                        )
                    }
                }
            } else {
                sender.sendMessage(TextUtils.parseColors(config.getString("no_invites")))
            }
        } else {
            sender?.sendMessage("Only players can run that command")
        }
    }
}