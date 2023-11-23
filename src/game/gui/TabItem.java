package game.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class TabItem extends xyz.xenondevs.invui.item.impl.controlitem.TabItem {

    private final int tab;

    public TabItem(int tab) {
        super(tab);
        this.tab = tab;
    }

    @Override
    public ItemProvider getItemProvider(TabGui gui) {
        if (gui.getCurrentTab() == tab) {
            return new ItemBuilder(Material.GLOWSTONE_DUST).setDisplayName("Tab " + tab + ChatColor.GREEN + " (SELECTED)");

        } else {
            return new ItemBuilder(Material.GUNPOWDER).setDisplayName("Tab " + tab + ChatColor.RED + " (CLICK TO SELECT)");
        }
    }
}
