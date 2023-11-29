package me.Markcreator.SurvivalGames;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerJoinListener implements Listener {

	private Main plugin;
    public PlayerJoinListener(Main Plugin) {
        this.plugin = Plugin;   
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	
    	if(plugin.started == false && plugin.locked == false) {
    		event.setJoinMessage(null);
    		Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + player.getName() + " joined the server.");
    		
    		if(plugin.getConfig().getString("winner").equals(player.getName())) {
    			plugin.playerRank.put(player, "4");
    			plugin.Winner.addPlayer(player);
    			player.setDisplayName(ChatColor.YELLOW + player.getName());
    			
    		} else if(player.isOp()) {
    			plugin.playerRank.put(player, "5");
				plugin.Op.addPlayer(player);
				player.setDisplayName(ChatColor.RED + player.getName());
				
    		} else {
    			if(plugin.playerData.getString("players." + player.getName() + ".rank") != null) {
    				if(plugin.playerData.getString("players." + player.getName() + ".rank").equalsIgnoreCase("Iron")) {
    					plugin.playerRank.put(player, "1");
    					plugin.Iron.addPlayer(player);
    					player.setDisplayName(ChatColor.WHITE + player.getName());
        			
    				} else if(plugin.playerData.getString("players." + player.getName() + ".rank").equalsIgnoreCase("Gold")) {
    					plugin.playerRank.put(player, "2");
    					plugin.Gold.addPlayer(player);
    					player.setDisplayName(ChatColor.GOLD + player.getName());
        			
    				} else if(plugin.playerData.getString("players." + player.getName() + ".rank").equalsIgnoreCase("Diamond")) {
    					plugin.playerRank.put(player, "3");
    					plugin.Diamond.addPlayer(player);
    					player.setDisplayName(ChatColor.AQUA + player.getName());
        			
    				} else if(plugin.playerData.getString("players." + player.getName() + ".rank").equalsIgnoreCase("Emerald")) {
    					plugin.playerRank.put(player, "4");
    					plugin.Emerald.addPlayer(player);
    					player.setDisplayName(ChatColor.GREEN + player.getName());
        			
    				} else {
    					plugin.playerRank.put(player, "0");
    					plugin.Default.addPlayer(player);
    					player.setDisplayName(ChatColor.BLUE + player.getName());
    				}
    			} else {
    				plugin.playerRank.put(player, "0");
    				plugin.Default.addPlayer(player);
    				player.setDisplayName(ChatColor.BLUE + player.getName());
    			}
    		}
    		
    		if(plugin.playerData.getString("players." + player.getName() + ".points") == null) {
    			plugin.playerData.set("players." + player.getName() + ".points", 50);
    			plugin.saveCustomConfig(plugin.playerData, plugin.players);
    		}
    		
    		if(plugin.playerData.getString("players." + player.getName() + ".kits") == null) {
    			plugin.playerData.set("players." + player.getName() + ".kits", "");
    			plugin.saveCustomConfig(plugin.playerData, plugin.players);
    		}
    		
    		if(Bukkit.getOnlinePlayers().size() > plugin.maxPlayers) {
        		int playerRank = Integer.parseInt(plugin.playerRank.get(player));
        		ArrayList<Player> kickablePlayers = new ArrayList<Player>();
        		
        		for(Player all : Bukkit.getOnlinePlayers()) {
        			if(Integer.parseInt(plugin.playerRank.get(all)) < playerRank) {
        				kickablePlayers.add(all);
        			}
        		}
        		if(kickablePlayers.size() > 0) {
        			Random r = new Random();
        			int selectedPlayer = r.nextInt(kickablePlayers.size());
        			Player kicked = kickablePlayers.get(selectedPlayer);
        			
        			kicked.kickPlayer(plugin.sg + ChatColor.RED + "You got kicked to make space for a higher ranked member.");
        		} else {
        			player.kickPlayer(plugin.sg + ChatColor.RED + "The server is full.");
        			event.setJoinMessage(null);
        		}
        	}
    		
    		player.setHealth(20);
    		player.setFoodLevel(20);
    		player.getInventory().clear();
    		player.getInventory().setHelmet(null);
    		player.getInventory().setChestplate(null);
    		player.getInventory().setLeggings(null);
    		player.getInventory().setBoots(null);
    		player.setLevel(0);
    		player.setExp(0);
    		
    		for(PotionEffect all : player.getActivePotionEffects()) {
    			player.removePotionEffect(all.getType());
    		}
    		
    		player.setGameMode(GameMode.ADVENTURE);
    		player.setAllowFlight(false);
    		
    		if(plugin.getConfig().getBoolean("enableKits") == true) {
    			player.getInventory().setItem(0, plugin.kitBook);
    			player.getInventory().setItem(1, plugin.priceList);
    		}
			player.getInventory().setItem(8, plugin.vote);

    		if(plugin.getConfig().getBoolean("enableScoreboard") == true) {
    			player.setScoreboard(plugin.board);
    		}
    		
    		player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
    		
    		plugin.livingPlayers.add(player);
    		
    		player.sendMessage(ChatColor.GRAY + "-----------------------------------------------");
    		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Seed" + ChatColor.GRAY + "] " + ChatColor.AQUA + Bukkit.getWorld(plugin.world).getSeed());
    		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Spawn Location" + ChatColor.GRAY + "] " + ChatColor.AQUA + "X: 0 Y: " + (Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY()) + " Z: 0");
    		if(plugin.getConfig().getBoolean("dropCrates") == true) {
    			player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Crates" + ChatColor.GRAY + "] " + ChatColor.AQUA + plugin.crateAmount + " Crates + 10 Spawn Crates.");
    		}
    		if(Bukkit.getOnlinePlayers().size() > 1) {
    			if(plugin.counter != 0) {
    				player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Starts in" + ChatColor.GRAY + "] " + ChatColor.AQUA + plugin.counter + " seconds.");
    				
    			} else {
    				player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Starts in" + ChatColor.GRAY + "] " + ChatColor.RED + "Waiting for players.");
    			}
    		} else {
    			player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Starts in" + ChatColor.GRAY + "] " + ChatColor.RED + "Waiting for players.");
    		}
    		
    	} else {
    		event.setJoinMessage(null);
    		
    		if(plugin.locked) {
    			player.kickPlayer(plugin.sg + ChatColor.DARK_RED + "You can't join at this time, please try again later.");
    		}
    		
    		if(plugin.winner == null) {
    			player.setHealth(20);
        		player.setFoodLevel(20);
        		player.getInventory().clear();
        		player.getInventory().setHelmet(null);
        		player.getInventory().setChestplate(null);
        		player.getInventory().setLeggings(null);
        		player.getInventory().setBoots(null);
        		player.setLevel(0);
        		player.setExp(0);
        		
        		for(PotionEffect all : player.getActivePotionEffects()) {
        			player.removePotionEffect(all.getType());
        		}
        		
        		player.setGameMode(GameMode.ADVENTURE);
        		player.setAllowFlight(true);
        		player.setHealth(20);
        		player.setFoodLevel(20);
        		player.getInventory().addItem(plugin.teleport);
        		
        		player.teleport(new Location(Bukkit.getWorld(plugin.world), 0.5, Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
        		
        		player.sendMessage(plugin.sg + ChatColor.AQUA + "Welcome " + player.getName() + " to " + plugin.sg + "!");
        		player.sendMessage("");
        		
        		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Seed" + ChatColor.GRAY + "] " + ChatColor.AQUA + Bukkit.getWorld(plugin.world).getSeed());
        		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Spawn Location" + ChatColor.GRAY + "] " + ChatColor.AQUA + "X: 0 Y: " + (Bukkit.getWorld(plugin.world).getHighestBlockAt(0, 0).getLocation().getBlockY()) + " Z: 0");
        		if(plugin.getConfig().getBoolean("dropCrates") == true) {
        			player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Crates" + ChatColor.GRAY + "] " + ChatColor.AQUA + plugin.crateAmount + " Crates + 10 Spawn Crates.");
        		}
        		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Starts in" + ChatColor.GRAY + "] " + ChatColor.RED + "Started.");
        		player.sendMessage("");
        		player.sendMessage(plugin.sg + ChatColor.AQUA + "You are spectating this round.");
        		
        		for(Player all : Bukkit.getOnlinePlayers()) {
        			if(!plugin.livingPlayers.contains(all)) {
        				player.hidePlayer(all);
        			}
        			all.hidePlayer(player);
        		}
    		} else {
    			player.kickPlayer(plugin.sg + ChatColor.DARK_RED + plugin.winner + " won the " + plugin.sg + ", the server is restarting any moment.");
    		}
    		
    		if(plugin.livingPlayers.contains(player)) {
        		plugin.livingPlayers.remove(player);
        	}
    	}
    }
	@EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
    	Player player = event.getPlayer();
    	
    	event.setQuitMessage(null);
    	
    	plugin.hasVoted.remove(player);
    	
    	if(plugin.livingPlayers.contains(player)) {
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
    		} else {
    			Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + player.getName() + " left the server.");
    		}
    	}
    	if(plugin.livingPlayers.contains(player)) {
    		plugin.livingPlayers.remove(player);
    	}
    }
}
