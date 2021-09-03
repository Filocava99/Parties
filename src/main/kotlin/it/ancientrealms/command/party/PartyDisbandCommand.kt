package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.utils.TextUtils
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@ExperimentalSerializationApi
class PartyDisbandCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.disband"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val partyManager = Parties.INSTANCE.partyManager
            val config = Parties.INSTANCE.config.config
            if (partyManager.isPartyLeader(sender.uniqueId)) {
                Parties.INSTANCE.chatManager.sendMessageToPartyMembers(
                    sender.uniqueId,
                    Objects.requireNonNull<String>(config.getString("disband_message")).replace("%sender%", sender.name)
                )
                partyManager.deleteParty(sender.uniqueId)
            } else {
                sender.sendMessage(TextUtils.parseColors(config.getString("error_message")))
            }
        } else {
            sender!!.sendMessage("Only players can run that command")
        }
    }
}