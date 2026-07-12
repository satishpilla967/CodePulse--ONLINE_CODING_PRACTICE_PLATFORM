import PasswordResetConfirmButton from "@/components/ui/auth/PasswordResetConfirmButton";
import CodePulseCard from "@/components/ui/CodePulseCard";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { Button, Center, Space, Text } from "@mantine/core";
import { Link, useSearchParams } from "react-router-dom";

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  return (
    <>
      <DocumentTitle title={`CodePulse - Reset Password`} />
      <DocumentDescription description={`CodePulse - Reset your password`} />
      <Center style={{ height: "100vh" }}>
        <CodePulseCard
          style={{
            width: 500,
            textAlign: "center",
          }}
        >
          <Text fw={400} size="xl">
            Reset your password
          </Text>
          <Space h="sm" />
          {token ?
            <PasswordResetConfirmButton token={token} />
          : <>
              <Text size="sm" c="dimmed">
                This reset link is missing its token. Please request a new
                one.
              </Text>
              <Space h="sm" />
              <Link to="/forgot-password">Request a new link</Link>
            </>
          }
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
