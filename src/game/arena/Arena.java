package game.arena;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import game.Main;
import game.manager.GameBarManager;
import game.scoreboard.ScoreboardStatus;
import game.user.User;
import game.utility.FontConverter;
import game.utility.MiscUtils;
import game.utility.XSound;
import net.md_5.bungee.api.ChatColor;

public class Arena extends BukkitRunnable {
	
  private BossBar bar;
  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private final String id;
  private final Map<ArenaOption, Integer> arenaOptions;
  private final Map<GameLocation, Location> gameLocations;
  private final List<User> players;
  private final List<User> spectators;
  private final List<User> deadPlayers;
  private final GameBarManager gameBarManager;
  private boolean ready;
  private boolean forceStart;
  private String mapName;
  private ArenaState arenaState = ArenaState.INACTIVE;
  private ArenaGameState arenaGameState = ArenaGameState.BLOODBATH;
  private List<Location> playerSpawnPoints;
  private LinkedHashMap<UUID, ScoreboardStatus> status = new LinkedHashMap<>();
  private boolean isStarted;
  private int day;

  public Arena(String id) {
    this.id = id;
    this.mapName = id;
    this.players = new ArrayList<>();
    this.spectators = new ArrayList<>();
    this.deadPlayers = new ArrayList<>();
    this.playerSpawnPoints = new ArrayList<>();
    this.arenaOptions = new EnumMap<>(ArenaOption.class);
    this.gameLocations = new EnumMap<>(GameLocation.class);
    this.gameBarManager = new GameBarManager(this, plugin);
    this.day = 1;

    for (ArenaOption option : ArenaOption.values())
      this.arenaOptions.put(option, Integer.valueOf(option.getIntegerValue()));
  }

  public boolean isInArena(User user) {
    return (user != null && this.players.contains(user));
  }

  public boolean isArenaState(ArenaState... arenaStates) {
    for (ArenaState state : arenaStates) {
      if (this.arenaState == state)
        return true;
    }
    return false;
  }

  public boolean isArenaGameState(ArenaGameState... arenaGameStates) {
	    for (ArenaGameState Gamestate : arenaGameStates) {
	      if (this.arenaGameState == Gamestate)
	        return true;
	    }
	    return false;
	  }

  private void teleportToGameLocation(User user, GameLocation gameLocation) {
    if (!validateLocation(gameLocation)) {
      return;

    }

    Player player = user.getPlayer();
    user.removePotionEffectsExcept(new PotionEffectType[] { PotionEffectType.BLINDNESS });
    player.setFoodLevel(20);
    player.setFlying(false);
    player.setAllowFlight(false);
    player.setFlySpeed(0.1F);
    player.setWalkSpeed(0.2F);
    player.teleport(this.gameLocations.get(gameLocation));

  }

  public int getDay() {
	  return this.day;
  }

  public void setDay(int day) {
	  this.day = day;
  }

  public void teleportToLobby(User user) {
    teleportToGameLocation(user, GameLocation.LOBBY);
  }

  public void teleportToEndLocation(User user) {
    teleportToGameLocation(user, GameLocation.END);
  }

  public LinkedHashMap<UUID, ScoreboardStatus> getStatus() {
	    return this.status;
  }

  public boolean isStarted() {
	   	return this.isStarted;
  }

  public GameBarManager getGameBar() {
    return this.gameBarManager;
  }

  public ArenaState getArenaState() {
    return this.arenaState;
  }

  public void setArenaState(ArenaState arenaState) {
    this.arenaState = arenaState;
    this.gameBarManager.handleGameBar();
  }

  public ArenaGameState getArenaGameState() {
	return this.arenaGameState;
  }
    
  public List<User> getFallenTributes() {
	    List<User> recentlyAddedFallenTributes = new ArrayList<>();
	    List<User> fallenTributes = new ArrayList<>(deadPlayers);
	    recentlyAddedFallenTributes.addAll(deadPlayers);
	    fallenTributes.removeAll(recentlyAddedFallenTributes);
	    return fallenTributes;
	}

  public void setArenaGameState(ArenaGameState arenaGameState) {
	this.arenaGameState = arenaGameState;
  }

  public boolean isReady() {
    return this.ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
  }

  public int getSetupProgress() {
    return this.ready ? 100 : 0;
  }

  public int getClassicGameplayTime() {
    return getOption(ArenaOption.GAMEPLAY_TIME);
  }

  public String getMapName() {
    return this.mapName;
  }

  public void setMapName(String mapName) {
    this.mapName = mapName;
  }

