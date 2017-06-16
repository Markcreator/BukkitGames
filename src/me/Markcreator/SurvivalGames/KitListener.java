package me.Markcreator.SurvivalGames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitListener implements Listener {
	
	private Main plugin;
    public KitListener(Main Plugin) {
        this.plugin = Plugin;   
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
    		if(player.getItemInHand().equals(plugin.kitBook)) {
    			Inventory kits = createKitList(player);
    			
    			player.openInventory(kits);
    		} else if(player.getItemInHand().equals(plugin.priceList)) {
    			Inventory priceList = createPriceList(player);
    			
    			player.openInventory(priceList);
    			
    		} else if(player.getItemInHand().equals(plugin.vote)) {
    			player.chat("/vote");
    		}
    	}
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	if(event.getWhoClicked() instanceof Player) {
    		Player player = (Player) event.getWhoClicked();
    		
    		if(event.getCurrentItem() != null) {
    			if(event.getCurrentItem().equals(plugin.kitBook)) {
    				event.setCancelled(true);
    			} else if(event.getCurrentItem().equals(plugin.priceList)) {
    				event.setCancelled(true);
    			} else if(event.getCurrentItem().equals(plugin.vote)) {
    				event.setCancelled(true);
    				
    			} else if(!event.getInventory().equals(player.getInventory())) {
    				if(event.getInventory().getName().equals(ChatColor.BLACK + "[" + ChatColor.DARK_BLUE + "Kits" + ChatColor.BLACK + "]")) {
    					if(event.getCurrentItem().getItemMeta() != null) {
    						
    						for(String all : plugin.kitData.getConfigurationSection("kits").getKeys(false)) {
    							if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + all + ChatColor.DARK_GRAY + "]")) {
    								if(plugin.playerData.getString("players." + player.getName() + ".kits").contains(all + ", ") || plugin.playerData.getString("players." + player.getName() + ".rank") != null || plugin.getConfig().getString("winner").equals(player.getName())) {
    									plugin.playerKit.put(player, all);
    									player.sendMessage(plugin.sg + ChatColor.GREEN + "You selected the kit " + all + ".");
    									event.setCancelled(true);
    									player.closeInventory();
        							} else {
        								player.sendMessage(plugin.sg + ChatColor.RED + "You did not unlock that kit yet.");
        							}
    							}
    						}
    						player.closeInventory();
    					}
    					event.setCancelled(true);
    					
    				} else if(event.getInventory().getName().equals(ChatColor.BLACK + "[" + ChatColor.DARK_RED + "Price List" + ChatColor.BLACK + "]")) {
    					if(event.getCurrentItem().getItemMeta() != null) {
    						
    						for(String all : plugin.kitData.getConfigurationSection("kits").getKeys(false)) {
    							if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + all + ChatColor.DARK_GRAY + "]")) {
    								if(plugin.playerData.getInt("players." + player.getName() + ".points") - plugin.kitData.getInt("kits." + all + ".price") >= 0) {
    									plugin.playerData.set("players." + player.getName() + ".points", plugin.playerData.getInt("players." + player.getName() + ".points") - plugin.kitData.getInt("kits." + all + ".price"));
        								plugin.playerData.set("players." + player.getName() + ".kits", plugin.playerData.getString("players." + player.getName() + ".kits") + all + ", ");
        								plugin.saveCustomConfig(plugin.playerData, plugin.players);
        								
        								player.sendMessage(plugin.sg + ChatColor.GREEN + "You purchased the kit " + all + "!");
        							} else {
        								player.sendMessage(plugin.sg + ChatColor.RED + "You don't have enough points to buy that.");
        							}
    							}
    						}
    						player.closeInventory();
    					}
    					event.setCancelled(true);
    				}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
    	//Player player = event.getPlayer();
    	
    	if(event.getItemDrop().getItemStack().equals(plugin.kitBook)) {
    		event.setCancelled(true);
    		
    	} else if(event.getItemDrop().getItemStack().equals(plugin.priceList)) {
    		event.setCancelled(true);
    	}
    }
    
    @SuppressWarnings("deprecation")
	public Inventory createKitList(Player player) {
    	Inventory kitInv = Bukkit.createInventory(null, 36, ChatColor.BLACK + "[" + ChatColor.DARK_BLUE + "Kits" + ChatColor.BLACK + "]");
    	
    	for(String all : plugin.kitData.getConfigurationSection("kits").getKeys(false)) {
    		if(plugin.kitData.getInt("kits." + all + ".price") == 0 && !plugin.playerData.getString("players." + player.getName() + ".kits").contains(all + ", ")) {
				plugin.playerData.set("players." + player.getName() + ".kits", plugin.playerData.getString("players." + player.getName() + ".kits") + all + ", ");
				plugin.saveCustomConfig(plugin.playerData, plugin.players);
			}
    		
    		ItemStack logoItem = new ItemStack(plugin.kitData.getInt("kits." + all + ".logoItem"));
    		
    		ItemMeta logoMeta = logoItem.getItemMeta();
        	logoMeta.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + all + ChatColor.DARK_GRAY + "]");
        	
        	List<String> kitLore = plugin.kitData.getStringList("kits." + all + ".kitLore");
        	for(int x = 0; x < kitLore.size(); x++) {
				kitLore.set(x, kitLore.get(x).replaceAll("&", "§"));
        	}
        	
        	if(plugin.playerData.getString("players." + player.getName() + ".kits").contains(all + ", ") || plugin.playerData.getString("players." + player.getName() + ".rank") != null || plugin.getConfig().getString("winner").equals(player.getName())) {
        		kitLore.add(ChatColor.GRAY + "______________________");
        		kitLore.add(ChatColor.GREEN + "Unlocked!");
        	} else {
        		kitLore.add(ChatColor.GRAY + "______________________");
        		kitLore.add(ChatColor.RED + "Locked.");
        	}
        	
        	logoMeta.setLore(kitLore);
        	logoItem.setItemMeta(logoMeta);
        	
        	kitInv.addItem(logoItem);
    	}
    	
		return kitInv;
    }
    
    @SuppressWarnings("deprecation")
	public Inventory createPriceList(Player player) {
    	Inventory priceInv = Bukkit.createInventory(null, 36, ChatColor.BLACK + "[" + ChatColor.DARK_RED + "Price List" + ChatColor.BLACK + "]");
    	
    	for(String all : plugin.kitData.getConfigurationSection("kits").getKeys(false)) {
    		if(plugin.kitData.getInt("kits." + all + ".price") == 0 && !plugin.playerData.getString("players." + player.getName() + ".kits").contains(all + ", ")) {
				plugin.playerData.set("players." + player.getName() + ".kits", plugin.playerData.getString("players." + player.getName() + ".kits") + all + ", ");
				plugin.saveCustomConfig(plugin.playerData, plugin.players);
			}
    		
    		if(!plugin.playerData.getString("players." + player.getName() + ".kits").contains(all + ", ")) {
    			ItemStack logoItem = new ItemStack(plugin.kitData.getInt("kits." + all + ".logoItem"));
    		
    			ItemMeta logoMeta = logoItem.getItemMeta();
    			logoMeta.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + all + ChatColor.DARK_GRAY + "]");
        	
    			List<String> kitLore = plugin.kitData.getStringList("kits." + all + ".kitLore");
    			for(int x = 0; x < kitLore.size(); x++) {
    				kitLore.set(x, kitLore.get(x).replaceAll("&", "§"));
            	}
    			
    			kitLore.add(ChatColor.GRAY + "______________________");
    			kitLore.add(ChatColor.GRAY + "[" + ChatColor.GOLD + "Price" + ChatColor.GRAY + "] " + plugin.kitData.getInt("kits." + all + ".price") + " points");
        	
    			logoMeta.setLore(kitLore);
    			logoItem.setItemMeta(logoMeta);
        	
    			priceInv.addItem(logoItem);
    		}
    	}
    	
    	ItemStack points = new ItemStack(Material.GOLD_NUGGET, 1);
    	ItemMeta pointsMeta = points.getItemMeta();
    	
    	pointsMeta.setDisplayName(ChatColor.GRAY + "[" + ChatColor.GOLD + "Points" + ChatColor.GRAY + "]");
    	ArrayList<String> pointsLoreList = new ArrayList<String>();
    	pointsLoreList.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "You currently have " + plugin.playerData.getInt("players." + player.getName() + ".points") + " points.");
    	pointsMeta.setLore(pointsLoreList);
    	
    	points.setItemMeta(pointsMeta);
    	priceInv.setItem(35, points);
    	
		return priceInv;
    }
    
    @SuppressWarnings("deprecation")
	public void equipPlayer(Player player) {
    	player.getInventory().clear();
    	
    	if(plugin.playerKit.containsKey(player)) {
    		if(plugin.kitData.getConfigurationSection("kits." + plugin.playerKit.get(player) + ".items") != null) {
	    		for(String all : plugin.kitData.getConfigurationSection("kits." + plugin.playerKit.get(player) + ".items").getKeys(false)) {
	    			int allInt = Integer.parseInt(all);
	    			player.getInventory().addItem(new ItemStack(allInt, plugin.kitData.getInt("kits." + plugin.playerKit.get(player) + ".items." + all)));
	    		}
    		}
    		
    		if(plugin.kitData.getConfigurationSection("kits." + plugin.playerKit.get(player) + ".potions") != null) {
    			for(String all : plugin.kitData.getConfigurationSection("kits." + plugin.playerKit.get(player) + ".potions").getKeys(false)) {
    				int amplifier = plugin.kitData.getInt("kits." + plugin.playerKit.get(player) + ".potions." + all + ".amplifier") - 1;
    				int duration = plugin.kitData.getInt("kits." + plugin.playerKit.get(player) + ".potions." + all + ".duration") * 20;
    				PotionEffectType type = null;
    			
    				if(all.equalsIgnoreCase("SPEED") || all.equals("1")) {
    					type = PotionEffectType.SPEED;
    				} else if(all.equalsIgnoreCase("SLOW") || all.equals("2")) {
    					type = PotionEffectType.SLOW;
    				} else if(all.equalsIgnoreCase("FAST_DIGGING") || all.equals("3")) {
    					type = PotionEffectType.FAST_DIGGING;
    				} else if(all.equalsIgnoreCase("SLOW_DIGGING") || all.equals("4")) {
    					type = PotionEffectType.SLOW_DIGGING;
    				} else if(all.equalsIgnoreCase("INCREASE_DAMAGE") || all.equals("5")) {
    					type = PotionEffectType.INCREASE_DAMAGE;
    				} else if(all.equalsIgnoreCase("HEAL") || all.equals("6")) {
    					type = PotionEffectType.HEAL;
    				} else if(all.equalsIgnoreCase("HARM") || all.equals("7")) {
    					type = PotionEffectType.HARM;
    				} else if(all.equalsIgnoreCase("JUMP") || all.equals("8")) {
    					type = PotionEffectType.JUMP;
    				} else if(all.equalsIgnoreCase("CONFUSION") || all.equals("9")) {
    					type = PotionEffectType.CONFUSION;
    				} else if(all.equalsIgnoreCase("REGENERATION") || all.equals("10")) {
    					type = PotionEffectType.REGENERATION;
    				} else if(all.equalsIgnoreCase("DAMAGE_RESISTANCE") || all.equals("11")) {
    					type = PotionEffectType.DAMAGE_RESISTANCE;
    				} else if(all.equalsIgnoreCase("FIRE_RESISTANCE") || all.equals("12")) {
    					type = PotionEffectType.FIRE_RESISTANCE;
    				} else if(all.equalsIgnoreCase("WATER_BREATHING") || all.equals("13")) {
    					type = PotionEffectType.WATER_BREATHING;
    				} else if(all.equalsIgnoreCase("INVISIBILITY") || all.equals("14")) {
    					type = PotionEffectType.INVISIBILITY;
    				} else if(all.equalsIgnoreCase("BLINDNESS") || all.equals("15")) {
    					type = PotionEffectType.BLINDNESS;
    				} else if(all.equalsIgnoreCase("NIGHT_VISION") || all.equals("16")) {
    					type = PotionEffectType.NIGHT_VISION;
    				} else if(all.equalsIgnoreCase("HUNGER") || all.equals("17")) {
    					type = PotionEffectType.HUNGER;
    				} else if(all.equalsIgnoreCase("WEAKNESS") || all.equals("18")) {
    					type = PotionEffectType.WEAKNESS;
    				} else if(all.equalsIgnoreCase("POISON") || all.equals("19")) {
    					type = PotionEffectType.POISON;
    				} else if(all.equalsIgnoreCase("WITHER") || all.equals("20")) {
    					type = PotionEffectType.WITHER;
    				} else if(all.equalsIgnoreCase("HEALTH_BOOST") || all.equals("21")) {
    					type = PotionEffectType.HEALTH_BOOST;
    				} else if(all.equalsIgnoreCase("ABSORPTION") || all.equals("22")) {
    					type = PotionEffectType.ABSORPTION;
    				} else if(all.equalsIgnoreCase("SATURATION") || all.equals("23")) {
    					type = PotionEffectType.SATURATION;
    				}
    			
    				player.addPotionEffect(new PotionEffect(type, duration, amplifier));
    			}
    		}
    	}
    }
}
