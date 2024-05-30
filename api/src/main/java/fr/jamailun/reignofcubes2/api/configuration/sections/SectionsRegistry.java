package fr.jamailun.reignofcubes2.api.configuration.sections;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public final class SectionsRegistry {
    private SectionsRegistry() {}

    private final static Map<Class<? extends RocConfigurationSection>, Method> DESERIALIZE_METHODS = new HashMap<>();

    public static <T extends RocConfigurationSection> void register(Class<T> sectionClass) throws InvalidAnnotationException {
        Method defaultMethod = null;
        Method deserializeMethod = null;
        for(Method method : sectionClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                if(method.isAnnotationPresent(DefaultConfiguration.class)) {
                    if(defaultMethod != null)
                        throw new InvalidAnnotationException("Duplicate 'DefaultConfiguration' annotation in " + sectionClass);
                    // input must be a ConfigurationSection
                    checkParameters(sectionClass+"/DefaultConfiguration", method.getParameters(), ConfigurationSection.class);
                    defaultMethod = method;
                }

                else if(method.isAnnotationPresent(DeserializeConfiguration.class)) {
                    if(deserializeMethod != null)
                        throw new InvalidAnnotationException("Duplicate 'DeserializeConfiguration' annotation in " + sectionClass);
                    checkParameters(sectionClass+"/DeserializeConfiguration", method.getParameters());
                    deserializeMethod = method;
                }
            }
        }
        if(defaultMethod == null)
            throw new InvalidAnnotationException("Missing 'DefaultConfiguration' annotation in " + sectionClass);
        if(deserializeMethod == null)
            throw new InvalidAnnotationException("Missing 'DeserializeConfiguration' annotation in " + sectionClass);

        // Save
        DESERIALIZE_METHODS.put(sectionClass, deserializeMethod);
    }

    private static void checkParameters(String name, Parameter[] params, Class<?>... expected) throws InvalidAnnotationException {
        if(params.length != expected.length)
            throw new InvalidAnnotationException("Invalid parameter for " + name + " : expected " + expected.length + " arguments. Got" + params.length);
        for(int i = 0; i < params.length; i++) {
            if( ! params[i].getType().equals(expected[i])) {
                throw new InvalidAnnotationException("Invalid parameter for " + name + " : expected " + expected[i] + " argument on index "+i+". Got" + params[i].getType());
            }
        }
    }

}
