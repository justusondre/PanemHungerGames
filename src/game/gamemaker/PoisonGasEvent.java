package game.gamemaker;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Main;
import game.arena.Arena;
import game.arena.ArenaGameState;
import game.manager.CountdownManager;
import game.user.User;

public class PoisonGasEvent {
	
	private final Main plugin;
	private CountdownManager countdownManager;
	private final World world;
    private final Location location;
    private final int maxRadius;
    private final int duration; 
    private int currentRadius;
    private int particleCount; 
    private Arena arena;

    public PoisonGasEvent(Main plugin, Arena arena, World world, Location location, int maxRadius, int duration) {
        this.world = world;
        this.location = location;
        this.maxRadius = maxRadius;
        this.duration = duration;
        this.currentRadius = 1;
        this.particleCount = 35; 
        this.arena = arena;
        this.plugin = plugin;
    }
    
    public Main plugin() {
		return this.plugin;
	}

    public void startPoisonEvent() {
    	arena.setArenaGameState(ArenaGameState.POISON_GAS);
    	arena.broadcastMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Poison has begun to spread in the tunnels, get to the surface!");
    	
    	for(User user : arena.getPlayersLeftWithoutSpectators()) { 
    		user.getPlayer().playSound(user.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.5F, 2.0F);
    	}
    	
        this.countdownManager = new CountdownManager(duration, JavaPlugin.getPlugin(Main.class), 3, 7) {

            @Override
            public void count(int current) {

                if (currentRadius <= maxRadius) {
                    currentRadius++;

                    particleCount += Math.ceil(particleCount * 0.05);
                    playPoisonEffect(location, currentRadius, particleCount);

                } else {
                    playPoisonEffect(location, currentRadius, 5000);
                }
                
                if(arena.getArenaGameState() == ArenaGameState.INACTIVE) {
                	countdownManager.stopTimer();
                }

                if (current == 0) {
                	arena.broadcastMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "The event has ended, you are safe now!");
                	
                	for(User user : arena.getPlayersLeftWithoutSpectators()) { 
                		user.getPlayer().playSound(user.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.5F, 2.0F);
                	}
                	arena.setArenaGameState(ArenaGameState.INACTIVE);
                }
            }
        };
        countdownManager.start();
    }

    private void playPoisonEffect(Location centerLocation, int radius, int particleCount) {
    	
    	for(User user : arena.getPlayersLeftWithoutSpectators()) {
    		user.getPlayer().playSound(user.getLocation(), Sound.AMBIENT_BASALT_DELTAS_ADDITIONS, 1.5F, 2.0F);
    	}
    	
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 16) {
            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;
            Location soundLocation = centerLocation.clone().add(xOffset, 0, zOffset);
            world.playSound(soundLocation, Sound.BLOCK_POWDER_SNOW_STEP, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }

        for (int i = 0; i < particleCount; i++) {
            double theta = Math.random() * Math.PI;
            double phi = Math.random() * 2 * Math.PI;
            double r = Math.pow(Math.random(), 1.0 / 3.0) * radius;

            double x = r * Math.sin(theta) * Math.cos(phi);
            double y = Math.random() * 5; // Random y between 0 and 5
            double z = r * Math.cos(theta);

            Location particleLocation = centerLocation.clone().add(x, y, z);
            world.spawnParticle(Particle.SNEEZE, particleLocation, 1, 0, 0, 0, 0);
            world.spawnParticle(Particle.SNEEZE, particleLocation, 1, 0, 0, 0, 0);
        }

    	for(User user : arena.getPlayersLeftWithoutSpectators()) {
            Location playerLocation = user.getLocation();

            if (playerLocation.distance(centerLocation) <= radius && playerLocation.getY() <= (location.getBlockY() + 6)) {
            	user.getPlayer().damage(1.0);

                if (!user.getPlayer().hasPotionEffect(PotionEffectType.CONFUSION)) {
                    user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 2));
                }
            }
        }
    }
}