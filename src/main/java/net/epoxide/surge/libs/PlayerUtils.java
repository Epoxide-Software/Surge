package net.epoxide.surge.libs;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.Resources;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlayerUtils {

    /**
     * A cache for storing known username uuid pairs.
     */
    public static BiMap<String, UUID> PROFILE_CACHE = HashBiMap.<String, UUID> create();

    /**
     * Attempts to get the username associated with a UUID from Mojang. If no username is
     * detected or an exception takes place, the exception message will be returned.
     *
     * @param uuid The UUID to search for.
     * @return The name of the player associated to that uuid.
     */
    public static String getPlayerNameFromUUID (UUID uuid) {

        if (PROFILE_CACHE.containsValue(uuid))
            return PROFILE_CACHE.inverse().get(uuid);

        String name = null;

        try {

            final BufferedReader reader = Resources.asCharSource(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "")), StandardCharsets.UTF_8).openBufferedStream();
            final JsonReader json = new JsonReader(reader);
            json.beginObject();

            while (json.hasNext())
                if (json.nextName().equals("name"))
                    name = json.nextString();

                else
                    json.skipValue();

            json.endObject();
            json.close();
            reader.close();
        }

        catch (final Exception exception) {

            Constants.LOG.warn("Could not get name for " + uuid + " " + exception.getMessage());
            name = exception.getMessage();
        }

        return name;
    }

    /**
     * Attempts to get the UUID associated with a username from Mojang. If no uuid is found,
     * null will return.
     *
     * @param username The username to look for.
     * @return The UUID for the player, or null if it could not be found.
     */
    public static UUID getUUIDFromName (String username) {

        if (PROFILE_CACHE.containsKey(username))
            return PROFILE_CACHE.get(username);

        UUID uuid = null;

        try {

            final BufferedReader reader = Resources.asCharSource(new URL("https://api.mojang.com/users/profiles/minecraft/" + username), StandardCharsets.UTF_8).openBufferedStream();
            final JsonReader json = new JsonReader(reader);

            json.beginObject();

            while (json.hasNext())
                if (json.nextName().equals("id"))
                    uuid = fixStrippedUUID(json.nextString());

                else
                    json.skipValue();

            json.endObject();
            json.close();
            reader.close();
        }

        catch (final Exception exception) {

            Constants.LOG.warn("Could not get name for " + username + " " + exception.getMessage());
        }

        return uuid;
    }

    /**
     * Attempts to fix a stripped UUID. Usually used to fix stripped uuid strings from Mojang.
     *
     * @param uuidString The UUID string to fix.
     * @return The fixed UUID, or null if the uuid string is invalid.
     */
    public static UUID fixStrippedUUID (String uuidString) {

        return uuidString.length() != 32 ? null : UUID.fromString(uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20, 32));
    }
}
