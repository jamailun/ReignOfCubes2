package fr.jamailun.reignofcubes2.gameplay.throne;

import fr.jamailun.reignofcubes2.api.music.MusicType;
import fr.jamailun.reignofcubes2.gameplay.AbstractCapture;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.jetbrains.annotations.NotNull;

public class ThroneCapture extends AbstractCapture {

    // Setting
    private final static double TICK_RATE = 0.5d;

    // Dings
    private static final int dingFrequency = 2;
    private int dingCounter = 0;
    private final boolean playsDings;

    public ThroneCapture(@NotNull RocPlayer player) {
        super(player);
        // Plays dings ?
        playsDings = ! game.getMusicManager().hasRadioFor(MusicType.CEREMONY);
    }

    @Override
    protected double getDuration() {
        return game.getRules().getCrownDuration();
    }

    @Override
    protected double getTickRate() {
        return TICK_RATE;
    }

    @Override
    protected void captureStarted() {
        // Add player to playlist
        game.getMusicManager().addPlayer(getPlayerUUID(), MusicType.CEREMONY);

        // Message and sound
        if(game.hasKing()) {
            game.broadcast("ceremony.start-steal", getPlayerName());
            game.playSound(SoundsLibrary.CEREMONY_STARTS_STEAL);
        } else {
            game.broadcast("ceremony.start", getPlayerName());
            game.playSound(SoundsLibrary.CEREMONY_STARTS);
        }
    }

    @Override
    protected void captureEnded() {
        if(isSuccess()) {
            game.playSound(SoundsLibrary.KING_CROWNED);
            getPlayer().sendMessage("throne.end-ceremony");
            game.throneCaptureCompleted(getPlayer());
        } else {
            game.broadcast("ceremony.fail", getPlayerName());
            game.playSound(SoundsLibrary.CEREMONY_FAILS);
            game.getMusicManager().addPlayer(getPlayerUUID(), MusicType.PLAY_NORMAL);
        }
    }

    @Override
    protected void ticked() {
        if(playsDings && (++dingCounter) % dingFrequency == 0) {
            double pitch = (getElapsed() * (1.05d)/getElapsed()) + 0.05d;
            getPlayer().playSound(SoundsLibrary.CEREMONY_DING, 0.9f, (float) pitch);
        }
    }

    @Override
    public @NotNull String getColor() {
        return "YELLOW"; // TODO ?
    }

}
