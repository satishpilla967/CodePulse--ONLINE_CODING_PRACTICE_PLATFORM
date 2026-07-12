package org.patinanetwork.codepulse.common.db.repos.judge;

import java.util.List;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemStarterCode;

public interface ProblemStarterCodeRepository {

    ProblemStarterCode createStarterCode(ProblemStarterCode starterCode);

    List<ProblemStarterCode> getStarterCodeByProblemId(String problemId);

    ProblemStarterCode getStarterCodeByProblemIdAndLanguage(String problemId, JudgeLanguage language);
}
