package me.Markcreator.SurvivalGames;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
	private Main plugin;

	public PlayerMoveListener(Main Plugin) {
		this.plugin = Plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if ((this.plugin.locked) && ((event.getFrom().getX() != event.getTo().getX()) || (event.getFrom().getZ() != event.getTo().getZ()))) {
			Location newLoc = event.getFrom();
			
			if(newLoc.getBlock().getRelative(0, -1, 0).getType() == Material.AIR) {
				newLoc.setY(newLoc.getY()-1);
			}
			newLoc.setY(Math.floor(newLoc.getY()));
			
			player.teleport(newLoc);
		}
	}
}
