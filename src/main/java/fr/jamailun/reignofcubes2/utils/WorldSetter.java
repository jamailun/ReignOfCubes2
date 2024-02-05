package fr.jamailun.reignofcubes2.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

public final class WorldSetter {
    private WorldSetter() {}

    public static void configure(World world) {
        // Essentials
        world.setPVP(true);
        world.setGameRule(GameRule.SPAWN_RADIUS, 1);
        world.setGameRule(GameRule.KEEP_INVENTORY, true); // no drops on death
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.NATURAL_REGENERATION, true);

        // No drops, no mobs
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);

        // Others
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
        world.setGameRule(GameRule.DO_VINES_SPREAD, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);

        // Conclusion
        Bukkit.getConsoleSender().sendMessage("§aWorld §f"+world.getName()+"§a successfully configured.");
    }

}
