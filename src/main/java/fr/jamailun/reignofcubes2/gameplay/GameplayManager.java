package fr.jamailun.reignofcubes2.gameplay;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class GameplayManager {

    private final Set<MineImpl> mines = new HashSet<>();
    @Getter private Throne throne;

    private BukkitRunnable minesTask;

    public void gameStart() {
        minesTask = new BukkitRunnable() {
            @Override
            public void run() {
                mines.forEach(m -> m.tick(1));
            }
        };
        minesTask.runTaskTimerAsynchronously(MainROC2.plugin(), 20, 20);
    }

    public void gameStop() {
        if(minesTask != null) {
            minesTask.cancel();
            minesTask = null;
        }
    }


}
