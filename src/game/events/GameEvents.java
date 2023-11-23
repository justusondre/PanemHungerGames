package game.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import game.Main;
import game.arena.Arena;
import game.arena.ArenaState;
import game.user.User;
import game.utility.MiscUtils;
import game.utility.NumberUtils;
import net.md_5.bungee.api.ChatColor;

public class GameEvents implements Listener {

	private final Main plugin;

	public GameEvents(Main plugin) {
		this.plugin = plugin;
	}

	public Main plugin() {
		return this.plugin;
	}

	@EventHandler
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
	    HumanEntity humanEntity = event.getEntity();

	    if (humanEntity instanceof Player) {
	        Player player = (Player) humanEntity;
	        User user = this.plugin.getUserManager().getUser(player);

	        if (user != null && user.isInArena()) {
	            Arena arena = user.getArena();

	            if (arena != null) {
	                ArenaState arenaState = arena.getArenaState();

	                if (arenaState == ArenaState.WAITING_FOR_PLAYERS || arenaState == ArenaState.STARTING || arenaState == ArenaState.PREGAME) {
	                    event.setCancelled(true);
	                }
	            }
	        }
	    }
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		User user = this.plugin.getUserManager().getUser(event.getPlayer());
		Arena arena = user.getArena();

		if (user.isInArena() && arena.getArenaState() == ArenaState.PREGAME) {
			event.setCancelled(true);
			return;
		}

		if (user.isSpectator()) {
            event.setCancelled(true);

            if (event.getClickedBlock() != null) {
                Material blockType = event.getClickedBlock().getType();

                if (isContainer(blockType)) {
                    event.setCancelled(true);
                }
            }
        }
	}

    private boolean isContainer(Material material) {
        return material == Material.CHEST || material == Material.SHULKER_BOX || material == Material.ENDER_CHEST;
    }

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {

		User user = this.plugin.getUserManager().getUser(event.getEntity().getPlayer());
		Arena arena = user.getArena();

		if (user.isInArena() && arena.getArenaState() == ArenaState.IN_GAME && !user.isSpectator()) {

			user.setSpectator(true);
			user.enableSpectateMode(user.getPlayer());
            user.setDeathTimestamp(NumberUtils.convertTimeStringToSeconds(user.getArena().timeSurvived(user)));

			event.setDeathMessage(ChatColor.GRAY + "A cannon could be heard in the distance.");
			event.setDroppedExp(0);

	        MiscUtils.sendCenteredMessage(user.getPlayer(), ChatColor.GRAY + " ");
	        MiscUtils.sendCenteredMessage(user.getPlayer(), ChatColor.GRAY + "────── « ɢᴀᴍᴇ sᴛᴀᴛs » ──────");
	        MiscUtils.sendCenteredMessage(user.getPlayer(), ChatColor.GRAY + " ");
	        MiscUtils.sendCenteredMessage(user.getPlayer(), ChatColor.WHITE + "ᴛᴏᴛᴀʟ ᴋɪʟʟs: " + ChatColor.AQUA + user.getKills());
	        MiscUtils.sendCenteredMessage(user.getPlayer(), ChatColor.WHITE + "ᴛɪᴍᴇ sᴜʀᴠɪᴠᴇᴅ: " + ChatColor.AQUA + "(Day " + arena.getDay() + ") " + NumberUtils.convertSecondsToTimeFormat(user.getDeathTimestamp()));
	        MiscUtils.sendCenteredMessage(user.getPlayer(), ChatColor.GRAY + " ");
	        MiscUtils.sendCenteredMessage(user.getPlayer(), ChatColor.GRAY + "────── « ɢᴀᴍᴇ sᴛᴀᴛs » ──────");
	        MiscUtils.sendCenteredMessage(user.getPlayer(), ChatColor.GRAY + " ");

			Player player = event.getEntity();

			for (User online : arena.getAllPlayers()) {
				online.getPlayer().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1F);

			}
		}
	}

	@EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {

		if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player killedPlayer = event.getEntity();

        if (killedPlayer.getKiller() instanceof Player) {
            Player killer = killedPlayer.getKiller();

            User killerUser = plugin.getUserManager().getUser(killer);
            User killedUser = plugin.getUserManager().getUser(killedPlayer);

            if (killerUser != null && killedUser != null && killerUser.isInArena() && killedUser.isInArena()) {
                killerUser.addKill();
            }
        }
    }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		User user = this.plugin.getUserManager().getUser(event.getPlayer());

		if (user.isInArena()) {
			event.setRespawnLocation(user.getPlayer().getLocation().add(0D, 3.0D, 0D));
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		HumanEntity humanEntity = event.getPlayer();
		if (humanEntity instanceof Player) {
			Player player = (Player) humanEntity;
			User user = this.plugin.getUserManager().getUser(player);
			if (user.isInArena()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		HumanEntity humanEntity = event.getPlayer();
		if (humanEntity instanceof Player) {
			Player player = (Player) humanEntity;
			User user = this.plugin.getUserManager().getUser(player);
			
			if (user.isInArena()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		User user = this.plugin.getUserManager().getUser(event.getPlayer());

		if (user != null && user.getArena() != null) {
			Arena arena = user.getArena();

			for (User alive : arena.getPlayersLeftWithoutSpectators()) {
				if (arena.getArenaState() == ArenaState.PREGAME) {
					if ((int) event.getFrom().getX() != (int) event.getTo().getX()
							|| (int) event.getFrom().getY() != (int) event.getTo().getY()
							|| (int) event.getFrom().getZ() != (int) event.getTo().getZ()) {
						if (((event.getTo().getX() != event.getFrom().getX())
								|| (event.getTo().getZ() != event.getFrom().getZ()))) {
							event.setTo(event.getFrom());

							Block b = null;
							Location loc = alive.getLocation();
							for (int y = (int) (loc.getBlockY() - 0.5); y > 0; y--) {
								b = loc.getWorld().getBlockAt(loc.add(0, -0.5, 0));
								if (b.getType() == Material.AIR) {
									break;
								}
							}
						}
					}
				}
			}
		}
	}
}