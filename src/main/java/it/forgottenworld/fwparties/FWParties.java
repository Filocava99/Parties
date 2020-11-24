package it.forgottenworld.fwparties;

import it.forgottenworld.fwparties.command.PartyCommand;
import it.forgottenworld.fwparties.config.Config;
import it.forgottenworld.fwparties.controller.PartyController;
import it.forgottenworld.fwparties.controller.StorageController;
import it.forgottenworld.fwparties.listener.PlayerListener;
import it.forgottenworld.fwparties.task.PartySerializationTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FWParties extends JavaPlugin {

    private static FWParties INSTANCE;

    private StorageController storageController;
    private PartyController partyController;
    private Config config;

    @Override
    public void onEnable() {
       loadData();
       registerListeners();
       registerCommands();
       registerTasks();
    }

    @Override
    public void onDisable() {
        saveParties();
    }

    public static FWParties getInstance(){
        return INSTANCE;
    }

    public PartyController getPartyController() {
        return partyController;
    }

    public Config getPluginConfig(){
        return config;
    }

    public void saveParties(){
        storageController.saveParties(partyController);
    }

    private void loadData(){
        storageController = new StorageController();
        config = storageController.loadConfig();
        partyController = storageController.loadParties();
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    private void registerCommands(){
        Objects.requireNonNull(getCommand("party")).setExecutor(new PartyCommand());
    }

    private void registerTasks(){
        new PartySerializationTask();
    }
}
