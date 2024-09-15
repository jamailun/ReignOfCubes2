package fr.jamailun.reignofcubes2.gameplay.mine;

import fr.jamailun.reignofcubes2.api.gameplay.CaptureProcess;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.gameplay.AbstractCapture;
import org.jetbrains.annotations.NotNull;

public class MineCapture extends AbstractCapture {

    private static final double TICK_RATE = 0.5;

    public MineCapture(@NotNull RocPlayer player) {
        super(player);
    }

    @Override
    protected double getDuration() {
        return game.getRules().getMineCaptureDuration();
    }

    @Override
    protected double getTickRate() {
        return TICK_RATE;
    }

    @Override
    protected void captureStarted() {

    }

    @Override
    protected void captureEnded() {

    }

    @Override
    protected void ticked() {

    }

    @Override
    public @NotNull String getColor() {
        return "GOLD";
    }
}
