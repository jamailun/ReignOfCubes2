package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.api.configuration.kits.KitAlreadyExistsException;
import fr.jamailun.reignofcubes2.api.configuration.kits.KitsManager;
import fr.jamailun.reignofcubes2.configuration.kits.RocKit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * Manager of kits configuration.
 */
public class KitsConfigurationManager implements KitsManager {

    private final File folder;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitsConfigurationManager(File folder) {
        this.folder = folder;
        if(!folder.isDirectory()) {
            throw new RuntimeException("Kits folder is NOT a folder : '"+folder+"'.");
        }

        if(!folder.exists()) {
            if(!folder.mkdirs())
                throw new RuntimeException("Cannot create KitsConfiguration folder "+folder+"'.");
        }

        ReignOfCubes2.logInfo("Kits | Folder = " + folder);
        reload();
    }

    @Override
    public void reload() {
        kits.clear();
        File[] files = folder.listFiles();
        if(files == null) {
            ReignOfCubes2.logWarning("No kit file in " + folder);
            return;
        }
        for(File file : files) {
            if( ! (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml"))) {
                ReignOfCubes2.logWarning("Invalid kit extension : " + file);
                continue;
            }
            RocKit kit = new RocKit(file);
            kits.put(kit.getId(), kit);
        }

        ReignOfCubes2.logInfo("KitsConfiguration loaded " + kits.size() + " kits.");
    }

    @Override
    public @NotNull Kit create(@NotNull String id, @Nullable String displayName) throws KitAlreadyExistsException {
        if(kits.containsKey(id))
            throw new KitAlreadyExistsException("Kit of ID '" + id + "' already exists.");
        Kit kit = new RocKit(folder, id);
        kit.setDisplayName(Objects.requireNonNullElse(displayName, id));
        kit.setIconType(Material.GRASS_BLOCK);
        kit.setCost(-1);
        kits.put(id, kit);
        return kit;
    }

    @Override
    public @NotNull Optional<Kit> getKit(String id) {
        return Optional.ofNullable(kits.get(id));
    }

    @Override
    public @NotNull List<Kit> getKits() {
        return kits.values().stream()
                .sorted(Comparator.comparing(Kit::getId))
                .toList();
    }

    @Override
    public void delete(@Nullable Kit kit) {
        if(kit == null) return;
        Kit deleted = kits.remove(kit.getId());
        if(deleted != null)
            deleted.archiveFile(folder);
    }

    @Override
    public Kit getDefaultKit() {
        return kits.values().stream()
                .filter(k -> k.getCost() == 0)
                .findFirst()
                .orElse(null);
    }
}
