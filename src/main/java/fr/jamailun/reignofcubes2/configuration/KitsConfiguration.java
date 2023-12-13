package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KitsConfiguration {

    private final File file;
    private final Map<String, Kit> kits = new HashMap<>();
    private final KitsConfigurationSaver saver = new KitsConfigurationSaver();

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
            Kit kit = Kit.deserialize(map, saver);

            kits.put(kit.getId(), kit);
        }

        ReignOfCubes2.info("KitsConfiguration loaded " + kits.size() + " kits.");
    }

    public Kit create(String id, String displayName) {
        Kit kit = new Kit(saver, id);
        kit.setDisplayName(displayName);
        kit.setIconType(Material.GRASS_BLOCK);
        kit.setCost(-1);
        return kit;
    }

    public Kit getKit(String id) {
        return kits.get(id);
    }

    public List<Kit> getKits() {
        return kits.values().stream()
                .sorted(Comparator.comparing(Kit::getId))
                .toList();
    }

    public void delete(Kit kit) {
        if(kit == null) return;
        kits.remove(kit.getId());
        applyChanges();
    }

    private void applyChanges() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Clear
        config.set("", null);

        // set data
        for(String id : kits.keySet()) {
            config.set(id, kits.get(id).serialize());
        }

        // Save in FS
        try {
            config.save(file);
        } catch(IOException e) {
            throw new RuntimeException("Cannot save kits", e);
        }
    }

    public class KitsConfigurationSaver {
        protected KitsConfigurationSaver() {}

        public void save(Kit kit) {
            applyChanges();
        }
    }

}
