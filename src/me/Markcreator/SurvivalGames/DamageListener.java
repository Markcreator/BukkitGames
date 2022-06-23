package me.Markcreator.SurvivalGames;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DamageListener implements Listener {

	private Main plugin;
    public DamageListener(Main Plugin) {
        this.plugin = Plugin;   
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    	if(plugin.started == true && plugin.graceperiod <= 0) {
    		if(event.getEntity() instanceof Player) {
    			Player player = (Player) event.getEntity();
    		
    			if(event.getDamager().getType() != EntityType.PLAYER) {
    				player.getWorld().playEffect(player.getLocation().add(0, 1, 0), Effect.STEP_SOUND, 55, 30);
    			}
    			
    			if(event.getDamager() instanceof Player) {
    				Player damager = (Player) event.getDamager();
    				
    				if(plugin.livingPlayers.contains(damager) && plugin.livingPlayers.contains(player)) {
        				player.getWorld().playEffect(player.getLocation().add(0, 1, 0), Effect.STEP_SOUND, 55, 30);
        			}
    				
    				if(player != damager) {
    					if(player.getHealth() - event.getDamage() <= 0) {
    	    				plugin.livingPlayers.remove(player);
    	    				
    	    				for(ItemStack all : player.getInventory().getContents()) {
    	        				if(all != null) {
    	        					player.getWorld().dropItem(player.getLocation(), all);
    	        				}
    	        			}
    	        			if(player.getInventory().getHelmet() != null) {
    	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getHelmet());
    	        			}
    	        			if(player.getInventory().getChestplate() != null) {
    	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getChestplate());
    	        			}
    	        			if(player.getInventory().getLeggings() != null) {
    	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getLeggings());
    	        			}
    	        			if(player.getInventory().getBoots() != null) {
    	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getBoots());
    	        			}
    	        			player.getInventory().clear();
    		    		
    	    				player.getWorld().strikeLightningEffect(player.getLocation());
    	    				
    	    				player.sendMessage(plugin.sg + ChatColor.DARK_RED + "You got killed by " + damager.getName() + " with a " + damager.getItemInHand().getType().toString().toLowerCase().replace("_", " ") + ".");
    	    				plugin.livingPlayers.remove(player);
    	    	    		
    	    	    		if(player.getInventory().getHelmet() != null) {
    	    					player.getInventory().setHelmet(null);
    	    				}
    	    				if(player.getInventory().getChestplate() != null) {
    	    					player.getInventory().setChestplate(null);
    	    				}
    	    				if(player.getInventory().getLeggings() != null) {
    	    					player.getInventory().setLeggings(null);
    	    				}
    	    				if(player.getInventory().getBoots() != null) {
    	    					player.getInventory().setBoots(null);
    	    				}
    	        			player.getInventory().clear();
    	    				player.setGameMode(GameMode.ADVENTURE);
        	        		player.setAllowFlight(true);
        	        		player.setHealth(20);
        	        		player.setFoodLevel(20);
        	        		player.getInventory().addItem(plugin.teleport);
        	        		
        	        		for(Player all : Bukkit.getOnlinePlayers()) {
        	        			if(!plugin.livingPlayers.contains(all)) {
        	        				player.hidePlayer(all);
        	        			}
        	        			all.hidePlayer(player);
        	        		}
        	        		
        					player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
    	    				player.sendMessage(plugin.sg + ChatColor.AQUA + "You are now spectating this round.");
    	        			
    	    				int lostPoints = (plugin.playerData.getInt("players." + player.getName() + ".points") / 25) + 5;
    	    				
    	    				if(plugin.playerData.getInt("players." + player.getName() + ".points") - lostPoints < 0) {
    	    					plugin.playerData.set("players." + player.getName() + ".points", 0);
    	    				} else {
    	    					plugin.playerData.set("players." + player.getName() + ".points", plugin.playerData.getInt("players." + player.getName() + ".points") - lostPoints);
    	    				}
    	    				plugin.playerData.set("players." + damager.getName() + ".points", plugin.playerData.getInt("players." + damager.getName() + ".points") + lostPoints);
    	    				plugin.saveCustomConfig(plugin.playerData, plugin.players);
    	    				
    	    				damager.sendMessage(plugin.sg + ChatColor.GOLD + "You earned " + lostPoints + " points for killing " + player.getName() + ".");
    	    				
    	    				if(plugin.livingPlayers.size() == 1) {
    	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.RED + player.getName() + " got killed by " + damager.getName() + ".");
    	    					damager.sendMessage(plugin.sg + ChatColor.GOLD + "Congratulations! You've won the Survival Games!");
    	    					
    	    					Random r = new Random();
    	    					int points = r.nextInt(75) + 30;
    	    					
    	    					damager.sendMessage(plugin.sg + ChatColor.GOLD + "You earned " + points + " points for winning!");
    	    					plugin.playerData.set("players." + damager.getName() + ".points", plugin.playerData.getInt("players." + damager.getName() + ".points") + points);
    	    					plugin.getConfig().set("winner", damager.getName());
    	    					plugin.saveCustomConfig(plugin.playerData, plugin.players);
    	    					plugin.saveConfig();
    	        				
    	    					plugin.winner = damager.getName();
    	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.DARK_RED + "The server is restarting in 10 seconds.");
    						
    	    					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    	    						public void run() {
    	    							Bukkit.shutdown();
    	    						}
    	    					}, 200L);
    	    				} else {
    	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.RED + player.getName() + " got killed by " + damager.getName() + ", there are " + (plugin.livingPlayers.size()) + " players remaining.");
    	    					damager.getWorld().strikeLightningEffect(damager.getLocation());
    	    				}
    	    			}
    				}
    			} else if(event.getDamager() instanceof Arrow) {
    				Arrow arrow = (Arrow) event.getDamager();
    				
    				if(arrow.getShooter() instanceof Player) {
    					if(player.getHealth() - event.getDamage() <= 0) {
    						Player damager = (Player) arrow.getShooter();
    						
    	    				plugin.livingPlayers.remove(player);
    		    		
    	    				for(ItemStack all : player.getInventory().getContents()) {
    	        				if(all != null) {
    	        					player.getWorld().dropItem(player.getLocation(), all);
    	        				}
    	        			}
    	        			if(player.getInventory().getHelmet() != null) {
    	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getHelmet());
    	        			}
    	        			if(player.getInventory().getChestplate() != null) {
    	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getChestplate());
    	        			}
    	        			if(player.getInventory().getLeggings() != null) {
    	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getLeggings());
    	        			}
    	        			if(player.getInventory().getBoots() != null) {
    	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getBoots());
    	        			}
    	        			player.getInventory().clear();
    	        			
    	    				player.getWorld().strikeLightningEffect(player.getLocation());
    		    		
    	    				player.sendMessage(plugin.sg + ChatColor.DARK_RED + "You got shot by " + damager.getName() + ".");
    	    				plugin.livingPlayers.remove(player);
    	    	    		
    	    	    		if(player.getInventory().getHelmet() != null) {
    	    					player.getInventory().setHelmet(null);
    	    				}
    	    				if(player.getInventory().getChestplate() != null) {
    	    					player.getInventory().setChestplate(null);
    	    				}
    	    				if(player.getInventory().getLeggings() != null) {
    	    					player.getInventory().setLeggings(null);
    	    				}
    	    				if(player.getInventory().getBoots() != null) {
    	    					player.getInventory().setBoots(null);
    	    				}
    	        			player.getInventory().clear();
    	    				player.setGameMode(GameMode.ADVENTURE);
        	        		player.setAllowFlight(true);
        	        		player.setHealth(20);
        	        		player.setFoodLevel(20);
        	        		player.getInventory().addItem(plugin.teleport);
        	        		
        	        		for(Player all : Bukkit.getOnlinePlayers()) {
        	        			if(!plugin.livingPlayers.contains(all)) {
        	        				player.hidePlayer(all);
        	        			}
        	        			all.hidePlayer(player);
        	        		}
        	        		
        					player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
    	    				player.sendMessage(plugin.sg + ChatColor.AQUA + "You are now spectating this round.");
    				
    	    				int lostPoints = (plugin.playerData.getInt("players." + player.getName() + ".points") / 25) + 5;
    	    				
    	    				if(plugin.playerData.getInt("players." + player.getName() + ".points") - lostPoints < 0) {
    	    					plugin.playerData.set("players." + player.getName() + ".points", 0);
    	    				} else {
    	    					plugin.playerData.set("players." + player.getName() + ".points", plugin.playerData.getInt("players." + player.getName() + ".points") - lostPoints);
    	    				}
    	    				plugin.playerData.set("players." + damager.getName() + ".points", plugin.playerData.getInt("players." + damager.getName() + ".points") + lostPoints);
    	    				plugin.saveCustomConfig(plugin.playerData, plugin.players);
    	    				
    	    				damager.sendMessage(plugin.sg + ChatColor.GOLD + "You earned " + lostPoints + " points for killing " + player.getName() + ".");
    	    				
    	    				if(plugin.livingPlayers.size() == 1) {
    	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.RED + player.getName() + " got killed by " + damager.getName() + ".");
    	    					damager.sendMessage(plugin.sg + ChatColor.GOLD + "Congratulations! You've won the Survival Games!");
    	    					
    	    					Random r = new Random();
    	    					int points = r.nextInt(75) + 30;
    	    					
    	    					damager.sendMessage(plugin.sg + ChatColor.GOLD + "You earned " + points + " points for winning!");
    	    					plugin.playerData.set("players." + damager.getName() + ".points", plugin.playerData.getInt("players." + damager.getName() + ".points") + points);
    	    					plugin.getConfig().set("winner", damager.getName());
    	    					plugin.saveCustomConfig(plugin.playerData, plugin.players);
    	    					plugin.saveConfig();
    	        				
    	    					plugin.winner = damager.getName();
    	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.DARK_RED + "The server is restarting in 10 seconds.");
    						
    	    					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    	    						public void run() {
    	    							Bukkit.shutdown();
    	    						}
    	    					}, 200L);
    	    				} else {
    	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.RED + player.getName() + " got shot by " + damager.getName() + ", there are " + (plugin.livingPlayers.size()) + " players remaining.");
    	    					damager.getWorld().strikeLightningEffect(damager.getLocation());
    	    				}
    					}
    				}
    			} else {
    				if(player.getHealth() - event.getDamage() <= 0) {
						
	    				plugin.livingPlayers.remove(player);
		    		
	    				for(ItemStack all : player.getInventory().getContents()) {
	        				if(all != null) {
	        					player.getWorld().dropItem(player.getLocation(), all);
	        				}
	        			}
	        			if(player.getInventory().getHelmet() != null) {
	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getHelmet());
	        			}
	        			if(player.getInventory().getChestplate() != null) {
	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getChestplate());
	        			}
	        			if(player.getInventory().getLeggings() != null) {
	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getLeggings());
	        			}
	        			if(player.getInventory().getBoots() != null) {
	        				player.getWorld().dropItem(player.getLocation(), player.getInventory().getBoots());
	        			}
	        			player.getInventory().clear();
	        			
	    				player.getWorld().strikeLightningEffect(player.getLocation());
		    		
	    				player.sendMessage(plugin.sg + ChatColor.DARK_RED + "You died.");
	    				plugin.livingPlayers.remove(player);
	    	    		
	    	    		if(player.getInventory().getHelmet() != null) {
	    					player.getInventory().setHelmet(null);
	    				}
	    				if(player.getInventory().getChestplate() != null) {
	    					player.getInventory().setChestplate(null);
	    				}
	    				if(player.getInventory().getLeggings() != null) {
	    					player.getInventory().setLeggings(null);
	    				}
	    				if(player.getInventory().getBoots() != null) {
	    					player.getInventory().setBoots(null);
	    				}
	        			player.getInventory().clear();
	    				player.setGameMode(GameMode.ADVENTURE);
    	        		player.setAllowFlight(true);
    	        		player.setHealth(20);
    	        		player.setFoodLevel(20);
    	        		player.getInventory().addItem(plugin.teleport);
    	        		
    	        		for(Player all : Bukkit.getOnlinePlayers()) {
    	        			if(!plugin.livingPlayers.contains(all)) {
    	        				player.hidePlayer(all);
    	        			}
    	        			all.hidePlayer(player);
    	        		}
    	        		
    					player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
	    				player.sendMessage(plugin.sg + ChatColor.AQUA + "You are now spectating this round.");
				
	    				Player winner = null;
	    				
	    				if(plugin.livingPlayers.size() == 1) {
	    					for(Player all : Bukkit.getOnlinePlayers()) {
	    						if(plugin.livingPlayers.contains(all)) {
	    							winner = all;
	    						}
	    					}
	    					
	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.RED + player.getName() + " got killed by " + winner.getName() + ".");
	    					winner.sendMessage(plugin.sg + ChatColor.GOLD + "Congratulations! You've won the Survival Games!");
	    					
	    					Random r = new Random();
	    					int points = r.nextInt(75) + 30;
	    					
	    					winner.sendMessage(plugin.sg + ChatColor.GOLD + "You earned " + points + " points for winning!");
	    					plugin.playerData.set("players." + winner.getName() + ".points", plugin.playerData.getInt("players." + winner.getName() + ".points") + points);
	    					plugin.getConfig().set("winner", winner.getName());
	    					plugin.saveCustomConfig(plugin.playerData, plugin.players);
	    					plugin.saveConfig();
	        				
	    					plugin.winner = winner.getName();
	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.DARK_RED + "The server is restarting in 10 seconds.");
						
	    					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    						public void run() {
	    							Bukkit.shutdown();
	    						}
	    					}, 200L);
	    				} else {
	    					Bukkit.broadcastMessage(plugin.sg + ChatColor.RED + player.getName() + " died, there are " + (plugin.livingPlayers.size()) + " players remaining.");
	    					player.getWorld().strikeLightningEffect(player.getLocation());
	    				}
					}
    			}
    		}
    	} else if(plugin.graceperiod > 0) {
    		event.setCancelled(true);
    	}
    }
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	Player player = event.getEntity();
    	
    	event.setDeathMessage(null);
    	
    	/*if(player.getLastDamageCause().getCause() != null) {
	    	if(player.getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK) {
	    		Entity killer = player.getLastDamageCause().getEntity();
	    		
	    		if(killer.getType() != EntityType.PLAYER) {
	    			player.kickPlayer(plugin.sg + ChatColor.RED + "You died.");
	    		}
	    		
	    	} else if(player.getLastDamageCause().getCause() == DamageCause.PROJECTILE) {
	    		Projectile proj = (Projectile) pl
	    		
	    		if(proj instanceof Arrow) {
	    			Arrow arrow = (Arrow) proj;
	    			
	    			if(!(arrow.getShooter() instanceof Player)) {
	    				deathHandler(player);
	    				player.kickPlayer(plugin.sg + ChatColor.RED + "You died.");
	    				plugin.livingPlayers.remove(player);
	    	    		
	    	    		if(player.getInventory().getHelmet() != null) {
	    					player.getInventory().setHelmet(null);
	    				}
	    				if(player.getInventory().getChestplate() != null) {
	    					player.getInventory().setChestplate(null);
	    				}
	    				if(player.getInventory().getLeggings() != null) {
	    					player.getInventory().setLeggings(null);
	    				}
	    				if(player.getInventory().getBoots() != null) {
	    					player.getInventory().setBoots(null);
	    				}
	        			player.getInventory().clear();
	    				player.setGameMode(GameMode.ADVENTURE);
		        		player.setAllowFlight(true);
		        		player.setHealth(20);
		        		player.setFoodLevel(20);
		        		player.getInventory().addItem(plugin.teleport);
		        		
		        		for(Player all : Bukkit.getOnlinePlayers()) {
		        			if(!plugin.livingPlayers.contains(all)) {
		        				player.hidePlayer(all);
		        			}
		        			all.hidePlayer(player);
		        		}
		        		
						player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
	    				player.sendMessage(plugin.sg + ChatColor.AQUA + "You are now spectating this round.");
	    			}
	    			
	    		} else {
	    			deathHandler(player);
	    			player.sendMessage(plugin.sg + ChatColor.RED + "You died.");
	    			plugin.livingPlayers.remove(player);
	        		
	        		if(player.getInventory().getHelmet() != null) {
	    				player.getInventory().setHelmet(null);
	    			}
	    			if(player.getInventory().getChestplate() != null) {
	    				player.getInventory().setChestplate(null);
	    			}
	    			if(player.getInventory().getLeggings() != null) {
	    				player.getInventory().setLeggings(null);
	    			}
	    			if(player.getInventory().getBoots() != null) {
	    				player.getInventory().setBoots(null);
	    			}
	    			player.getInventory().clear();
	    			player.setGameMode(GameMode.ADVENTURE);
	        		player.setAllowFlight(true);
	        		player.setHealth(20);
	        		player.setFoodLevel(20);
	        		player.getInventory().addItem(plugin.teleport);
	        		
	        		for(Player all : Bukkit.getOnlinePlayers()) {
	        			if(!plugin.livingPlayers.contains(all)) {
	        				player.hidePlayer(all);
	        			}
	        			all.hidePlayer(player);
	        		}
	        		
					player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
					player.sendMessage(plugin.sg + ChatColor.AQUA + "You are now spectating this round.");
	    		}
	    		
	    	} else {
	    		deathHandler(player);
	    		player.sendMessage(plugin.sg + ChatColor.RED + "You died.");
	    		plugin.livingPlayers.remove(player);
	    		
	    		if(player.getInventory().getHelmet() != null) {
					player.getInventory().setHelmet(null);
				}
				if(player.getInventory().getChestplate() != null) {
					player.getInventory().setChestplate(null);
				}
				if(player.getInventory().getLeggings() != null) {
					player.getInventory().setLeggings(null);
				}
				if(player.getInventory().getBoots() != null) {
					player.getInventory().setBoots(null);
				}
	    		player.getInventory().clear();
	    		player.setGameMode(GameMode.ADVENTURE);
	    		player.setAllowFlight(true);
	    		player.setHealth(20);
	    		player.setFoodLevel(20);
	    		player.getInventory().addItem(plugin.teleport);
	    		
	    		for(Player all : Bukkit.getOnlinePlayers()) {
	    			if(!plugin.livingPlayers.contains(all)) {
	    				player.hidePlayer(all);
	    			}
	    			all.hidePlayer(player);
	    		}
	    		
				player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
				player.sendMessage(plugin.sg + ChatColor.AQUA + "You are now spectating this round.");
	    	}
	    	
    	} else {*/
    		deathHandler(player);
    		player.sendMessage(plugin.sg + ChatColor.RED + "You died.");
    		plugin.livingPlayers.remove(player);
    		
    		if(player.getInventory().getHelmet() != null) {
				player.getInventory().setHelmet(null);
			}
			if(player.getInventory().getChestplate() != null) {
				player.getInventory().setChestplate(null);
			}
			if(player.getInventory().getLeggings() != null) {
				player.getInventory().setLeggings(null);
			}
			if(player.getInventory().getBoots() != null) {
				player.getInventory().setBoots(null);
			}
    		player.getInventory().clear();
    		player.setGameMode(GameMode.ADVENTURE);
    		player.setAllowFlight(true);
    		player.setHealth(20);
    		player.setFoodLevel(20);
    		player.getInventory().addItem(plugin.teleport);
    		
    		for(Player all : Bukkit.getOnlinePlayers()) {
    			if(!plugin.livingPlayers.contains(all)) {
    				player.hidePlayer(plugin, all);
    			}
    			all.hidePlayer(plugin, player);
    		}
    		
			player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
			player.sendMessage(plugin.sg + ChatColor.AQUA + "You are now spectating this round.");
    	}
			
	public void deathHandler(Player player) {
		if(plugin.started == true) {
			if(plugin.livingPlayers.size() - 1 == 1) {
				Bukkit.broadcastMessage(plugin.sg + ChatColor.RED + player.getName() + " died.");
				Player winner = null;
				
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(all != player) {
						if(plugin.livingPlayers.contains(all)) {
							winner = all;
						}
					}
				}
				Random r = new Random();
				int points = r.nextInt(75) + 30;
			
				winner.sendMessage(plugin.sg + ChatColor.GOLD + "You earned " + points + " points for winning!");
				plugin.playerData.set("players." + winner.getName() + ".points", plugin.playerData.getInt("players." + winner.getName() + ".points") + points);
				plugin.getConfig().set("winner", winner.getName());
				plugin.saveCustomConfig(plugin.playerData, plugin.players);
				plugin.saveConfig();
				
				winner.sendMessage(plugin.sg + ChatColor.GOLD + "Congratulations! You've won the Survival Games!");
				plugin.winner = winner.getName();
				Bukkit.broadcastMessage(plugin.sg + ChatColor.DARK_RED + "The server is restarting in 10 seconds.");
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						Bukkit.shutdown();
					}
				}, 200L);
				
			} else {
				Bukkit.broadcastMessage(plugin.sg + ChatColor.RED + player.getName() + " died, there are " + (Bukkit.getOnlinePlayers().size() - 1) + " players remaining.");
				player.getWorld().strikeLightningEffect(player.getLocation());
			}
		
			for(ItemStack all : player.getInventory().getContents()) {
				if(all != null) {
					player.getWorld().dropItem(player.getLocation(), all);
				}
			}
			if(player.getInventory().getHelmet() != null) {
				player.getWorld().dropItem(player.getLocation(), player.getInventory().getHelmet());
			}
			if(player.getInventory().getChestplate() != null) {
				player.getWorld().dropItem(player.getLocation(), player.getInventory().getChestplate());
			}
			if(player.getInventory().getLeggings() != null) {
				player.getWorld().dropItem(player.getLocation(), player.getInventory().getLeggings());
			}
			if(player.getInventory().getBoots() != null) {
				player.getWorld().dropItem(player.getLocation(), player.getInventory().getBoots());
			}
			player.getInventory().clear();
		
			player.getWorld().strikeLightningEffect(player.getLocation());
		}
	}
}
