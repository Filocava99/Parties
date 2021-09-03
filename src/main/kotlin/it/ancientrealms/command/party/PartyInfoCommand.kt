package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.utils.TextUtils
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@ExperimentalSerializationApi
class PartyInfoCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.info"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val partyManager = Parties.INSTANCE.partyManager
            if (partyManager.isPlayerInParty(sender.uniqueId)) {
                sender.sendMessage(TextUtils.parseColors(partyManager.getPlayerParty(sender.uniqueId)?.getPartyInfo()))
            } else {
                sender.sendMessage(TextUtils.parseColors(Parties.INSTANCE.config.config.getString("not_on_party")))
            }
        } else {
            sender!!.sendMessage("Only players can run that command")
        }
    }
}