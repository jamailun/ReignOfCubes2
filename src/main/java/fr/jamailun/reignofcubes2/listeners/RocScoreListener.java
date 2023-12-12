package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.events.ScoreGainedEvent;
import org.bukkit.event.EventHandler;

public class RocScoreListener extends RocListener {

    public RocScoreListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerGainedScore(ScoreGainedEvent event) {
        game().checkVictory(event.getPlayer());
    }

}
