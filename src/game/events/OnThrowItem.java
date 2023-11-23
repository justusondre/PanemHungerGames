package game.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import game.throwables.ThrowableList;

public class OnThrowItem implements Listener {
	
    public static List<ArmorStand> armorStands = new ArrayList<>();
	
    private final long THROWABLE_LIFE_TIME = 140L;
    private final float HITBOX_REACH = 1.7f;
    private final long COBWEB_BASE_TIME = 60L;
    private final long COBWEB_RANDOM_TIME = 40;

    Plugin plugin;

    public OnThrowItem(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    (ignoreCancelled = false)
    public void InteractEvent(PlayerInteractEvent event) {
        if (event.hasItem()) {
            if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                Material type = event.getMaterial();
                //event.getItem().setAmount(event.getItem().getAmount() - 1);
                if (type == Material.POPPED_CHORUS_FRUIT || ThrowableList.linearThrowable.contains(type) || ThrowableList.spinningThrowable.contains(type))  {
                    Player player = event.getPlayer();
                    ItemStack heldItem = event.getItem();
                    ItemStack thrownItem = event.getItem().clone();

                    if(heldItem.getAmount() == 1) {
                        event.setCancelled(true);
                        player.setItemInHand(new ItemStack(Material.AIR));
                    }
                    else {
                        heldItem.setAmount(heldItem.getAmount() - 1);
                    }

                    summonThrowingArmourStand(player, new ItemStack(thrownItem));
                    player.updateInventory();
                }
            }
        }
    }

