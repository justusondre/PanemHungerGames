package game.arena;

import org.bukkit.plugin.java.JavaPlugin;

import game.Main;

public enum ArenaOption {
	TIMER(45), MINIMUM_PLAYERS(12), MAXIMUM_PLAYERS(26),
	LOBBY_WAITING_TIME("Time-Settings.Lobby-Waiting-Time", 15),
	LOBBY_STARTING_TIME("Time-Settings.Lobby-Starting-Time", 15),
	LOBBY_ENDING_TIME("Time-Settings.Ending-Time", 10),
	PREGAME_TIME("Time-Settings.Pregame-Time", 15),
	GAMEPLAY_TIME("Time-Settings.Default-Gameplay-Time", 10800),
	GAME_ENDING_TIME("Time-Settings.Ending-Time", 30),
	GAME_RESTARTING_TIME("Time-Settings.Restarting-Time", 10);

	int integerValue;

	boolean booleanValue;

	ArenaOption(int defaultValue) {
		this.integerValue = defaultValue;
	}

	ArenaOption(String path, boolean defaultValue) {
		Main plugin = JavaPlugin.getPlugin(Main.class);
		this.booleanValue = plugin.getConfig().getBoolean(path, defaultValue);
	}

	ArenaOption(String path, int defaultValue) {
		Main plugin = JavaPlugin.getPlugin(Main.class);
		int value = plugin.getConfig().getInt(path, defaultValue);
		this.integerValue = (value < 0) ? defaultValue : value;
	}

	public boolean getBooleanValue() {
		return this.booleanValue;
	}

	public int getIntegerValue() {
		return this.integerValue;
	}
}
