package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.utils.TextUtils
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@ExperimentalSerializationApi
class PartyChatCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.chat"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val partyManager = Parties.INSTANCE.partyManager
            val chatManager = Parties.INSTANCE.chatManager
            val config = Parties.INSTANCE.config.config
            if (args.size > 1) {
                if (partyManager.isPlayerInParty(sender.uniqueId)) {
                    val message = java.lang.String.join(" ", listOf(*args.copyOfRange(1, args.size)))
                    partyManager.getPlayerParty(sender.uniqueId)?.let {
                        chatManager.sendMessageToPartyMembers(
                            it.leader,
                            "&2[PARTY] &a" + sender.name + ": " + message
                        )
                    }
                } else {
                    sender.sendMessage(TextUtils.parseColors(config.getString("not_on_party")))
                }
            } else {
                if (chatManager.isPlayerChatting(sender.uniqueId)) {
                    sender.sendMessage(TextUtils.parseColors("&eParty chat disabilitata!"))
                    chatManager.removeChattingPlayer(sender.uniqueId)
                } else {
                    sender.sendMessage(TextUtils.parseColors("&eParty chat abilitata!"))
                    chatManager.addChattingPlayer(sender.uniqueId)
                }
            }
        } else {
            sender?.sendMessage("Only players can run that command")
        }
    }
}