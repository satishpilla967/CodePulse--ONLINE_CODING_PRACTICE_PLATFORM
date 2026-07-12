import RegisterButton from "@/components/ui/auth/RegisterButton";
import CodePulseCard from "@/components/ui/CodePulseCard";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Button, Center, Loader, Space, Text } from "@mantine/core";
import { Link } from "react-router-dom";

export default function RegisterPage() {
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
      <DocumentTitle title={`CodePulse - Register`} />
      <DocumentDescription description={`CodePulse - Create a new account`} />
      <Center style={{ height: "100vh" }}>
        <CodePulseCard
          style={{
            width: 500,
            textAlign: "center",
          }}
        >
          <Text fw={400} size="xl">
            Create your CodePulse account
          </Text>
          <Space h="sm" />
          <RegisterButton />
          <Space h="sm" />
          <Text size="sm">
            Already have an account? <Link to="/login">Log in</Link>
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