  public int getTimer() {
    return getOption(ArenaOption.TIMER);
  }

  public void setTimer(int timer) {
    setOptionValue(ArenaOption.TIMER, timer);
  }

  public int getMaximumPlayers() {
    return getOption(ArenaOption.MAXIMUM_PLAYERS);
  }

  public void setMaximumPlayers(int maximumPlayers) {
    setOptionValue(ArenaOption.MAXIMUM_PLAYERS, maximumPlayers);
  }

  public int getMinimumPlayers() {
    return getOption(ArenaOption.MINIMUM_PLAYERS);
  }

  public void setMinimumPlayers(int minimumPlayers) {
    setOptionValue(ArenaOption.MINIMUM_PLAYERS, minimumPlayers);
  }

  public List<Location> getPlayerSpawnPoints() {
    return this.playerSpawnPoints;
  }

  public void setPlayerSpawnPoints(List<Location> playerSpawnPoints) {
    this.playerSpawnPoints = playerSpawnPoints;
  }

  public Location getLobbyLocation() {
    return this.gameLocations.get(GameLocation.LOBBY);
  }

  public void setLobbyLocation(Location lobbyLocation) {
    this.gameLocations.put(GameLocation.LOBBY, lobbyLocation);
  }

  public Location getEndLocation() {
    return this.gameLocations.get(GameLocation.END);
  }

  public void setEndLocation(Location endLocation) {
    this.gameLocations.put(GameLocation.END, endLocation);

  }

  public List<User> getPlayers() {
    return this.players;
  }

  public void addUser(User user) {
    this.players.add(user);
  }

  public void removeUser(User user) {
    this.players.remove(user);
  }

  public List<User> getDeadPlayers() {
    return this.deadPlayers;
  }

  public boolean isForceStart() {
    return this.forceStart;
  }

  public void setForceStart(boolean forceStart) {
    this.forceStart = forceStart;
  }

  public List<User> getAllPlayers() {
    return this.players;
  }

  public List<User> getSpectators() {
      return this.players.stream()
          .filter(User::isSpectator)
          .collect(Collectors.toList());
  }

  public boolean isPlayerAlive(User user) {
    for (User u : getPlayersLeftWithoutSpectators()) {
      if (u.equals(user) && this.players.contains(u) && !isPlayerDead(u) && !isSpectator(u))
        return true;
    }
    return false;
  }

  public boolean isPlayerDead(User user) {
    return this.deadPlayers.contains(user);
  }

  public void addSpectator(User user) {
    this.spectators.add(user);
  }

  public void removeSpectator(User user) {
    this.spectators.remove(user);
  }

  public void addDeadPlayer(User user) {
	this.deadPlayers.add(user);
  }

  public void removeDeadPlayer(User user) {
	this.deadPlayers.remove(user);
  }

  public boolean isSpectator(User user) {
    return this.spectators.contains(user);
  }

  public String timeSurvived(User user) {
	    ArenaState arenaState = getArenaState(); 

	    if (arenaState != ArenaState.IN_GAME) {
	        return null;
	    }

	    long totalTimeInSeconds = getOption(ArenaOption.GAMEPLAY_TIME) - getTimer();

	    if (totalTimeInSeconds <= 0) {
	        return "Not available";
	    }

	    long hours = totalTimeInSeconds / 3600;
	    long minutes = (totalTimeInSeconds % 3600) / 60;
	    long seconds = totalTimeInSeconds % 60;

	    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

  public void start() {
    runTaskTimer(plugin, 20L, 20L);
    setArenaState(ArenaState.WAITING_FOR_PLAYERS);
    this.isStarted = true;
  }

  public void stop() {
    if (this.arenaState != ArenaState.INACTIVE) {
      cancel();
      this.isStarted = false;

    }
    cleanUpArena();
    this.players.forEach(user -> plugin.getArenaManager().leaveAttempt(user, user.getArena()));
    this.players.forEach(user -> user.clearPlayerScoreboard(user.getPlayer()));
  }

  public void cleanUpArena() {
	    this.players.clear();
	    this.deadPlayers.clear();
	    this.spectators.clear();
	    this.forceStart = false;
		this.players.forEach(user -> user.setSpectator(false));
		this.players.forEach(this::removeDeadPlayer);
		this.players.forEach(User::resetKills);
	  }

  public Set<User> getPlayersLeftWithoutSpectators() {
	  return this.players.stream().filter(user -> !user.isSpectator() && !isSpectator(user) && !isPlayerDead(user)).collect(Collectors.toSet());
  }

  public User getLastPlayerAlive() {
	    Set<User> playersLeft = getPlayersLeftWithoutSpectators();
	    return playersLeft.isEmpty() ? null : playersLeft.iterator().next();
	}

	public boolean checkForWinner() {
	    return !isArenaState(ArenaState.WAITING_FOR_PLAYERS) && getPlayersLeftWithoutSpectators().size() == 1;
	}

	public void initiateTheFallen() {
	    int delayBetweenNames = 4; // Delay in seconds

	    List<User> recentlyAddedFallen = getFallenTributes();

	    if (!recentlyAddedFallen.isEmpty()) {
	        broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + FontConverter.convertFont(" "));
	        broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + FontConverter.convertFont("Take a moment to recognize our fallen tributes."));
	        broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + FontConverter.convertFont(" "));

