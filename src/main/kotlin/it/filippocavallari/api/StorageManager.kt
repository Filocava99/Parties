package it.filippocavallari.api

import it.filippocavallari.configapi.Config

interface StorageManager {

    fun loadConfig() : Config

    fun loadParties(): PartyManager

    fun saveParties(partyManager: PartyManager)

    fun runSerializationTask()

}