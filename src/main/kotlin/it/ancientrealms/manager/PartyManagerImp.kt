package it.ancientrealms.manager

import it.ancientrealms.api.PartyManager
import it.ancientrealms.model.Party
import java.io.Serializable
import java.util.*


class PartyManagerImp : PartyManager, Serializable {

    private val inviteMap = HashMap<UUID, UUID>()
    private val partyMap = HashMap<UUID, Party>()
    private val playerMap = HashMap<UUID, Party>()

    override fun addInvite(playerInvited: UUID, partyLeader: UUID) {
        inviteMap[playerInvited] = partyLeader
    }

    override fun hasPendingInvite(player: UUID) = inviteMap.containsKey(player)

    override fun removeInvite(player: UUID): UUID? = inviteMap.remove(player)

    override fun addPlayerToParty(playerToAdd: UUID, partyLeader: UUID) {
        val party = partyMap[partyLeader]
        party?.let {
            it.playerList.add(playerToAdd)
            playerMap[playerToAdd] = it
        }
    }

    override fun removePlayerFromParty(playerToRemove: UUID, partyLeader: UUID) {
        playerMap.remove(playerToRemove)?.playerList?.remove(playerToRemove)
    }

    override fun doesPartyExist(partyLeader: UUID) = partyMap.containsKey(partyLeader)

    override fun isPartyLeader(player: UUID) = doesPartyExist(player)

    override fun getParty(partyLeader: UUID): Party? = partyMap[partyLeader]

    override fun deleteParty(partyLeader: UUID) {
        partyMap.remove(partyLeader)?.playerList?.forEach { player -> playerMap.remove(player) }
    }

    override fun getPlayerParty(player: UUID): Party? = playerMap[player]

    override fun isPlayerInParty(player: UUID): Boolean = playerMap.containsKey(player)

    override fun createParty(partyLeader: UUID, password: String?) {
        val party = Party(partyLeader, password ?: UUID.randomUUID().toString(), mutableSetOf<UUID>(partyLeader))
        partyMap[partyLeader] = party
        playerMap[partyLeader] = party
    }

    override fun createParty(partyLeader: UUID) {
        createParty(partyLeader, null)
    }

    override fun getPartySize(partyLeader: UUID): Int = partyMap[partyLeader]?.playerList?.size ?: 0

    override fun arePlayersInSameParty(firstPlayer: UUID, secondPlayer: UUID): Boolean = playerMap[firstPlayer] == playerMap[secondPlayer]
}