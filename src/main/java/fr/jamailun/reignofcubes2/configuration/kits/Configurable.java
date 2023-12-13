package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class Configurable implements ConfigurationSerializable {

    protected static @Nullable Material loadMaterial(String owner, String input) {
        try {
            return Material.valueOf(input);
        } catch(IllegalArgumentException e) {
            ReignOfCubes2.error("Invalid TYPE for "+owner+": '" + input + "'.");
            return null;
        }
    }

    protected static @Nullable Map.Entry<Enchantment, Integer> loadEnchant(String owner, String input) {
        String[] tokens = input.split(";");
        if(tokens.length != 2) {
            ReignOfCubes2.error("Invalid enchant ENTRY for "+owner+": '" + input + "'.");
            return null;
        }
        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(tokens[0]));
        if(enchant == null) {
            ReignOfCubes2.error("Invalid enchant NAME for "+owner+": '" + tokens[0] + "'.");
            return null;
        }
        int level;
        try {
            level = Integer.parseInt(tokens[1]);
            if(level < 0) throw new NumberFormatException();
        } catch(NumberFormatException ignored) {
            ReignOfCubes2.error("Invalid enchant LEVEL for "+owner+": '" + tokens[1] + "'.");
            return null;
        }
        return Map.entry(enchant, level);
    }


}
