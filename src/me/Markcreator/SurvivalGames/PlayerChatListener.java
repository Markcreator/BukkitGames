package me.Markcreator.SurvivalGames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@SuppressWarnings("deprecation")
public class PlayerChatListener implements Listener {

	private Main plugin;
    public PlayerChatListener(Main Plugin) {
        this.plugin = Plugin;   
    }
    
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	
    	if(plugin.livingPlayers.contains(player) || plugin.winner != null) {
    		if(plugin.playerKit.containsKey(player)) {
    			Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + plugin.playerData.getInt("players." + player.getName() + ".points") + ChatColor.GRAY + "] " + ChatColor.GRAY + "[" + ChatColor.BLUE + plugin.playerKit.get(player) + ChatColor.GRAY + "] " + ChatColor.RESET + player.getDisplayName() + ChatColor.GRAY + "> " + ChatColor.RESET + event.getMessage());
    		} else {
    			Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + plugin.playerData.getInt("players." + player.getName() + ".points") + ChatColor.GRAY + "] " + ChatColor.RESET + player.getDisplayName() + ChatColor.GRAY + "> " + ChatColor.RESET + event.getMessage());
    		}
    			
    		event.setCancelled(true);
    	} else {
    		if(plugin.getConfig().getBoolean("deadPlayersCanChat") == true) {
    			Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + plugin.playerData.getInt("players." + player.getName() + ".points") + ChatColor.GRAY + "] " + ChatColor.GRAY + "[" + ChatColor.RED + "Dead" + ChatColor.GRAY + "] " + ChatColor.RESET + player.getDisplayName() + ChatColor.GRAY + "> " + ChatColor.RESET + event.getMessage());
    			
    		} else {
    			event.setCancelled(true);
    		}
    	}
    }
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	
    	if(event.getMessage().equalsIgnoreCase("/reload")) {
    		event.setCancelled(true);
    		
    		player.sendMessage(plugin.sg + ChatColor.RED + "/reload has been disabled.");
    	} else if(event.getMessage().startsWith("/msg")) {
    		event.setCancelled(true);
    		
    		player.sendMessage(plugin.sg + ChatColor.RED + "/msg has been disabled.");
    		
    	} else if(event.getMessage().startsWith("/tell")) {
    		event.setCancelled(true);
    		
    		player.sendMessage(plugin.sg + ChatColor.RED + "/tell has been disabled.");
    		
    	} else if(event.getMessage().startsWith("/me")) {
    		event.setCancelled(true);
    		
    		player.sendMessage(plugin.sg + ChatColor.RED + "/me has been disabled.");
    	}
    }
}
