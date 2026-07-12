import CodePulseCard from "@/components/ui/CodePulseCard";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { useMyRecentLeaderboardData } from "@/lib/api/queries/leaderboard";
import { getUserProfileUrl } from "@/lib/helper/leaderboardDateRange";
import { theme } from "@/lib/theme";
import { Avatar, Flex, Text } from "@mantine/core";
import { Link } from "react-router-dom";

export default function MyRankCard() {
  const { data: authData, status: authStatus } = useAuthQuery();
  const userId = authData?.user?.id;

  const { data, status } = useMyRecentLeaderboardData({
    userId: userId ?? "",
  });

  if (authStatus !== "success" || !userId) {
    return null;
  }

  if (status !== "success" || !data.success) {
    return null;
  }

  const me = data.payload;

  return (
    <CodePulseCard
      component={Link}
      to={getUserProfileUrl(me.id)}
      padding="lg"
      mb="md"
      bg={theme.colors.baltic[8]}
      styles={{
        root: {
          borderColor: theme.colors.baltic[5],
          borderWidth: 2,
        },
      }}
      style={{ textDecoration: "none" }}
    >
      <Flex direction="row" justify="space-between" align="center" gap="md">
        <Flex align="center" gap="sm">
          <Avatar src={me.profileUrl ?? undefined} radius="sm" size={40}>
            {(me.nickname ?? me.discordName).charAt(0).toUpperCase()}
          </Avatar>
          <Flex direction="column">
            <Text size="xs" c="dimmed">
              Your Standing
            </Text>
            <Text size="md" fw={600}>
              {me.nickname ?? me.discordName}
            </Text>
          </Flex>
        </Flex>
        <Text size="lg" fw={700} c={theme.colors.baltic[3]}>
          {me.totalScore} Pts
        </Text>
      </Flex>
    </CodePulseCard>
  );
}
