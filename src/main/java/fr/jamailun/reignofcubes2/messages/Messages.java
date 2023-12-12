package fr.jamailun.reignofcubes2.messages;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Messages {

    private String defaultLanguage = "fr";
    private final Map<String, String> shared = new HashMap<>();
    private final Map<String, Map<String, String>> messages = new HashMap<>();
    private final MiniMessage messageFormatter = MiniMessage.builder()
            .tags(TagResolver.builder().resolver(StandardTags.defaults()).build())
            .build();

    private void load(ConfigurationSection config) {
        defaultLanguage = config.getString("default", "fr");
        ConfigurationSection section = config.getConfigurationSection("messages");
        if(section == null)
            return;
        for(String lan : section.getKeys(false)) {
            ConfigurationSection messages = section.getConfigurationSection(lan);
            assert messages != null;

            Map<String, String> entries = new HashMap<>();
            for(String key : messages.getKeys(true)) {
                Object obj = messages.get(key);
            //    ReignOfCubes2.info("["+lan+"] : "+((obj instanceof String)?"[valid]":"")+" '" + key + "' => {"+obj+"}");
                if(obj instanceof String str) {
                    entries.put(key, str);
                }
            }
            if(lan.equalsIgnoreCase("_all")) {
                this.shared.putAll(entries);
            } else {
                this.messages.put(lan, entries);
            }
        }
    }

    private Messages() {
        init();
    }

    private void init() {
        // Get real messages.yml
        File file = ReignOfCubes2.getFile("messages.yml");
        if(assertFileExists(file)) {
            createFileFromJar(file);
        }

        // Read
        load(YamlConfiguration.loadConfiguration(file));
    }

    private static boolean assertFileExists(File file) {
        assert file != null : "File is null";
        if(file.exists())
            return false;
        try {
            assert file.createNewFile() : "Could not create file.";
        } catch(IOException e) {
            assert false : "Could not create file: " + e.getMessage();
        }
        return true;
    }

    private static void createFileFromJar(File file) {
        try(InputStream in = Messages.class.getClassLoader().getResourceAsStream("messages.yml")) {
            assert in != null : "Could not get ressource messages.yml";
            byte[] data = in.readAllBytes();

            try(FileOutputStream out = new FileOutputStream(file)) {
                out.write(data);
                out.flush();
            }
        } catch(IOException e) {
            throw new RuntimeException("Could not create Messages instance : " + e.getMessage());
        }
    }

    private static Messages INSTANCE;
    private static Messages instance() {
        if(INSTANCE == null) {
            INSTANCE = new Messages();
        }
        return INSTANCE;
    }

    public @Nullable String getEntry(@Nullable String language, String entry) {
        if(shared.containsKey(entry)) return shared.get(entry);
        if(language == null)
            language = defaultLanguage;
        if(!messages.containsKey(language)) {
            ReignOfCubes2.warning("Language not supported: " + language);
            language = defaultLanguage;
        }
        return messages.get(language).get(entry);
    }

    public static @NonNull String get(@Nullable String language, String entry) {
        String msg = instance().getEntry(language, entry);
        return msg == null ? "{unknown{"+language+";"+entry+"}}" : msg;
    }

    public static String format(String language, String entry, Object... args) {
        return niceFormat(get(language, entry), args);
    }

    public static void send(Player p, String l, String e, Object... a) {
        String msg = format(l, e, a);
        Component cmp = instance().messageFormatter.deserialize(msg);
        p.sendMessage(cmp);
    }

    public static Component formatComponent(String language, String entry, Object... args) {
        return instance().messageFormatter.deserialize(format(language, entry, args));
    }

    private static String niceFormat(String string, Object... values) {
        assert string != null;
        if(values == null) return string;

        for(int i = 0; i < values.length; i++) {
            string = string.replace("{" + i + "}", Objects.toString(values[i]));
        }
        return string;
    }

    public static void reload() {
        INSTANCE.messages.clear();
        INSTANCE.shared.clear();
        INSTANCE.init();
    }

    public static String getDefaultLanguage() {
        return instance().defaultLanguage;
    }

}
