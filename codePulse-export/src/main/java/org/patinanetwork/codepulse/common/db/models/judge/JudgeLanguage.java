package org.patinanetwork.codepulse.common.db.models.judge;

import io.swagger.v3.oas.annotations.media.Schema;

/** Supported source languages, mapped to Judge0 CE language IDs in {@link Judge0LanguageMapper}. */
@Schema(description = "JudgeLanguage")
public enum JudgeLanguage {
    C,
    CPP,
    JAVA,
    PYTHON3,
    JAVASCRIPT,
    TYPESCRIPT,
    GO,
    RUST,
    CSHARP,
    KOTLIN,
}
