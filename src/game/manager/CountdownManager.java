package game.manager;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import game.Main;

public abstract class CountdownManager {

    private int time;
    private int delay;
    private long secondsInBetweenWaves;
    protected BukkitTask task;
    protected final Plugin main;

    public CountdownManager(int time, Plugin main, int delay, long secondsInBetweenWaves) {
        this.time = time;
        this.delay = delay;
        this.secondsInBetweenWaves = secondsInBetweenWaves;
        this.main = main;

    }

    public abstract void count(int current);

    public final CountdownManager start() {
        task = new BukkitRunnable() {

            @Override
            public void run() {
                count(time);
                if (time-- <= 0) task.cancel();
            }

        }.runTaskTimer(JavaPlugin.getPlugin(Main.class), 20*delay, secondsInBetweenWaves);
		return null;
    }

    public void stopTimer() {
        if (task != null) {
            task.cancel();
        }
    }
}