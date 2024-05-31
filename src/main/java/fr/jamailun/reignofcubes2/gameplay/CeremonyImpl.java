package fr.jamailun.reignofcubes2.gameplay;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.gameplay.Ceremony;
import fr.jamailun.reignofcubes2.api.music.MusicType;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class CeremonyImpl implements Ceremony {

    // Relations
    private final GameManagerImpl game;
    @NotNull @Getter private final RocPlayer player;
    private final BukkitTask task;

    // Durations
    private final static double TICK_RATE = 0.5d;
    private final double duration;
    private double elapsed = 0;
    private boolean success = false;

    private static final int dingFrequency = 2;
    private int dingCounter = 0;
    private final boolean playsDings;

    public CeremonyImpl(GameManagerImpl game, RocPlayer player) {
        this.game = game;
        this.player = player;
        this.duration = game.getRules().getCrownDuration();
        // Start
        task = MainROC2.runTaskTimer(this::tick, TICK_RATE);
        game.getMusicManager().addPlayer(player.getUUID(), MusicType.CEREMONY);
        playsDings = ! game.getMusicManager().hasRadioFor(MusicType.CEREMONY);

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
        //ReignOfCubes2.info("[CEREMONY] " + elapsed + "/" + duration);
        if(elapsed >= duration) {
            success = true;
            game.ceremonyIsOver(player);
            game.playSound(SoundsLibrary.KING_CROWNED);
            return;
        }
        // Play a 'ding' (every N ticks)
        if(playsDings && (++dingCounter) % dingFrequency == 0) {
            double pitch = (elapsed * (1.05d)/duration) + 0.05d;
            player.playSound(SoundsLibrary.CEREMONY_DING, 0.9f, (float) pitch);
        }
    }

    public void stop() {
        if(!task.isCancelled()) {
            task.cancel();
            if( ! success) {
                game.broadcast("ceremony.fail", player.getName());
                game.playSound(SoundsLibrary.CEREMONY_FAILS);
                game.getMusicManager().addPlayer(player.getUUID(), MusicType.PLAY_NORMAL);
            }
        }
    }

    public boolean isPlayer(@NotNull RocPlayer player) {
        return player.getUUID().equals(this.player.getUUID());
    }

    public String getPlayerName() {
        return player.getName();
    }

    public double getRatio() {
        assert duration > 0;
        return elapsed / duration;
    }

    public @NotNull String getColor() {
        return "YELLOW"; // TODO ?
    }

}
