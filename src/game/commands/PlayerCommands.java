package game.commands;

import java.util.List;

import org.bukkit.entity.Player;

import commandframework.Command;
import commandframework.CommandArguments;
import game.Main;
import game.arena.Arena;
import game.arena.ArenaState;
import game.user.User;
import net.md_5.bungee.api.ChatColor;

public class PlayerCommands extends AbstractCommand {
	public PlayerCommands(Main plugin) {
		super(plugin);
	}

	@Command(name = "panem.join", senderType = Command.SenderType.PLAYER)
	public void joinCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (arguments.isArgumentsEmpty()) {
			user.sendMessage(ChatColor.RED + "Please specify an arena name!");
			return;
		}
		Arena arena = this.plugin.getArenaRegistry().getArena(arguments.getArgument(0));
		if (arena == null) {
			user.sendMessage(ChatColor.RED + "There is not an arena with that name!");
			return;
		}
		this.plugin.getArenaManager().joinAttempt(user, arena);
	}

	@Command(name = "panem.spectate", senderType = Command.SenderType.PLAYER)
	public void spectateCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		if (arguments.isArgumentsEmpty()) {
			user.sendMessage(ChatColor.RED + "Please specify an arena name!");
			return;
		}
		Arena arena = this.plugin.getArenaRegistry().getArena(arguments.getArgument(0));
		if (arena == null) {
			user.sendMessage(ChatColor.RED + "There is not an arena with that name!");
			return;
		}
		this.plugin.getArenaManager().joinAsSpectatorAttempt(user, arena);
	}

	@Command(name = "panem.randomjoin", senderType = Command.SenderType.PLAYER)
	public void randomJoinCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		List<Arena> arenas = this.plugin.getArenaRegistry().getArenas().stream().filter(
				arena -> (arena.isArenaState(new ArenaState[] { ArenaState.WAITING_FOR_PLAYERS, ArenaState.STARTING })
						&& arena.getPlayers().size() < arena.getMaximumPlayers()))
				.toList();
		if (!arenas.isEmpty()) {
			Arena arena = arenas.get(0);
			this.plugin.getArenaManager().joinAttempt(user, arena);
			return;
		}
		user.sendMessage(ChatColor.RED + "There are no available arenas at the moment. Try again soon!");
	}

	@Command(name = "panem.leave", senderType = Command.SenderType.PLAYER)
	public void mmLeaveCommand(CommandArguments arguments) {
		User user = this.plugin.getUserManager().getUser((Player) arguments.getSender());
		Arena arena = user.getArena();
		if (arena == null) {
			user.sendMessage(ChatColor.RED + "That arena is null!");
			return;
		}
		this.plugin.getArenaManager().leaveAttempt(user, arena);
	}
}
