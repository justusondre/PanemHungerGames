package game;

import org.bukkit.scheduler.BukkitRunnable;

import game.arena.Arena;
import game.arena.ArenaState;

public class UpdateTask extends BukkitRunnable {
	private Main main;

	private long ticks = 0L;

	public UpdateTask(Main main) {
		this.main = main;
		runTaskTimer(main, 1L, 1L);

	}

	@Override
	public void run() {
		for (Arena a : this.main.getArenaRegistry().getArenas()) {
			try {
			    if (a.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || this.ticks % 20L == 0L) {
		            a.getStatus().values().forEach(s -> this.main.getArenaManager().updateScoreboardObjectives(a, s));
			    }

			} catch (Exception e) {
				a.stop();
			    e.printStackTrace();
			}
			this.ticks++;
		}
	}
}