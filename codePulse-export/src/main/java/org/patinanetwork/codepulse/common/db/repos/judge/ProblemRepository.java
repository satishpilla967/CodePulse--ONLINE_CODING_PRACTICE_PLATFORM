package org.patinanetwork.codepulse.common.db.repos.judge;

import java.util.List;
import java.util.Optional;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;

public interface ProblemRepository {

    Problem createProblem(Problem problem);

    Optional<Problem> getRandomProblem();

    Problem getProblemById(String id);

    Problem getProblemBySlug(String slug);

    List<Problem> getAllProblems();

    void updateProblem(Problem problem);

    void deleteProblem(String id);
}
