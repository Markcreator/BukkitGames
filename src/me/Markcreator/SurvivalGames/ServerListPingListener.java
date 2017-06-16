package me.Markcreator.SurvivalGames;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

	private Main plugin;
    public ServerListPingListener(Main Plugin) {
        this.plugin = Plugin;   
    }
    
    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
    	if(plugin.winner != null) {
    		event.setMotd(plugin.sg + ChatColor.GOLD + plugin.winner + " has won!");
    		event.setMaxPlayers(-1);
    	} else if(plugin.started == false && plugin.starting == false) {
    		event.setMotd(plugin.sg + ChatColor.DARK_GREEN + "Waiting for players...");
    		event.setMaxPlayers(plugin.maxPlayers);
    		
    	} else if(plugin.started == false && plugin.starting == true) {
    		event.setMotd(plugin.sg + ChatColor.GREEN + "The round is starting in " + plugin.counter + ".");
    		event.setMaxPlayers(plugin.maxPlayers);
    		
    	} else {
    		event.setMotd(plugin.sg + ChatColor.DARK_RED + plugin.livingPlayers.size() + " players left.");
    		event.setMaxPlayers(plugin.maxPlayers);
    	}
    }
}
