package fr.jamailun.reignofcubes2.api.gameplay;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.jetbrains.annotations.NotNull;

public interface Ceremony {

    void stop();

    boolean isPlayer(@NotNull RocPlayer player);

    @NotNull RocPlayer getPlayer();

    double getRatio();

    @NotNull String getColor();

}
