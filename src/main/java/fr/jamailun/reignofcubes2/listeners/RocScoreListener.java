package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.events.player.ScoreGainedEvent;
import org.bukkit.event.EventHandler;

public class RocScoreListener extends RocListener {

    public RocScoreListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerGainedScore(ScoreGainedEvent event) {
        game().checkVictory(event.getPlayer());
    }

}