	        bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
	        bar.setTitle(ChatColor.AQUA + "" + ChatColor.BOLD + FontConverter.convertFont("the fallen"));

	        for (User user : recentlyAddedFallen) {
	            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 1, 1);
	            bar.addPlayer(user.getPlayer());
	        }

	        final int[] currentIndex = {0};

	        BukkitRunnable nameBroadcastTask = new BukkitRunnable() {
	            @Override
	            public void run() {
	                if (currentIndex[0] < recentlyAddedFallen.size()) {
	                    String currentName = recentlyAddedFallen.get(currentIndex[0]).getName();
	                    broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + FontConverter.convertFont("The fallen tribute: ") + ChatColor.RED + "" + ChatColor.BOLD + FontConverter.convertFont(currentName));
	                    bar.setTitle(ChatColor.AQUA + "" + ChatColor.BOLD + FontConverter.convertFont("The fallen tribute: ") + ChatColor.RED + "" + ChatColor.BOLD + FontConverter.convertFont(currentName));
	                    currentIndex[0]++;
	                } else {
	                    cancel();
	                    gameBarManager.removeAll();
	                    for (User user : recentlyAddedFallen) {
	                        user.getPlayer().getInventory().clear();
	                        bar.removePlayer(user.getPlayer());
	                    }
	                }
	            }
	        };
	        nameBroadcastTask.runTaskTimer(plugin, 100L, 20L * delayBetweenNames);
	    }
	}

	public void displayFinalResults() {
	    if (checkForWinner()) {
	        User winner = getLastPlayerAlive();
	        broadcastCenteredMessage(" ");
	        broadcastCenteredMessage(ChatColor.GRAY + "───── « ᴛᴀʟᴇs ᴏғ ᴘᴀɴᴇᴍ » ─────");
	        broadcastCenteredMessage(" ");
	        broadcastCenteredMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "ᴠɪᴄᴛᴏʀ: " + ChatColor.AQUA + "" + ChatColor.BOLD + "" + FontConverter.convertFont(winner.getName()));
	        broadcastCenteredMessage(" ");

	        Set<User> playersLeft = getPlayersLeftWithoutSpectators();

	        if (playersLeft.size() == 3) {
	            Iterator<User> iterator = playersLeft.iterator();

	            User thirdPlace = iterator.next();
	            broadcastCenteredMessage(ChatColor.GOLD + "3ʀᴅ ᴘʟᴀᴄᴇ: " + ChatColor.GRAY + thirdPlace.getName());
	            User secondPlace = iterator.next();
	            broadcastCenteredMessage(ChatColor.YELLOW + "2ɴᴅ ᴘʟᴀᴄᴇ: " + ChatColor.GRAY + secondPlace.getName());
		        broadcastCenteredMessage(" ");
		        broadcastCenteredMessage(ChatColor.GRAY + "───── « ᴛᴀʟᴇs ᴏғ ᴘᴀɴᴇᴍ » ─────");
		        broadcastCenteredMessage(" ");

	        } else if (playersLeft.size() == 2) {
	            Iterator<User> iterator = playersLeft.iterator();

	            User secondPlace = iterator.next();
	            broadcastCenteredMessage(ChatColor.YELLOW + "2ɴᴅ ᴘʟᴀᴄᴇ: " + ChatColor.GRAY + secondPlace.getName());
	            broadcastCenteredMessage(ChatColor.GOLD + "3rd Place: " + ChatColor.GRAY + "ᴇᴍᴘᴛʏ :(");
		        broadcastCenteredMessage(" ");
		        broadcastCenteredMessage(ChatColor.GRAY + "───── « ᴛᴀʟᴇs ᴏғ ᴘᴀɴᴇᴍ » ─────");
		        broadcastCenteredMessage(" ");

	        } else {
	            broadcastCenteredMessage(ChatColor.YELLOW + "2ɴᴅ ᴘʟᴀᴄᴇ: " + ChatColor.GRAY + "ᴇᴍᴘᴛʏ :(");
	            broadcastCenteredMessage(ChatColor.GOLD + "3ʀᴅ ᴘʟᴀᴄᴇ: " + ChatColor.GRAY + "ᴇᴍᴘᴛʏ :(");
		        broadcastCenteredMessage(" ");
		        broadcastCenteredMessage(ChatColor.GRAY + "───── « ᴛᴀʟᴇs ᴏғ ᴘᴀɴᴇᴍ » ─────");
		        broadcastCenteredMessage(" ");

	        	}

	    } else {
	        broadcastMessage(ChatColor.YELLOW + "The game has ended, but there is no clear winner.");
	    }
	}

  public void teleportToStartLocation(User user) {
    Location location = getRandomLocation();
    if (location == null) {
      return;
    }
    user.getPlayer().teleport(location);
  }

  public void teleportAllToStartLocation() {
    int i = 0, size = this.playerSpawnPoints.size();
    for (User user : this.players) {
      if (i + 1 > size) {
        plugin.getLogger().warning("There aren't enough spawn points to teleport players!");
        plugin.getLogger().warning("We are teleporting player to a random location for now!");
        user.getPlayer().teleport(getRandomLocation());
        break;
      }
      user.getPlayer().teleport(this.playerSpawnPoints.get(i++));
    }
  }

  public Location getRandomLocation() {
    return this.playerSpawnPoints.get(ThreadLocalRandom.current().nextInt(this.playerSpawnPoints.size()));
  }

  public User getRandomPlayer() {
    Set<User> players = getPlayersLeftWithoutSpectators();
    return players.stream().skip(ThreadLocalRandom.current().nextInt(players.size())).findFirst().orElse(null);
  }

  public int getGameplayTime() {
    return getOption(ArenaOption.GAMEPLAY_TIME);
  }

  public void setGameplayTime(int gameplayTime) {
    setOptionValue(ArenaOption.GAMEPLAY_TIME, gameplayTime);
  }

  public void showPlayers() {
    for (User user : this.players) {
      Player player = user.getPlayer();
      user.removePotionEffectsExcept(new PotionEffectType[] { PotionEffectType.BLINDNESS });
      for (User u : this.players) {
        player.showPlayer(plugin, u.getPlayer());
        u.getPlayer().showPlayer(plugin, player);
      }
    }
  }
  
  public void showGamePlayersToEachother() {
	    for (User user : this.getPlayersLeftWithoutSpectators()) {
	      Player player = user.getPlayer();
	      user.removePotionEffectsExcept(new PotionEffectType[] { PotionEffectType.BLINDNESS });
	      for (User u : this.players) {
	        player.showPlayer(plugin, u.getPlayer());
	        u.getPlayer().showPlayer(plugin, player);
	      }
	    }
	  }

  public void showUserToArena(User user) {
    Player player = user.getPlayer();
    for (User targetUser : this.players) {
      Player targetPlayer = targetUser.getPlayer();
      targetPlayer.showPlayer(plugin, player);
      player.showPlayer(plugin, targetPlayer);
    }
  }

  public void hideSpectator(User user) {
    if (!user.isSpectator()) {
      return;

    }
    
    Player player = user.getPlayer();
    for (User targetUser : this.players) {

      Player targetPlayer = targetUser.getPlayer();
      player.showPlayer(plugin, targetPlayer);

      if (targetUser.isSpectator()) {
        targetPlayer.showPlayer(plugin, player);
        continue;
      }
      targetPlayer.hidePlayer(plugin, player);
    }
  }

  public void hideUserOutsideTheGame(User user) {
    Player player = user.getPlayer();

    for (User targetUser : plugin.getUserManager().getUsers()) {
      Player targetPlayer = targetUser.getPlayer();
      if (isInArena(targetUser)) {
        showUserToArena(targetUser);
        continue;
      }
      targetPlayer.hidePlayer(plugin, player);
      player.hidePlayer(plugin, targetPlayer);
    }
  }

  public void showUserOutsideTheGame(User user) {
    Player player = user.getPlayer();
    for (User targetUser : plugin.getUserManager().getUsers()) {
      Player targetPlayer = targetUser.getPlayer();
      if (player == null || targetPlayer == null)
        continue;
      if (!isInArena(targetUser)) {
        targetPlayer.showPlayer(plugin, player);
        player.showPlayer(plugin, targetPlayer);
        continue;
      }
      targetPlayer.hidePlayer(plugin, player);
      player.hidePlayer(plugin, targetPlayer);
    }
  }

  private int getOption(ArenaOption option) {
    return this.arenaOptions.get(option).intValue();
  }

  private void setOptionValue(ArenaOption option, int value) {
    this.arenaOptions.put(option, Integer.valueOf(value));

  }

  private boolean validateLocation(GameLocation gameLocation) {
    Location location = this.gameLocations.get(gameLocation);
    if (location == null) {
      plugin.getLogger().log(Level.WARNING, "Lobby location isn't initialized for arena {0}!", this.id);
      return false;
    }
    return true;
  }

  private void incrementDays() {
      if (getTimer() % 60 == 0) {
          this.day++;
      }
  }

  @Override
