package fr.jamailun.reignofcubes2.api.configuration;

public class UnknownConfigurationException extends Exception {

    public UnknownConfigurationException(String path) {
        super("Unknown path = '" + path + "'.");
    }

    public UnknownConfigurationException(Class<?> clazz) {
        super("Unknown type '" + clazz + "'.");
    }

}
