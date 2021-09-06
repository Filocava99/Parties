package it.ancientrealms.command.party

import it.ancientrealms.Parties
import it.ancientrealms.command.SubCommand
import it.ancientrealms.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class PartyInviteCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.invite"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        try {
            if (sender is Player) {
                val playerInvited = Bukkit.getPlayer(args[1])
                val partyManager = Parties.INSTANCE.partyManager
                val config = Parties.INSTANCE.config.config
                if (sender == playerInvited) {
                    sender.sendMessage(TextUtils.parseColors(Objects.requireNonNull(config.getString("cannot_invite_yourself"))))
                } else if (partyManager.getParty(sender.uniqueId)?.playerList?.contains(playerInvited?.uniqueId) == true) {
                    sender.sendMessage(
                        TextUtils.parseColors(
                            config.getString("player_already_in_party")?.replace("%player%", playerInvited!!.name)
                        )
                    )
                } else {
                    assert(playerInvited != null)
                    partyManager.addInvite(playerInvited!!.uniqueId, sender.uniqueId)
                    sender.sendMessage(
                        TextUtils.parseColors(
                            config.getString("invite_message")?.replace("%player%", playerInvited.name)
                        )
                    )
                    playerInvited.sendMessage(
                        TextUtils.parseColors(
                            config.getString("invite_received_message")?.replace("%player%", sender.name)
                        )
                    )
                }
            } else {
                sender?.sendMessage("Only players can run that command")
            }
        } catch (e: IndexOutOfBoundsException) {
            sender?.sendMessage("Missing or invalid parameters. Use /party to see the list of available commands")
        } catch (e: NullPointerException) {
            sender?.sendMessage("Missing or invalid parameters. Use /party to see the list of available commands")
        }
    }
}