package game.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import game.Main;
import game.arena.Arena;
import game.arena.ArenaGameState;
import game.gamemaker.AcidRainEvent;
import game.gamemaker.MeteorShowerEvent;
import game.gamemaker.PoisonGasEvent;
import game.user.User;
import game.utility.ItemFactory;
import net.md_5.bungee.api.ChatColor;

public class GamemakerEvents implements Listener {

	private final Main plugin;

	public GamemakerEvents(Main plugin) {
		this.plugin = plugin;
	}

	public Main plugin() {
		return this.plugin;
	}
	
	public void openGamemakerOptionsGUI(User user) {
        Inventory gui = Bukkit.createInventory(user.getPlayer(), 54, "Gamemaker Options");
        gui.setItem(11, ItemFactory.createItem(Material.POTION, 1, ChatColor.AQUA + "Acid Rain", ChatColor.GRAY + "This will release acidic rain into the arena!"));
        gui.setItem(12, ItemFactory.createItem(Material.GOLDEN_APPLE, 1, ChatColor.AQUA + "Hardcore Mode", ChatColor.GRAY + "Enable hardcore mode!"));
        gui.setItem(13, ItemFactory.createItem(Material.FIRE_CHARGE, 1, ChatColor.AQUA + "Meteor Shower", ChatColor.GRAY + "Meteor showers will spawn across the map!"));
        gui.setItem(14, ItemFactory.createItem(Material.PACKED_ICE, 1, ChatColor.AQUA + "Freeze Arena", ChatColor.GRAY + "Deep freeze the arena!"));
        gui.setItem(15, ItemFactory.createItem(Material.SKELETON_SKULL, 1, ChatColor.AQUA + "Poisonous Gas", ChatColor.GRAY + "Leak a poisonous gas into the arena!"));

        gui.setItem(20, ItemFactory.createItem(Material.BELL, 1, ChatColor.AQUA + "The Fallen", ChatColor.GRAY + "Display the fallen tributes!"));
        gui.setItem(21, ItemFactory.createItem(Material.BARREL, 1, ChatColor.AQUA + "Feast Event", ChatColor.GRAY + "Spawn a feast event!"));
        gui.setItem(22, ItemFactory.createItem(Material.BARRIER, 1, ChatColor.AQUA + "Border Settings", ChatColor.GRAY + "Change the border settings"));
        gui.setItem(23, ItemFactory.createItem(Material.GLOWSTONE_DUST, 1, ChatColor.AQUA + "Time Settings", ChatColor.GRAY + "Change the time settings!"));
        gui.setItem(24, ItemFactory.createItem(Material.WOLF_SPAWN_EGG, 1, ChatColor.AQUA + "Spawn Mutts", ChatColor.GRAY + "Spawn mutts across the arena!"));

        gui.setItem(40, ItemFactory.createItem(Material.RED_WOOL, 1, ChatColor.RED + "End Gamemaker Events", ChatColor.GRAY + "Stop any ongoing gamemaker events!"));

        user.getPlayer().openInventory(gui);
    }

	@EventHandler
    public void onInventoryClickWithTitle(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Spectate a Tribute")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        User user = this.plugin.getUserManager().getUser(player);

        if (item != null && item.getType() == Material.REPEATER && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Gamemaker Options")) {
        	user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 0.5F);
        	openGamemakerOptionsGUI(user);
        }
    }

	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
        User user = this.plugin.getUserManager().getUser(player);
        Arena arena = user.getArena();

        if (event.getView().getTitle().equals("Gamemaker Options")) {
            event.setCancelled(true);

            if (event.getRawSlot() == 11 && user.isInArena() && arena.getArenaGameState() == ArenaGameState.INACTIVE) {
               AcidRainEvent acidRain = new AcidRainEvent(JavaPlugin.getPlugin(Main.class), arena, user.getPlayer().getWorld(), 30);
               acidRain.startEvent();
               event.getWhoClicked().closeInventory();
            }

            if (event.getRawSlot() == 13 && user.isInArena() && arena.getArenaGameState() == ArenaGameState.INACTIVE) {
            	MeteorShowerEvent meteor = new MeteorShowerEvent(JavaPlugin.getPlugin(Main.class), arena, 30);
                meteor.startEvent();
                event.getWhoClicked().closeInventory();
             }
            
            if (event.getRawSlot() == 15 && user.isInArena() && arena.getArenaGameState() == ArenaGameState.INACTIVE) {
            	Location centerLocation = new Location(user.getPlayer().getWorld(), -3890, -8, -3943);
                PoisonGasEvent poison = new PoisonGasEvent(JavaPlugin.getPlugin(Main.class), arena, user.getPlayer().getWorld(), centerLocation, 150, 99999*99999);
                poison.startPoisonEvent();
                event.getWhoClicked().closeInventory();
             }
            
            if (event.getRawSlot() == 40 && user.isInArena() && arena.getArenaGameState() != ArenaGameState.INACTIVE && arena.getArenaGameState() != ArenaGameState.BLOODBATH) {
            	arena.setArenaGameState(ArenaGameState.INACTIVE);
            	
            	for(User users : arena.getAllPlayers()) {
            		users.sendMessage(ChatColor.RED + "The gamemaker has stopped the event, you are safe for now!");
            	}
            	
                event.getWhoClicked().closeInventory();
             }
        }
    }
}
