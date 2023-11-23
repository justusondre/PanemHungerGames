package game;

import java.io.File;
import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import commandframework.CommandFramework;
import game.arena.Arena;
import game.arena.ArenaManager;
import game.arena.ArenaRegistry;
import game.commands.AbstractCommand;
import game.events.GameEvents;
import game.events.GamemakerEvents;
import game.events.JoinQuitEvents;
import game.events.OnThrowItem;
import game.events.SpectateEvents;
import game.throwables.ThrowableList;
import game.user.User;
import game.user.UserManager;

public class Main extends JavaPlugin {

	private UpdateTask update;
	private UserManager userManager;
	private CommandFramework commandFramework;
	private ArenaRegistry arenaRegistry;
	private ArenaManager arenaManager;
    private ThrowableList itemLists;


	@Override
	public void onEnable() {
		initializeClasses();
		this.update = new UpdateTask(this);

	}

	@Override
	public void onDisable() {
	    if (this.update != null) {
	        this.update.cancel();
		    this.update = null;
	    }

		for (Arena arena : this.arenaRegistry.getArenas()) {
			arena.getGameBar().removeAll();
			for (User user : arena.getPlayers()) {
				Player player = user.getPlayer();
				arena.teleportToEndLocation(user);
				player.setFlySpeed(0.1F);
				player.setWalkSpeed(0.2F);
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
				player.setInvisible(false);
				arena.getGameBar().doBarAction(user, 0);
			}
			arena.cleanUpArena();
		}
	}

	private void registerListeners() {
        Listener[] listeners = {
                new GameEvents(this),
                new JoinQuitEvents(this),
                new GamemakerEvents(this),
                new SpectateEvents(this),
                new OnThrowItem(this),
        };

        PluginManager pluginManager = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

	private void initializeClasses() {
		registerListeners();
		setupConfigurationFiles();
		this.userManager = new UserManager(this);
		this.commandFramework = new CommandFramework(this);
		this.arenaRegistry = new ArenaRegistry(this);
		this.arenaManager = new ArenaManager(this);
		this.itemLists = new ThrowableList();
		AbstractCommand.registerCommands(this);
		User.cooldownHandlerTask();
	}

	private void setupConfigurationFiles() {
		saveDefaultConfig();
		Stream.<String>of(
				new String[] { "arena", "stats", "mysql", "rewards"})
				.filter(fileName -> !(new File(getDataFolder(), fileName + ".yml")).exists())
				.forEach(fileName -> saveResource(fileName + ".yml", false));
	}

	public UserManager getUserManager() {
		return this.userManager;
	}

	public CommandFramework getCommandFramework() {
		return this.commandFramework;
	}

	public ArenaRegistry getArenaRegistry() {
		return this.arenaRegistry;
	}

	public ArenaManager getArenaManager() {
		return this.arenaManager;
	}

	public UpdateTask getUpdateTask() {
	    return this.update;
	}
}