package fr.jamailun.reignofcubes2.configuration.sections;

import fr.jamailun.reignofcubes2.api.configuration.sections.DeserializeConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSection;
import fr.jamailun.reignofcubes2.api.tags.RocTag;
import fr.jamailun.reignofcubes2.api.tags.TagsRegistry;
import fr.jamailun.reignofcubes2.configuration.TagName;
import fr.jamailun.reignofcubes2.configuration.TagProperty;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * Configuration for native tags.
 */
@Getter
@Setter
public class TagsConfigurationSection extends RocConfigurationSection {

    private static @NotNull String configurationNameOfTag(@NotNull Class<? extends RocTag> tagClass) {
        TagName tagName = tagClass.getAnnotation(TagName.class);
        return (tagName == null) ? tagClass.getSimpleName().toLowerCase() : tagName.value();
    }

    @DeserializeConfiguration
    public static @Nonnull TagsConfigurationSection load(@Nullable ConfigurationSection config) {
        if(config == null)
            return defaultConfiguration();

        for(RocTag tag : TagsRegistry.list()) {
            String name = configurationNameOfTag(tag.getClass());
            ConfigurationSection section = config.getConfigurationSection(name);
            if(section == null) section = config.createSection(name);

            for(Field field : tag.getClass().getDeclaredFields()) {
                TagProperty annotation = field.getAnnotation(TagProperty.class);
                if(annotation == null) continue;

                Object value;
                if(int.class.equals(field.getType()) || Integer.class.equals(field.getType())) {
                    value = section.getInt(annotation.name(), (int) annotation.defaultValue());
                } else if(double.class.equals(field.getType()) || Double.class.equals(field.getType())) {
                    value = section.getDouble(annotation.name(), annotation.defaultValue());
                } else {
                    throw new RuntimeException("Bad plugin value. Unexpected type '" + field.getType() + "' in tag " + name + ".");
                }

                try {
                    field.setAccessible(true);
                    field.set(tag, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return new TagsConfigurationSection();
    }

    @Override
    public void write(@NotNull ConfigurationSection config) {
        ConfigurationSection regicide = config.createSection("regicide");
        regicide.set("attack-king-flat", regicideAttackFlatKing);
        regicide.set("attack-king-mult", regicideAttackMultiplicativeKing);
        regicide.set("attack-others-flat", regicideAttackFlatOthers);
        regicide.set("attack-others-mult", regicideAttackMultiplicativeOthers);
        regicide.set("defend-king-flat", regicideDefendFlatKing);
        regicide.set("defend-king-mult", regicideDefendMultiplicativeKing);
        regicide.set("defend-others-flat", regicideDefendFlatOthers);
        regicide.set("defend-others-mult", regicideDefendMultiplicativeOthers);
        ConfigurationSection stealer = config.createSection("stealer");
        stealer.set("points-per-hit", stealerPointsPerHit);
    }

    @Override
    public boolean isValid() {
        return ( true );
    }

    public static @Nonnull TagsConfigurationSection defaultConfiguration() {
        TagsConfigurationSection config = new TagsConfigurationSection();

        // Regicide
        config.regicideAttackFlatKing = 0;
        config.regicideAttackMultiplicativeKing = 1.2;
        config.regicideAttackFlatOthers = 0;
        config.regicideAttackMultiplicativeOthers = 1.1;
        config.regicideDefendFlatKing = 0;
        config.regicideDefendMultiplicativeKing = 0.8;
        config.regicideDefendFlatOthers = 0;
        config.regicideDefendMultiplicativeOthers = 0.9;

        // Stealer
        config.stealerPointsPerHit = 1;

        return config;
    }

    @Override
    public String nicePrint(String prefix, String last) {
        return "§7{"
                + prefix + "§7regicide = {"
                + prefix + "  §7attack.king.flat = " + niceDouble(regicideAttackFlatKing, 0)
                + prefix + "  §7attack.king.mult = " + niceDouble(regicideAttackMultiplicativeKing, 1)
                + prefix + "  §7attack.others.flat = " + niceDouble(regicideAttackFlatOthers, 0)
                + prefix + "  §7attack.others.mult = " + niceDouble(regicideAttackMultiplicativeOthers, 1)
                + prefix + "  §7defend.king.flat = " + niceDouble(regicideDefendFlatKing, 0)
                + prefix + "  §7defend.king.mult = " + niceDouble(regicideDefendMultiplicativeKing, 1)
                + prefix + "  §7defend.others.flat = " + niceDouble(regicideDefendFlatOthers, 0)
                + prefix + "  §7defend.others.mult = " + niceDouble(regicideDefendMultiplicativeOthers, 1)
                + prefix + "},"
                + prefix + "§7stealer.points-per-hit = " + niceInt(stealerPointsPerHit, 0)
                + last + "§7}";
    }

}
