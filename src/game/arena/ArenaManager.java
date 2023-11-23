package game.arena;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import game.GameIdGenerator;
import game.Main;
import game.scoreboard.ScoreboardStatus;
import game.user.User;

public final class ArenaManager {

	private final Main plugin;

    String formattedDate = new SimpleDateFormat("dd/MM/YY").format(new Date());
    String id = GameIdGenerator.generateUniqueArenaId();

	public ArenaManager(Main plugin) {
		this.plugin = plugin;
	}

	public Main plugin() {
		return this.plugin;
	}

	public void joinAttempt(User user, Arena arena) {
		if (!arena.isReady()) {
			user.sendMessage(ChatColor.RED + "The arena has not been configured yet!");
			return;
		}

		if (user.isInArena()) {
			user.sendMessage(ChatColor.RED + "You are already in an arena!");
			return;
		}

		if (arena.getArenaState() == ArenaState.RESTARTING) {
			user.sendMessage(ChatColor.RED + "The arena is restarting!");
			return;
		}
		
		if (arena.getArenaState() == ArenaState.PREGAME || arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.RESTARTING || 
				arena.getArenaState() == ArenaState.ENDING) {
			user.sendMessage(ChatColor.RED + "The game has already begun, use /panem spectate Capitol");
			return;
		}

		Player player = user.getPlayer();

		ArenaUtils.updateNameTagsVisibility(user);
		arena.addUser(user);
		arena.teleportToLobby(user);
		
		ScoreboardStatus status = new ScoreboardStatus(user);
		Objective objective = status.getObjective();
		startGlitchingTitleAnimation(objective);
	    arena.getStatus().put(user.getUniqueId(), status);
	    
	    arena.getGameBar().doBarAction(user, 1);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setFoodLevel(20);
		player.getInventory().clear();
		player.getInventory().setHeldItemSlot(0);
		player.getInventory().setArmorContents(null);
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(false);
		player.setGlowing(false);
		player.setInvisible(false);
		user.heal();
		user.removePotionEffectsExcept(new PotionEffectType[0]);
		user.makePlayerNonCollidable(player);
		arena.broadcastMessage(ChatColor.AQUA + user.getPlayer().getDisplayName() + ChatColor.GRAY + " has joined the fight!");

	}

	public void leaveAttempt(User user, Arena arena) {
		Player player = user.getPlayer();
		arena.broadcastMessage(ChatColor.AQUA + user.getPlayer().getDisplayName() + ChatColor.GRAY + " has left the game!");
		arena.removeUser(user);
		arena.removeSpectator(user);
		user.setSpectator(false);
		arena.removeDeadPlayer(user);
		arena.teleportToEndLocation(user);
		arena.getStatus().clear();
		arena.getGameBar().doBarAction(user, 0);
		user.clearPlayerScoreboard(player);
		user.heal();
		user.setSpectator(false);
		user.removePotionEffectsExcept(new PotionEffectType[0]);
		user.makePlayerCollidable(player);

		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setFlySpeed(0.1F);
		player.setWalkSpeed(0.2F);
		player.setFireTicks(0);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().setHeldItemSlot(0);
		player.setInvisible(false);

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
	        if (!onlinePlayer.equals(player)) {
	            onlinePlayer.showPlayer(plugin, player);
	        }
	    }

