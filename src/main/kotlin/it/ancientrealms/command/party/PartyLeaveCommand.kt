package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.utils.TextUtils
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@ExperimentalSerializationApi
class PartyLeaveCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.leave"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val partyManager = Parties.INSTANCE.partyManager
            val config = Parties.INSTANCE.config.config
            if (partyManager.isPlayerInParty(sender.uniqueId)) {
                if (partyManager.isPartyLeader(sender.uniqueId)) {
                    sender.sendMessage(TextUtils.parseColors(config.getString("party_leader_left")))
                } else {
                    val partyLeaderUUID = partyManager.getPlayerParty(sender.uniqueId)?.leader
                    partyLeaderUUID?.let {
                        partyManager.removePlayerFromParty(sender.uniqueId, it)
                        val partyLeader = Bukkit.getPlayer(it)
                        partyLeader?.sendMessage(
                            TextUtils.parseColors(config.getString("player_left_party"))
                                .replace("%player%", sender.name)
                        )
                        sender.sendMessage(TextUtils.parseColors(config.getString("player_left")))
                    }
                }
            } else {
                sender.sendMessage(TextUtils.parseColors("not_on_party"))
            }
        } else {
            sender!!.sendMessage("Only players can run that command")
        }
    }
}