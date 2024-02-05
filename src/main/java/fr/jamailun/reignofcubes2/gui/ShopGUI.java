package fr.jamailun.reignofcubes2.gui;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.SoundsLibrary;
import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.ScoreRemoveReason;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import fr.jamailun.reignofcubes2.utils.MenuGUI;

import java.util.Comparator;
import java.util.List;

public class ShopGUI extends MenuGUI {

    public ShopGUI(RocPlayer player) {
        super(3, player, "gui.shop.title");
        String costPrefix = "&7" + player.i18n("gui.shop.cost_prefix");

        // Kits ordered by price
        int slot = 0;
        for(Kit kit : listKits()) {
            ItemBuilder ib = new ItemBuilder(kit.toIcon())
                    .addLoreLine(costPrefix + printCost(player, kit));
            if(player.getLastMoneySpent() >= kit.getCost()) {
                ib.addLoreLine(player.i18n("gui.shop.too-rich"));
            } else if(player.hasScore(kit.getCost())) {
                ib.shine();
            }
            set(slot, ib.toItemStack(), () -> clickedOnKit(kit));
            slot ++;
        }

        // default& open
        setDefaultFooter();
        open();
    }

    private void clickedOnKit(Kit kit) {
        int cost = kit.getCost();

        // Check
        if(!getPlayer().hasScore(cost)) {
            getPlayer().sendMessage("score.messages.too-poor");
            getPlayer().playSound(SoundsLibrary.TOO_POOR);
            return;
        }

        // Buy it
        getPlayer().playSound(SoundsLibrary.KIT_BOUGHT);
        getPlayer().removeScore(cost, ScoreRemoveReason.BUY_KIT);
        kit.equip(getPlayer());
        getPlayer().sendMessage("score.messages.kit-bought");
        getPlayer().getPlayer().closeInventory();
    }

    private String printCost(RocPlayer player, Kit kit) {
        String color = player.hasScore(kit.getCost()) ? "&e" : "&4";
        return color + kit.getCost();
    }

    private List<Kit> listKits() {
        return ReignOfCubes2.getKits().getKits()
                .stream()
                .filter(k -> k.getCost() > 0)
                .sorted(Comparator.comparing(Kit::getCost))
                .toList();
    }

}
