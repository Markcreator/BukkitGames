package me.Markcreator.SurvivalGames;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CrateListener implements Listener {

	private Main plugin;
    public CrateListener(Main Plugin) {
        this.plugin = Plugin;   
    }
	
	@EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
    	if (event.getEntity() instanceof FallingBlock) {
    		if(event.getTo() == Material.CHEST) {
    			Inventory crateInv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Crate");
    			plugin.crates.put(event.getBlock().getLocation(), crateInv);
    			plugin.crateLocs.add(event.getBlock().getLocation());
    			fillCrate(event.getBlock().getLocation());
    		}
    	}
    }

	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
    	if(plugin.livingPlayers.contains(player)) {
    		if(plugin.started == true || player.isOp()) {
    			if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    				if(event.getClickedBlock().getType() == Material.CHEST) {
    					if(plugin.crates.containsKey(event.getClickedBlock().getLocation())) {
    						player.openInventory(plugin.crates.get(event.getClickedBlock().getLocation()));
    						event.setCancelled(true);
    					}
    				}
    			}
    		}
    	}
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.getBlock().getType() == Material.CHEST) {
    		event.setCancelled(true);
    	}
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
    	for(Block all : event.blockList()) {
    		if(all != null) {
    			if(all.getType() == Material.CHEST) {
    				event.blockList().remove(all);
    			}
    		}
    	}
    }
    
    public void fillCrate(Location loc) {
    	if(plugin.crates.containsKey(loc)) {
    		Inventory crateInv = plugin.crates.get(loc);
    		
    		crateInv.clear();
    		
    		for (int i = 0; i < crateInv.getSize(); i++) {
    		    Random ifDoItem = new Random();
    		    int doItem = ifDoItem.nextInt(100);
    		    
    		    if(doItem <= plugin.getConfig().getInt("crateItemSpawnChance")) {
    		    	Random getItemLevel = new Random();
    		    	int itemLevel = getItemLevel.nextInt(100);
    		    	
    		    	if(itemLevel < 50) {
    		    		Random selectItem = new Random();
    		    		int itemIndex = selectItem.nextInt(plugin.crateData.getConfigurationSection("crateItems.1").getKeys(false).size());
    		    		String itemName = (String) plugin.crateData.getConfigurationSection("crateItems.1").getKeys(false).toArray()[itemIndex];
		    			Material mat = Material.matchMaterial(itemName);
		    			if(mat != null) {
		    				ItemStack item = new ItemStack(mat, plugin.crateData.getInt("crateItems.1." + itemName));
	    		    		crateInv.setItem(i, item);
		    			}
    		    		    		    		
    		    	} else if(itemLevel >= 50 && itemLevel < 80) {
    		    		Random selectItem = new Random();
    		    		int itemIndex = selectItem.nextInt(plugin.crateData.getConfigurationSection("crateItems.2").getKeys(false).size());
    		    		String itemName = (String) plugin.crateData.getConfigurationSection("crateItems.2").getKeys(false).toArray()[itemIndex];
		    			Material mat = Material.matchMaterial(itemName);
		    			if(mat != null) {
		    				ItemStack item = new ItemStack(mat, plugin.crateData.getInt("crateItems.2." + itemName));
	    		    		crateInv.setItem(i, item);
		    			}
    		    		
    		    	} else if(itemLevel >= 80 && itemLevel < 95) {
    		    		Random selectItem = new Random();
    		    		int itemIndex = selectItem.nextInt(plugin.crateData.getConfigurationSection("crateItems.3").getKeys(false).size());
    		    		String itemName = (String) plugin.crateData.getConfigurationSection("crateItems.3").getKeys(false).toArray()[itemIndex];
		    			Material mat = Material.matchMaterial(itemName);
		    			if(mat != null) {
		    				ItemStack item = new ItemStack(mat, plugin.crateData.getInt("crateItems.3." + itemName));
	    		    		crateInv.setItem(i, item);
		    			}
    		    		
    		    	} else if(itemLevel >= 95 && itemLevel < 99) {
    		    		Random selectItem = new Random();
    		    		int itemIndex = selectItem.nextInt(plugin.crateData.getConfigurationSection("crateItems.4").getKeys(false).size());
    		    		String itemName = (String) plugin.crateData.getConfigurationSection("crateItems.4").getKeys(false).toArray()[itemIndex];
		    			Material mat = Material.matchMaterial(itemName);
		    			if(mat != null) {
		    				ItemStack item = new ItemStack(mat, plugin.crateData.getInt("crateItems.4." + itemName));
	    		    		crateInv.setItem(i, item);
		    			}
    		    		
    		    	} else if(itemLevel >= 99) {
    		    		Random selectItem = new Random();
    		    		int itemIndex = selectItem.nextInt(plugin.crateData.getConfigurationSection("crateItems.5").getKeys(false).size());
    		    		String itemName = (String) plugin.crateData.getConfigurationSection("crateItems.5").getKeys(false).toArray()[itemIndex];
		    			Material mat = Material.matchMaterial(itemName);
		    			if(mat != null) {
		    				ItemStack item = new ItemStack(mat, plugin.crateData.getInt("crateItems.5." + itemName));
	    		    		crateInv.setItem(i, item);
		    			}
    		    	}
    		    }
    		}
    	}
    }
}
