package fr.jamailun.reignofcubes2.api.entities;

public interface RocMessager {

    String getLanguage();

    void setLanguage(String lan);

    String i18n(String entry, Object... args);

    void sendMessage(String entry, Object... args);

}
