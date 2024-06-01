package fr.jamailun.reignofcubes2.gameplay;

import fr.jamailun.reignofcubes2.api.gameplay.Mine;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class MineImpl implements Mine {

    @Getter @NotNull private final Location location;
    @Getter private final double radius;
    private final double rate;

    @Getter private RocPlayer owner;

    @Override
    public boolean hasOwner() {
        return owner != null;
    }

}
