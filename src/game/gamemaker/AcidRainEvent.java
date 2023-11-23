package game.gamemaker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import game.Main;
import game.arena.Arena;
import game.arena.ArenaGameState;
import game.manager.CountdownManager;
import game.user.User;
import game.utility.ActionBar;

public class AcidRainEvent {

	private final Main plugin;
    private final World world;
    private final int duration;
    private Arena arena;

    public AcidRainEvent(Main plugin, Arena arena, World world, int duration) {
        this.world = world;
        this.duration = duration;
		this.plugin = plugin;
		this.arena = arena;
    }

    public Main plugin() {
		return this.plugin;
	}

	public void startEvent() {
		world.setStorm(true);

        arena.setArenaGameState(ArenaGameState.ACID_RAIN);
		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.ITALIC + "The rain begins to feel acidic across the arena.");
		new CountdownManager(duration, JavaPlugin.getPlugin(Main.class), 5, 1) {

			@Override
			public void count(int current) {
				arena.getPlayersLeftWithoutSpectators().forEach(o -> o.getPlayer().setExp(current / (float) (duration)));
				arena.getPlayersLeftWithoutSpectators().forEach(o -> ActionBar.sendActionBar(o.getPlayer(), ChatColor.RED + "Take shelter and avoid the acidic rain."));
		        applyAcidRainEffect();

				if (current == 0) {
					arena.setArenaGameState(ArenaGameState.INACTIVE);
					world.setStorm(false);

					Bukkit.getOnlinePlayers().forEach(o -> ActionBar.sendActionBar(o.getPlayer(), ChatColor.GREEN + "The event has ended, you are safe now."));
					Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F));

				}
			}
		}.start();
	}

    private void applyAcidRainEffect() {
        for (User  player : this.arena.getPlayersLeftWithoutSpectators()) {
            Location playerLocation = player.getLocation();
            if (world.hasStorm() && world.getHighestBlockYAt(playerLocation) <= playerLocation.getBlockY()) {
                player.getPlayer().damage(1.0);
                playerLocation.getWorld().playEffect(playerLocation, Effect.SMOKE, 1);
            }
        }
    }
}