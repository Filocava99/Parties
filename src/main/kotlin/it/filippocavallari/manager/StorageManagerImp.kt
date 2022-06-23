package it.filippocavallari.manager

import it.filippocavallari.Parties
import it.filippocavallari.api.PartyManager
import it.filippocavallari.api.StorageManager
import it.tigierrei.configapi.Config
import java.io.*

class StorageManagerImp : StorageManager {

    override fun loadConfig(): Config = Config(File(Parties.INSTANCE.dataFolder, "config.yml"), Parties.INSTANCE)

    override fun loadParties(): PartyManager {
        val file = File(
            Parties.INSTANCE.dataFolder,
            "parties.dat"
        )
        return if(file.exists()){
            ObjectInputStream(FileInputStream(file)).readObject() as PartyManagerImp
        }else{
            PartyManagerImp()
        }

    }

    override fun saveParties(partyManager: PartyManager) {
        ObjectOutputStream(FileOutputStream(File(
            Parties.INSTANCE.dataFolder,
            "parties.dat"
        ))).writeObject(partyManager)
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