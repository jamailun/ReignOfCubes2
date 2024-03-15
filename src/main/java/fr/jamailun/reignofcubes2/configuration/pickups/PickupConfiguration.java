package fr.jamailun.reignofcubes2.configuration.pickups;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
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
public class PickupConfiguration {

    private final List<PickupConfigEntry> entries = new ArrayList<>();
    private final RandomBag<PickupConfigEntry> bag = new RandomBag<>();

    /**
     * Regenerate the configuration from config.
     * @param section the ConfigurationSection to deserialize from.
     * @throws WorldConfiguration.BadWorldConfigurationException if configuration is invalid.
     */
    public void deserialize(@NotNull ConfigurationSection section) throws WorldConfiguration.BadWorldConfigurationException {
        for(String key : section.getKeys(false)) {
            ConfigurationSection sub = section.getConfigurationSection(key);
            if(sub == null) {
                throw new WorldConfiguration.BadWorldConfigurationException("Invalid pickup section : '" + key + "'");
            }
            entries.add(PickupConfigEntry.deserialize(key, sub));
        }
        regenerate();
    }

    /**
     * Save the configuration to a ConfigurationSection.
     * @param section ConfigurationSection to serialize to.
     */
    public void save(@NotNull ConfigurationSection section) {
        // Clear section
        for(String key : section.getKeys(false))
            section.set(key, null);
        // Serialize all entries
        for(PickupConfigEntry entry : entries) {
            entry.serialize(section);
        }
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
