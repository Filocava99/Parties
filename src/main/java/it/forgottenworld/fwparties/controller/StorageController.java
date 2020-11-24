package it.forgottenworld.fwparties.controller;

import it.forgottenworld.fwparties.FWParties;
import it.forgottenworld.fwparties.config.Config;
import it.forgottenworld.fwparties.controller.PartyController;

import java.io.*;
import java.util.logging.Level;

public class StorageController {
    private final FWParties plugin;
    private final File configDir;

    public StorageController() {
        this.plugin = FWParties.getInstance();
        this.configDir = plugin.getDataFolder();
    }

    public Config loadConfig(){
        try{
        return new Config("config.yml", plugin);
        }catch (IOException e){
            plugin.getLogger().log(Level.SEVERE,"Error while loading config.yml. Disabling FWParties...");
            plugin.getPluginLoader().disablePlugin(plugin);
            return null;
        }
    }

    public PartyController loadParties(){
        File partyFile = new File(configDir, "parties");
        if(partyFile.exists()){
            try{
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(partyFile));
                return (PartyController) objectInputStream.readObject();
            }catch (IOException | ClassNotFoundException e){
                return new PartyController();
            }
        }else{
            return new PartyController();
        }
    }

    public void saveParties(PartyController partyManager){
        File partyFile = new File(configDir, "parties");
        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(partyFile));
            objectOutputStream.writeObject(partyManager);
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Error while saving parties on file " + partyFile.getName());
        }
    }
}
