package me.Markcreator.SurvivalGames;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobListener implements Listener {

	@SuppressWarnings("unused")
	private Main plugin;
    public MobListener(Main Plugin) {
        this.plugin = Plugin;   
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
    	LivingEntity mob = event.getEntity();
    	
    	if(mob instanceof Enderman) {
    		event.setCancelled(true);
    	}
    }
}
