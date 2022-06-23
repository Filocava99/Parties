package it.filippocavallari.command

import it.filippocavallari.Parties
import it.filippocavallari.utils.TextUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChatCommand : SubCommand() {
    override fun getPermission(): String = "parties.chat"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val partyManager = Parties.INSTANCE.partyManager
            val chatManager = Parties.INSTANCE.chatManager
            if (partyManager.isPlayerInParty(sender.uniqueId)) {
                if (args.isNotEmpty()) {
                    val message = java.lang.String.join(" ", listOf(*args.copyOfRange(0, args.size)))
                    partyManager.getPlayerParty(sender.uniqueId)?.let {
                        chatManager.sendMessageToPartyMembers(
                            it.leader,
                            "&2[PARTY] &a" + sender.name + ": " + message
                        )
                    }
                } else {
                    if (chatManager.isPlayerChatting(sender.uniqueId)) {
                        sender.sendMessage(TextUtils.parseColors("&eParty chat disabled!"))
                        chatManager.removeChattingPlayer(sender.uniqueId)
                    } else {
                        sender.sendMessage(TextUtils.parseColors("&eParty chat enabled!"))
                        chatManager.addChattingPlayer(sender.uniqueId)
                    }
                }
            } else {
                sender.sendMessage(TextUtils.parseColors(Parties.INSTANCE.config.config.getString("not_on_party")))
            }
        } else {
            sender?.sendMessage("Only players can run that command")
        }
    }
}