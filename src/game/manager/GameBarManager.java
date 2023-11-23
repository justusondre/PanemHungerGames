package game.manager;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.Main;
import game.arena.Arena;
import game.user.User;
import net.md_5.bungee.api.ChatColor;

public class GameBarManager {

	private BossBar gameBar;
	private final Arena arena;

	private final Main plugin;

	public Main plugin() {
		return this.plugin;
	}

	public GameBarManager(Arena arena, Main plugin) {
		this.arena = arena;
		this.plugin = plugin;
		this.gameBar = plugin.getServer().createBossBar("", BarColor.BLUE, BarStyle.SOLID, new org.bukkit.boss.BarFlag[0]);
	}

	public void doBarAction(User user, int action) {
		Player player = user.getPlayer();
		if (action == 1) {
			this.gameBar.addPlayer(player);
		} else {
			this.gameBar.removePlayer(player);
		}
	}

	public void removeAll() {
		if (this.gameBar != null) {
			this.gameBar.removeAll();
		}
	}

	public void handleGameBar() {
		if (this.gameBar == null) {
			return;
		}

		switch (this.arena.getArenaState()) {
		case WAITING_FOR_PLAYERS:
			//this.gameBar.setTitle(ChatColor.AQUA + "" + ChatColor.AQUA + FontConverter.convertFont("YOU ARE PLAYING TALES OF PANEM"));
			break;
		case STARTING:
			break;
		case PREGAME:
			this.gameBar.setTitle("");
			break;
		case IN_GAME:
			this.gameBar.setTitle("");
			break;
		case ENDING:
			this.gameBar.setTitle("");
			break;
		case RESTARTING:
			this.gameBar.setTitle("");
			break;
		case INACTIVE:
			this.gameBar.setTitle("");
			break;
		default:
			break;
		}
		this.gameBar.setVisible(!this.gameBar.getTitle().isEmpty());
	}

	public void startGlitchingTitleAnimation() {
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
                        	gameBar.setTitle(ChatColor.WHITE + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + " ᴛᴀʟᴇѕ ᴏꜰ ᴘᴀɴᴇᴍ ");
                        } else {
                        	gameBar.setTitle(ChatColor.RED + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + " ᴛᴀʟᴇѕ ᴏꜰ ᴘᴀɴᴇᴍ ");
                        }
                    } else {
                    	gameBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + " ᴛᴀʟᴇѕ ᴏꜰ ᴘᴀɴᴇᴍ ");
                        isGlitching = false;
                    }
                } else {
                	gameBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + " ᴛᴀʟᴇѕ ᴏꜰ ᴘᴀɴᴇᴍ ");
                }
            }
        };
        animationTask.runTaskTimer(this.plugin, 0L, 1L); // Run every tick
    }
}
