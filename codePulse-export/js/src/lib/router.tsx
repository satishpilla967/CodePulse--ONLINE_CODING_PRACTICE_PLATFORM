import AdminPage from "@/app/admin/admin.page";
import ClubSignUp from "@/app/club/[clubSlug]/ClubSignUp.page";
import DebugChallengePage from "@/app/debug-challenges/[problemId]/DebugChallenge.page";
import DebugChallengesListPage from "@/app/debug-challenges/DebugChallengesList.page";
import DuelPage from "@/app/duel/[lobbyCode]/Duel.page";
import CurrentDuelPage from "@/app/duel/current/CurrentDuel.page";
import PartyEntryPage from "@/app/duel/PartyEntry.page";
import LeaderboardEmbed from "@/app/embed/leaderboard/LeaderboardEmbed";
import PotdEmbed from "@/app/embed/potd/PotdEmbed";
import ErrorPage from "@/app/error/Error.page";
import ForgotPasswordPage from "@/app/forgot-password/ForgotPassword.page";
import ReportIssuePage from "@/app/issue/report/ReportIssue.page";
import LeaderboardWithIdPage from "@/app/leaderboard/[leaderboardId]/LeaderboardWithId.page";
import AllLeaderboardsPage from "@/app/leaderboard/all/AllLeaderboards.page";
import LeaderboardPage from "@/app/leaderboard/Leaderboard.page";
import LoginPage from "@/app/login/Login.page";
import Onboarding from "@/app/onboarding/Onboarding.page";
import RegisterPage from "@/app/register/Register.page";
import PolicyPage from "@/app/privacy/Policy.page";
import ResetPasswordPage from "@/app/reset-password/ResetPassword.page";
import RootPage from "@/app/Root.page";
import ProblemPage from "@/app/problem/[problemId]/Problem.page";
import ProblemListPage from "@/app/problem/all/ProblemList.page";
import SubmissionDetailsPage from "@/app/submission/[submissionId]/SubmissionDetails.page";
import UserSubmissionsPage from "@/app/user/[userId]/submissions/UserSubmissions.page";
import UserProfilePage from "@/app/user/[userId]/UserProfile.page";
import PageShell from "@/components/ui/page/PageShell";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { duelFF, schoolFF } from "@/lib/ff";
import { createBrowserRouter } from "react-router-dom";

export const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <PageShell>
        <RootPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/login",
    element: (
      <PageShell hideHeader hideFooter>
        <LoginPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/register",
    element: (
      <PageShell hideHeader hideFooter>
        <RegisterPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/forgot-password",
    element: (
      <PageShell hideHeader hideFooter>
        <ForgotPasswordPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/reset-password",
    element: (
      <PageShell hideHeader hideFooter>
        <ResetPasswordPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/leaderboard",
    element: (
      <PageShell>
        <LeaderboardPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/leaderboard/all",
    element: (
      <PageShell>
        <AllLeaderboardsPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/leaderboard/:leaderboardId",
    element: (
      <PageShell>
        <LeaderboardWithIdPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/onboarding",
    element: (
      <PageShell>
        <Onboarding />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/submission/:submissionId",
    element: (
      <PageShell>
        <SubmissionDetailsPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/problem/all",
    element: (
      <PageShell>
        <ProblemListPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/problem/:problemId",
    element: (
      <PageShell>
        <ProblemPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/debug-challenges",
    element: (
      <PageShell>
        <DebugChallengesListPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/debug-challenges/:problemId",
    element: (
      <PageShell>
        <DebugChallengePage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/user/:userId",
    element: (
      <PageShell>
        <UserProfilePage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/user/:userId/submissions",
    element: (
      <PageShell>
        <UserSubmissionsPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/embed/leaderboard",
    element: (
      <PageShell hideHeader hideFooter>
        <LeaderboardEmbed />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/embed/potd",
    element: (
      <PageShell hideHeader hideFooter>
        <PotdEmbed />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/admin",
    element: (
      <PageShell>
        <AdminPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/privacy",
    element: (
      <PageShell>
        <PolicyPage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/club/:clubSlug?",
    element: (
      <PageShell>
        <ClubSignUp />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/duel",
    element:
      duelFF ?
        <PageShell>
          <PartyEntryPage />
        </PageShell>
      : <ToastWithRedirect
          to={"/"}
          message={
            "Sorry, this is not available right now. Please try again later."
          }
        />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/duel/current",
    element:
      duelFF ?
        <PageShell>
          <CurrentDuelPage />
        </PageShell>
      : <ToastWithRedirect
          to={"/"}
          message={
            "Sorry, this is not available right now. Please try again later."
          }
        />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/duel/:lobbyCode",
    element:
      duelFF ?
        <PageShell>
          <DuelPage />
        </PageShell>
      : <ToastWithRedirect
          to={"/"}
          message={
            "Sorry, this is not available right now. Please try again later."
          }
        />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/issue/report",
    element: (
      <PageShell>
        <ReportIssuePage />
      </PageShell>
    ),
    errorElement: <ErrorPage />,
  },
]);
