package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KitsConfiguration {

    private final File file;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitsConfiguration(File file) {
        this.file = file;

        if(!file.exists()) {
            try {
                assert file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Cannot create KitsConfiguration file: " + e.getMessage());
            }
        }

        reload();
    }

    @SuppressWarnings("unchecked")
    public void reload() {
        kits.clear();
        ConfigurationSection config = YamlConfiguration.loadConfiguration(file);

        for(Map<?,?> mapRaw : config.getMapList("")) {
            ReignOfCubes2.info("DEBUG. map = " + mapRaw);
            Map<String, Object> map = (Map<String, Object>) mapRaw;
            Kit kit = Kit.deserialize(map);

            kits.put(kit.getId(), kit);
        }

        ReignOfCubes2.info("KitsConfiguration loaded " + kits.size() + " kits.");
    }

    public Kit getKit(String id) {
        return kits.get(id);
    }

    public List<Kit> getKits() {
        return kits.values().stream()
                .sorted(Comparator.comparing(Kit::getId))
                .toList();
    }

}
