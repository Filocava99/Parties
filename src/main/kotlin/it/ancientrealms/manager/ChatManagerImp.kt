package it.ancientrealms.manager

import it.ancientrealms.Parties
import it.ancientrealms.api.ChatManager
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*
import java.util.stream.Stream
import kotlin.collections.HashSet

@ExperimentalSerializationApi
class ChatManagerImp : ChatManager {

    private val chattingPlayers = HashSet<UUID>()
    private val spyingChatPlayers = HashSet<UUID>()

    override fun isPlayerSpyingChat(player: UUID): Boolean = spyingChatPlayers.contains(player)

    override fun addSpyingChatPlayer(player: UUID) {
        spyingChatPlayers.add(player)
    }

    override fun removeSpyingChatPlayer(player: UUID) {
        spyingChatPlayers.remove(player)
    }

    override fun sendMessageToPartyMembers(partyLeader: UUID, message: String) {
        val partyManager = Parties.INSTANCE.partyManager
        partyManager.getParty(partyLeader)?.let {
            Stream.concat(it.playerList.stream(), spyingChatPlayers.stream()).forEach { uuid ->
                val player = Bukkit.getPlayer(uuid)
                player?.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
            }
        }
    }

    override fun addChattingPlayer(player: UUID) {
        chattingPlayers.add(player)
    }

    override fun removeChattingPlayer(player: UUID) {
        chattingPlayers.remove(player)
    }

    override fun isPlayerChatting(player: UUID): Boolean = chattingPlayers.contains(player)


}