package game.events;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import game.Main;
import game.arena.Arena;
import game.arena.ArenaState;
import game.user.User;
import net.md_5.bungee.api.ChatColor;

public class JoinQuitEvents implements Listener {

private final Main plugin;

	public JoinQuitEvents(Main plugin) {
		this.plugin = plugin;
	}

	public Main plugin() {
		return this.plugin;
	}

	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event) {
		Player eventPlayer = event.getPlayer();
		for (User targetUser : this.plugin.getUserManager().getUsers()) {
			if (!targetUser.isInArena())
				continue;

			Player player = targetUser.getPlayer();
			eventPlayer.hidePlayer(this.plugin, player);
			player.hidePlayer(this.plugin, eventPlayer);
		}
	}

	@EventHandler
	public void onQuitEvent(PlayerQuitEvent event) {
		handleQuitEvent(event.getPlayer());
	}

	@EventHandler
	public void onKickEvent(PlayerKickEvent event) {
		handleQuitEvent(event.getPlayer());
	}

	private void handleQuitEvent(Player player) {
		User user = this.plugin.getUserManager().getUser(player);
		Arena arena = user.getArena();

		if (user.isInArena() && !user.isSpectator()) {
			this.plugin.getArenaManager().leaveAttempt(user, arena);
			arena.removeDeadPlayer(user);
			arena.removeSpectator(user);
			arena.removeUser(user);
		}

		if (user.isInArena() && !user.isSpectator() && arena.getArenaState() == ArenaState.IN_GAME) {
			this.plugin.getArenaManager().leaveAttempt(user, arena);
			arena.removeSpectator(user);
			arena.removeUser(user);
			arena.addDeadPlayer(user);

			for (User online : arena.getAllPlayers()) {
				online.getPlayer().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1F);
				online.getPlayer().sendMessage(ChatColor.GRAY + "A cannon could be heard in the distance.");
			}
		}

		if(user.isSpectator()) {
			this.plugin.getArenaManager().leaveAttempt(user, arena);

		}
		this.plugin.getUserManager().removeUser(player);
	}
}
