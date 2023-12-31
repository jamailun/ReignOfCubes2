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

    public void reload() {
        kits.clear();
        ConfigurationSection config = YamlConfiguration.loadConfiguration(file);

        for(String key : config.getKeys(false)) {
            Kit k = config.getSerializable(key, Kit.class);
            if(k == null) {
                ReignOfCubes2.error("Null key ! obj="+config.get(key));
                continue;
            }
            k.setSaver(saver);
            kits.put(k.getId(), k);
        }

        ReignOfCubes2.info("KitsConfiguration loaded " + kits.size() + " kits.");
    }

    public Kit create(String id, String displayName) {
        if(getKit(id) != null) return null;
        Kit kit = new Kit(id);
        kit.setSaver(saver);
        kit.setDisplayName(displayName);
        kit.setIconType(Material.GRASS_BLOCK);
        kit.setCost(-1);
        kits.put(id, kit);
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
        debug("Starting applying changes. Kits = " + kits.size());

        // Erase old data
        for(String key : config.getKeys(false)) {
            if(getKit(key) == null) {
                debug("erasing: " + key);
                config.set(key, null);
            }
        }

        // Write new data
        for(Kit kit : kits.values()) {
            config.set(kit.getId(), kit);
            debug("writing: " + kit.getId());
        }

        // Save in FS
        try {
            debug("saving: " + file);
            config.save(file);
        } catch(IOException e) {
            throw new RuntimeException("Cannot save kits", e);
        }
    }

    private void debug(String msg) {
        ReignOfCubes2.info("[Kits:debug] " + msg);
    }

    public Kit getDefaultKit() {
        return kits.values().stream()
                .filter(k -> k.getCost() == 0)
                .findFirst()
                .orElse(null);
    }

    public class KitsConfigurationSaver {
        protected KitsConfigurationSaver() {}

        public void save(Kit ignored) {
            applyChanges();
        }
    }

}
