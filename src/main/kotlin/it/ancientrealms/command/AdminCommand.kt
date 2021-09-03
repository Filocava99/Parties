package it.ancientrealms.command

import it.ancientrealms.Parties
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@ExperimentalSerializationApi
class AdminCommand : SubCommand() {
    override fun getPermission(): String = "parties.admin"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if (sender is Player) {
            if (args[0].equals("reload", ignoreCase = true)) {
                Parties.INSTANCE.reloadConfig()
                sender.sendMessage(ChatColor.GREEN.toString() + "Config reloaded")
            } else if (args[0].equals("spy", ignoreCase = true)) {
                val chatManager = Parties.INSTANCE.chatManager
                val uuid = sender.uniqueId
                if (chatManager.isPlayerSpyingChat(uuid)) {
                    chatManager.removeSpyingChatPlayer(uuid)
                    sender.sendMessage(ChatColor.YELLOW.toString() + "Disabled parties spy chat")
                } else {
                    chatManager.addSpyingChatPlayer(uuid)
                    sender.sendMessage(ChatColor.YELLOW.toString() + "Enabled parties spy chat")
                }
            } else {
                sender.sendMessage(ChatColor.DARK_GREEN.toString() + "\"-------{ Parties Admin }-------")
                sender.sendMessage(ChatColor.GREEN.toString() + "/pa reload")
                sender.sendMessage(ChatColor.GREEN.toString() + "/pa spy")
            }
        }
    }
}