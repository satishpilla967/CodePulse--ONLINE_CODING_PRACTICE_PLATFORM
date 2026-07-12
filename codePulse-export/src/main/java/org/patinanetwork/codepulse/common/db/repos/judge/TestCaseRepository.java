package org.patinanetwork.codepulse.common.db.repos.judge;

import java.util.List;
import org.patinanetwork.codepulse.common.db.models.judge.TestCase;

public interface TestCaseRepository {

    TestCase createTestCase(TestCase testCase);

    List<TestCase> getTestCasesByProblemId(String problemId);

    List<TestCase> getPublicTestCasesByProblemId(String problemId);

    void deleteTestCase(String id);
}
