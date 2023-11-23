package game.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import game.gui.GamemakerGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import commandframework.Command;
import commandframework.CommandArguments;
import commandframework.Completer;
import game.Main;
import game.arena.Arena;
import game.arena.ArenaGameState;
import game.arena.ArenaOption;
import game.arena.ArenaState;
import game.events.GamemakerEvents;
import game.user.User;
import game.utility.ConfigUtils;
import game.utility.LocationSerializer;
import game.utility.MiscUtils;
import net.md_5.bungee.api.ChatColor;

public class AdminCommands extends AbstractCommand {

	public AdminCommands(Main plugin) {
		super(plugin);
	}

	@Command(name = "panem", usage = "/panem help", desc = "Main command for the Game Setup")
	public void mmCommand(CommandArguments arguments) {
		if (arguments.isArgumentsEmpty()) {
			arguments.sendMessage(ChatColor.YELLOW + "This server is running the Tales of Panem plugin V1.20.2");
			if (arguments.hasPermission("panem.admin"));
		}
	}

	@Command(name = "panem.create", permission = "panem.admin.create", desc = "Create an arena with default configuration", usage = "/panem create <arena name>", senderType = Command.SenderType.PLAYER)
	public void createArena(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (this.plugin.getArenaRegistry().isInArena(user)) {
			user.sendMessage(ChatColor.RED + "You cannot do that command whilist in game!");
			return;
		}

		if (arguments.isArgumentsEmpty()) {
			user.sendMessage(ChatColor.RED + "Please provide an arena name!");
			return;
		}

		String arenaId = arguments.getArgument(0);
		if (this.plugin.getArenaRegistry().isArena(arenaId)) {
			user.sendMessage(ChatColor.RED + "There already is an arena with that name!");
			return;
		}

		String path = "instance.%s.".formatted(new Object[] { arenaId });
		Arena arena = new Arena(arenaId);
		FileConfiguration config = ConfigUtils.getConfig(this.plugin, "arena");
		this.plugin.getArenaRegistry().registerArena(arena);
		config.set(path + "mapName", arenaId);
		config.set(path + "minimumPlayers", Integer.valueOf(4));
		config.set(path + "maximumPlayers", Integer.valueOf(26));
		config.set(path + "gameplayTime", Integer.valueOf(ArenaOption.GAMEPLAY_TIME.getIntegerValue()));
		config.set(path + "ready", Boolean.valueOf(false));
		config.set(path + "lobbyLocation", LocationSerializer.SERIALIZED_LOCATION);
		config.set(path + "endLocation", LocationSerializer.SERIALIZED_LOCATION);
		config.set(path + "parkourEndLocation", LocationSerializer.SERIALIZED_LOCATION);
		config.set(path + "playerSpawnPoints", new ArrayList());
		config.set(path + "signs", new ArrayList());
		ConfigUtils.saveConfig(this.plugin, config, "arena");
		Player player = user.getPlayer();
		user.sendMessage("");
		MiscUtils.sendCenteredMessage(player, ChatColor.GRAY + "You have created the arena: " + ChatColor.AQUA + arenaId);
		user.sendMessage("");
	}

	@Command(name = "panem.setarenalobby", permission = "panem.admin.create", desc = "Create an arena with default configuration", usage = "/setarenalobby", senderType = Command.SenderType.PLAYER)
	public void setArenaLobby(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (this.plugin.getArenaRegistry().isInArena(user)) {
			user.sendMessage(ChatColor.RED + "You cannot do that in-game!");
			return;
		}

		if (arguments.isArgumentsEmpty()) {
			user.sendMessage(ChatColor.RED + "Please provide an arena name!");
			return;
		}

		String arenaId = arguments.getArgument(0);

		if (this.plugin.getArenaRegistry().isArena(arenaId)) {
			Arena arena = new Arena(arenaId);
			FileConfiguration config = ConfigUtils.getConfig(this.plugin, "arena");
			String path = "instance.%s.".formatted(new Object[] { arenaId });
			Location location = user.getLocation();
			config.set(path + "lobbyLocation", LocationSerializer.toString(location));
			ConfigUtils.saveConfig(this.plugin, config, "arena");
			arena.setLobbyLocation(location);
			user.getPlayer().sendMessage(ChatColor.GRAY + "You have added the lobby location for: " + ChatColor.AQUA + "" + arenaId);
			return;
		}
	}

