package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Ceremony {

    // Relations
    private final GameManager game;
    private final RocPlayer player;
    private final BukkitTask task;

    // Durations
    private final static double TICK_RATE = 0.5d;
    private final double duration;
    private double elapsed = 0;

    public Ceremony(GameManager game, RocPlayer player) {
        this.game = game;
        this.player = player;
        this.duration = game.getRules().getCrownDuration();
        // Start
        task = ReignOfCubes2.runTaskTimer(this::tick, TICK_RATE);
        ReignOfCubes2.info("[CEREMONY] Started.");
    }

    private void tick() {
        elapsed += TICK_RATE;
        ReignOfCubes2.info("[CEREMONY] " + elapsed + "/" + duration);
        if(elapsed >= duration) {
            game.ceremonyIsOver(player);
            stop();
        }
    }

    public void stop() {
        if(!task.isCancelled())
            task.cancel();
        ReignOfCubes2.info("[CEREMONY] Stopped.");
    }

    public boolean isPlayer(RocPlayer player) {
        return player.getUUID().equals(this.player.getUUID());
    }

}
