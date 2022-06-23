package it.filippocavallari.command.party

import it.filippocavallari.Parties
import it.filippocavallari.command.SubCommand
import it.filippocavallari.utils.TextUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartySetPasswordCommand : SubCommand() {
    override fun getPermission(): String = "parties.party.password"

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>) {
        try {
            if (sender is Player) {
                try {
                    val password = args[1]
                    val partyManager = Parties.INSTANCE.partyManager
                    val config = Parties.INSTANCE.config.config
                    if (partyManager.isPartyLeader(sender.uniqueId)) {
                        partyManager.getParty(sender.uniqueId)?.password = password
                        sender.sendMessage(TextUtils.parseColors(config.getString("password_changed")))
                    } else {
                        sender.sendMessage(TextUtils.parseColors(config.getString("error_message")))
                    }
                } catch (e: IllegalArgumentException) {
                    sender.sendMessage(TextUtils.parseColors(e.message))
                }
            } else {
                sender!!.sendMessage("Only players can run that command")
            }
        } catch (e: IndexOutOfBoundsException) {
            sender!!.sendMessage("You must specify the password")
        }
    }
}