	@Command(name = "panem.addplayerspawn", permission = "panem.admin.create", desc = "Create an arena with default configuration", usage = "/addplayerspawn", senderType = Command.SenderType.PLAYER)
	public void addPlayerSpawn(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (this.plugin.getArenaRegistry().isInArena(user)) {
			user.sendMessage(ChatColor.RED + "You cannot do that in-game!");
			return;
		}

		if (arguments.isArgumentsEmpty()) {
			user.sendMessage(ChatColor.RED + "Please provide an arena name!");
			return;
		}

		String arenaId = arguments.getArgument(0);

		if (this.plugin.getArenaRegistry().isArena(arenaId)) {
		    Arena arena = new Arena(arenaId);
		    FileConfiguration config = ConfigUtils.getConfig(this.plugin, "arena");
		    String path = "instance.%s.".formatted(new Object[] { arenaId });

		    Location location = user.getLocation();

		    List<String> spawnPoints = config.getStringList(path + "playerSpawnPoints");
		    spawnPoints.add(LocationSerializer.toString(location));

		    config.set(path + "playerSpawnPoints", spawnPoints);

		    ConfigUtils.saveConfig(this.plugin, config, "arena");

		    ArrayList<Location> spawns = new ArrayList<>(arena.getPlayerSpawnPoints());
            spawns.add(location);
            arena.setPlayerSpawnPoints(spawns);

            ConfigUtils.saveConfig(this.plugin, config, "arena");		    user.getPlayer().sendMessage(ChatColor.GRAY + "You have added a player spawnpoint for: " + ChatColor.AQUA + "" + arenaId);
		    return;
		}
	}

	@Command(name = "panem.setendlocation", permission = "panem.admin.create", desc = "Create an arena with default configuration", usage = "/setarenalobby", senderType = Command.SenderType.PLAYER)
	public void setArenaEndLocation(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (this.plugin.getArenaRegistry().isInArena(user)) {
			user.sendMessage(ChatColor.RED + "You cannot do that in-game!");
			return;
		}

		if (arguments.isArgumentsEmpty()) {
			user.sendMessage(ChatColor.RED + "Please provide an arena name!");
			return;
		}

		String arenaId = arguments.getArgument(0);

		if (this.plugin.getArenaRegistry().isArena(arenaId)) {
			Arena arena = new Arena(arenaId);
			FileConfiguration config = ConfigUtils.getConfig(this.plugin, "arena");
			String path = "instance.%s.".formatted(new Object[] { arenaId });
			Location location = user.getLocation();
			config.set(path + "endLocation", LocationSerializer.toString(location));
			ConfigUtils.saveConfig(this.plugin, config, "arena");
			arena.setEndLocation(location);
			user.getPlayer().sendMessage(ChatColor.GRAY + "You have added the end location for: " + ChatColor.AQUA + "" + arenaId);
			return;
		}
	}

