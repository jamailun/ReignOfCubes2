package fr.jamailun.reignofcubes2.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
public final class WorldSetter {

  public static void configure(World world) {
    // Essentials
    world.setGameRule(GameRules.PVP, true);
    world.setGameRule(GameRules.RESPAWN_RADIUS, 1);
    world.setGameRule(GameRules.KEEP_INVENTORY, true); // no drops on death
    world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
    world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, true);

    // No drops, no mobs
    world.setGameRule(GameRules.BLOCK_DROPS, false);
    world.setGameRule(GameRules.ENTITY_DROPS, false);
    world.setGameRule(GameRules.MOB_DROPS, false);
    world.setGameRule(GameRules.SPAWN_MOBS, false);
    world.setGameRule(GameRules.SPAWN_PATROLS, false);
    world.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
    world.setGameRule(GameRules.SPAWN_WARDENS, false);
    world.setGameRule(GameRules.SPAWN_PHANTOMS, false);

    // Others
    world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
    world.setGameRule(GameRules.LIMITED_CRAFTING, true);
    world.setGameRule(GameRules.SPREAD_VINES, false);
    world.setGameRule(GameRules.MOB_GRIEFING, false);
    world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);

    // Conclusion
    Bukkit.getConsoleSender().sendMessage("§aWorld §f" + world.getName() + "§a successfully configured.");
  }

}
