package fr.jamailun.reignofcubes2.gameplay;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.GameState;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.gameplay.GameCountdown;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Handles the countdown before the game.
 */
public class GameCountdownImpl implements GameCountdown {

    //TODO rendre la liste configurable !
    // Ainsi que la durée d'attente.
    private final static List<Integer> ALERT_AT = List.of(1, 2, 3, 5, 10, 30, 45, 60);

    private final GameManagerImpl game;
    private int secondsRemaining;
    private final BukkitTask countdownTask;

    public GameCountdownImpl(GameManagerImpl game) {
        this.game = game;
        secondsRemaining = 31;
        countdownTask = MainROC2.runTaskTimer(this::countdownTick, 1);
        game.broadcast("countdown.start");
    }

    private void countdownTick() {
        // Check GameManager state.
        if(game.getState() != GameState.COUNT_DOWN) {
            ReignOfCubes2.logWarning("Countdown expected game being in COUNTDOWN state.");
            cancel();
            return;
        }

        // Check enough players
        if(game.getPlayersCount() < game.getRules().getPlayerCountMin()) {
            game.broadcast("countdown.cancelled-players");
            game.stopCountdown();
            return;
        }

        // Decrease time
        secondsRemaining --;

        // Message & sound
        if(ALERT_AT.contains(secondsRemaining)) {
            // message
            game.broadcast("countdown.message", secondsRemaining, secondsRemaining > 1 ? "s" : "");

            // play tick
            if(secondsRemaining > 20) {
                game.playSound(SoundsLibrary.GAME_COUNTER_TICK_LOW);
            } else if(secondsRemaining > 10) {
                game.playSound(SoundsLibrary.GAME_COUNTER_TICK_MIDDLE);
            } else {
                game.playSound(SoundsLibrary.GAME_COUNTER_TICK_HIGH);
            }
        }

        // Start if needed
        if(secondsRemaining == 0) {
            game.start();
        }
    }

    public void cancel() {
        if(!countdownTask.isCancelled())
            countdownTask.cancel();
    }

    public int getRemainingSeconds() {
        return secondsRemaining;
    }

}
