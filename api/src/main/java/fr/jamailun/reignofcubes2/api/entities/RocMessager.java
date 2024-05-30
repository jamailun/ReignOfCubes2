package fr.jamailun.reignofcubes2.api.entities;

public interface RocMessager {

    String getLanguage();

    void setLanguage(String lan);

    void sendMessage(String entry, Object... args);

}