    private void summonThrowingArmourStand(Player player, ItemStack thrownItem) {
        ItemStack item = new ItemStack(thrownItem);
        Material material = item.getType();
        Vector dir = player.getEyeLocation().getDirection();
        float pitch = player.getLocation().getPitch();
        ArmorStand armorStand;
        Location location = player.getLocation().add(dir);
            // We have to duplicate code inside of constructors, otherwise the effects will be applied a tick later, which may cause troubles.
        if(material == Material.POPPED_CHORUS_FRUIT || material == Material.COBWEB) {
            armorStand = player.getWorld().spawn(location, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setHeadPose(new EulerAngle((pitch / 60) + -89.8, 0, 0));
                stand.setHelmet(item);
                stand.setCanPickupItems(false);
                stand.setGravity(false);
            });
        } else if (material == Material.NETHERITE_INGOT || material == Material.STONE_AXE) {
            armorStand = player.getWorld().spawn(location, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setHeadPose(new EulerAngle((pitch / 45) -45, 0, 0));
                stand.setHelmet(item);
                stand.setCanPickupItems(false);
                stand.setGravity(false);
            });
        } else if (material == Material.NETHERITE_AXE) {
            armorStand = player.getWorld().spawn(location, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setHeadPose(new EulerAngle(0, 24.9, 0));
                stand.setHelmet(item);
                stand.setCanPickupItems(false);
                stand.setGravity(false);
            });
        } else if(ThrowableList.linearThrowable.contains(material))  {
            armorStand = player.getWorld().spawn(location, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setHeadPose(new EulerAngle((pitch / 60) + 3.26, 6.3, 0.001 ));
                stand.setHelmet(item);
                stand.setCanPickupItems(false);
                stand.setGravity(false);
            });
        } else if (ThrowableList.spinningThrowable.contains(material)){
            armorStand = player.getWorld().spawn(location, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setHeadPose(new EulerAngle(-24.7, 0 , 0));
                stand.setHelmet(item);
                stand.setCanPickupItems(false);
                stand.setGravity(false);
            });
        } else {
            return;
        }

        armorStands.add(armorStand);
        throwItem(armorStand, player, material, pitch, item, dir);
    }

    private void throwItem(ArmorStand armorStand, Player player, Material material, float pitch, ItemStack item, Vector dir) {
        final long MAX_TIME = THROWABLE_LIFE_TIME;
        final long[] time = {0L};
        new BukkitRunnable() {
            @Override
            public void run() {

                if(time[0] >= MAX_TIME) {
                    armorStand.getWorld().dropItem(armorStand.getLocation().add(0,1,0), new ItemStack(item));
                    armorStand.remove();
                    cancel();
                }
                if(material == Material.POPPED_CHORUS_FRUIT || material == Material.COBWEB) {
                    armorStand.setHeadPose(new EulerAngle((pitch / 60) + -89.8, (float) time[0] / 2, 0));
                } else if (material == Material.NETHERITE_INGOT) {
                    armorStand.setHeadPose(new EulerAngle((pitch / 57) - 45, 0, 0));
                } else if(material == Material.NETHERITE_AXE || material == Material.STONE_AXE) {
                    armorStand.setHeadPose(new EulerAngle((float) time[0] / 2, 24.9, 0)); //((float) time[0] / 2, 24.9, 0));
                } else if(ThrowableList.linearThrowable.contains(item)) {
                    armorStand.setHeadPose(new EulerAngle((pitch / 60) + 3.26, 6.3, 0.001 ));
                } else if (ThrowableList.spinningThrowable.contains(item)){
                    armorStand.setHeadPose(new EulerAngle(-24.7,(float) time[0] / 2, 0));
                }

                Vector velocity = new Vector(dir.getX() * 1.33f, dir.getY() * 1.33f, dir.getZ() * 1.33f);
                armorStand.teleport(armorStand.getLocation().add(velocity).add(new Vector(0, -0.08f * ((float) time[0] / 5), 0))); // Movement

                if (!armorStand.getLocation().add(0, 1, 0).getBlock().getType().isSolid()) {

                    for (Entity entity : armorStand.getNearbyEntities(HITBOX_REACH,HITBOX_REACH,HITBOX_REACH)) {
                        if(entity == player) continue; // If the item hits the thrower.
                        if (entity.getLocation().distance(armorStand.getLocation().add(0,1,0)) <= HITBOX_REACH) {
                            if(entity instanceof Item || entity instanceof Projectile || entity instanceof ArmorStand) continue; // We avoid collision with items or projectiles as it's not realistic for them to block our weapon
                            if (entity instanceof Player) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 0);
                            }

                            if(material == Material.COBWEB) setCobwebs(armorStand.getLocation().add(0,1,0).add(new Vector(velocity.getX(), 0, velocity.getZ())));
                            HitEntity(player, (LivingEntity) entity, armorStand, velocity, item);
                            armorStand.remove();
                            cancel();
                            break;
                        }
                    }

                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 0);
                    item.setAmount(1);
                    armorStand.getWorld().dropItem(armorStand.getLocation().add(-velocity.getX(),1,-velocity.getZ()), item);
                    armorStands.remove(armorStand);
                    armorStand.remove();
                    cancel();
                }
                time[0] += 1L;
            }

        }.runTaskTimer(plugin, 0L, 0L);
    }

    private void HitEntity(Player sender, LivingEntity damaged, ArmorStand armorStand, Vector velocity, ItemStack item) {
        item.setAmount(1);
        sender.playSound(sender.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 0);
        damaged.damage(getThrowingItemDamage(item.getType()));
        armorStands.remove(armorStand);
        Item droppedItem = armorStand.getWorld().dropItem(armorStand.getLocation().add(-velocity.getX(),1,-velocity.getZ()), item);
        droppedItem.setOwner(sender.getUniqueId());
        removeItemOwnership(droppedItem);
    }

    public float getThrowingItemDamage(Material item) {
        if(item == Material.GOLDEN_PICKAXE) return 7;
        if(ThrowableList.tier1.contains(item)) return 4;
        else if(ThrowableList.tier2.contains(item)) return 5;
        else if(ThrowableList.tier3.contains(item) || ThrowableList.tier4.contains(item)) return 6;
        else if(ThrowableList.tier5.contains(item)) return 7.5f;
        return  4;
    }

    public void setCobwebs(Location loc) {
        for(int z = -1; z < 2; z++) {
            for(int x = -1; x < 2; x++) {
                Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY(), loc.getZ() + z);
                Material type = newLoc.getBlock().getType();
                if (type == Material.AIR || type == Material.GRASS || type == Material.TALL_GRASS) {
                    newLoc.getBlock().setType(Material.COBWEB);
                    long randomTime = (long) (COBWEB_BASE_TIME + Math.random() * (COBWEB_RANDOM_TIME));
                    deleteCobweb(newLoc, randomTime);
                }
            }
        }
    }

    public void deleteCobweb(Location loc, long delay) {
        new BukkitRunnable() {
            @Override
            public void run () {
                if(loc.getBlock().getType() == Material.COBWEB) loc.getBlock().setType(Material.AIR);
            }
        }.runTaskLater(plugin, delay);
    }

    public void removeItemOwnership(Item item) {
        new BukkitRunnable() {
            @Override
            public void run () {
                if(item != null) item.setOwner(null);
            }
        }.runTaskLater(plugin, 15 * 20L);
    }
    
    @EventHandler
    public void onEntityDamage (EntityInteractEvent event) {
        if(event.getEntity().getType() == EntityType.ARMOR_STAND) {
            ArmorStand armorStand = (ArmorStand) event.getEntity();
            if(armorStands.contains(armorStand)) {
                event.setCancelled(true);
            }
        }
    }
}
