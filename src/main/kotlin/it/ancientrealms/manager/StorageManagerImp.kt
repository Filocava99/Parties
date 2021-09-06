package it.ancientrealms.manager

import it.ancientrealms.Parties
import it.ancientrealms.api.PartyManager
import it.ancientrealms.api.StorageManager
import it.tigierrei.configapi.Config
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.StandardOpenOption

@ExperimentalSerializationApi
class StorageManagerImp : StorageManager {

    override fun loadConfig(): Config = Config(File(Parties.INSTANCE.dataFolder, "config.yml"), Parties.INSTANCE)

    override fun loadParties(): PartyManager {
        val file = File(
            Parties.INSTANCE.dataFolder,
            "parties.json"
        )
        return if(file.exists()){
            Json.decodeFromString<PartyManager>(
                Files.readAllLines(
                    file.toPath()
                ).joinToString())
        }else{
            PartyManagerImp()
        }

    }

    override fun saveParties(partyManager: PartyManager) {
        Files.writeString(
            File(Parties.INSTANCE.dataFolder, "parties.json").toPath(),
            Json.encodeToString(partyManager),
            Charset.defaultCharset(),
            StandardOpenOption.WRITE
        )
    }

    override fun runSerializationTask() {
        //30 minutes
        val period = (20 * 60 * 30).toLong()
        val delay: Long = 0
        Parties.INSTANCE.server.scheduler.runTaskTimerAsynchronously(
            Parties.INSTANCE,
            Runnable { saveParties(Parties.INSTANCE.partyManager) }, delay, period
        )
    }
}