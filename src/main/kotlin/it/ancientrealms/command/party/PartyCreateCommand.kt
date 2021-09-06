package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.utils.TextUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartyCreateCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.create"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val password = if (args.size > 1) {
                args[1]
            } else {
                null
            }
            val partyManager = Parties.INSTANCE.partyManager
            val config = Parties.INSTANCE.config.config
            if (partyManager.isPlayerInParty(sender.uniqueId) || partyManager.doesPartyExist(sender.uniqueId)) {
                sender.sendMessage(TextUtils.parseColors(config.getString("already_in_party")))
            } else {
                partyManager.createParty(sender.uniqueId, password)
                sender.sendMessage(TextUtils.parseColors(config.getString("party_created")))
            }
        } else {
            sender!!.sendMessage("Only players can run that command")
        }
    }
}