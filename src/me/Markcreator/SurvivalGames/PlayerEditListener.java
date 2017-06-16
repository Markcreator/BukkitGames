package me.Markcreator.SurvivalGames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerEditListener implements Listener {

	private Main plugin;
    public PlayerEditListener(Main Plugin) {
        this.plugin = Plugin;   
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	Player player = event.getPlayer();
    	
    	if(plugin.livingPlayers.contains(player)) {
    		if(plugin.started == false) {
    			if(!player.isOp()) {
    				event.setCancelled(true);
    			}
    		} else if(plugin.getConfig().getBoolean("canBuild") == false) {
    			if(!player.isOp()) {
    				event.setCancelled(true);
    			}
    		}
    	} else {
    		event.setCancelled(true);
    	}
    	if(event.getBlock().getType() == Material.CHEST) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	Player player = event.getPlayer();
    	
    	if(plugin.livingPlayers.contains(player)) {
    		if(plugin.started == false) {
    			if(!player.isOp()) {
    				event.setCancelled(true);
    			}
    		} else if(plugin.getConfig().getBoolean("canBuild") == false) {
    			if(!player.isOp()) {
    				event.setCancelled(true);
    			}
    		}
    	} else {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
    	if(plugin.getConfig().getBoolean("canBuild") == false) {
    		event.blockList().clear();
    	}
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
    	if(event.getTarget() instanceof Player) {
    		Player player = (Player) event.getTarget();
    		
    		if(plugin.started == false) {
    			event.setCancelled(true);
    		} else if(!plugin.livingPlayers.contains(player)) {
    			event.setCancelled(true);
    		}
		}
    }
    
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    	Player player = event.getPlayer();
    	
    	if(!plugin.livingPlayers.contains(player) || plugin.started == false) {
    		if(!player.isOp()) {
    			event.setCancelled(true);
    	    }
    	}
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
    	Player player = event.getPlayer();
    	
    	if(!plugin.livingPlayers.contains(player) || !plugin.started) {
    		if(!player.isOp()) {
    			event.setCancelled(true);
    		}
		}
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
    	if(plugin.started == false) {
    		event.setCancelled(true);
    	}
    	if(event.getEntity() instanceof Player) {
    		if(!plugin.livingPlayers.contains(event.getEntity())) {
    			event.setCancelled(true);
    		}
    	}
    }
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
    	if(plugin.livingPlayers.contains(player)) {
    		if(plugin.started == false) {
    			if(!player.isOp()) {
    				event.setCancelled(true);
    			}
    		}
    	} else {
    		event.setCancelled(true);
    		
    		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    			if(player.getItemInHand().equals(plugin.teleport)) {
    				int size = 0;
    			
    				if(Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() < 9) {
    					size = 9;
    				} else if(Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() > 9 && (Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() <= 18)) {
    					size = 18;
    				} else if(Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() > 18 && (Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() <= 27)) {
    					size = 27;
    				} else if(Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() > 27 && (Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() <= 36)) {
    					size = 36;
    				} else if(Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() > 36 && (Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() <= 45)) {
    					size = 45;
    				} else if(Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() > 45 && (Bukkit.getOnlinePlayers().size() - plugin.livingPlayers.size() <= 54)) {
    					size = 54;
    				}
    			
    				Inventory teleInv = Bukkit.createInventory(null, size, ChatColor.DARK_GRAY + "[" + ChatColor.DARK_BLUE + "Teleport" + ChatColor.DARK_GRAY + "]");
    			
    				for(Player all : Bukkit.getOnlinePlayers()) {
    					if(plugin.livingPlayers.contains(all)) {
    						ItemStack playerItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    					
    						ItemMeta playerItemMeta = playerItem.getItemMeta();
    						playerItemMeta.setDisplayName(all.getName());
    						playerItem.setItemMeta(playerItemMeta);
    					
    						teleInv.addItem(playerItem);
    					}
    				}
    				player.openInventory(teleInv);
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	if(event.getWhoClicked().getType() == EntityType.PLAYER) {
    		Player player = (Player) event.getWhoClicked();
    		
    		if(!plugin.livingPlayers.contains(player)) {
    			if(event.getInventory().getName().equals(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_BLUE + "Teleport" + ChatColor.DARK_GRAY + "]")) {
    				if(event.getCurrentItem() != null) {
    					event.setCancelled(true);
    					
    					if(event.getCurrentItem().getItemMeta() != null) {
    						if(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()) != null) {
    							player.teleport(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
    							player.sendMessage(plugin.sg + ChatColor.AQUA + "You're now spectating " + event.getCurrentItem().getItemMeta().getDisplayName() + ".");
    						}
    					}
    				}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	if(event.getDamager() instanceof Player) {
    		Player damager = (Player) event.getDamager();
    		
    		if(!plugin.livingPlayers.contains(damager)) {
    			event.setCancelled(true);
    		}
    	}
    }
}
