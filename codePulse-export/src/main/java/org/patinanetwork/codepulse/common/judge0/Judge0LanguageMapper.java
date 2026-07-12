package org.patinanetwork.codepulse.common.judge0;

import java.util.Map;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;

/** Maps our internal {@link JudgeLanguage} enum to Judge0 CE's numeric language IDs (default Judge0 CE catalog). */
public final class Judge0LanguageMapper {

    private static final Map<JudgeLanguage, Integer> LANGUAGE_ID_MAP = Map.of(
            JudgeLanguage.C, 50,
            JudgeLanguage.CPP, 54,
            JudgeLanguage.JAVA, 62,
            JudgeLanguage.PYTHON3, 71,
            JudgeLanguage.JAVASCRIPT, 63,
            JudgeLanguage.TYPESCRIPT, 74,
            JudgeLanguage.GO, 60,
            JudgeLanguage.RUST, 73,
            JudgeLanguage.CSHARP, 51,
            JudgeLanguage.KOTLIN, 78);

    private Judge0LanguageMapper() {}

    public static int toJudge0LanguageId(final JudgeLanguage language) {
        Integer id = LANGUAGE_ID_MAP.get(language);
        if (id == null) {
            throw new IllegalArgumentException("Unsupported judge language: " + language);
        }
        return id;
    }
}
