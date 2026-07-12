import DashboardListView from "@/app/leaderboard/all/_components/DashboardListView";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { Box } from "@mantine/core";

export default function AllLeaderboardsPage() {
  return (
    <>
      <DocumentTitle title={`CodePulse - All Leaderboards`} />
      <DocumentDescription description={`CodePulse - View all leaderboards`} />
      <Box>
        <DashboardListView />
      </Box>
    </>
  );
}
