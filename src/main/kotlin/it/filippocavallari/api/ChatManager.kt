package it.filippocavallari.api

import java.util.*

interface ChatManager {

    fun isPlayerSpyingChat(player: UUID): Boolean

    fun addSpyingChatPlayer(player: UUID)

    fun removeSpyingChatPlayer(player: UUID)

    fun sendMessageToPartyMembers(partyLeader: UUID, message: String)

    fun addChattingPlayer(player: UUID)

    fun removeChattingPlayer(player: UUID)

    fun isPlayerChatting(player: UUID): Boolean

}