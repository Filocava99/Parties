package it.filippocavallari.command.party

import it.filippocavallari.command.SubCommand
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartyHelpCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.help"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        if(sender is Player){
            val componentBuilder = ComponentBuilder(
                ChatColor.translateAlternateColorCodes(
                    '&',
                    """
                  &6-------{ Parties }-------
                  &a/party create <password>
                  &a/party disband
                  &a/party setPassword <password>
                  &a/party join <partyLeaderName> <password>
                  &a/party invite <playerName>
                  &a/party accept
                  &a/party decline
                  &a/party leave
                  &a/party kick <player>
                  &a/party chat [message]> &ooppure &r&a/pc [message]
                  &a/party info
                  &a/pos &oto send your position in the party chat
                  """.trimIndent()
                )
            )
            sender.spigot().sendMessage(*componentBuilder.create())
        }
    }
}