package me.Markcreator.SurvivalGames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Counters {
	
	private Main plugin;
    public Counters(Main Plugin) {
        this.plugin = Plugin;   
    }
    
	public void startCounting(int number) {
    	plugin.counter = number;
		plugin.starting = true;
    	
    	Bukkit.getScheduler().cancelTask(plugin.countdownTask);
    	
		Bukkit.broadcastMessage(plugin.sg + ChatColor.GRAY +  (number / 60) + " minutes until The round starts.");
    	
    	plugin.countdownTask = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
    		public void run() {
    			if(plugin.counter != -1) {
    				plugin.counter = plugin.counter - 1;
				
    				for(Player all : Bukkit.getOnlinePlayers()) {
    					all.setLevel(plugin.counter);
    					all.setFoodLevel(20);
    				}
    				
    				ArrayList<Integer> notifyMoments = new ArrayList<Integer>(Arrays.asList(240, 180, 120, 60, 30, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    				String timeUnit = plugin.counter < 60 ? "seconds" : "minutes";
    				int timeAmount = plugin.counter < 60 ? plugin.counter : plugin.counter / 60;
    				
    				if(notifyMoments.contains(plugin.counter)) {
    					Bukkit.broadcastMessage(plugin.sg + ChatColor.GRAY + timeAmount + " " + timeUnit + " until the round starts.");
					
    					for(Player all : Bukkit.getOnlinePlayers()) {
    						all.getWorld().playSound(all.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.5F, 2);
    					}
    					
    				} else if(plugin.counter == 0) {
						Bukkit.getScheduler().cancelTask(plugin.countdownTask);

    					if(Bukkit.getOnlinePlayers().size() > 1) {
    						Bukkit.broadcastMessage(plugin.sg + ChatColor.GRAY + "The round had started, The game begins in 30 seconds.");
					
    						for(Player all : Bukkit.getOnlinePlayers()) {
    							all.getWorld().playSound(all.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 2);
    						}
    						
    						if(plugin.getConfig().getBoolean("randomWorldTime")) {
	    						Random time = new Random();
	    						Bukkit.getWorld(plugin.world).setTime(time.nextInt(24000));
    						} else {
    							Bukkit.getWorld(plugin.world).setTime(0);
    						}
					    					    					
    						for(Player all : Bukkit.getOnlinePlayers()) {
    							all.setHealth(20);
    							all.setFoodLevel(20);
    							all.getInventory().clear();
    							all.getInventory().setHelmet(null);
    							all.getInventory().setChestplate(null);
    							all.getInventory().setLeggings(null);
    							all.getInventory().setBoots(null);
    							all.setLevel(0);
    							all.setExp(0);
    							all.setGameMode(GameMode.SURVIVAL);
    							
    							if(all.getVehicle() != null) {
    								all.getVehicle().remove();
    								all.getVehicle().getWorld().spawnEntity(all.getVehicle().getLocation(), all.getVehicle().getType());
    							}
    						}
    						
    						startLockedCounter(30);
    						
    					} else {
    						plugin.locked = false;
    						plugin.starting = false;
    						plugin.started = false;
    						
    						Bukkit.broadcastMessage(plugin.sg + ChatColor.GRAY + "There are not enough players to start playing, the counter has been reset.");
    					}
    				}
    			}
    		}
    	}, 0L, 20L);
    }
	
	public void startLockedCounter(int number) {
    	plugin.counter = number;
		plugin.locked = true;

		for(Player all : Bukkit.getOnlinePlayers()) {
			randomSpawn(all);
		}
		
    	plugin.countdownTask = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
    		public void run() {
    			if(plugin.counter != -1) {
    				plugin.counter = plugin.counter - 1;
    				
    				for(Player all : Bukkit.getOnlinePlayers()) {
    					all.setLevel(plugin.counter);
    					all.setFoodLevel(20);
    				}
    				
    				if(plugin.counter < 10) {
    					plugin.createSpawnCrate();
    				}
    				
    				if(plugin.counter == 30 || (plugin.counter <= 10 && plugin.counter > 0)) {
    					Bukkit.broadcastMessage(plugin.sg + ChatColor.GRAY + plugin.counter + " seconds until the game starts.");
		
    					for(Player all : Bukkit.getOnlinePlayers()) {
    						all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
    					}
    				}
    				
    				if(plugin.counter == 0) {
						Bukkit.getScheduler().cancelTask(plugin.countdownTask);

    					if(Bukkit.getOnlinePlayers().size() > 1) {
    						Bukkit.broadcastMessage(plugin.sg + ChatColor.GOLD + "The game has started! Good luck.");
    						plugin.locked = false;
    						plugin.started = true;
    						plugin.starting = false;
    						
    						for(Player all : Bukkit.getOnlinePlayers()) {
    							all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);
    							plugin.kl.equipPlayer(all);
    						}
    						
    						plugin.hasVoted.clear();
    						plugin.startVotes = 0;
    						if(plugin.getConfig().getBoolean("enableScoreboard") == true) {
    							plugin.board.resetScores(ChatColor.GOLD + "Start votes:");
    						}
    						
    						if(plugin.graceperiod > 0) {
    							Bukkit.broadcastMessage(plugin.sg + ChatColor.GRAY + plugin.graceperiod + " seconds untill the grace period ends.");
    							
    							plugin.graceperiodid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
    								public void run() {
    									if(plugin.graceperiod > 0) {
    										plugin.graceperiod--;
    									} else {
    										plugin.graceperiod = 0;
    										Bukkit.broadcastMessage(plugin.sg + ChatColor.GRAY + "The grace period has ended.");
    										Bukkit.getScheduler().cancelTask(plugin.graceperiodid);
    									}
    								}
    							}, 0L, 20L);
    						}
    						
    					} else {
    						plugin.locked = false;
    						plugin.starting = false;
    						plugin.started = false;
    						
    						for(Player all : Bukkit.getOnlinePlayers()) {
    							all.teleport(Bukkit.getWorld(plugin.world).getSpawnLocation());
    							if(plugin.getConfig().getBoolean("enableKits") == true) {
	    							all.getInventory().setItem(0, plugin.kitBook);
	    							all.getInventory().setItem(1, plugin.priceList);
    							}
    							all.getInventory().setItem(8, plugin.vote);
    						}
    						Bukkit.broadcastMessage(plugin.sg + ChatColor.GRAY + "There are not enough players to start playing, the counter has been reset.");
    					}
    				}
    			}
    		}
    	}, 0L, 20L);
    }
	
	public void randomSpawn(Player player) {
		Location loc = null;

		while(loc == null) {
			Random rx = new Random();
			int x = rx.nextInt(plugin.getConfig().getInt("playerSpreadRadius") * 2) - plugin.getConfig().getInt("playerSpreadRadius");
		
			Random rz = new Random();
			int z = rz.nextInt(plugin.getConfig().getInt("playerSpreadRadius") * 2) - plugin.getConfig().getInt("playerSpreadRadius");
			
			Location newLoc = new Location(Bukkit.getWorld(plugin.world), x + 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(x, z).getLocation().getBlockY() + 1.5, z + 0.5);
			
			Material mat;
			if(!((mat = newLoc.getBlock().getRelative(0, -2, 0).getType()) == Material.WATER || 
				mat == Material.LAVA || 
				mat == Material.CACTUS ||
				mat == Material.FIRE)) {
					loc = newLoc;
			}
		}	
				
		player.teleport(loc);	
	}
}
