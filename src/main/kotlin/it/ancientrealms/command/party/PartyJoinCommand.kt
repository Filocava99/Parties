package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.exception.InvalidPartyException
import it.ancientrealms.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class PartyJoinCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.join"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        try {
            if (sender is Player) {
                val partyManager = Parties.INSTANCE.partyManager
                val config = Parties.INSTANCE.config.config
                try {
                    val partyLeaderName = args[1]
                    val password = args[2]
                    val partyLeader = Bukkit.getOfflinePlayer(partyLeaderName)
                    val partyLeaderUUID = partyLeader.uniqueId
                    if (partyManager.isPlayerInParty(sender.uniqueId)) {
                        sender.sendMessage(TextUtils.parseColors(config.getString("already_in_party")))
                    } else if (partyManager.doesPartyExist(partyLeaderUUID)) {
                        val party = partyManager.getParty(partyLeaderUUID)
                        if (party?.password == password) {
                            try {
                                if (partyManager.getPartySize(partyLeaderUUID) >= config.getInt("party_limit")) {
                                    sender.sendMessage(TextUtils.parseColors(config.getString("party_limit_reached")))
                                } else {
                                    partyManager.addPlayerToParty(sender.uniqueId, partyLeaderUUID)
                                    sender.sendMessage(
                                        TextUtils.parseColors(
                                            Objects.requireNonNull<String>(
                                                config.getString(
                                                    "invite_accepted"
                                                )
                                            ).replace("%player%", partyLeaderName)
                                        )
                                    )
                                    if (partyLeader.isOnline) {
                                        Bukkit.getPlayer(partyLeaderUUID)?.sendMessage(
                                            TextUtils.parseColors(
                                                Objects.requireNonNull<String>(config.getString("accept_party_notification"))
                                                    .replace("%player%", sender.name)
                                            )
                                        )
                                    }
                                }
                            } catch (e: InvalidPartyException) {
                                sender.sendMessage(TextUtils.parseColors(config.getString("invalid_party")))
                            }
                        } else {
                            sender.sendMessage(TextUtils.parseColors(config.getString("wrong_password")))
                        }
                    } else {
                        sender.sendMessage(TextUtils.parseColors(config.getString("invalid_party")))
                    }
                } catch (e: IllegalArgumentException) {
                    sender.sendMessage(TextUtils.parseColors(e.message))
                }
            } else {
                sender!!.sendMessage("Only players can run that command")
            }
        } catch (e: IndexOutOfBoundsException) {
            sender!!.sendMessage("Missing parameters. Use /party to see the list of available commands")
        }
    }
}