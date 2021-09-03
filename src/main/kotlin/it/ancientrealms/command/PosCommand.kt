package it.ancientrealms.command

import it.ancientrealms.Parties
import it.ancientrealms.utils.TextUtils
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@ExperimentalSerializationApi
class PosCommand : SubCommand() {
    override fun getPermission(): String = "parties.pos"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            val partyManager = Parties.INSTANCE.partyManager
            if (partyManager.isPlayerInParty(sender.uniqueId)) {
                val message =
                    ChatColor.GREEN.toString() + "X: " + ChatColor.GOLD + sender.location.blockX + ChatColor.GREEN + "  Y: " + ChatColor.GOLD + sender.location.blockY + ChatColor.GREEN + "  Z: " + ChatColor.GOLD + sender.location.blockZ
                partyManager.getPlayerParty(sender.uniqueId)?.leader?.let {
                    Parties.INSTANCE.chatManager.sendMessageToPartyMembers(
                        it,
                        "&2[PARTY] &a" + sender.name + ": " + message
                    )
                }
            } else {
                sender.sendMessage(TextUtils.parseColors(Parties.INSTANCE.config.config.getString("not_on_party")))
            }
        } else {
            sender?.sendMessage("Only players can run that command")
        }
    }
}