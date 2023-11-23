package game.gamemaker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.plugin.java.JavaPlugin;

import game.Main;
import game.arena.Arena;
import game.arena.ArenaGameState;
import game.manager.CountdownManager;
import game.user.User;
import game.utility.ActionBar;
import game.utility.NumberUtils;

public class MeteorShowerEvent {

	private final Main plugin;
    private final int duration;
    private Arena arena;

    public MeteorShowerEvent(Main plugin, Arena arena, int duration) {
        this.duration = duration;
		this.plugin = plugin;
		this.arena = arena;
    }

    public Main plugin() {
		return this.plugin;
	}

	public void startEvent() {
        arena.setArenaGameState(ArenaGameState.METEOR_SHOWER);
		Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 0.3F));
		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Meteors are now falling from the sky.");

		new CountdownManager(duration, JavaPlugin.getPlugin(Main.class), 5, 1) {

			@Override
			public void count(int current) {
				arena.getPlayersLeftWithoutSpectators().forEach(o -> o.getPlayer().setExp(current / (float) (duration)));
				arena.getPlayersLeftWithoutSpectators().forEach(o -> ActionBar.sendActionBar(o.getPlayer(), ChatColor.RED + "Take shelter and avoid the falling meteors."));

				if (current == 0) {
					arena.setArenaGameState(ArenaGameState.INACTIVE);
					Bukkit.getOnlinePlayers().forEach(o -> ActionBar.sendActionBar(o.getPlayer(), ChatColor.GREEN + "The event has ended, you are safe now."));
					Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F));
				}

				if (current <= 60 && current >= 0) {
					for (User user : arena.getPlayersLeftWithoutSpectators()) {
						if (NumberUtils.randomRange(1, 100) > 80)
							return;

						for (int i = 0; i < NumberUtils.randomRange(1, 5); i++) {
							Location toSpawn;
							int x = user.getLocation().getBlockX() + NumberUtils.randomRange(-10, 10);
							int z = user.getLocation().getBlockZ() + NumberUtils.randomRange(-10, 10);
							int y = user.getLocation().getBlockY() + NumberUtils.randomRange(25, 40);
							toSpawn = new Location(user.getPlayer().getWorld(), x, y, z);
							Fireball fireball = toSpawn.getWorld().spawn(toSpawn, Fireball.class);
							fireball.setDirection(fireball.getDirection().setY(-0.5).multiply(0.7));

						}
					}
				}
			}
		}.start();
	}
}