package org.patinanetwork.codepulse.scheduled.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

public class PgChannelTest {

    @Test
    void testEnumValues() {
        assertEquals("upsertLobbyChannel", PgChannel.UPSERT_LOBBY.getChannelName());
    }

    @Test
    void testList() {
        List<PgChannel> channels = PgChannel.list();

        assertEquals(1, channels.size());
        assertTrue(channels.contains(PgChannel.UPSERT_LOBBY));
    }

    @Test
    void testFromChannelNameValid() {
        assertEquals(PgChannel.UPSERT_LOBBY, PgChannel.fromChannelName("upsertLobbyChannel"));
    }

    @Test
    void testFromChannelNameInvalid() {
        assertThrows(NoSuchElementException.class, () -> {
            PgChannel.fromChannelName("nonExistentChannel");
        });
    }

    @Test
    void testFromChannelNameNull() {
        assertThrows(NoSuchElementException.class, () -> {
            PgChannel.fromChannelName(null);
        });
    }

    @Test
    void testFromChannelNameEmpty() {
        assertThrows(NoSuchElementException.class, () -> {
            PgChannel.fromChannelName("");
        });
    }

    @Test
    void testToString() {
        String upsertLobbyString = PgChannel.UPSERT_LOBBY.toString();

        assertTrue(upsertLobbyString.contains("UPSERT_LOBBY"));
    }
}
