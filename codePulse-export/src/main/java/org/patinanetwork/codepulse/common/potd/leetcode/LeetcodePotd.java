package org.patinanetwork.codepulse.common.potd.leetcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;

/**
 * A narrow, self-contained representation of LeetCode's "Problem of the Day" as fetched from LeetCode's public
 * GraphQL API. This is intentionally decoupled from the (now removed) LeetCode submission-polling client — POTD
 * syncing is a distinct, retained feature, not part of the submission-polling pipeline that was replaced by the
 * in-app Judge0-backed judge.
 */
@Getter
@AllArgsConstructor
public class LeetcodePotd {
    private final String title;
    private final String titleSlug;
    private final QuestionDifficulty difficulty;
}
