package me.Markcreator.SurvivalGames;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin {

	//public static Main plugin;
	
	public final PlayerJoinListener pjl = new PlayerJoinListener(this);
	public final ServerListPingListener slpl = new ServerListPingListener(this);
	public final PlayerEditListener pel = new PlayerEditListener(this);
	public final Counters c = new Counters(this);
	public final PlayerMoveListener pml = new PlayerMoveListener(this);
	public final DamageListener dl = new DamageListener(this);
	public final CrateListener cl = new CrateListener(this);
	public final PlayerChatListener pcl = new PlayerChatListener(this);
	public final MobListener ml = new MobListener(this);
	public final EffectListener el = new EffectListener(this);
	public final KitListener kl = new KitListener(this);
	
	File folder = new File("plugins" + File.separator + "BukkitGames");
	File players = new File(folder + File.separator + "playerdata.yml");
	File crate = new File(folder + File.separator + "cratedata.yml");
	File kit = new File(folder + File.separator + "kitdata.yml");
	
	FileConfiguration playerData = null;
	FileConfiguration crateData = null;
	FileConfiguration kitData = null;
	
	public ArrayList<Player> livingPlayers = new ArrayList<Player>();
	public HashMap<Location, Inventory> crates = new HashMap<Location, Inventory>();
	public ArrayList<Location> crateLocs = new ArrayList<Location>();
	public HashMap<Player, String> playerRank = new HashMap<Player, String>();
	public HashMap<Player, String> playerKit = new HashMap<Player, String>();
	
	public boolean started = false;
	public boolean starting = false;
	public boolean locked = false;
	
	public int counter = 0;
	public int countdownTask;
	
	public int graceperiod = 0;
	public int graceperiodid;
	
	public String winner = null;
	
	public int crateAmount = 0;
	public int spawnCrateAmount = 0;
	public boolean cratesDropped = false;
	public int crateTask;
	public int minCrates = 0;
	public int maxCrates = 0;
	
	public int maxPlayers = 24;
	
	public ArrayList<Player> hasVoted = new ArrayList<Player>();
	public int startVotes = 0;
	
	public int arenaSize = 300;
	public int minimalArenaSize = 30;
	int shrinkId;
	
	public String world = null;
	
	public String sg = ChatColor.GRAY + "[" + ChatColor.YELLOW + "BukkitGames" + ChatColor.GRAY + "] " + ChatColor.RESET;
	
	public ItemStack kitBook = new ItemStack(Material.BOOK, 1);
	public ItemStack priceList = new ItemStack(Material.PAPER, 1);
	public ItemStack teleport = new ItemStack(Material.BOOK, 1);
	public ItemStack vote = new ItemStack(Material.SLIME_BALL, 1);
	
	public ScoreboardManager manager = null;
	public Scoreboard board = null;
	public Objective objective = null;
	
	public Score online = null;
	public Score alive = null;
	public Score arenaSizeInfo = null;
	public Score voteAmount = null;
	
	public Team Default = null;
	public Team Iron = null;
	public Team Gold = null;
	public Team Diamond = null;
	public Team Emerald = null;
	public Team Op = null;
	public Team Winner = null;
	
	@Override
	public void onLoad() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		sg = (getConfig().getString("pluginDisplayName") + " &r").replace("&", "§");
		maxPlayers = getConfig().getInt("maxPlayers");
		
		world = getConfig().getString("worldName");
		
		spawnCrateAmount = getConfig().getInt("spawnCrateAmount");
		
		graceperiod = getConfig().getInt("graceperiod");
		
		minCrates = getConfig().getInt("minimalCrates");
		maxCrates = getConfig().getInt("maximalCrates");
		
		if(getConfig().getBoolean("deleteWorldOnServerBoot") == true) {
			deleteDir(new File(world));
		}
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.getLogger().info(pdfFile.getName() + " has been disabled.");
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.kickPlayer(sg + ChatColor.DARK_RED + "The server is restarting."); 
		}
		Bukkit.getScheduler().cancelTasks(this);
		
		for(Location all : crateLocs) {
			if(all.getBlock().getType() == Material.CHEST) {
				all.getBlock().setBlockData(Bukkit.createBlockData(Material.AIR));
				all.getBlock().setType(Material.AIR);
			}
		}
		
		if(getConfig().getBoolean("saveWorldOnServerShutDown") == true) {
			Bukkit.getWorld(world).save();
			Bukkit.unloadWorld(world, true);
		} else {
			Bukkit.unloadWorld(world, false);
		}
	}
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.getLogger().info(pdfFile.getName() + " has been enabled.");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(pjl, this);
		pm.registerEvents(slpl, this);
		pm.registerEvents(pel, this);
		pm.registerEvents(pml, this);
		pm.registerEvents(dl, this);
		pm.registerEvents(cl, this);
		pm.registerEvents(pcl, this);
		pm.registerEvents(ml, this);
		pm.registerEvents(el, this);
		pm.registerEvents(kl, this);
		
		playerData = reloadCustomConfig(players);
		crateData = reloadCustomConfig(crate);
		kitData = reloadCustomConfig(kit);
		
		Random r = new Random();
		crateAmount = r.nextInt(maxCrates);
		
		if(crateAmount < minCrates) {
			crateAmount = crateAmount + minCrates;
		}
		
		arenaSize = getConfig().getInt("arenaSize");
		minimalArenaSize = getConfig().getInt("minimalArenaSize");
		
		//ci.setCrateConfig();
		
		//WorldCreator wc = new WorldCreator("world");
        //wc.environment(Environment.NORMAL);
        //wc.generator(getDefaultWorldGenerator("world", null));
        //World w = wc.createWorld();
		
		if(Bukkit.getWorld(world) != null) {
			//Bukkit.getWorld(world).regenerateChunk(0, 0);
			
			if(Bukkit.getWorld(world).getBiome(0, 0, 0) == Biome.OCEAN || Bukkit.getWorld(world).getBiome(0, 0, 0) == Biome.DEEP_OCEAN) {
				Bukkit.broadcastMessage("------------------------------------------------------------------------ ");
				Bukkit.broadcastMessage(sg + "The spawn location was inside an ocean biome, please stand by while a new world is generated.");
				Bukkit.broadcastMessage("------------------------------------------------------------------------ ");
				
				Bukkit.shutdown();
			}
				
			Bukkit.getWorld(world).setSpawnLocation(0, Bukkit.getWorld(world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0);
		} else {
			Bukkit.broadcastMessage("------------------------------------------------------------------------ ");
			Bukkit.broadcastMessage(sg + "No world called '" + world + "' has been found, the server is shutting down. Please add a world called '" + world + "' to the server to make the server work.");
			Bukkit.broadcastMessage("------------------------------------------------------------------------ ");
			
			Bukkit.getScheduler().cancelTasks(this);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					Bukkit.shutdown();
				}
			}, 100L);
		}
		
		//Scoreboard
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		objective = board.registerNewObjective("bukkitgames", "dummy", sg);
		
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(sg);
		
		online = objective.getScore(ChatColor.GOLD + "Online:");
		online.setScore(0);
		
		alive = objective.getScore(ChatColor.GOLD + "Alive:");
		alive.setScore(0);
		
		arenaSizeInfo = objective.getScore(ChatColor.GOLD + "Arena size:");
		arenaSizeInfo.setScore(0);
		
		voteAmount = objective.getScore(ChatColor.GOLD + "Start votes:");
		voteAmount.setScore(0);
		
		//Teams
		
		Default = board.registerNewTeam("Default");
		Iron = board.registerNewTeam("Iron");
		Gold = board.registerNewTeam("Gold");
		Diamond = board.registerNewTeam("Diamond");
		Emerald = board.registerNewTeam("Emerald");
		Op = board.registerNewTeam("Op");
		Winner = board.registerNewTeam("Winner");
		
		
		Default.setAllowFriendlyFire(true);
		Default.setCanSeeFriendlyInvisibles(false);
		Default.setPrefix(ChatColor.BLUE + "");
		
		Iron.setAllowFriendlyFire(true);
		Iron.setCanSeeFriendlyInvisibles(false);
		Iron.setPrefix(ChatColor.WHITE + "");
		
		Gold.setAllowFriendlyFire(true);
		Gold.setCanSeeFriendlyInvisibles(false);
		Gold.setPrefix(ChatColor.GOLD + "");
		
		Diamond.setAllowFriendlyFire(true);
		Diamond.setCanSeeFriendlyInvisibles(false);
		Diamond.setPrefix(ChatColor.AQUA + "");
		
		Emerald.setAllowFriendlyFire(true);
		Emerald.setCanSeeFriendlyInvisibles(false);
		Emerald.setPrefix(ChatColor.GREEN + "");
		
		Op.setAllowFriendlyFire(true);
		Op.setCanSeeFriendlyInvisibles(false);
		Op.setPrefix(ChatColor.RED + "");
		
		Winner.setAllowFriendlyFire(true);
		Winner.setCanSeeFriendlyInvisibles(false);
		Winner.setPrefix(ChatColor.YELLOW + "");

		final int waitTime = getConfig().getInt("roundStartWaitTime");
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(started == false) {
						all.setFoodLevel(20);
					}
				}

				int intendedSeconds = (int) ((float) waitTime * (float) ((float) ((float) maxPlayers - Bukkit.getOnlinePlayers().size()+2) / (float) maxPlayers));
								
				if(Bukkit.getOnlinePlayers().size() >= 2 && starting == false && started == false) {	    			
	    			c.startCounting(waitTime);
				}
				
				if(intendedSeconds < counter) {
	    			c.startCounting(intendedSeconds);
	    			Bukkit.broadcastMessage(sg + ChatColor.GRAY + "The timer has been shortened to " + intendedSeconds + " seconds.");	
	    		}
				
				if(started == true) {
	    			if(Bukkit.getOnlinePlayers().size() <= 1) {
	    				int stop = 10;
	    				
	    				if(stop - 1 == 0) {
	    					Bukkit.shutdown();
	    				} else {
	    					stop = stop - 1;
	    				}
	    			}
				}
			}
		}, 40L, 20L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				online.setScore(Bukkit.getOnlinePlayers().size());
				alive.setScore(livingPlayers.size());
				arenaSizeInfo.setScore(arenaSize);
				
				if(started == false) {
					voteAmount.setScore(startVotes);
				}
			}
		}, 0L, 20L);
			
		shrinkId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if(started == true) {
					if(arenaSize > minimalArenaSize) {
						arenaSize = arenaSize - 1;
					} else {
						Bukkit.getScheduler().cancelTask(shrinkId);
					}
				}
			}
		}, 0L, getConfig().getInt("shrinkSpeedInSeconds") * 20);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if(started == true) {
					for(Player all : Bukkit.getOnlinePlayers()) {
						if(isOutsideArena(all)) {
							if(livingPlayers.contains(all)) {
								all.damage(2);
								all.sendMessage(sg + ChatColor.RED + "You're outside the arena, get back quickly!");
								
							} else {
								all.teleport(new Location(Bukkit.getWorld(world), 0.5, Bukkit.getWorld(world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
								all.sendMessage(sg + ChatColor.RED + "You went outside the arena, so you've got teleported back to the spawn.");
							}
						}
					}
					
				} else {
					for(Player all : Bukkit.getOnlinePlayers()) {
						if(all.getLocation().distance(Bukkit.getWorld(world).getSpawnLocation()) > arenaSize) {
							all.teleport(new Location(Bukkit.getWorld(world), 0.5, Bukkit.getWorld(world).getHighestBlockAt(0, 0).getLocation().getBlockY(), 0.5));
							all.sendMessage(sg + ChatColor.RED + "You went outside the arena, so you've got teleported back to the spawn.");
						}
					}
				}
			}
		}, 0L, 20L);
		
		for(int i = 0; i < crateAmount; i++) {
			if(crateAmount - 1 >= 0) {
				createCrate();
			}
		}
		
		for(int i = 0; i < spawnCrateAmount; i++) {
			if(spawnCrateAmount - 1 >= 0) {
				createSpawnCrate();
			}
		}
		
		ItemMeta kitBookMeta = kitBook.getItemMeta();
		kitBookMeta.setDisplayName(ChatColor.GRAY + "[" + ChatColor.GOLD + "Kit Book" + ChatColor.GRAY + "]");
		kitBook.setItemMeta(kitBookMeta);
		
		ItemMeta priceListMeta = priceList.getItemMeta();
		priceListMeta.setDisplayName(ChatColor.GRAY + "[" + ChatColor.YELLOW + "Price List" + ChatColor.GRAY + "]");
		priceList.setItemMeta(priceListMeta);
		
		ItemMeta teleportMeta = teleport.getItemMeta();
		teleportMeta.setDisplayName(ChatColor.GRAY + "[" + ChatColor.AQUA + "Teleport" + ChatColor.GRAY + "]");
		teleport.setItemMeta(teleportMeta);
		
		ItemMeta voteMeta = vote.getItemMeta();
		voteMeta.setDisplayName(ChatColor.GRAY + "[" + ChatColor.AQUA + "Vote" + ChatColor.GRAY + "]");
		vote.setItemMeta(voteMeta);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(commandLabel.equalsIgnoreCase("bg")) {
				if(args.length == 0) {
					player.sendMessage(sg + ChatColor.GRAY + "BukkitGames plugin by Markcreator.");
					
				} else if(args.length == 1) {
					if(args[0].equalsIgnoreCase("forcestart")) {
						if(player.isOp()) {
							if(counter != 0) {
								Bukkit.broadcastMessage(sg + ChatColor.AQUA + "The round has been forcestarted!");
								counter = 10;
							} else {
								player.sendMessage(sg + ChatColor.RED + "The countdown hasn't started yet.");
							}
						} else {
							player.sendMessage(sg + ChatColor.RED + "You don't have permission to do that.");
						}
						
					} else if(args[0].equalsIgnoreCase("votestart") || args[0].equalsIgnoreCase("vote")) {
						if(started == false) {
							if(!hasVoted.contains(player)) {
								startVotes++;
								player.sendMessage(sg + ChatColor.GOLD + "You have voted to start the game successfully.");
								hasVoted.add(player);
								
								if(startVotes == getConfig().getInt("votesNeededToAutostart")) {
									Bukkit.broadcastMessage(sg + ChatColor.GREEN + "The round has been votestarted!");
									counter = 10;
								}
							} else {
								player.sendMessage(sg + ChatColor.RED + "You have already voted to start the game.");
							}
						} else {
							player.sendMessage(sg + ChatColor.RED + "You can't vote the start the game right now.");
						}
					}
					
				} else if(args.length == 2) {
					if(player.isOp()) {
						if(args[0].equalsIgnoreCase("iron")) {
							playerData.set("players." + args[1] + ".rank", "Iron");
							saveCustomConfig(playerData, players);
							player.sendMessage(sg + ChatColor.GREEN + args[1] + " is now Iron Member.");
						
						} else if(args[0].equalsIgnoreCase("gold")) {
							playerData.set("players." + args[1] + ".rank", "Gold");
							saveCustomConfig(playerData, players);
							player.sendMessage(sg + ChatColor.GREEN + args[1] + " is now Gold Member.");

						} else if(args[0].equalsIgnoreCase("diamond")) {
							playerData.set("players." + args[1] + ".rank", "Diamond");
							saveCustomConfig(playerData, players);
							player.sendMessage(sg + ChatColor.GREEN + args[1] + " is now Diamond Member.");

						} else if(args[0].equalsIgnoreCase("emerald")) {
							playerData.set("players." + args[1] + ".rank", "Emerald");
							saveCustomConfig(playerData, players);
							player.sendMessage(sg + ChatColor.GREEN + args[1] + " is now Emerald Member.");
						
						} else if(args[0].equalsIgnoreCase("norank")) {
							playerData.set("players." + args[1] + ".rank", null);
							saveCustomConfig(playerData, players);
							player.sendMessage(sg + ChatColor.GREEN + args[1] + " is now a Default Member.");
						
						} else if(args[0].equalsIgnoreCase("takeallkits")) {
							playerData.set("players." + args[1] + ".kits", "");
							saveCustomConfig(playerData, players);
							sender.sendMessage(sg + ChatColor.GREEN + args[1] + " now has no kits.");
						}
					} else {
						player.sendMessage(sg + ChatColor.RED + "You don't have permission to do that.");
					}
					
				} else if(args.length == 3) {
					if(player.isOp()) {
						if(args[0].equalsIgnoreCase("addpoints")) {
							if(playerData.getInt("players." + args[1] + ".points") + Integer.parseInt(args[2]) < 0) {
								playerData.set("players." + args[1] + ".points", 0);
								saveCustomConfig(playerData, players);
							} else {
								playerData.set("players." + args[1] + ".points", playerData.getInt("players." + args[1] + ".points") + Integer.parseInt(args[2]));
								saveCustomConfig(playerData, players);
							}
							player.sendMessage(sg + ChatColor.GREEN + args[1] + " now has " + playerData.getInt("players." + args[1] + ".points") + " points.");
							
						} else if(args[0].equalsIgnoreCase("giveKit")) {
							if(!playerData.getString("players." + args[1] + ".kits").contains(args[2] + ", ")) {
								playerData.set("players." + args[1] + ".kits", playerData.getString("players." + args[1] + ".kits") + args[2] + ", ");
								saveCustomConfig(playerData, players);
							}
							player.sendMessage(sg + ChatColor.GREEN + args[1] + " has now has the kit: " + args[2] + ".");
						
						} else if(args[0].equalsIgnoreCase("takekit")) {
							if(playerData.getString("players." + args[1] + ".kits").contains(args[2] + ", ")) {
								playerData.set("players." + args[1] + ".kits", playerData.getString("players." + args[1] + ".kits").replace(args[2] + ", ", ""));
								saveCustomConfig(playerData, players);
								
								player.sendMessage(sg + ChatColor.RED + args[1] + " now lost the kit: " + args[2] + ".");
							} else {
								player.sendMessage(sg + ChatColor.RED + args[1] + " doesnt have that kit.");
							}
						}
					} else {
						player.sendMessage(sg + ChatColor.RED + "You don't have permission to do that.");
					}
				}
			} else if(commandLabel.equalsIgnoreCase("vote") || commandLabel.equalsIgnoreCase("votestart")) {
				if(started == false) {
					if(!hasVoted.contains(player)) {
						startVotes++;
						player.sendMessage(sg + ChatColor.GOLD + "You have voted to start the game successfully.");
						hasVoted.add(player);
						
						if(startVotes == getConfig().getInt("votesNeededToAutostart")) {
							Bukkit.broadcastMessage(sg + ChatColor.GREEN + "The round has been votestarted!");
							counter = 10;
						}
					} else {
						player.sendMessage(sg + ChatColor.RED + "You have already voted to start the game.");
					}
				} else {
					player.sendMessage(sg + ChatColor.RED + "You can't vote the start the game right now.");
				}
			}
			
		} else {
			if(commandLabel.equalsIgnoreCase("bg")) {
				if(args.length == 0) {
					sender.sendMessage(sg + ChatColor.GRAY + "Survival Games plugin by Markcreator.");
					
				} else if(args.length == 2) {
					if(args[0].equalsIgnoreCase("iron")) {
						playerData.set("players." + args[1] + ".rank", "Iron");
						saveCustomConfig(playerData, players);
						sender.sendMessage(sg + ChatColor.GREEN + args[1] + " is now Iron Member.");
						
					} else if(args[0].equalsIgnoreCase("gold")) {
						playerData.set("players." + args[1] + ".rank", "Gold");
						saveCustomConfig(playerData, players);
						sender.sendMessage(sg + ChatColor.GREEN + args[1] + " is now Gold Member.");

					} else if(args[0].equalsIgnoreCase("diamond")) {
						playerData.set("players." + args[1] + ".rank", "Diamond");
						saveCustomConfig(playerData, players);
						sender.sendMessage(sg + ChatColor.GREEN + args[1] + " is now Diamond Member.");

					} else if(args[0].equalsIgnoreCase("emerald")) {
						playerData.set("players." + args[1] + ".rank", "Emerald");
						saveCustomConfig(playerData, players);
						sender.sendMessage(sg + ChatColor.GREEN + args[1] + " is now Emerald Member.");
					
					} else if(args[0].equalsIgnoreCase("norank")) {
						playerData.set("players." + args[1] + ".rank", null);
						saveCustomConfig(playerData, players);
						sender.sendMessage(sg + ChatColor.GREEN + args[1] + " is now a Default Member.");
						
					} else if(args[0].equalsIgnoreCase("takeallkits")) {
						playerData.set("players." + args[1] + ".kits", "");
						saveCustomConfig(playerData, players);
						sender.sendMessage(sg + ChatColor.GREEN + args[1] + " now has no kits.");
					}
				}
			} else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("addpoints")) {
					if(playerData.getInt("players." + args[1] + ".points") + Integer.parseInt(args[2]) < 0) {
						playerData.set("players." + args[1] + ".points", 0);
						saveCustomConfig(playerData, players);
					} else {
						playerData.set("players." + args[1] + ".points", playerData.getInt("players." + args[1] + ".points") + Integer.parseInt(args[2]));
						saveCustomConfig(playerData, players);
					}
					sender.sendMessage(sg + ChatColor.GREEN + args[1] + " now has " + playerData.getInt("players." + args[1] + ".points") + " points.");
					
				} else if(args[0].equalsIgnoreCase("giveKit")) {
					if(!playerData.getString("players." + args[1] + ".kits").contains(args[2] + ", ")) {
						playerData.set("players." + args[1] + ".kits", playerData.getString("players." + args[1] + ".kits") + args[2] + ", ");
						saveCustomConfig(playerData, players);
					}
					sender.sendMessage(sg + ChatColor.GREEN + args[1] + " now has has the kit: " + args[2] + ".");
				
				} else if(args[0].equalsIgnoreCase("takekit")) {
					if(playerData.getString("players." + args[1] + ".kits").contains(args[2] + ", ")) {
						playerData.set("players." + args[1] + ".kits", playerData.getString("players." + args[1] + ".kits").replace(args[2] + ", ", ""));
						saveCustomConfig(playerData, players);
						
						sender.sendMessage(sg + ChatColor.RED + args[1] + " now lost the kit: " + args[2] + ".");
					} else {
						sender.sendMessage(sg + ChatColor.RED + args[1] + " doesnt have that kit.");
					}
				}
			}
		}
		return false;
	}
	
	public static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(dir, children[i]));
			}
		}
		dir.delete();
	}
	
	@SuppressWarnings("deprecation")
	public void createSpawnCrate() {
		Random rx = new Random();
		int x = rx.nextInt(getConfig().getInt("spawnCrateSpawnRadius")) - (getConfig().getInt("spawnCrateSpawnRadius") / 2);
		
		Random rz = new Random();
		int z = rz.nextInt(getConfig().getInt("spawnCrateSpawnRadius")) - (getConfig().getInt("spawnCrateSpawnRadius") / 2);
		
		if(getConfig().getBoolean("dropCrates") == true) {
			Location newCrateLoc = new Location(Bukkit.getWorld(world), x, Bukkit.getWorld(world).getHighestBlockAt(x, z).getLocation().getBlockY() + 20, z);
			Bukkit.getWorld(world).spawnFallingBlock(newCrateLoc, Material.CHEST, (byte) 0);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void createCrate() {
		Random rx = new Random();
		int x = (rx.nextInt(arenaSize) * 2) - arenaSize;
		
		Random rz = new Random();
		int z = (rz.nextInt(arenaSize) * 2) - arenaSize;
		
		if(getConfig().getBoolean("dropCrates") == true) {
			Location newCrateLoc = new Location(Bukkit.getWorld(world), x, Bukkit.getWorld(world).getHighestBlockAt(x, z).getLocation().getBlockY() + 20, z);
			Bukkit.getWorld(world).spawnFallingBlock(newCrateLoc, Material.CHEST, (byte) 0);
		}
	}
	
	public boolean isOutsideArena(Player player) {
		boolean outside = false;
		
		if(player.getLocation().getBlockX() > arenaSize || player.getLocation().getBlockX() < Integer.parseInt("-" + arenaSize)) {
			outside = true;
		}
		if(player.getLocation().getBlockZ() > arenaSize || player.getLocation().getBlockZ() < Integer.parseInt("-" + arenaSize)) {
			outside = true;
		}
		
		return outside;
	}
	
	public void saveCustomConfig(FileConfiguration config, File file) {
		try {
			config.save(file);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void createFile(File file) {
		if(!file.exists()) {
            try {
                file.createNewFile();
                                
                InputStream in = getClass().getClassLoader().getResourceAsStream(file.getName());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                
                OutputStream out = new FileOutputStream(file);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                
                String str;
                while((str = br.readLine()) != null) {
                	bw.write(str);
                	bw.newLine();
                }
                br.close();
                bw.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	protected void createFolder(File folder) {
		if(!folder.exists()) {
            folder.mkdir();
        }
	}
	
	public FileConfiguration reloadCustomConfig(File file) {
		FileConfiguration fileConfig;
		
	    createFolder(folder);
	    createFile(file);
	    
	    fileConfig = YamlConfiguration.loadConfiguration(file);
	    
	    return fileConfig;
	}
}
