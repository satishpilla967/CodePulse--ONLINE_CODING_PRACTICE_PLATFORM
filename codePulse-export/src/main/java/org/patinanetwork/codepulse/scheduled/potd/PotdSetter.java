package org.patinanetwork.codepulse.scheduled.potd;

import java.util.Optional;
import org.patinanetwork.codepulse.common.db.models.potd.POTD;
import org.patinanetwork.codepulse.common.db.repos.potd.POTDRepository;
import org.patinanetwork.codepulse.common.leetcode.score.ScoreCalculator;
import org.patinanetwork.codepulse.common.potd.leetcode.LeetcodePotd;
import org.patinanetwork.codepulse.common.potd.leetcode.LeetcodePotdClient;
import org.patinanetwork.codepulse.common.time.StandardizedLocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!ci")
public class PotdSetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PotdSetter.class);

    private final LeetcodePotdClient leetcodePotdClient;
    private final POTDRepository potdRepository;

    public PotdSetter(final LeetcodePotdClient leetcodePotdClient, final POTDRepository potdRepository) {
        this.leetcodePotdClient = leetcodePotdClient;
        this.potdRepository = potdRepository;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 5)
    public void setPotd() {
        LeetcodePotd leetcodePotd = leetcodePotdClient.getPotd();

        if (leetcodePotd == null) {
            LOGGER.warn("No POTD was been returned.");
            return;
        }

        Optional<POTD> potd = potdRepository.getCurrentPOTD();

        if (potd.map(p -> p.getTitle().equals(leetcodePotd.getTitle())).orElse(false)) {
            // It's already the latest POTD, don't want to do it again.
            LOGGER.info("POTD has already been set before, will not be doing it again.");
            return;
        }

        potdRepository.createPOTD(POTD.builder()
                .title(leetcodePotd.getTitle())
                .slug(leetcodePotd.getTitleSlug())
                .multiplier(ScoreCalculator.calculateMultiplier((leetcodePotd.getDifficulty())))
                .createdAt(StandardizedLocalDateTime.now())
                .build());
    }
}
