package fr.jamailun.reignofcubes2.configuration;

import org.bukkit.Sound;

public class SoundsLibrary {

    public final static SoundEntry DEAD = new SoundEntry(Sound.ENTITY_ALLAY_HURT, 0.5f);
    public final static SoundEntry DEAD_AS_KING = new SoundEntry(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.9f);
    public final static SoundEntry KILLED = new SoundEntry(Sound.ENTITY_ALLAY_ITEM_GIVEN, 1.3f);
    public final static SoundEntry KILLED_KING = new SoundEntry(Sound.ENTITY_ALLAY_ITEM_GIVEN, 1.3f);//TODO
    public final static SoundEntry KILLED_AS_KING = new SoundEntry(Sound.ENTITY_ALLAY_ITEM_GIVEN, 1.3f);//TODO

    public final static SoundEntry PLAYER_JOINED = new SoundEntry(Sound.ITEM_BOOK_PAGE_TURN, 1.3f);


    // BLOCK_BEACON_POWER_SELECT
    // ENTITY_ELDER_GUARDIAN_DEATH
    public final static SoundEntry KING_CROWNED = new SoundEntry(Sound.ITEM_TRIDENT_THUNDER, 1.3f);
    public final static SoundEntry KING_KILLED = new SoundEntry(Sound.ENTITY_ENDER_DRAGON_DEATH, 0.92f);


    public final static SoundEntry KIT_BOUGHT = new SoundEntry(Sound.ENTITY_PLAYER_LEVELUP, 0.9f);

    // Game counter
    public final static SoundEntry GAME_COUNTER_TICK_LOW = new SoundEntry(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 0.25f);
    public final static SoundEntry GAME_COUNTER_TICK_MIDDLE = new SoundEntry(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1f);
    public final static SoundEntry GAME_COUNTER_TICK_HIGH = new SoundEntry(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 2f);

    // Ceremony
    public final static Sound CEREMONY_DING = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

    // ----
    public record SoundEntry(Sound sound, float pitch)
    {}
}
