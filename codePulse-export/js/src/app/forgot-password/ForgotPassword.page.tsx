import PasswordResetRequestButton from "@/components/ui/auth/PasswordResetRequestButton";
import CodePulseCard from "@/components/ui/CodePulseCard";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Button, Center, Loader, Space, Text } from "@mantine/core";
import { Link } from "react-router-dom";

export default function ForgotPasswordPage() {
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

  if (authenticated) {
    return (
      <ToastWithRedirect
        to="/leaderboard"
        message="You are already authenticated"
      />
    );
  }

  return (
    <>
      <DocumentTitle title={`CodePulse - Forgot Password`} />
      <DocumentDescription description={`CodePulse - Reset your password`} />
      <Center style={{ height: "100vh" }}>
        <CodePulseCard
          style={{
            width: 500,
            textAlign: "center",
          }}
        >
          <Text fw={400} size="xl">
            Forgot your password?
          </Text>
          <Space h="sm" />
          <Text size="sm" c="dimmed">
            Enter your email and we&apos;ll send you a link to reset it. If
            you signed up before we added passwords, this is also how you set
            one for the first time.
          </Text>
          <Space h="sm" />
          <PasswordResetRequestButton />
          <Space h="sm" />
          <Text size="sm">
            <Link to="/login">Back to log in</Link>
          </Text>
          <Space h="sm" />
          <Link to="/" reloadDocument>
            <Button size="xs" variant="subtle" style={{ fontSize: "12px" }}>
              Go Back
            </Button>
          </Link>
        </CodePulseCard>
      </Center>
    </>
  );
}