		if (arena.getArenaState() == ArenaState.IN_GAME && arena.getPlayersLeftWithoutSpectators().size() <= 1) {
			stopGame(false, arena);
		}
	}

	public void joinAsSpectatorAttempt(User user, Arena arena) {
		if (!arena.isReady()) {
			user.sendMessage(ChatColor.RED + "The arena has not been configured yet!");
			return;
		}

		if (user.isInArena()) {
			user.sendMessage(ChatColor.RED + "You are already in an arena!");
			return;
		}

		if (arena.getArenaState() == ArenaState.RESTARTING) {
			user.sendMessage(ChatColor.RED + "The arena is restarting!");
			return;
		}

		Player player = user.getPlayer();
		player.setInvisible(true);
		arena.addUser(user);
		arena.addSpectator(user);
		arena.hideSpectator(user);
		user.setSpectator(true);
		arena.teleportToStartLocation(user);
		user.enableSpectateMode(player);
		ScoreboardStatus status = new ScoreboardStatus(user);
		Objective objective = status.getObjective();
		startGlitchingTitleAnimation(objective);
	    arena.getStatus().put(user.getUniqueId(), status);
		user.sendMessage(ChatColor.RED + "You are now spectating the Hunger Games!");
		user.makePlayerNonCollidable(player);
	}

	public void stopGame(boolean quickStop, Arena arena) {
		arena.setArenaState(ArenaState.ENDING);
		arena.setTimer(quickStop ? 2 : ArenaOption.LOBBY_ENDING_TIME.getIntegerValue());
		arena.showPlayers();

		for (User user : arena.getPlayers()) {
			user.removePotionEffectsExcept(new PotionEffectType[] { PotionEffectType.BLINDNESS });
		}

		if (quickStop)
			return;
	}

	public Scoreboard createScoreboard() {
        return org.bukkit.Bukkit.getScoreboardManager().getNewScoreboard();

    }

    public void updateTitle(User user, Scoreboard scoreboard) {
    	ScoreboardStatus scoreboardStatus = new ScoreboardStatus(user);
		Objective objective = scoreboardStatus.getObjective();
	    startGlitchingTitleAnimation(objective);
    }

	public void updateScoreboardObjectives(Arena arena, ScoreboardStatus scoreboard) {
		User user = scoreboard.getPlayer();

		if ((arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) && !user.getArena().isSpectator(user)) {

			scoreboard.updateLine(8, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "ѕᴛᴀᴛᴜѕ: " + ChatColor.GRAY + "" + (arena.getArenaState().name.toString()));
			scoreboard.updateLine(7, "");
			scoreboard.updateLine(6, "ᴍᴀᴘ: " + ChatColor.GREEN + "ᴄᴀᴘɪᴛᴏʟ ᴀʀᴇɴᴀ");
			scoreboard.updateLine(5, "ᴛʀɪʙᴜᴛᴇѕ: " + ChatColor.GREEN + arena.getPlayersLeftWithoutSpectators().size());
			scoreboard.updateLine(4, "");
			scoreboard.updateLine(3, "ѕᴛᴀʀᴛɪɴɢ ɪɴ " + ChatColor.GREEN + arena.getTimer() + "ѕ");
			scoreboard.updateLine(2, "");
			scoreboard.updateLine(1, ChatColor.AQUA + "ᴍᴄ.ᴛᴀʟᴇѕᴏꜰᴘᴀɴᴇᴍ.ɴᴇᴛ");
		}

		if(arena.getArenaState() == ArenaState.PREGAME && !user.getArena().isSpectator(user)) {
			scoreboard.updateLine(8, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "ѕᴛᴀᴛᴜѕ: " + ChatColor.GRAY + "" + (arena.getArenaState().name.toString()));
			scoreboard.updateLine(7, "");
			scoreboard.updateLine(6, "ᴇᴠᴇɴᴛ: " + ChatColor.RED + (arena.getArenaGameState().name.toString()));
			scoreboard.updateLine(5, "ᴛʀɪʙᴜᴛᴇѕ: " + ChatColor.GREEN + arena.getPlayersLeftWithoutSpectators().size());
			scoreboard.updateLine(4, "");
			scoreboard.updateLine(3, "ѕᴛᴀʀᴛɪɴɢ ɪɴ " + ChatColor.GREEN + (arena.getTimer()+1) + "ѕ");
			scoreboard.updateLine(2, "");
			scoreboard.updateLine(1, ChatColor.AQUA + "ᴍᴄ.ᴛᴀʟᴇѕᴏꜰᴘᴀɴᴇᴍ.ɴᴇᴛ");

		}

		if((arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING)  && !user.getArena().isSpectator(user)) {
			scoreboard.updateLine(12, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "ѕᴛᴀᴛᴜѕ: " + ChatColor.GRAY + "" + (arena.getArenaState().name.toString()));
			scoreboard.updateLine(11, "");
			scoreboard.updateLine(10, ChatColor.WHITE + "ᴇᴠᴇɴᴛ: " + ChatColor.RED + (arena.getArenaGameState().name.toString()));
			scoreboard.updateLine(9, "ᴛʀɪʙᴜᴛᴇѕ: " + ChatColor.GREEN + arena.getPlayersLeftWithoutSpectators().size());
			scoreboard.updateLine(8, "");
			scoreboard.updateLine(7, ChatColor.RED + "❤ " + ChatColor.WHITE + "ʜᴇᴀʟᴛʜ: " + ChatColor.RED + "" + Math.round(user.getPlayer().getHealth()));
			scoreboard.updateLine(6, ChatColor.AQUA + "🗡 " + ChatColor.WHITE + "ᴋɪʟʟs: " + ChatColor.GREEN + "" + user.getKills());
			scoreboard.updateLine(5, "");
			scoreboard.updateLine(4, ChatColor.GOLD + "☀ " + ChatColor.WHITE + "ᴄᴜʀʀᴇɴᴛ ᴅᴀʏ: " + ChatColor.GREEN + arena.getDay());
			scoreboard.updateLine(3, ChatColor.YELLOW + "🕒 " + ChatColor.WHITE + "ᴛɪᴍᴇ: " + ChatColor.GREEN + arena.timeSurvived(user));
			scoreboard.updateLine(2, "");
			scoreboard.updateLine(1, ChatColor.AQUA + "ᴍᴄ.ᴛᴀʟᴇѕᴏꜰᴘᴀɴᴇᴍ.ɴᴇᴛ");
		}

		if (user.getArena().isSpectator(user)) {
			scoreboard.updateLine(8, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "ѕᴛᴀᴛᴜѕ: " + ChatColor.GRAY + "" + (arena.getArenaState().name.toString()));
			scoreboard.updateLine(7, "");
			scoreboard.updateLine(6, "ᴇᴠᴇɴᴛ: " + ChatColor.RED + (arena.getArenaGameState().name.toString()));
			scoreboard.updateLine(5, "ᴛʀɪʙᴜᴛᴇѕ: " + ChatColor.GREEN + arena.getPlayersLeftWithoutSpectators().size());
			scoreboard.updateLine(4, "");
			scoreboard.updateLine(3, ChatColor.GREEN + "ʏᴏᴜ ᴀʀᴇ ᴀ sᴘᴇᴄᴛᴀᴛᴏʀ");
			scoreboard.updateLine(2, "");
			scoreboard.updateLine(1, ChatColor.AQUA + "ᴍᴄ.ᴛᴀʟᴇѕᴏꜰᴘᴀɴᴇᴍ.ɴᴇᴛ");
		}
	}

    public void clearScoreboard(Player player) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
			public void run() {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

            }
        }, 0L);
    }

    public void startGlitchingTitleAnimation(Objective objective) {
        BukkitRunnable animationTask = new BukkitRunnable() {
            int tick = 0;
            boolean isGlitching = false;

            @Override
            public void run() {
                tick++;

                if (tick % 200 == 0) {
                    isGlitching = true;
                }

                if (isGlitching) {
                    if (tick % 20 >= 0 && tick % 20 < 10) { // Glitch for 1 second (20 ticks)
                        if (tick % 2 == 0) { // Switch every 2 ticks
                            objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + " ᴛᴀʟᴇѕ ᴏꜰ ᴘᴀɴᴇᴍ ");
                        } else {
                            objective.setDisplayName(ChatColor.RED + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + " ᴛᴀʟᴇѕ ᴏꜰ ᴘᴀɴᴇᴍ ");
                        }
                    } else {
                        objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + " ᴛᴀʟᴇѕ ᴏꜰ ᴘᴀɴᴇᴍ ");
                        isGlitching = false;
                    }
                } else {
                    objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + " ᴛᴀʟᴇѕ ᴏꜰ ᴘᴀɴᴇᴍ ");
                }
            }
        };
        animationTask.runTaskTimer(this.plugin, 0L, 1L); // Run every tick
    }
}
