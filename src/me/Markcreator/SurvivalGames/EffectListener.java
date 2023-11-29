package me.Markcreator.SurvivalGames;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EffectListener implements Listener {

	private Main plugin;
    public EffectListener(Main Plugin) {
        this.plugin = Plugin;   
    }
    
    private ArrayList<Player> effectCooldown = new ArrayList<Player>();
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
    	if(event.getEntity() instanceof Arrow) {
    		Arrow arrow = (Arrow) event.getEntity();
    		
    		if(arrow.getShooter() instanceof Player) {
    			Player shooter = (Player) arrow.getShooter();
    			
    			if(playerKitEffect(shooter, "explodingArrows")) {
    				arrow.getWorld().createExplosion(arrow.getLocation().getX(), arrow.getLocation().getY(), arrow.getLocation().getZ(), 1.5F, false, true);
    				arrow.remove();
    					
    			} else if(playerKitEffect(shooter, "flamingArrows")) {
    				arrow.getWorld().createExplosion(arrow.getLocation().getX(), arrow.getLocation().getY(), arrow.getLocation().getZ(), 1.0F, true, false);
    				arrow.remove();
    			}
    		}
    	}
    }
	@SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
    	if(playerKitEffect(player, "flamethrower")) {
			if(event.getAction() == Action.RIGHT_CLICK_AIR) {
				if(player.getItemInHand().getType() == Material.FIRE_CHARGE) {
					final Fireball ball = (Fireball) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.FIREBALL);
						
					ball.setShooter(player);
					ball.setDirection(player.getEyeLocation().getDirection());
						
					if(player.getItemInHand().getAmount() - 1 < 1) {
						player.getInventory().remove(player.getItemInHand());
					} else {
						player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
					}
						
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							if(ball != null) {
								ball.remove();
							}
						}
					}, 20L);
				}
			}
			
		} else if(playerKitEffect(player, "granades")) {
			if(event.getAction() == Action.RIGHT_CLICK_AIR) {
				if(player.getItemInHand().getType() == Material.FIREWORK_STAR) {
					final Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(player.getItemInHand().getType(), 1));
						
					item.setPickupDelay(9999);
					item.setVelocity(new Vector(player.getEyeLocation().getDirection().getX(), player.getEyeLocation().getDirection().getY(), player.getEyeLocation().getDirection().getZ()));
						
					if(player.getItemInHand().getAmount() - 1 < 1) {
						player.getInventory().remove(player.getItemInHand());
					} else {
						player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
					}
						
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							item.remove();
							item.getWorld().createExplosion(item.getLocation().getX(), item.getLocation().getY(), item.getLocation().getZ(), 2.0F, false, true);
						}
					}, 60L);
				}
			}
			
		} else if(playerKitEffect(player, "leap")) {
			if(event.getAction() == Action.RIGHT_CLICK_AIR) {
				if(!effectCooldown.contains(player)) {
					effectCooldown.add(player);
					player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().normalize()));
					player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							if (player != null) {
								effectCooldown.remove(player);
								player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
							}
						}
					}, 40L);
				}
			}
    	}
    }
	
	public boolean playerKitEffect(Player player, String effect) {
		if(plugin.playerKit.containsKey(player)) {			
			if(plugin.kitData.getString("kits." + plugin.playerKit.get(player) + ".effect") != null) {
				if(plugin.kitData.getString("kits." + plugin.playerKit.get(player) + ".effect").equalsIgnoreCase(effect)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
