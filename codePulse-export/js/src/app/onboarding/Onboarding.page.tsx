import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Loader } from "@mantine/core";

// TODO: LeetCode-username-linking was the only onboarding step, and has been removed now that
// submissions are judged in-app (Judge0) rather than pulled from a linked LeetCode account. There is
// currently no replacement onboarding step defined, so this route just forwards straight to the
// dashboard. Revisit if a new onboarding flow (e.g. school verification) should live here instead.
export default function Onboarding() {
  const { data, status } = useAuthQuery();

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  const authenticated = !!data.user && !!data.session;

  if (!authenticated) {
    return (
      <ToastWithRedirect to="/login" message="You are not authenticated!" />
    );
  }

  return <ToastWithRedirect to="/leaderboard" message="Welcome to CodePulse!" />;
}
