package org.patinanetwork.codepulse.common.judge.score;

import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codepulse.common.leetcode.score.ScoreCalculator;

/**
 * Scoring for internally-authored judge problems. Unlike LeetCode-sourced questions there is no acceptance-rate
 * signal, so this uses only {@link QuestionDifficulty} via {@link ScoreCalculator#calculateMultiplier}.
 */
public final class JudgeScoreCalculator {

    private static final int EASY_BASE = 100;
    private static final int MEDIUM_BASE = 300;
    private static final int HARD_BASE = 600;

    private JudgeScoreCalculator() {}

    public static int calculateScore(final QuestionDifficulty difficulty) {
        int base =
                switch (difficulty) {
                    case Easy -> EASY_BASE;
                    case Medium -> MEDIUM_BASE;
                    case Hard -> HARD_BASE;
                };
        float multiplier = ScoreCalculator.calculateMultiplier(difficulty);
        return Math.round(base * multiplier);
    }
}
