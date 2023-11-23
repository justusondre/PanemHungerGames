package game.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;

public class GamemakerGUI {

    public void openGameMakerMenu(Player player) {
        Window window = Window.single()
                .setViewer(player)
                .setTitle("Gamemaker Options")
                .setGui(createTabGui())
                .build();

        window.open();
    }

    Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));

    Gui page0 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui page1 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui page2 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui page3 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui page4 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui page5 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui page6 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui page7 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui page8 = Gui.normal()
            .setStructure(
                    "0 1 2 3 4 5 6 7 8",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x")
            .addIngredient('#', border)
            .build();

    Gui tabGui = createTabGui();

    private Gui createTabGui() {
        return TabGui.normal()
                .setStructure(
                        "0 1 2 3 4 5 6 7 8",
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x")
                .addIngredient('#', border)
                .addIngredient('0', new TabItem(0))
                .addIngredient('1', new TabItem(1))
                .addIngredient('2', new TabItem(2))
                .addIngredient('3', new TabItem(3))
                .addIngredient('4', new TabItem(4))
                .addIngredient('5', new TabItem(5))
                .addIngredient('6', new TabItem(6))
                .addIngredient('7', new TabItem(7))
                .addIngredient('8', new TabItem(8))

                .setTabs(Arrays.asList(page0, page1, page2, page3, page4, page5, page6, page7, page8))
                .build();
    }
}