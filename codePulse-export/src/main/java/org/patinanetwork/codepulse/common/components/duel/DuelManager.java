package org.patinanetwork.codepulse.common.components.duel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codepulse.common.db.models.lobby.Lobby;
import org.patinanetwork.codepulse.common.db.models.lobby.LobbyQuestion;
import org.patinanetwork.codepulse.common.db.models.lobby.LobbyStatus;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmission;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;
import org.patinanetwork.codepulse.common.db.models.lobby.player.LobbyPlayer;
import org.patinanetwork.codepulse.common.db.models.lobby.player.LobbyPlayerQuestion;
import org.patinanetwork.codepulse.common.db.models.user.User;
import org.patinanetwork.codepulse.common.db.repos.judge.JudgeSubmissionRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemRepository;
import org.patinanetwork.codepulse.common.db.repos.lobby.LobbyQuestionRepository;
import org.patinanetwork.codepulse.common.db.repos.lobby.LobbyRepository;
import org.patinanetwork.codepulse.common.db.repos.lobby.player.LobbyPlayerRepository;
import org.patinanetwork.codepulse.common.db.repos.lobby.player.question.LobbyPlayerQuestionRepository;
import org.patinanetwork.codepulse.common.db.repos.question.QuestionRepository;
import org.patinanetwork.codepulse.common.db.repos.user.UserRepository;
import org.patinanetwork.codepulse.common.dto.judge.ProblemDto;
import org.patinanetwork.codepulse.common.dto.lobby.DuelData;
import org.patinanetwork.codepulse.common.dto.lobby.LobbyDto;
import org.patinanetwork.codepulse.common.dto.question.QuestionDto;
import org.patinanetwork.codepulse.common.dto.user.UserDto;
import org.patinanetwork.codepulse.common.time.StandardizedOffsetDateTime;
import org.patinanetwork.codepulse.common.utils.function.FunctionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DuelManager {

    @FunctionalInterface
    private interface DuelSupplier<T> {
        T get() throws DuelException;
    }

    @FunctionalInterface
    private interface DuelProcedure {
        void run() throws DuelException;
    }

    private final LobbyRepository lobbyRepository;
    private final LobbyQuestionRepository lobbyQuestionRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final JudgeSubmissionRepository judgeSubmissionRepository;

    public DuelManager(
            final LobbyRepository lobbyRepository,
            final LobbyQuestionRepository lobbyQuestionRepository,
            final LobbyPlayerRepository lobbyPlayerRepository,
            final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository,
            final QuestionRepository questionRepository,
            final UserRepository userRepository,
            final ProblemRepository problemRepository,
            final JudgeSubmissionRepository judgeSubmissionRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyQuestionRepository = lobbyQuestionRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyPlayerQuestionRepository = lobbyPlayerQuestionRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.judgeSubmissionRepository = judgeSubmissionRepository;
    }

    private Map<String, List<QuestionDto>> buildPlayerSolvedQuestionsMap(final String lobbyId) {
        Map<String, List<QuestionDto>> playerQuestionsMap = new HashMap<>();

        var lobbyPlayers = lobbyPlayerRepository.findPlayersByLobbyId(lobbyId);

        for (var player : lobbyPlayers) {
            var lobbyPlayerQuestions = lobbyPlayerQuestionRepository.findQuestionsByLobbyPlayerId(player.getId());

            List<QuestionDto> playerQuestions = lobbyPlayerQuestions.stream()
                    .filter(lpq -> lpq.getQuestionId().isPresent())
                    .map(lpq -> questionRepository.getQuestionById(
                            lpq.getQuestionId().get()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(QuestionDto::fromQuestion)
                    .collect(Collectors.toList());

            playerQuestionsMap.put(player.getPlayerId(), playerQuestions);
        }

        return playerQuestionsMap;
    }

    private List<UserDto> buildPlayersInLobby(final String lobbyId) {
        var lobbyPlayers = lobbyPlayerRepository.findPlayersByLobbyId(lobbyId);

        return lobbyPlayers.stream()
                .map(lobbyPlayer -> userRepository.getUserById(lobbyPlayer.getPlayerId()))
                .filter(Objects::nonNull)
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    private <T> T wrap(DuelSupplier<T> supplier) throws DuelException {
        try {
            return supplier.get();
        } catch (DuelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception thrown in DuelManager", e);
            throw new DuelException(e);
        }
    }

    private void wrap(DuelProcedure procedure) throws DuelException {
        try {
            procedure.run();
        } catch (DuelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception thrown in DuelManager", e);
            throw new DuelException(e);
        }
    }

    public DuelData generateDuelData(final String lobbyId) throws DuelException {
        return wrap(() -> {
            var fetchedLobby = lobbyRepository
                    .findLobbyById(lobbyId)
                    .map(LobbyDto::fromLobby)
                    .orElse(null);

            List<ProblemDto> lobbyQuestions = lobbyQuestionRepository.findLobbyQuestionsByLobbyId(lobbyId).stream()
                    .map(LobbyQuestion::getProblemId)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(problemRepository::getProblemById)
                    .filter(Objects::nonNull)
                    .map(ProblemDto::fromProblem)
                    .collect(Collectors.toList());

            return DuelData.builder()
                    .lobby(fetchedLobby)
                    .questions(lobbyQuestions)
                    .players(buildPlayersInLobby(lobbyId))
                    .playerQuestions(buildPlayerSolvedQuestionsMap(lobbyId))
                    .build();
        });
    }

    /**
     * @param playerId - equivalent to User.id
     * @param isAdminOverride - If user is admin, we can start the duel without needing 2 players.
     */
    public void startDuel(final String playerId, final boolean isAdminOverride) throws DuelException {
        wrap(() -> {
            LobbyPlayer player = lobbyPlayerRepository
                    .findValidLobbyPlayerByPlayerId(playerId)
                    .orElseThrow(() -> new DuelException(HttpStatus.NOT_FOUND, "You are not currently in a party!"));

            Lobby lobby = lobbyRepository
                    .findLobbyById(player.getLobbyId())
                    .orElseThrow(
                            () -> new DuelException(HttpStatus.INTERNAL_SERVER_ERROR, "Hmm, something went wrong."));

            if (lobby.getStatus() != LobbyStatus.AVAILABLE) {
                throw new DuelException(HttpStatus.CONFLICT, "Lobby is not available!");
            }

            if (!isAdminOverride && lobby.getPlayerCount() < 2) {
                throw new DuelException(HttpStatus.CONFLICT, "You must have at least 2 players!");
            }

            lobby.setStatus(LobbyStatus.ACTIVE);
            lobby.setExpiresAt(Optional.of(StandardizedOffsetDateTime.now().plusMinutes(30)));
            lobbyRepository.updateLobby(lobby);

            Problem randomProblem = problemRepository
                    .getRandomProblem()
                    .orElseThrow(() -> new DuelException(HttpStatus.NOT_FOUND, "No problems available."));

            LobbyQuestion lobbyQuestion = LobbyQuestion.builder()
                    .lobbyId(lobby.getId())
                    .problemId(Optional.of(randomProblem.getId()))
                    .userSolvedCount(0)
                    .build();

            lobbyQuestionRepository.createLobbyQuestion(lobbyQuestion);
        });
    }

    public void endDuel(final String lobbyId, boolean isDuelCleanup) throws DuelException {
        wrap(() -> {
            var activeLobby = lobbyRepository
                    .findLobbyById(lobbyId)
                    .orElseThrow(() -> new DuelException(HttpStatus.NOT_FOUND, "Duel cannot be found."));

            if (activeLobby.getStatus() != LobbyStatus.ACTIVE) {
                throw new DuelException(HttpStatus.CONFLICT, "This duel is not currently active.");
            }

            var activeLobbyExpiresAt = activeLobby.getExpiresAt();
            if (!isDuelCleanup
                    && activeLobbyExpiresAt.isPresent()
                    && activeLobbyExpiresAt.get().isAfter(StandardizedOffsetDateTime.now())) {
                throw new DuelException(HttpStatus.CONFLICT, "This duel is not ready for expiration yet.");
            }

            var lobbyPlayers = lobbyPlayerRepository.findPlayersByLobbyId(activeLobby.getId());

            if (lobbyPlayers.isEmpty()) {
                throw new DuelException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No winner can be found because there are no players in the duel. This should not be happening.");
            }

            if (lobbyPlayers.size() == 1) {
                activeLobby.setWinnerId(Optional.of(lobbyPlayers.get(0).getPlayerId()));
            } else {
                var playerOne = lobbyPlayers.get(0);
                var playerTwo = lobbyPlayers.get(1);
                var playerOnePts = playerOne.getPoints();
                var playerTwoPts = playerTwo.getPoints();

                if (playerOnePts == playerTwoPts) {
                    activeLobby.setTie(true);
                } else {
                    var winner = playerOnePts > playerTwoPts ? playerOne : playerTwo;
                    activeLobby.setWinnerId(Optional.of(winner.getPlayerId()));
                }
            }

            activeLobby.setStatus(LobbyStatus.COMPLETED);

            lobbyRepository.updateLobby(activeLobby);
        });
    }

    public Lobby getLobbyByUserId(String userId) throws DuelException {
        return wrap(() -> {
            var lobby = lobbyRepository
                    .findAvailableLobbyByLobbyPlayerPlayerId(userId)
                    .or(() -> lobbyRepository.findActiveLobbyByLobbyPlayerPlayerId(userId))
                    .orElseThrow(() ->
                            new DuelException(HttpStatus.NOT_FOUND, "No duel or party found for the given player."));

            return lobby;
        });
    }

    public Lobby getDuelByUserId(String userId) throws DuelException {
        return wrap(() -> {
            var lobby = lobbyRepository
                    .findActiveLobbyByLobbyPlayerPlayerId(userId)
                    .orElseThrow(() -> new DuelException(HttpStatus.NOT_FOUND, "No duel found for the given player."));

            return lobby;
        });
    }

    /**
     * Looks up {@code judge_submission} rows created via the in-app judge (instead of polling LeetCode) to
     * determine which of this lobby's assigned problems the player has solved, and awards duel points accordingly.
     * Downstream {@code LobbyPlayerQuestion}/points bookkeeping is unchanged from the LeetCode-polling era.
     */
    public int processSubmissions(User user, Lobby activeLobby) throws DuelException {
        return wrap(() -> {
            var lobbyPlayer = lobbyPlayerRepository
                    .findValidLobbyPlayerByPlayerId(user.getId())
                    .orElseThrow(() -> new DuelException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "A duel was found but the player instance cannot be found."));

            List<LobbyQuestion> lobbyQuestions =
                    lobbyQuestionRepository.findLobbyQuestionsByLobbyId(activeLobby.getId());

            List<LobbyPlayerQuestion> alreadyRecorded =
                    lobbyPlayerQuestionRepository.findQuestionsByLobbyPlayerId(lobbyPlayer.getId());

            List<LobbyPlayerQuestion> newlyRecorded = new java.util.ArrayList<>();

            for (LobbyQuestion lobbyQuestion : lobbyQuestions) {
                if (lobbyQuestion.getProblemId().isEmpty()) {
                    continue;
                }
                String problemId = lobbyQuestion.getProblemId().get();

                boolean alreadyCounted = alreadyRecorded.stream()
                        .anyMatch(lpq -> lpq.getQuestionId().isPresent()
                                && lpq.getQuestionId().get().equals(problemId));
                if (alreadyCounted) {
                    continue;
                }

                List<JudgeSubmission> submissions = judgeSubmissionRepository
                        .getSubmissionsByUserAndLobbyAndProblem(user.getId(), activeLobby.getId(), problemId);

                Optional<JudgeSubmission> accepted = submissions.stream()
                        .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                        .findFirst();

                if (accepted.isEmpty()) {
                    continue;
                }

                LobbyPlayerQuestion lobbyPlayerQuestion = LobbyPlayerQuestion.builder()
                        .lobbyPlayerId(lobbyPlayer.getId())
                        .questionId(Optional.of(problemId))
                        .points(Optional.of(accepted.get().getPointsAwarded()))
                        .build();

                FunctionUtils.swallow(() -> lobbyPlayerQuestionRepository.createLobbyPlayerQuestion(lobbyPlayerQuestion));
                newlyRecorded.add(lobbyPlayerQuestion);
            }

            if (!newlyRecorded.isEmpty()) {
                int additionalPoints = newlyRecorded.stream()
                        .mapToInt(q -> q.getPoints().orElse(0))
                        .sum();
                lobbyPlayer.setPoints(lobbyPlayer.getPoints() + additionalPoints);
                lobbyPlayerRepository.updateLobbyPlayer(lobbyPlayer);
            }

            return newlyRecorded.size();
        });
    }
}
