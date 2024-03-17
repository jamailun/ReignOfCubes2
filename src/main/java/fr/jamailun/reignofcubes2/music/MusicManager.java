package fr.jamailun.reignofcubes2.music;

import com.xxmicloxx.NoteBlockAPI.model.*;
import com.xxmicloxx.NoteBlockAPI.songplayer.Fade;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * Manages musics for players.
 */
public class MusicManager {

    private final File folder;
    private final Map<MusicType, RadioSongPlayer> radios = new HashMap<>();
    private final Map<UUID, MusicType> listeners = new HashMap<>();

    public MusicManager(File folder) {
        this.folder = folder;
        assertFolder(folder);
        reload();

        // Every 30 seconds, disable vanilla sounds
        ReignOfCubes2.runTaskTimer(() -> {
            Bukkit.getOnlinePlayers().forEach(p -> p.stopSound(org.bukkit.SoundCategory.MUSIC));
        }, 30);
    }

    /**
     * Reload music playlists, but keep listeners in place.
     */
    public void reload() {
        radios.clear();

        for(MusicType type : MusicType.values()) {
            List<Song> songs = loadSongs(type);
            if(songs.isEmpty()) {
                ReignOfCubes2.warning("No music for playlist " + type + " !");
                continue;
            }

            Playlist playlist = new Playlist(songs.toArray(new Song[0]));
            RadioSongPlayer radio = new RadioSongPlayer(playlist, SoundCategory.MUSIC);
            radio.setRandom(true);
            radio.setRepeatMode(RepeatMode.ALL);
            radios.put(type, radio);

            ReignOfCubes2.info("Music type " + type + " loaded " + songs.size() + " songs.");
        }

        // refresh listeners
        List<Map.Entry<UUID, MusicType>> currentListeners = List.copyOf(listeners.entrySet());
        listeners.clear();
        for(Map.Entry<UUID, MusicType> listener : currentListeners) {
            addPlayer(listener.getKey(), listener.getValue());
        }
    }

    private List<Song> loadSongs(MusicType type) {
        File typeFolder = new File(folder, type.name().toLowerCase());
        assertFolder(typeFolder);
        File[] files = typeFolder.listFiles();
        if(files == null)
            return Collections.emptyList();
        List<Song> songs = new ArrayList<>();
        for(File file : files) {
            try {
                Song song = NBSDecoder.parse(file);
                songs.add(song);
            } catch (Throwable e) {
                ReignOfCubes2.error("Error when parsing " + file + " : " + e.getMessage());
            }
        }
        return songs;
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        MusicType type = listeners.remove(uuid);
        if(type != null && radios.containsKey(type)) {
            RadioSongPlayer radio = radios.get(type);
            radio.removePlayer(uuid);
            ReignOfCubes2.info("Player left radio " + type);
            if(radio.getPlayerUUIDs().isEmpty()) {
                radio.setPlaying(false);
                ReignOfCubes2.info("Radio stopped.");
            }
        }
    }

    public void addPlayer(Player player, MusicType type) {
        addPlayer(player.getUniqueId(), type);
        player.stopSound(org.bukkit.SoundCategory.MUSIC);
    }

    public void addPlayer(UUID uuid, MusicType type) {
        MusicType current = listeners.get(uuid);
        if(current != null && current == type)
            return;
        removePlayer(uuid);

        if(radios.containsKey(type)) {
            RadioSongPlayer radio = radios.get(type);
            radio.addPlayer(uuid);
            listeners.put(uuid, type);
            ReignOfCubes2.info("Player joined radio " + type);
            if(!radio.isPlaying()) {
                radio.setPlaying(true, new Fade(FadeType.LINEAR, 40));
                ReignOfCubes2.info("Radio started (" + radio.getVolume() + ")");
            }
        }
    }

    public boolean hasRadioFor(MusicType type) {
        return radios.containsKey(type);
    }

    private static void assertFolder(File folder) {
        if(!folder.exists()) {
            if(!folder.mkdirs()) {
                throw new RuntimeException("Could not assert folder existence : '" + folder + "'.");
            }
        }
    }

    public Optional<Song> getHeardSong(@NotNull Player player) {
        MusicType type = listeners.get(player.getUniqueId());
        if(type == null) return Optional.empty();
        RadioSongPlayer radio = radios.get(type);
        return Optional.of(radio.getSong());
    }

}
