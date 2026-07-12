import JudgeWorkspace from "@/app/problem/[problemId]/_components/JudgeWorkspace";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Box } from "@mantine/core";
import { useParams } from "react-router";

export default function DebugChallengePage() {
  const { problemId } = useParams();

  if (!problemId) {
    return <ToastWithRedirect to={-1} message={"Invalid problem ID."} />;
  }

  return (
    <>
      <DocumentTitle title="Debug Challenge | CodePulse" />
      <Box p="md" h="100vh">
        <JudgeWorkspace problemId={problemId} debugMode />
      </Box>
    </>
  );
}
