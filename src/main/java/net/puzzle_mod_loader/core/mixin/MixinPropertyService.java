package net.puzzle_mod_loader.core.mixin;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.HashMap;

public class MixinPropertyService implements IGlobalPropertyService {
    static class Key implements IPropertyKey {

        private final String key;

        Key(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return this.key;
        }
    }

    private static HashMap<String, Object> mixinProperties = new HashMap<>();

    @Override
    public IPropertyKey resolveKey(String name) {
        return new Key(name);
    }

    /**
     * Get a value from the blackboard and duck-type it to the specified type
     *
     * @param key blackboard key
     * @return value
     * @param <T> duck type
     */
    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getProperty(IPropertyKey key) {
        return (T) mixinProperties.get(key.toString());
    }

    /**
     * Put the specified value onto the blackboard
     *
     * @param key blackboard key
     * @param value new value
     */
    @Override
    public final void setProperty(IPropertyKey key, Object value) {
        mixinProperties.put(key.toString(), value);
    }

    /**
     * Get the value from the blackboard but return <tt>defaultValue</tt> if the
     * specified key is not set.
     *
     * @param key blackboard key
     * @param defaultValue value to return if the key is not set or is null
     * @return value from blackboard or default value
     * @param <T> duck type
     */
    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) mixinProperties.getOrDefault(key.toString(), defaultValue);
    }

    /**
     * Get a string from the blackboard, returns default value if not set or
     * null.
     *
     * @param key blackboard key
     * @param defaultValue default value to return if the specified key is not
     *      set or is null
     * @return value from blackboard or default
     */
    @Override
    public final String getPropertyString(IPropertyKey key, String defaultValue) {
        return mixinProperties.getOrDefault(key.toString(), defaultValue).toString();
    }
}
