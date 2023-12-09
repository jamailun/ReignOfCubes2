package fr.jamailun.reignofcubes2.players;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Messages {

    private final Map<Language, Map<String, String>> messages = new HashMap<>();

    private final MiniMessage messageFormatter = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.decorations())
                    .build()
            ).build();

    private Messages() {
        for(Language language : Language.values()) {
            Map<String, String> messages = new HashMap<>();
            String file = "i18n/messages_" + language.name().toLowerCase() + ".properties";
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(file))))) {
                String line;
                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    if(line.isBlank() || line.startsWith("#")) continue;
                    String[] parts = line.split("=", 2);
                    if(parts.length != 2) {
                        Bukkit.getLogger().severe("Invalid config line in " + file + " : " + line);
                        continue;
                    }
                    messages.put(parts[0], parts[1]);
                }
            } catch(IOException e) {
                throw new RuntimeException("Could not open '" + file + "' in ressources.");
            }
            this.messages.put(language, messages);
        }
    }

    private static Messages INSTANCE;
    private static Messages instance() {
        if(INSTANCE == null) {
            INSTANCE = new Messages();
        }
        return INSTANCE;
    }

    public static String get(Language language, String entry) {
        return instance().messages.get(language).get(entry);
    }

    public static String format(Language language, String entry, Object... args) {
        return String.format(get(language, entry), args);
    }

    public static void send(Player p, Language l, String e, Object... a) {
        String msg = format(l, e, a);
        Component cmp = instance().messageFormatter.deserialize(msg);
        p.sendMessage(cmp);
    }

    public enum Language {
        FR, EN
    }
}
