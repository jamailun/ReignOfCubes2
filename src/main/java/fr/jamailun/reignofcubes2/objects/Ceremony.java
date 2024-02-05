package fr.jamailun.reignofcubes2.objects;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.SoundsLibrary;
import fr.jamailun.reignofcubes2.players.RocPlayer;
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
    private boolean success = false;

    private static final int dingFrequency = 2;
    private int dingCounter = 0;

    public Ceremony(GameManager game, RocPlayer player) {
        this.game = game;
        this.player = player;
        this.duration = game.getRules().getCrownDuration();
        // Start
        task = ReignOfCubes2.runTaskTimer(this::tick, TICK_RATE);
        ReignOfCubes2.info("[CEREMONY] Started.");

        if(game.hasKing()) {
            game.broadcast("ceremony.start-steal", player.getName());
            game.playSound(SoundsLibrary.CEREMONY_STARTS_STEAL);
        } else {
            game.broadcast("ceremony.start", player.getName());
            game.playSound(SoundsLibrary.CEREMONY_STARTS);
        }
    }

    private void tick() {
        elapsed += TICK_RATE;
        ReignOfCubes2.info("[CEREMONY] " + elapsed + "/" + duration);
        if(elapsed >= duration) {
            success = true;
            game.ceremonyIsOver(player);
            game.playSound(SoundsLibrary.KING_CROWNED);
            return;
        }
        // Play a 'ding' (every N ticks)
        if((++dingCounter) % dingFrequency == 0) {
            double pitch = (elapsed * (1.05d)/duration) + 0.05d;
            player.playSound(SoundsLibrary.CEREMONY_DING, 0.9f, (float) pitch);
        }
    }

    public void stop() {
        if(!task.isCancelled()) {
            task.cancel();
            ReignOfCubes2.info("[CEREMONY] Stopped.");
            if( ! success) {
                game.broadcast("ceremony.fail", player.getName());
                game.playSound(SoundsLibrary.CEREMONY_FAILS);
            }
        }
    }

    public boolean isPlayer(RocPlayer player) {
        return player.getUUID().equals(this.player.getUUID());
    }

    public String getPlayerName() {
        return player.getName();
    }

    public double getRatio() {
        assert duration > 0;
        return elapsed / duration;
    }

    public String getColor() {
        return "YELLOW"; // TODO
    }

}
