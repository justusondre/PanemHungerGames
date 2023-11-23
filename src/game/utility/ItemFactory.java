package game.utility;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemFactory {

	public static ItemStack create(Material mat, int amount) {
		return new ItemStack(mat, amount);

	}

	public static ItemStack createItem(Material material, String displayname, String lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayname);
		ArrayList<String> Lore = new ArrayList<>();
		Lore.add(lore);
		meta.setLore(Lore);

		item.setItemMeta(meta);
		return item; 
	}

	public static ItemStack createItem(Material material, int amount, String displayname, String lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayname);
		ArrayList<String> Lore = new ArrayList<>();
		Lore.add(lore);
		meta.setLore(Lore);

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(Material material, String displayname) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayname);
		item.setItemMeta(meta);
		return item;

	}

	public static ItemStack createCustom(Material mat, int amount, short id, String display) {
		ItemStack it = new ItemStack(mat, amount, id);
		ItemMeta meta = it.getItemMeta();
		meta.setDisplayName(display);
		it.setItemMeta(meta);
		return it;
	}

	public static String intToString(int num, int digits) {
		String output = Integer.toString(num);
		while (output.length() < digits)
			output = "0" + output;
		return output;
	}

	public static ItemStack Skull(String skullOwner, int quantity, String displayName) {
		ItemStack skull = new ItemStack(Material.LEGACY_SKULL_ITEM, quantity, (byte) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.LEGACY_SKULL_ITEM);
		skullMeta.setOwner(skullOwner);
		if (displayName != null) {
			skullMeta.setDisplayName(displayName);
		}

		skull.setItemMeta(skullMeta);
		return skull;
	}
}
