package fr.jamailun.reignofcubes2.objects;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.GameState;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.SoundsLibrary;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Handles the countdown before the game.
 */
public class GameCountdown {

    //TODO rendre la liste configurable !
    // Ainsi que la durée d'attente.
    private final static List<Integer> ALERT_AT = List.of(1, 2, 3, 5, 10, 30, 45, 60);

    private final GameManager game;
    private int secondsRemaining;
    private final BukkitTask countdownTask;

    public GameCountdown(GameManager game) {
        this.game = game;
        secondsRemaining = 31;
        countdownTask = ReignOfCubes2.runTaskTimer(this::countdownTick, 1);
        game.broadcast("countdown.start");
    }

    private void countdownTick() {
        // Check GameManager state.
        if(game.getState() != GameState.COUNT_DOWN) {
            ReignOfCubes2.warning("Countdown expected game being in COUNTDOWN state.");
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
            game.playSound(SoundsLibrary.GAME_STARTED_1);
            game.playSound(SoundsLibrary.GAME_STARTED_2);
        }
    }

    public void cancel() {
        if(!countdownTask.isCancelled())
            countdownTask.cancel();
    }

    public int getRemaining() {
        return secondsRemaining;
    }

}