public void run() {
	    if (this.players.isEmpty() && this.arenaState == ArenaState.WAITING_FOR_PLAYERS) {
	        return;
	    }

	    int waitingTime = getOption(ArenaOption.LOBBY_WAITING_TIME), startingTime = getOption(ArenaOption.LOBBY_STARTING_TIME),
	    ingameTime = getOption(ArenaOption.GAMEPLAY_TIME), endingTime = getOption(ArenaOption.GAME_ENDING_TIME), pregameTime = getOption(ArenaOption.PREGAME_TIME),
	    restartingTime = getOption(ArenaOption.GAME_RESTARTING_TIME);

	    switch (this.arenaState) {
	        case WAITING_FOR_PLAYERS:
	            if (getPlayersLeftWithoutSpectators().size() >= getMinimumPlayers()) {
	                setArenaState(ArenaState.STARTING);
	                setTimer(startingTime);

	            } else {

	                if (getTimer() <= 0) {
	                    setTimer(waitingTime);
						this.players.forEach(user -> user.sendMessage(ChatColor.RED + "Waiting for more players to join!"));

	                }
	            }

	            setTimer(getTimer() - 1);
	            break;

			case STARTING:
				if (getPlayersLeftWithoutSpectators().size() < getMinimumPlayers()) {
					setArenaState(ArenaState.WAITING_FOR_PLAYERS);
					setTimer(waitingTime);
					this.players.forEach(user -> user.getPlayer().sendMessage(ChatColor.RED + "Game cancelled, waiting for more players!"));
					break;
				}

				if (getTimer() == 45 || getTimer() == 30 || getTimer() <= 5 && getTimer() != 0) {
					this.players.forEach(user -> XSound.UI_BUTTON_CLICK.play(user.getPlayer()));
					this.players.forEach(user -> user.sendMessage(ChatColor.GRAY + "You will be teleported to your pods in " + ChatColor.AQUA + getTimer() +
					ChatColor.GRAY + " seconds!"));

	            }

				if (getTimer() == 0) {
					teleportAllToStartLocation();
					this.players.forEach(user -> user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 1)));
					setArenaState(ArenaState.PREGAME);
					setTimer(pregameTime);
					showGamePlayersToEachother();

				}
				setTimer(getTimer() - 1);
				break;

			case PREGAME:
				if(getTimer() == 10) {
					this.players.forEach(user -> XSound.ENTITY_IRON_GOLEM_REPAIR.play(user.getPlayer()));
				}

				if (getTimer() == 60 || getTimer() == 45 || getTimer() == 30 || getTimer() <= 5 && getTimer() != 0) {
					this.players.forEach(user -> XSound.UI_BUTTON_CLICK.play(user.getPlayer()));
					this.players.forEach(user -> user.sendMessage(ChatColor.GRAY + "The games will begin in " + ChatColor.AQUA + getTimer() + ChatColor.GRAY + " seconds!"));

	            }

				if (getTimer() == 0) {
					setArenaState(ArenaState.IN_GAME);
					setTimer(ingameTime);

				}
				setTimer(getTimer() - 1);
				break;

			case IN_GAME:
                incrementDays();
				if (getTimer() == 60 || getTimer() == 30 || getTimer() == 10 || getTimer() <= 5 && getTimer() >= 1) {
					this.players.forEach(user -> XSound.UI_BUTTON_CLICK.play(user.getPlayer()));
					this.players.forEach(user -> user.sendMessage(ChatColor.GRAY + "The games will end in " + ChatColor.AQUA + getTimer() + ChatColor.GRAY + " seconds!"));
				}

				if(getTimer() == (ingameTime - 60)) {
					this.arenaGameState = ArenaGameState.INACTIVE;
				}

				if(getTimer() <= ingameTime && getTimer() >= 0) {
					if(checkForWinner() == true) { // change BACK TO TRUE DONT FORGET
						setTimer(0);
					}
				}
    
				if (getTimer() == 0) {
					displayFinalResults();
					this.players.forEach(user -> XSound.ENTITY_IRON_GOLEM_DEATH.play(user.getPlayer()));
					User winner = getLastPlayerAlive();
					playFireworksEffect(winner);
					setArenaState(ArenaState.ENDING);
					setTimer(endingTime);
					showPlayers();
				}

				setTimer(getTimer() - 1);
				break;

			case ENDING:
				if (getTimer() == 60 || getTimer() == 30 || getTimer() == 10 || getTimer() <= 5) {
					this.players.forEach(user -> XSound.UI_BUTTON_CLICK.play(user.getPlayer()));
					this.players.forEach(user -> user.sendMessage(ChatColor.GRAY + "The games will end in " + ChatColor.AQUA + getTimer() + ChatColor.GRAY + " seconds!"));

				}

				if (getTimer() == 0) {
					showPlayers();
					this.players.forEach(user -> user.clearPlayerScoreboard(user.getPlayer()));
					this.players.forEach(user -> plugin.getArenaManager().leaveAttempt(user, user.getArena()));
					setArenaState(ArenaState.RESTARTING);
					setTimer(restartingTime);

				}

				setTimer(getTimer() - 1);
				break;

			case RESTARTING:
				cleanUpArena();
				setArenaState(ArenaState.WAITING_FOR_PLAYERS);
				this.players.forEach(user -> user.getPlayer().sendMessage(ChatColor.RED + "The arena has restarted!"));
				break;

            default:
				break;
			}
	}

  public void broadcastMessage(String message) {
      for (User user : players) {
          user.sendMessage(message);
      }
  }

  public void playFireworksEffect(User user) {
	    if (user == null || user.getPlayer() == null) {
	    	return;
	    }

	    int durationSeconds = 60;
	    int ticksPerSecond = 10; // 20 ticks per second

	    List<Location> playerSpawnPoints = this.playerSpawnPoints;
	    int size = playerSpawnPoints.size();

	    Random random = new Random();

	    Firework userFirework = user.getPlayer().getWorld().spawn(user.getPlayer().getLocation(), Firework.class);
	    FireworkMeta userMeta = userFirework.getFireworkMeta();

	    Color[] userColors = new Color[random.nextInt(4) + 1]; 
	    for (int j = 0; j < userColors.length; j++) {
	        userColors[j] = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	    }

	    userMeta.addEffect(FireworkEffect.builder()
	            .flicker(false)
	            .trail(true)
	            .with(Type.BURST)
	            .withColor(userColors)
	            .build());

	    userMeta.setPower(1);
	    userFirework.setFireworkMeta(userMeta);
	    BukkitRunnable fireworksTask = new BukkitRunnable() {
	        int i = 0;

	        @Override
	        public void run() {
	            if (i >= size) {
	                i = 0; 
	            }

	            Location spawnLocation = playerSpawnPoints.get(i++);
	            Firework firework = spawnLocation.getWorld().spawn(spawnLocation, Firework.class);
	            FireworkMeta meta = firework.getFireworkMeta();

	            Color[] colors = new Color[random.nextInt(4) + 1];
	            for (int j = 0; j < colors.length; j++) {
	                colors[j] = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	            }

	            meta.addEffect(FireworkEffect.builder()
	                    .flicker(false)
	                    .trail(true)
	                    .with(Type.BURST)
	                    .withColor(colors)
	                    .build());

	            meta.setPower(2);
	            firework.setFireworkMeta(meta);
	        }
	    };

	    BukkitTask task = fireworksTask.runTaskTimer(plugin, 0L, ticksPerSecond);
	    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
	        task.cancel();
	        userFirework.detonate();
	    }, durationSeconds * ticksPerSecond);
	}

  public void broadcastCenteredMessage(String message) {
      for (User user : players) {
          MiscUtils.sendCenteredMessage(user.getPlayer(), message);
      }
  }

  public String getId() {
    return this.id;
  }

  @Override
public String toString() {
    return this.id;
  }

  public enum GameLocation {
    LOBBY, END;
  }
}