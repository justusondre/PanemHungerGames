package game.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import game.Main;

public class UserManager {

	private final Set<User> users;

	public UserManager(Main plugin) {
		this.users = new HashSet<>();
	}

	public User addUser(Player player) {
		User user = new User(player.getUniqueId());
		this.users.add(user);
		return user;
	}

	public void removeUser(Player player) {
		this.users.remove(getUser(player));
	}

	public User getUser(Player player) {
		UUID uuid = player.getUniqueId();
		for (User user1 : this.users) {
			if (uuid.equals(user1.getUniqueId())) {
				user1.setPlayer();
				return user1;
			}
		}
		User user = addUser(player);
		return user;
	}

	public Set<User> getUsers() {
		this.users.forEach(User::setPlayer);
		return Set.copyOf(this.users);
	}

	public List<User> getTopKillers(int topCount) {
		List<User> allUsers = new ArrayList<>(this.users);
		allUsers.sort(Comparator.comparingInt(User::getKills).reversed());
		return allUsers.subList(0, Math.min(topCount, allUsers.size()));
	}

	public void displayTopKillers() {
	    List<User> topKillers = getTopKillers(3);

	    if (topKillers.isEmpty()) {
	        System.out.println("No top killers found.");
	        return;
	    }

	    System.out.println("Top Killers:");

	    for (int i = 0; i < topKillers.size(); i++) {
	        User user = topKillers.get(i);
	        System.out.println((i + 1) + ". " + user.getName() + " - Kills: " + user.getKills());
	    }
	}
}
