package it.filippocavallari.api

import it.tigierrei.configapi.Config

interface StorageManager {

    fun loadConfig() : Config

    fun loadParties(): PartyManager

    fun saveParties(partyManager: PartyManager)

    fun runSerializationTask()

}