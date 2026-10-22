package dansplugins.democracy.objects;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SavableFieldsTest {

    @Test
    void requireReturnsThePresentValue() {
        Map<String, String> data = new HashMap<>();
        data.put("factionName", "TestFaction");

        assertEquals("TestFaction", SavableFields.require(data, "factionName"));
    }

    @Test
    void requireAcceptsAnEmptyString() {
        Map<String, String> data = new HashMap<>();
        data.put("factionName", "");

        assertEquals("", SavableFields.require(data, "factionName"));
    }

    @Test
    void requireRejectsAMissingKeyAndNamesIt() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> SavableFields.require(new HashMap<>(), "creatorUUID"));

        assertEquals("Saved record is missing the required field 'creatorUUID'.", thrown.getMessage());
    }

    @Test
    void requireRejectsAKeyMappedToNull() {
        Map<String, String> data = new HashMap<>();
        data.put("creatorUUID", null);

        assertThrows(IllegalArgumentException.class, () -> SavableFields.require(data, "creatorUUID"));
    }

    @Test
    void uuidListToJsonWritesAnEmptyListAsEmptyBrackets() {
        assertEquals("[]", SavableFields.uuidListToJson(new ArrayList<>()));
    }

    @Test
    void uuidListToJsonWritesEachUUIDAsAQuotedString() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");

        assertEquals("[\"00000000-0000-0000-0000-000000000001\"]",
                SavableFields.uuidListToJson(Collections.singletonList(uuid)));
    }

    @Test
    void uuidListRoundTripPreservesOrderAndDuplicates() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        List<UUID> uuids = Arrays.asList(second, first, second);

        assertEquals(uuids, SavableFields.uuidListFromJson(SavableFields.uuidListToJson(uuids)));
    }

    @Test
    void uuidListFromJsonReadsEmptyInputAsAnEmptyList() {
        List<UUID> uuids = SavableFields.uuidListFromJson("");

        assertNotNull(uuids);
        assertTrue(uuids.isEmpty());
    }

    @Test
    void uuidListFromJsonReadsJsonNullAsAnEmptyList() {
        List<UUID> uuids = SavableFields.uuidListFromJson("null");

        assertNotNull(uuids);
        assertTrue(uuids.isEmpty());
    }

    @Test
    void uuidListFromJsonReturnsAModifiableList() {
        List<UUID> uuids = SavableFields.uuidListFromJson("[]");

        uuids.add(UUID.randomUUID());

        assertEquals(1, uuids.size());
    }

    @Test
    void uuidListFromJsonRejectsMalformedJson() {
        assertThrows(JsonSyntaxException.class, () -> SavableFields.uuidListFromJson("{not a list"));
    }

    @Test
    void uuidListFromJsonRejectsAMalformedUUID() {
        assertThrows(IllegalArgumentException.class, () -> SavableFields.uuidListFromJson("[\"not-a-uuid\"]"));
    }
}