	@Command(name = "panem.toggleready", permission = "panem.admin.create", desc = "Create an arena with default configuration", usage = "/setarenalobby", senderType = Command.SenderType.PLAYER)
	public void setReady(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (this.plugin.getArenaRegistry().isInArena(user)) {
			user.sendMessage(ChatColor.RED + "You cannot do that in-game!");
			return;
		}

		if (arguments.isArgumentsEmpty()) {
			user.sendMessage(ChatColor.RED + "Please provide an arena name!");
			return;
		}

		String arenaId = arguments.getArgument(0);

		if (this.plugin.getArenaRegistry().isArena(arenaId)) {
	        Arena arena = new Arena(arenaId);
	        FileConfiguration config = ConfigUtils.getConfig(this.plugin, "arena");
	        String path = "instance.%s.".formatted(new Object[] { arenaId });

	        boolean isReady = !config.getBoolean(path + "ready");
	        config.set(path + "ready", isReady);
	        ConfigUtils.saveConfig(this.plugin, config, "arena");
	        arena.setReady(isReady);
			arena.setArenaState(ArenaState.WAITING_FOR_PLAYERS);
			arena.setMapName(config.getString(path + "mapName"));
			arena.setMinimumPlayers(config.getInt(path + "minimumPlayers"));
			arena.setMaximumPlayers(config.getInt(path + "maximumPlayers"));
			arena.setGameplayTime(config.getInt(path + "gameplayTime"));
			arena.setLobbyLocation(LocationSerializer.fromString(config.getString(path + "lobbyLocation")));
			arena.setEndLocation(LocationSerializer.fromString(config.getString(path + "endLocation")));
			arena.setPlayerSpawnPoints(config.getStringList(path + "playerSpawnPoints").stream().map(LocationSerializer::fromString).collect(Collectors.toList()));
	        ConfigUtils.saveConfig(this.plugin, config, "arena");
			arena.start();

	        String status = isReady ? ChatColor.GREEN + "READY" : ChatColor.RED + "NOT READY";
	        user.getPlayer().sendMessage(ChatColor.GRAY + "The arena status for " + ChatColor.AQUA + arenaId + ChatColor.GRAY +
	        		" was toggled! " + "(" + status + ChatColor.GRAY + ")");
	        return;
	    }
	}

