package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import org.bukkit.Material;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitsConfiguration {

    private final File folder;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitsConfiguration(File folder) {
        this.folder = folder;
        if(!folder.isDirectory()) {
            throw new RuntimeException("Kits folder is NOT a folder : '"+folder+"'.");
        }

        if(!folder.exists()) {
            if(!folder.mkdirs())
                throw new RuntimeException("Cannot create KitsConfiguration folder "+folder+"'.");
        }

        MainROC2.info("Kits | Folder = " + folder);
        reload();
    }

    public void reload() {
        kits.clear();
        File[] files = folder.listFiles();
        if(files == null) {
            MainROC2.warning("No kit file in " + folder);
            return;
        }
        for(File file : files) {
            if( ! (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml"))) {
                MainROC2.warning("Invalid kit extension : " + file);
                continue;
            }
            Kit kit = new Kit(file);
            kits.put(kit.getId(), kit);
        }

        MainROC2.info("KitsConfiguration loaded " + kits.size() + " kits.");
    }

    public Kit create(String id, String displayName) {
        if(getKit(id) != null) return null;
        Kit kit = new Kit(folder, id);
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
        Kit deleted = kits.remove(kit.getId());
        if(deleted != null)
            deleted.archiveFile(folder);
    }

    public Kit getDefaultKit() {
        return kits.values().stream()
                .filter(k -> k.getCost() == 0)
                .findFirst()
                .orElse(null);
    }
}
