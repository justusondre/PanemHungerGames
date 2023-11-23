package game.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import game.Main;
import game.arena.Arena;
import game.utility.ItemFactory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class User {

	private static final Main plugin = JavaPlugin.getPlugin(Main.class);
	private static long cooldownCounter = 0L;
	private final UUID uuid;
	private final Map<String, Double> cooldowns;
	private Player player;
	private boolean spectator;
	private int kills;
	private long deathTimestamp;

	public User(UUID uuid) {
		this.uuid = uuid;
		this.cooldowns = new HashMap<>();
		this.kills = 0;
		this.deathTimestamp = 0L;
		setPlayer();
	}

	public int getKills() {
		return this.kills;
	}

	public void addKill() {
		this.kills++;
	}

	public void resetKills() {
		this.kills = 0;
	}

	public void setDeathTimestamp(long timestamp) {
        this.deathTimestamp = timestamp;
    }

    public long getDeathTimestamp() {
        return this.deathTimestamp;
    }

	public void setPlayer() {
		this.player = plugin.getServer().getPlayer(this.uuid);
	}

	public void closeOpenedInventory() {
		getPlayer().closeInventory();
	}

	public boolean isInArena() {
		return plugin.getArenaRegistry().isInArena(this);
	}

	public Arena getArena() {
		return plugin.getArenaRegistry().getArena(this);
	}

	public boolean isSpectator() {
		return this.spectator;
	}

	public void setSpectator(boolean spectator) {
		this.spectator = spectator;
	}

	public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

	public Location getLocation() {
		return getPlayer().getLocation();
	}

	public Player getPlayer() {
		if (this.player == null) {
			this.player = plugin.getServer().getPlayer(this.uuid);
			return this.player;
		}
		return this.player;
	}

	public String getName() {
		return getPlayer().getName();
	}

	public UUID getUniqueId() {
		return this.uuid;
	}

	public boolean hasPermission(String permission) {
		Player player = getPlayer();
		return (player.isOp() || player.hasPermission(permission));
	}

	public void setCooldown(String s, double seconds) {
		this.cooldowns.put(s, Double.valueOf(seconds + cooldownCounter));
	}

	public double getCooldown(String s) {
		Double cooldown = this.cooldowns.get(s);
		return (cooldown == null || cooldown.doubleValue() <= cooldownCounter) ? 0.0D
				: (cooldown.doubleValue() - cooldownCounter);
	}

	public void removePotionEffectsExcept(PotionEffectType... effectTypes) {
		Set<PotionEffectType> setOfEffects = Set.of(effectTypes);
		for (PotionEffect activePotion : getPlayer().getActivePotionEffects()) {
			if (setOfEffects.contains(activePotion.getType()))
				continue;
			this.player.removePotionEffect(activePotion.getType());
		}
	}

	public void clearPlayerScoreboard(Player player) {
	    Scoreboard emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	    player.setScoreboard(emptyScoreboard);
	}

	public void sendActionBar(String message) {
		getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));

	}

	public void enableSpectateMode(Player player) {
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setInvisible(true);
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.getInventory().clear();
		// player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
		// Integer.MAX_VALUE, Integer.MAX_VALUE, false));
		player.getInventory().setItem(1, ItemFactory.createItem(Material.CLOCK,
				ChatColor.translateAlternateColorCodes('&', "&b&lTeleporter &7(Right Click)")));

		if (player.isOp()) {
			player.getInventory().setItem(0, ItemFactory.createItem(Material.REPEATER,
					ChatColor.translateAlternateColorCodes('&', "&c&lGamemaker Options")));
		}

		player.teleport(player.getLocation().add(0D, 5.0D, 0D));

		for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
			if (!onlinePlayers.equals(player)) {
				onlinePlayers.hidePlayer(plugin, player);
			}
		}
	}
	

	public void setPlayerCollidable(Player player, boolean collidable) {
        player.setCollidable(collidable);
    }

	public void makePlayerNonCollidable(Player player) {
        setPlayerCollidable(player, false);
    }

    public void makePlayerCollidable(Player player) {
        setPlayerCollidable(player, true);
    }

	public void heal() {
		this.player.setHealth(20.0);
		this.player.setFoodLevel(20);
	}

	@Override
	public boolean equals(Object obj) {
		User other;
		if (obj instanceof User) {
			other = (User) obj;
		} else {
			return false;
		}
		return other.getUniqueId().equals(this.uuid);
	}

	@Override
	public String toString() {
		return "name=%s, uuid=%s".formatted(new Object[] { this.player.getName(), this.uuid });
	}

	public static void cooldownHandlerTask() {
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> cooldownCounter++, 20L,
				20L);
	}

	public Scoreboard getScoreboard() {
	    return getPlayer().getScoreboard();
	}
}