	@Command(name = "panem.delete", permission = "panem.admin.delete", desc = "Delete specified arena and its data", usage = "/panem delete <arena name>", senderType = Command.SenderType.PLAYER)
	public void mmDeleteCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (this.plugin.getArenaRegistry().isInArena(user)) {
			user.sendMessage(ChatColor.RED + "You cannot do that in-game!");
			return;
		}
		if (arguments.isArgumentsEmpty()) {
			user.sendMessage(ChatColor.RED + "Please provide an arena name!");
			return;
		}
		String arenaId = arguments.getArgument(0);
		if (!this.plugin.getArenaRegistry().isArena(arenaId)) {
			user.sendMessage(ChatColor.RED + "There is no arena found with that name!");
			return;
		}
		Arena arena = this.plugin.getArenaRegistry().getArena(arenaId);
		arena.stop();
		FileConfiguration config = ConfigUtils.getConfig(this.plugin, "arena");
		config.set("instance." + arenaId, null);
		ConfigUtils.saveConfig(this.plugin, config, "arena");
		this.plugin.getArenaRegistry().unregisterArena(arena);
	}

	@Command(name = "panem.list", permission = "panem.admin.list", desc = "Get a list of registered arenas and their status", usage = "/panem list", senderType = Command.SenderType.PLAYER)
	public void mmListCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		Set<Arena> arenas = this.plugin.getArenaRegistry().getArenas();
		if (arenas.isEmpty()) {
			user.sendMessage(ChatColor.RED + "There are no arenas listed!");
			return;
		}
		String list = arenas.stream().map(Arena::getId).collect(Collectors.joining(", "));
		arguments.sendMessage(("admin-commands.list-command.format").replace("%list%", list));
	}

	@Command(name = "panem.forcestart", permission = "panem.admin.forcestart", desc = "Forces arena to start without waiting time", usage = "/panem forcestart", senderType = Command.SenderType.PLAYER)
	public void mmForceStartCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (!user.isInArena()) {
			user.sendMessage(ChatColor.RED + "You must be in an arena to use that command!");
			return;
		}
		Arena arena = user.getArena();
		if (arena.getPlayers().size() < 2) {
			arena.broadcastMessage(ChatColor.RED + "Waiting for more players!");
			return;
		}
		if (arena.isForceStart()) {
			user.sendMessage(ChatColor.RED + "The games have already started!");
			return;
		}
		if (arena.isArenaState(new ArenaState[] { ArenaState.WAITING_FOR_PLAYERS, ArenaState.STARTING })) {
			arena.setArenaState(ArenaState.STARTING);
			arena.setForceStart(true);
			arena.setTimer(0);
			arena.getPlayers().forEach(u -> u.sendMessage(ChatColor.AQUA + "You have force started the arena!"));
		}
	}

	@Command(name = "panem.stop", permission = "panem.admin.stop", desc = "Stop the arena that you're in", usage = "/panem stop", senderType = Command.SenderType.PLAYER)
	public void stopCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		Arena arena = user.getArena();
		if (arena == null) {
			user.sendMessage(ChatColor.RED + "You must be in an arena to use that command!");
			return;
		}
		if (arena.getArenaState() != ArenaState.ENDING)
			this.plugin.getArenaManager().stopGame(false, arena);
	}

	@Command(name = "gamemaker", permission = "panem.admin.stop", desc = "Stop the arena that you're in", usage = "/panem stop", senderType = Command.SenderType.PLAYER)
    public void gamemakerCommand(CommandArguments arguments) {
        User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
        Arena arena = user.getArena();

        GamemakerEvents events = new GamemakerEvents(plugin);
        events.openGamemakerOptionsGUI(user);

        if (arena == null) {
            user.sendMessage(ChatColor.RED + "You must be in an arena to use that command!");
            return;
        }

        if (arena.getArenaState() != ArenaState.IN_GAME) {
            user.sendMessage(ChatColor.RED + "The game has to be started in order to use this command!");
            return;
        }

        if (arena.getArenaGameState() != ArenaGameState.INACTIVE) {
            user.sendMessage(ChatColor.RED + "There already is an event taking place! Please wait.");
            return;
        }

    }

	@Completer(name = "panem")
	public List<String> onTabComplete(CommandArguments arguments) {
		List<String> completions = new ArrayList<>(), commands = this.plugin.getCommandFramework().getCommands().stream().map(cmd -> cmd.name().replace(arguments.getLabel() + ".", "")).collect(Collectors.toList());
		String args[] = arguments.getArguments(), arg = args[0];
		commands.remove("panem");

		if (args.length == 1) {
			StringUtil.copyPartialMatches(arg,(arguments.hasPermission("panem.admin") || arguments.getSender().isOp()) ? commands: List.<String>of("top", "stats", "join", "leave", "randomjoin"), completions);

		}

		if (args.length == 2) {
			if (List.<String>of("create", "list", "randomjoin", "leave").contains(arg)) {
				return null;
			}

			if (arg.equalsIgnoreCase("panem")) {
				return List.of("wins", "loses", "kills", "deaths", "highest_score", "games_played");
			}

			if (arg.equalsIgnoreCase("stats")) {
				return this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

		}
			List<String> arenas = this.plugin.getArenaRegistry().getArenas().stream().map(Arena::getId).collect(Collectors.toList());
			StringUtil.copyPartialMatches(args[1], arenas, completions);
			arenas.sort((Comparator<? super String>) null);
			return arenas;
		}
		completions.sort((Comparator<? super String>) null);
		return completions;
	}

	@Command(name = "panem.test", permission = "panem.admin.test", desc = "test command for adminds", usage = "/panem stop", senderType = Command.SenderType.PLAYER)
	public void testCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());		

		if (arguments.isArgumentsEmpty()) {
			GamemakerGUI gamemakerGUI = new GamemakerGUI();
			gamemakerGUI.openGameMakerMenu(user.getPlayer());
			return;
		}
		
			if(arguments.getArgument(0).equalsIgnoreCase("off")) {
				Bukkit.broadcastMessage("stopping event");

			}
	}
}
