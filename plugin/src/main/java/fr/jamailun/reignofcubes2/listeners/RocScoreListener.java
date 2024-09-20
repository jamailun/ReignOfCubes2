package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.events.player.PlayerScoreGainedEvent;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class RocScoreListener extends RocListener {

    public RocScoreListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    void playerGainedScore(@NotNull PlayerScoreGainedEvent event) {
        game().checkVictory(event.getPlayer());
    }

}
