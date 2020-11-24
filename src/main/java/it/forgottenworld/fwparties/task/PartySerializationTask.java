package it.forgottenworld.fwparties.task;

import it.forgottenworld.fwparties.FWParties;

public class PartySerializationTask {

    public PartySerializationTask() {
        FWParties plugin = FWParties.getInstance();
        //30 minutes
        long period = 20 * 60 * 30;
        long delay = 0;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, plugin::saveParties,delay, period);
    }
}
