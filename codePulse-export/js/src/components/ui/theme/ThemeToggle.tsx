import { ActionIcon, useComputedColorScheme, useMantineColorScheme } from "@mantine/core";
import { BiMoon, BiSun } from "react-icons/bi";

export default function ThemeToggle() {
  const { setColorScheme } = useMantineColorScheme();
  const computedColorScheme = useComputedColorScheme("dark");

  const toggleColorScheme = () => {
    setColorScheme(computedColorScheme === "dark" ? "light" : "dark");
  };

  return (
    <ActionIcon
      onClick={toggleColorScheme}
      variant="subtle"
      size="lg"
      aria-label="Toggle color scheme"
    >
      {computedColorScheme === "dark" ?
        <BiSun size={20} />
      : <BiMoon size={20} />}
    </ActionIcon>
  );
}
