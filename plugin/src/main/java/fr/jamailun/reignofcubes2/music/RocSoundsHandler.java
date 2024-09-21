package fr.jamailun.reignofcubes2.music;

import fr.jamailun.reignofcubes2.api.GameState;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.events.game.GameStartEvent;
import fr.jamailun.reignofcubes2.api.events.game.GameStopEvent;
import fr.jamailun.reignofcubes2.api.events.player.KingChangedEvent;
import fr.jamailun.reignofcubes2.api.events.player.PlayerRejoinsGameEvent;
import fr.jamailun.reignofcubes2.api.music.MusicManager;
import fr.jamailun.reignofcubes2.api.music.MusicType;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class RocSoundsHandler implements Listener {

    private final MusicManager musicManager;

    @EventHandler(ignoreCancelled = true)
    void onKingChanged(@NotNull KingChangedEvent event) {
        // Old king back to player music
        if(event.getOldKing() != null) {
            RocPlayer oldKing = event.getOldKing();
            // Sounds
            musicManager.addPlayer(oldKing, MusicType.PLAY_NORMAL);
            oldKing.playSound(SoundsLibrary.DEAD_AS_KING);
        }

        // Set new king music
        if(event.getNewKing() != null) {
            musicManager.addPlayer(event.getNewKing().getPlayer(), MusicType.PLAY_KING);
        }
    }

    @EventHandler
    void onGameStart(@NotNull GameStartEvent event) {
        for(RocPlayer pl : ReignOfCubes2.players()) {
            musicManager.addPlayer(pl, MusicType.PLAY_NORMAL);
        }
    }

    @EventHandler
    void onGameStop(@NotNull GameStopEvent event) {
        for(Player pl : Bukkit.getOnlinePlayers()) {
            musicManager.addPlayer(pl, MusicType.LOBBY);
        }
    }

    @EventHandler
    void onPlayerRejoins(@NotNull PlayerRejoinsGameEvent event) {
        musicManager.addPlayer(event.getPlayer(), MusicType.PLAY_NORMAL);
    }

    @EventHandler
    void playerJoinsServer(@NotNull PlayerJoinEvent event) {
        if(ReignOfCubes2.state() != GameState.PLAYING) {
            musicManager.addPlayer(event.getPlayer(), MusicType.LOBBY);
        }
    }
}
