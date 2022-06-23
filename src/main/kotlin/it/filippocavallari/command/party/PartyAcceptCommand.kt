package it.filippocavallari.command.party

import it.filippocavallari.Parties
import it.filippocavallari.command.SubCommand
import it.filippocavallari.exception.InvalidPartyException
import it.filippocavallari.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class PartyAcceptCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.accept"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val partyManager = Parties.INSTANCE.partyManager
            val config = Parties.INSTANCE.config.config
            if (partyManager.hasPendingInvite(sender.uniqueId)) {
                if (!partyManager.isPartyLeader(sender.uniqueId)) {
                    val partyLeaderUUID = partyManager.removeInvite(sender.uniqueId)
                    partyLeaderUUID?.let {
                        try {
                            if (partyManager.getPartySize(it) >= config.getInt("party_limit")) {
                                sender.sendMessage(config.getString("party_limit_reached") ?: "")
                            } else {
                                partyManager.addPlayerToParty(sender.uniqueId, partyLeaderUUID)
                                sender.sendMessage(
                                    TextUtils.parseColors(
                                        config.getString("invite_accepted")
                                    ).replace(
                                        "%player%",
                                        Bukkit.getOfflinePlayer(partyLeaderUUID).name ?: ""
                                    )
                                )
                                if (Bukkit.getOfflinePlayer(partyLeaderUUID).isOnline) {
                                    val partyLeader = Bukkit.getPlayer(partyLeaderUUID)!!
                                    partyLeader.sendMessage(
                                        TextUtils.parseColors(
                                            Objects.requireNonNull<String>(
                                                config.getString(
                                                    "accept_party_notification"
                                                )
                                            ).replace("%player%", sender.name)
                                        )
                                    )
                                }
                            }
                        } catch (e: InvalidPartyException) {
                            sender.sendMessage(TextUtils.parseColors(config.getString("invalid_party")))
                        }
                    }
                } else {
                    sender.sendMessage(TextUtils.parseColors(config.getString("already_in_party")))
                }
            } else {
                sender.sendMessage(TextUtils.parseColors(config.getString("no_invites")))
            }
        } else {
            sender!!.sendMessage("Only players can run that command")
        }
    }
}