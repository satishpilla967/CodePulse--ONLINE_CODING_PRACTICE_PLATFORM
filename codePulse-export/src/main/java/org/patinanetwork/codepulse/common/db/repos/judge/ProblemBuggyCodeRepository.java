package org.patinanetwork.codepulse.common.db.repos.judge;

import java.util.List;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemBuggyCode;

public interface ProblemBuggyCodeRepository {

    ProblemBuggyCode createBuggyCode(ProblemBuggyCode buggyCode);

    List<ProblemBuggyCode> getBuggyCodeByProblemId(String problemId);

    ProblemBuggyCode getBuggyCodeByProblemIdAndLanguage(String problemId, JudgeLanguage language);

    /** Distinct problems that have at least one buggy-code entry, for the debug-challenges list page. */
    List<Problem> getProblemsWithBuggyCode();
}
