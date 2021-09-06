package it.ancientrealms

import it.ancientrealms.api.ChatManager
import it.ancientrealms.api.PartyManager
import it.ancientrealms.api.StorageManager
import it.ancientrealms.command.AdminCommand
import it.ancientrealms.command.ChatCommand
import it.ancientrealms.command.MainCommand
import it.ancientrealms.command.PosCommand
import it.ancientrealms.command.party.*
import it.ancientrealms.listener.PlayerListener
import it.ancientrealms.manager.ChatManagerImp
import it.ancientrealms.manager.StorageManagerImp
import it.tigierrei.configapi.Config
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin

class Parties : JavaPlugin() {

    lateinit var config: Config
    lateinit var storageManager: StorageManager
    lateinit var partyManager: PartyManager
    lateinit var chatManager: ChatManager

    override fun onEnable() {
        storageManager = StorageManagerImp()
        chatManager = ChatManagerImp()
        loadData()
        registerCommands()
        registerListener()
        storageManager.runSerializationTask()
    }

    override fun onDisable() {
        storageManager.saveParties(partyManager)
    }

    override fun reloadConfig(){
        config = storageManager.loadConfig()
    }

    private fun loadData(){
        config = storageManager.loadConfig()
        partyManager = storageManager.loadParties()
    }

    private fun registerCommands(){
        MainCommand().addSubCommand(listOf("accept"), PartyAcceptCommand())
            .addSubCommand(listOf("chat"), PartyChatCommand())
            .addSubCommand(listOf("create"), PartyCreateCommand())
            .addSubCommand(listOf("decline"), PartyDeclineCommand())
            .addSubCommand(listOf("disband"), PartyDisbandCommand())
            .addSubCommand(listOf("help"), PartyHelpCommand())
            .addSubCommand(listOf("info"), PartyInfoCommand())
            .addSubCommand(listOf("invite"), PartyInviteCommand())
            .addSubCommand(listOf("join"), PartyJoinCommand())
            .addSubCommand(listOf("kick"), PartyKickCommand())
            .addSubCommand(listOf("leave"), PartyLeaveCommand())
            .addSubCommand(listOf("setpassword", "sp", "password"), PartySetPasswordCommand())
            .register(this, "party")
        MainCommand().addSubCommand(listOf(""), ChatCommand()).register(this, "partychat", "pc")
        MainCommand().addSubCommand(listOf(""), AdminCommand()).register(this, "partyadmin", "pa")
        MainCommand().addSubCommand(listOf(""), PosCommand()).register(this, "partypos", "pos")
    }

    private fun registerListener() {
        Bukkit.getPluginManager().registerEvents(PlayerListener(), this)
    }

    companion object {
        private var plugin: Parties? = null
        val INSTANCE: Parties
            get() {
                if (plugin == null) {
                    plugin = getPlugin(Parties::class.java)
                }
                return plugin!!
            }
    }

    private fun CommandExecutor.register(javaPlugin: JavaPlugin, command: String, vararg aliases: String) {
        javaPlugin.getCommand(command)?.setExecutor(this)
        if (aliases.isNotEmpty()) javaPlugin.getCommand(command)?.aliases = aliases.toMutableList()
    }

}