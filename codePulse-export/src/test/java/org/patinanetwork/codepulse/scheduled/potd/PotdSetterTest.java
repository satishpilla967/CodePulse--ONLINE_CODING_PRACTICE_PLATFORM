package org.patinanetwork.codepulse.scheduled.potd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codepulse.common.db.repos.potd.POTDRepository;
import org.patinanetwork.codepulse.common.potd.leetcode.LeetcodePotd;
import org.patinanetwork.codepulse.common.potd.leetcode.LeetcodePotdClient;
import org.patinanetwork.codepulse.common.time.StandardizedLocalDateTime;

public class PotdSetterTest {
    private final LeetcodePotdClient leetcodePotdClient = mock(LeetcodePotdClient.class);
    private final POTDRepository potdRepository = mock(POTDRepository.class);

    private final PotdSetter potdSetter = new PotdSetter(leetcodePotdClient, potdRepository);

    @Test
    void testPotdSetterSetPotdWherePotdIsNull() {
        when(leetcodePotdClient.getPotd()).thenReturn(null);

        potdSetter.setPotd();

        verify(potdRepository, never()).getCurrentPOTD();
        verify(potdRepository, never()).createPOTD(any());
    }

    @Test
    void testPotdSetterSetPotdWherePotdIsFoundButStillCurrentPotd() {
        LeetcodePotd potd = new LeetcodePotd("Example title", "Example slug", QuestionDifficulty.Easy);
        Optional<org.patinanetwork.codepulse.common.db.models.potd.POTD> dbPotd = Optional.of(org.patinanetwork
                .codepulse
                .common
                .db
                .models
                .potd
                .POTD
                .builder()
                .createdAt(StandardizedLocalDateTime.now())
                .id(UUID.randomUUID().toString())
                .multiplier(1.3f)
                .slug(potd.getTitleSlug())
                .title(potd.getTitle())
                .build());

        when(leetcodePotdClient.getPotd()).thenReturn(potd);
        when(potdRepository.getCurrentPOTD()).thenReturn(dbPotd);

        potdSetter.setPotd();

        verify(potdRepository, times(1)).getCurrentPOTD();
        verify(potdRepository, never()).createPOTD(any());
    }

    @Test
    void testPotdSetterSetPotdWherePotdIsFoundButNoCurrentPotdYet() {
        LeetcodePotd potd = new LeetcodePotd("Example title", "Example slug", QuestionDifficulty.Easy);
        when(leetcodePotdClient.getPotd()).thenReturn(potd);
        when(potdRepository.getCurrentPOTD()).thenReturn(Optional.empty());

        potdSetter.setPotd();

        ArgumentCaptor<org.patinanetwork.codepulse.common.db.models.potd.POTD> potdCaptor = ArgumentCaptor.captor();
        verify(potdRepository, times(1)).getCurrentPOTD();
        verify(potdRepository, times(1)).createPOTD(potdCaptor.capture());

        var dbPotd = potdCaptor.getValue();
        assertNotNull(dbPotd);
        assertEquals(potd.getTitle(), dbPotd.getTitle());
        assertEquals(potd.getTitleSlug(), dbPotd.getSlug());
        assertEquals(potd.getDifficulty(), potd.getDifficulty());
    }

    @Test
    void testPotdSetterSetPotdWherePotdIsFoundAndDoesntMatchOldPotd() {
        LeetcodePotd potd = new LeetcodePotd("Example title", "Example slug", QuestionDifficulty.Easy);
        Optional<org.patinanetwork.codepulse.common.db.models.potd.POTD> oldDbPotd = Optional.of(org.patinanetwork
                .codepulse
                .common
                .db
                .models
                .potd
                .POTD
                .builder()
                .createdAt(StandardizedLocalDateTime.now())
                .id(UUID.randomUUID().toString())
                .multiplier(1.3f)
                .slug("old slug")
                .title("old title")
                .build());

        when(leetcodePotdClient.getPotd()).thenReturn(potd);
        when(potdRepository.getCurrentPOTD()).thenReturn(oldDbPotd);

        potdSetter.setPotd();

        ArgumentCaptor<org.patinanetwork.codepulse.common.db.models.potd.POTD> potdCaptor = ArgumentCaptor.captor();
        verify(potdRepository, times(1)).getCurrentPOTD();
        verify(potdRepository, times(1)).createPOTD(potdCaptor.capture());

        var dbPotd = potdCaptor.getValue();
        assertNotNull(dbPotd);
        assertEquals(potd.getTitle(), dbPotd.getTitle());
        assertEquals(potd.getTitleSlug(), dbPotd.getSlug());
    }
}
