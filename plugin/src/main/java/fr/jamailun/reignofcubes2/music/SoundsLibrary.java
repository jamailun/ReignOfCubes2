package fr.jamailun.reignofcubes2.music;

import fr.jamailun.reignofcubes2.api.sounds.SoundEffect;
import org.bukkit.Sound;

public final class SoundsLibrary {
    private SoundsLibrary() {}

    public final static SoundEntry DEAD = new SoundEntry(Sound.ENTITY_ALLAY_HURT, 0.5f);
    public final static SoundEntry DEAD_AS_KING = new SoundEntry(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.9f);
    public final static SoundEntry KILLED = new SoundEntry(Sound.ENTITY_ALLAY_ITEM_GIVEN, 1.3f);
    public final static SoundEntry KILLED_KING = new SoundEntry(Sound.ENTITY_ALLAY_ITEM_GIVEN, 1.3f);//TODO
    public final static SoundEntry KILLED_AS_KING = new SoundEntry(Sound.ENTITY_ALLAY_ITEM_GIVEN, 1.3f);//TODO

    public final static SoundEntry PLAYER_JOINED = new SoundEntry(Sound.ITEM_BOOK_PAGE_TURN, 1.3f);


    public final static SoundEntry GAME_STARTED_1 = new SoundEntry(Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 0.95f);
    public final static SoundEntry GAME_STARTED_2 = new SoundEntry(Sound.ENTITY_WOLF_HOWL, 1.1f);


    // BLOCK_BEACON_POWER_SELECT
    // ENTITY_ELDER_GUARDIAN_DEATH
    public final static SoundEntry KING_CROWNED = new SoundEntry(Sound.ITEM_TRIDENT_THUNDER, 1.3f);
    public final static SoundEntry KING_KILLED = new SoundEntry(Sound.ENTITY_ENDER_DRAGON_DEATH, 0.92f);


    public final static SoundEntry KIT_BOUGHT = new SoundEntry(Sound.ENTITY_PLAYER_LEVELUP, 0.9f);
    public final static SoundEntry TOO_POOR = new SoundEntry(Sound.ENTITY_VILLAGER_NO, 0.82f);

    // Game counter
    public final static SoundEntry GAME_COUNTER_TICK_LOW = new SoundEntry(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 0.25f);
    public final static SoundEntry GAME_COUNTER_TICK_MIDDLE = new SoundEntry(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1f);
    public final static SoundEntry GAME_COUNTER_TICK_HIGH = new SoundEntry(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 2f);

    // Ceremony
    public final static Sound CEREMONY_DING = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    public final static SoundEntry CEREMONY_STARTS = new SoundEntry(Sound.ENTITY_GHAST_HURT, 1.1f);
    public final static SoundEntry CEREMONY_STARTS_STEAL = new SoundEntry(Sound.ENTITY_GHAST_WARN, 0.8f);
    public final static SoundEntry CEREMONY_FAILS = new SoundEntry(Sound.ENTITY_GHAST_SCREAM, 0.75f);

    // ----
    public record SoundEntry(Sound sound, float pitch) implements SoundEffect {

        @Override
        public float volume() {
            return 1;
        }
    }
}
