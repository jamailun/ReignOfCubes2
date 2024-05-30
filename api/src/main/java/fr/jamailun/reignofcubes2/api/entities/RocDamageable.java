package fr.jamailun.reignofcubes2.api.entities;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;

public interface RocDamageable {

    RocPlayer getLastDamager();

    void setLastDamager(RocPlayer player);

}
