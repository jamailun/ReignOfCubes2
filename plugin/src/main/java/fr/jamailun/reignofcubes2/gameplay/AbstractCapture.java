package fr.jamailun.reignofcubes2.gameplay;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.gameplay.CaptureProcess;
import fr.jamailun.reignofcubes2.api.music.MusicType;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractCapture implements CaptureProcess {

    // Relations
    protected final GameManagerImpl game;
    @NotNull @Getter private final RocPlayer player;
    private final BukkitTask task;

    // Durations
    private final double duration;
    private double elapsed = 0;
    @Getter private boolean success = false;

    public AbstractCapture(@NotNull RocPlayer player) {
        // Settings
        this.game = GameManagerImpl.instance();
        this.player = player;
        this.duration = getDuration();

        // Start
        task = MainROC2.runTaskTimer(this::tick, getTickRate());

        captureStarted();
    }

    protected double getElapsed() {
        return elapsed;
    }

    protected abstract double getDuration();

    protected abstract double getTickRate();

    protected abstract void captureStarted();

    protected abstract void captureEnded();

    protected abstract void ticked();

    private void tick() {
        elapsed += getTickRate();
        //ReignOfCubes2.info("[CEREMONY] " + elapsed + "/" + duration);
        if(elapsed >= duration) {
            success = true;
            game.ceremonyIsOver(player);
            game.playSound(SoundsLibrary.KING_CROWNED);
            return;
        }
        ticked();
    }

    @Override
    public final void stop() {
        if(!task.isCancelled()) {
            task.cancel();
            captureEnded();
        }
    }

    @Override
    public final boolean isPlayer(@NotNull RocPlayer player) {
        return Objects.equals(player.getUUID(), this.player.getUUID());
    }

    public final @NotNull String getPlayerName() {
        return player.getName();
    }
    public final @NotNull UUID getPlayerUUID() {
        return player.getUUID();
    }

    @Override
    public final double getRatio() {
        if(duration == 0) return 0;
        return elapsed / duration;
    }

}
