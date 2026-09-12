package dansplugins.democracy.objects;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Shared helpers for the {@code save()}/{@code load()} maps of the domain objects.
 * @author Daniel McCoy Stephenson
 */
final class SavableFields {
    private static final Type UUID_LIST_TYPE = new TypeToken<ArrayList<UUID>>() {}.getType();
    private static final Gson GSON = new Gson();

    private SavableFields() {
    }

    /**
     * Returns the value stored under {@code key}, or rejects the record if it is absent.
     * A record with a missing field is refused outright rather than loaded with a null
     * in it, so a corrupt file fails at load time instead of at the first command.
     */
    static String require(Map<String, String> data, String key) {
        String value = data.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Saved record is missing the required field '" + key + "'.");
        }
        return value;
    }

    static String uuidListToJson(List<UUID> uuids) {
        return GSON.toJson(uuids);
    }

    static List<UUID> uuidListFromJson(String json) {
        List<UUID> uuids = GSON.fromJson(json, UUID_LIST_TYPE);
        return uuids == null ? new ArrayList<>() : uuids;
    }
}
