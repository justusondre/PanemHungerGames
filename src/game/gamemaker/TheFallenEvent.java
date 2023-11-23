package game.gamemaker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import game.Main;
import game.arena.Arena;
import game.arena.ArenaGameState;
import game.manager.CountdownManager;
import game.manager.GameBarManager;

public class TheFallenEvent {

	private final Main plugin;
    private final World world;
    private final int duration;
    private Arena arena;

    public TheFallenEvent(Main plugin, Arena arena, World world, int duration) {
        this.world = world;
        this.duration = duration;
		this.plugin = plugin;
		this.arena = arena;
    }

    public Main plugin() {
		return this.plugin;
	}

	public void startEvent() {
        arena.setArenaGameState(ArenaGameState.THE_FALLEN);
		arena.getAllPlayers().forEach(o -> o.getPlayer().playSound(o.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 1F, 1F));
		GameBarManager gameBar = new GameBarManager(arena, plugin);

		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Please take a moment to recognize the fallen tributes.");
		new CountdownManager(duration, JavaPlugin.getPlugin(Main.class), 5, 1) {

			@Override
			public void count(int current) {

				if(current == 30) {

				}

				if (current == 0) {
					arena.setArenaGameState(ArenaGameState.INACTIVE);
					world.setStorm(false);
				}
			}
		}.start();
	}
}