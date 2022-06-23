package it.filippocavallari.api

import it.filippocavallari.model.Party
import java.util.*

interface PartyManager {

    fun addInvite(playerInvited: UUID, partyLeader: UUID)

    fun hasPendingInvite(player: UUID): Boolean

    fun removeInvite(player: UUID): UUID?

    fun addPlayerToParty(playerToAdd: UUID, partyLeader: UUID)

    fun removePlayerFromParty(playerToRemove: UUID, partyLeader: UUID)

    fun doesPartyExist(partyLeader: UUID) : Boolean

    fun isPartyLeader(player: UUID): Boolean

    fun getParty(partyLeader: UUID): Party?

    fun deleteParty(partyLeader: UUID)

    fun getPlayerParty(player: UUID): Party?

    fun isPlayerInParty(player: UUID): Boolean

    fun createParty(partyLeader: UUID, password: String?)

    fun createParty(partyLeader: UUID)

    fun getPartySize(partyLeader: UUID): Int

    fun arePlayersInSameParty(firstPlayer: UUID, secondPlayer: UUID): Boolean

}