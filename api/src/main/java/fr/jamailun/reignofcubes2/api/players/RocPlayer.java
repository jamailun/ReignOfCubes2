package fr.jamailun.reignofcubes2.api.players;

import fr.jamailun.reignofcubes2.api.entities.RocDamageable;
import fr.jamailun.reignofcubes2.api.entities.RocEntity;
import fr.jamailun.reignofcubes2.api.entities.RocMessager;
import fr.jamailun.reignofcubes2.api.sounds.SoundEffect;
import fr.jamailun.reignofcubes2.api.tags.RocTagHolder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RocPlayer extends RocTagHolder, RocDamageable, RocEntity, RocMessager {

    @NotNull Player getPlayer();

    boolean isKing();

    void setKing(boolean isKing);

    int getLastMoneySpent();

    void setLastMoneySpent(int value);

    int getGold();

    boolean hasGold(float value);

    void addGold(float value);

    void removeGold(float delta);

    int getScore();

    boolean hasScore(int value);

    void addScore(int delta, @NotNull ScoreAddReason reason);

    void removeScore(int delta, @NotNull ScoreRemoveReason reason);

    void playSound(@NotNull SoundEffect effect);

    void playSound(@NotNull Sound sound, float volume, float pitch);

}
