package game.throwables;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ThrowableList {
    public static List<Material> linearThrowable = new ArrayList<>();
    public static List<Material> spinningThrowable = new ArrayList<>();

    public static List<Material> tier1 = new ArrayList<>();
    public static List<Material> tier2 = new ArrayList<>();
    public static List<Material> tier3 = new ArrayList<>();
    public static List<Material> tier4 = new ArrayList<>();
    public static List<Material> tier5 = new ArrayList<>();

    public ThrowableList() {
        linearThrowable.add(Material.GOLDEN_SHOVEL);
        linearThrowable.add(Material.STONE_SHOVEL);
        linearThrowable.add(Material.NETHERITE_INGOT);
        linearThrowable.add(Material.STONE_SWORD);
        linearThrowable.add(Material.WOODEN_HOE);
        linearThrowable.add(Material.WOODEN_SHOVEL);
        linearThrowable.add(Material.STONE_PICKAXE);
        linearThrowable.add(Material.DIAMOND_SHOVEL);
        linearThrowable.add(Material.NETHER_BRICK);

        spinningThrowable.add(Material.NETHERITE_AXE);
        spinningThrowable.add(Material.STONE_AXE);
        spinningThrowable.add(Material.WOODEN_AXE);
        spinningThrowable.add(Material.DIAMOND_PICKAXE);
        spinningThrowable.add(Material.GOLDEN_PICKAXE);
        spinningThrowable.add(Material.COBWEB);

        tier1.add(Material.STICK);
        tier1.add(Material.NETHERITE_INGOT);
        tier1.add(Material.SCUTE);
        tier1.add(Material.SHEARS);
        tier1.add(Material.GOLDEN_HOE);

        tier2.add(Material.POPPED_CHORUS_FRUIT);
        tier2.add(Material.STONE_SWORD);
        tier2.add(Material.WOODEN_HOE);
        tier2.add(Material.WOODEN_SHOVEL);
        tier2.add(Material.BLAZE_ROD);
        tier2.add(Material.GLOWSTONE_DUST);
        tier2.add(Material.STONE_AXE);

        tier3.add(Material.DIAMOND_SHOVEL);
        tier3.add(Material.DIAMOND_PICKAXE);
        tier3.add(Material.PHANTOM_MEMBRANE);
        tier3.add(Material.GOLDEN_SHOVEL);
        tier3.add(Material.IRON_SHOVEL);
        tier3.add(Material.STONE_SHOVEL);
        tier3.add(Material.IRON_HOE);
        tier3.add(Material.WOODEN_AXE);
        tier3.add(Material.GOLDEN_PICKAXE);

        tier4.add(Material.WOODEN_AXE);
        tier4.add(Material.IRON_AXE);
        tier4.add(Material.GOLDEN_SWORD);
        tier4.add(Material.QUARTZ);
        tier4.add(Material.WOODEN_SWORD);
        tier4.add(Material.IRON_SWORD);
        tier4.add(Material.NETHERITE_PICKAXE);
        tier4.add(Material.DIAMOND_HOE);
        tier4.add(Material.GOLDEN_AXE);

        tier5.add(Material.NETHERITE_AXE);
        tier5.add(Material.NETHERITE_HOE);
        tier5.add(Material.DIAMOND_AXE);
        tier5.add(Material.NETHER_BRICK);
        tier5.add(Material.NETHERITE_SWORD);
        tier5.add(Material.DIAMOND_SWORD);
        tier5.add(Material.NETHER_BRICK);
    }
}
