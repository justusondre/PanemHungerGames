package game.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import game.Main;
import game.arena.ArenaState;
import game.user.User;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class SpectateEvents implements Listener {

	private final Main plugin;

    public SpectateEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        User user = this.plugin.getUserManager().getUser(event.getPlayer());

        if (user.isSpectator() && user.getArena().getArenaState() != ArenaState.ENDING) {
            event.setCancelled(true);

            String message = event.getMessage();

            for (User spectator : user.getArena().getSpectators()) {
                spectator.getPlayer().sendMessage(ChatColor.GRAY + "[Spectator] " + user.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + message);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        User user = this.plugin.getUserManager().getUser(event.getPlayer());
        
        if (user.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
            Player player = (Player) event.getEntity();
            User user = this.plugin.getUserManager().getUser(player);

            if (user.isSpectator()) {
                event.setCancelled(true);
            
        }
    }
    
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        User user = this.plugin.getUserManager().getUser(event.getPlayer());

        if (user.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        User user = this.plugin.getUserManager().getUser(event.getPlayer());

        if (user.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        User user = this.plugin.getUserManager().getUser(event.getPlayer());

        if (user.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = this.plugin.getUserManager().getUser(player);

            if (user.isSpectator()) {
                event.setCancelled(true);
            }
        }
    }

    private void openSpectatorMenu(User user) {
        Inventory gui = Bukkit.createInventory(user.getPlayer(), 9 * 6, "Spectate a Tribute");

        List<Integer> fillSlots = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);

        List<User> aliveTributes = new ArrayList<>(user.getArena().getPlayersLeftWithoutSpectators());

        int slot = 0;
        for (User targetUser : aliveTributes) {
            if (slot >= fillSlots.size()) {
                break;
            }

            int fillSlot = fillSlots.get(slot);

            ItemStack tributeHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) tributeHead.getItemMeta();
            headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(targetUser.getPlayer().getName()));
            headMeta.setDisplayName(ChatColor.RED + targetUser.getPlayer().getName());
            tributeHead.setItemMeta(headMeta);

            gui.setItem(fillSlot, tributeHead);
            slot++;
        }

        user.getPlayer().openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Spectate a Tribute")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleporterInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        User user = this.plugin.getUserManager().getUser(player);

        if (item != null && item.getType() == Material.CLOCK && item.hasItemMeta() && item.getItemMeta().getDisplayName().
        		equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Teleporter " + ChatColor.GRAY + "(Right Click)")) {
            openSpectatorMenu(user);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        User user = this.plugin.getUserManager().getUser(player);

        if (user.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHeadClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Spectate a Tribute") && event.getCurrentItem() != null) {
            if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                Player player = (Player) event.getWhoClicked();
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
                String playerName = ChatColor.stripColor(displayName);
                Player target = Bukkit.getPlayerExact(playerName);
                if (target != null) {
                    player.teleport(target);
                    player.closeInventory();
                }
            }
        }
    }
}