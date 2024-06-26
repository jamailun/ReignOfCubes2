package fr.jamailun.reignofcubes2.api.configuration.sections;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Manages the sections of the configuration.
 */
public final class RocConfigurationSectionsRegistry {
    private RocConfigurationSectionsRegistry() {}

    private final static Map<Class<? extends RocConfigurationSection>, Method> DESERIALIZE_METHODS = new HashMap<>();

    public static <T extends RocConfigurationSection> void registerSection(Class<T> sectionClass) {
        if(DESERIALIZE_METHODS.containsKey(sectionClass)) {
            ReignOfCubes2.logWarning("You tried to register " + sectionClass + " as section twice.");
            return;
        }

        Method deserializeMethod = null;
        for(Method method : sectionClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                if(method.isAnnotationPresent(DeserializeConfiguration.class)) {
                    if(deserializeMethod != null)
                        throw new RuntimeException("Duplicate 'DeserializeConfiguration' annotation in " + sectionClass);
                    checkParameters(sectionClass+"/DeserializeConfiguration", method.getParameters(), ConfigurationSection.class);
                    checkOutput(sectionClass+"/DeserializeConfiguration", method);
                    deserializeMethod = method;
                }
            }
        }
        if(deserializeMethod == null)
            throw new RuntimeException("Missing 'DeserializeConfiguration' annotation in " + sectionClass);

        // Save
        DESERIALIZE_METHODS.put(sectionClass, deserializeMethod);
    }

    private static void checkParameters(String name, Parameter[] params, Class<?>... expected) {
        if(params.length != expected.length)
            throw new RuntimeException("Invalid parameter for " + name + " : expected " + expected.length + " arguments. Got" + params.length);
        for(int i = 0; i < params.length; i++) {
            if( ! params[i].getType().equals(expected[i])) {
                throw new RuntimeException("Invalid parameter for " + name + " : expected " + expected[i] + " argument on index "+i+". Got" + params[i].getType());
            }
        }
    }

    private static void checkOutput(String name, Method method) {
        Class<?> out = method.getReturnType();
        if(! RocConfigurationSection.class.isAssignableFrom(out))
            throw new RuntimeException(name + " was supposed to return a RocSection, but instead returns " + out);
    }

    public static @NotNull List<RocConfigurationSection> generateSections(@NotNull ConfigurationSection root) {
        List<RocConfigurationSection> list = new ArrayList<>();
        for(Map.Entry<Class<? extends RocConfigurationSection>, Method> entry : DESERIALIZE_METHODS.entrySet()) {
            try {
                RocConfigurationSection section = (RocConfigurationSection) entry.getValue().invoke(null, root);
                list.add(section);
            } catch (Exception e) {
                throw new RuntimeException("Could not invoke " + entry.getValue(), e);
            }
        }
        return list;
    }

}
