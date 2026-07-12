import AvatarButton from "@/components/ui/auth/AvatarButton";
import SkeletonButton from "@/components/ui/auth/SkeletonButton";
import TransitionalButtons from "@/components/ui/button/transitonal/TransitionalButtons";
import HeaderContainer from "@/components/ui/header/container/HeaderContainer";
import ThemeToggle from "@/components/ui/theme/ThemeToggle";
import { useAuthQuery } from "@/lib/api/queries/auth";
import {
  Box,
  Burger,
  Button,
  Drawer,
  Flex,
  Group,
  Text,
  Title,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { Link } from "react-router-dom";

const navButtons = [
  { to: "/", label: "Home" },
  { to: "/leaderboard", label: "Leaderboard" },
  { to: "/problem/all", label: "Problems" },
  { to: "/debug-challenges", label: "Debug Challenges" },
];

export default function Header() {
  const { data, status } = useAuthQuery();
  const [drawerOpened, { toggle: toggleDrawer, close: closeDrawer }] =
    useDisclosure(false);

  const renderButton = (className?: string) => {
    if (status === "pending") {
      return <SkeletonButton />;
    }

    if (status === "error") {
      return <Text c="red">Sorry, something went wrong.</Text>;
    }

    if (data && data.user && data.session) {
      const profileUrl = data.user.profileUrl;
      const initial =
        data.user.nickname ? data.user.nickname.charAt(0).toUpperCase() : "?";
      return (
        <AvatarButton
          src={profileUrl ?? ""}
          initial={initial}
          userId={data.user.id}
        />
      );
    }

    return (
      <Link to="/login" className={className}>
        <Button className={className}>Login</Button>
      </Link>
    );
  };

  return (
    <>
      <HeaderContainer>
        {() => (
          <>
            <Link to="/">
              <Group align="center" wrap="nowrap">
                <Title order={3} style={{ lineHeight: 1 }}>
                  <Text
                    span
                    inherit
                    gradient={{ from: "patina.4", to: "patina.8" }}
                    variant="gradient"
                  >
                    CodePulse
                  </Text>
                </Title>
              </Group>
            </Link>
            <Box visibleFrom="sm">
              <TransitionalButtons buttons={navButtons} />
            </Box>
            <Group visibleFrom="sm" gap="sm">
              <ThemeToggle />
              {renderButton()}
            </Group>
            <Group hiddenFrom="sm" gap="xs">
              <ThemeToggle />
              <Burger
                opened={drawerOpened}
                onClick={toggleDrawer}
                hiddenFrom="sm"
                aria-label={"Menu button"}
              />
            </Group>
          </>
        )}
      </HeaderContainer>
      <Drawer
        opened={drawerOpened}
        onClose={closeDrawer}
        withCloseButton={false}
        size="50%"
        title="Navigation"
        styles={{
          header: {
            justifyContent: "center",
          },
          title: {
            textAlign: "center",
            fontWeight: 700,
            fontSize: "var(--mantine-font-size-lg)",
            marginBottom: "var(--mantine-spacing-xs)",
          },
        }}
      >
        <Flex direction="column" align="center" gap="xs" w="100%">
          {navButtons.map(({ to, label }) => (
            <Button
              component={Link}
              to={to}
              size={"compact-md"}
              variant="transparent"
              fullWidth
              key={to}
              onClick={closeDrawer}
            >
              {label}
            </Button>
          ))}
          <Flex justify="center" w="100%" mt="xs">
            {renderButton("full-width")}
          </Flex>
        </Flex>
      </Drawer>
    </>
  );
}
