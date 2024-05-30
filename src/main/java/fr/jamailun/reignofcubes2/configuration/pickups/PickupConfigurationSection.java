package fr.jamailun.reignofcubes2.configuration.pickups;

import fr.jamailun.reignofcubes2.api.configuration.BadConfigurationException;
import fr.jamailun.reignofcubes2.api.configuration.sections.DeserializeConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSection;
import fr.jamailun.reignofcubes2.utils.RandomBag;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Manages the configuration of the pickups.
 */
public class PickupConfigurationSection extends RocConfigurationSection {

    private final List<PickupConfigEntry> entries = new ArrayList<>();
    private final RandomBag<PickupConfigEntry> bag = new RandomBag<>();

    @DeserializeConfiguration
    public void deserialize(@NotNull ConfigurationSection section) throws BadConfigurationException {
        for(String key : section.getKeys(false)) {
            ConfigurationSection sub = section.getConfigurationSection(key);
            if(sub == null) {
                throw new BadConfigurationException("Invalid pickup section : '" + key + "'");
            }
            entries.add(PickupConfigEntry.deserialize(key, sub));
        }
        regenerate();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void write(@NotNull ConfigurationSection section) {
        // Clear section
        for(String key : section.getKeys(false))
            section.set(key, null);
        // Serialize all entries
        for(PickupConfigEntry entry : entries) {
            entry.serialize(section);
        }
    }

    @Override
    public String nicePrint(String prefix, String last) {
        return "TODO";
    }

    private void regenerate() {
        bag.clear();
        entries.forEach(pe -> bag.add(pe, pe.chance()));
    }

    /**
     * Remove an entry, if exists.
     * @param id the ID to remove.
     */
    public void remove(@NotNull String id) {
        if(entries.removeIf(e -> Objects.equals(id, e.id())))
            regenerate();
    }

    /**
     * Add (or replace) an entry.
     * @param entry the entry to add.
     */
    public void add(@NotNull PickupConfigEntry entry) {
        remove(entry.id());
        entries.add(entry);
        regenerate();
    }

    /**
     * Randomize a new entry to pick.
     * @return an element.
     */
    public @NotNull PickupConfigEntry pickRandom() {
        assert ! isEmpty();
        return bag.next();
    }

    /**
     * Test if the configuration is empty.
     * @return true if no pickup has been defined.
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * List entries of the configuration.
     * @return a immuable copy of the entries-list.
     */
    public List<PickupConfigEntry> listEntries() {
        return List.copyOf(entries);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        for(PickupConfigEntry entry : entries) {
            sj.add(entry.id() + "/" + entry.score());
        }
        return "["+sj+"]";
    }
}
