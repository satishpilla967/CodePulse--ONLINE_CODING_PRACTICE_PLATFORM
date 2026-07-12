import {
  Card,
  CSSVariablesResolver,
  DEFAULT_THEME,
  MantineThemeOverride,
  mergeMantineTheme,
  rem,
  Tooltip,
} from "@mantine/core";
import { MergeDeep } from "type-fest";

/**
 * Custom mantine theme override
 *
 * This theme customization includes setting the "Figtree" font family for both
 * body text and headings, with specific sizes and weights for various heading levels.
 *
 * Font: Figtree
 * Weights: 400 (Regular), 600 (Semibold), 700 (Bold)
 *
 *
 * {@link https://mantine.dev/theming/theme-object/ Theme Object Docs}
 * {@link https://github.com/mantinedev/mantine/blob/master/packages/%40mantine/core/src/core/MantineProvider/default-theme.ts Default Theme}
 * {@link https://github.com/mantinedev/mantine/blob/master/packages/%40mantine/core/src/core/MantineProvider/default-colors.ts Default Colors}
 */
export const themeOverride = {
  components: {
    Tooltip: Tooltip.extend({
      defaultProps: {
        withArrow: true,
        color: "dark.4",
        events: { hover: true, focus: true, touch: true },
      },
    }),

    Card: Card.extend({
      defaultProps: {
        shadow: "sm",
        padding: "lg",
        radius: "md",
        withBorder: true,
      },
    }),
  },
  autoContrast: true,
  primaryColor: "baltic",
  colors: {
    patina: [
      "#e0fff9",
      "#c8fbf1",
      "#98f4e3",
      "#63ecd3",
      "#33E1C4", // Primary
      "#1fcbb0",
      "#14b89f",
      "#0aa38b",
      "#048d78",
      "#007565",
    ],
    // Baltic Blue - primary brand color
    baltic: [
      "#ecf2f7",
      "#d0deec",
      "#aac4dc",
      "#80a7cb",
      "#5186b7",
      "#145C9E", // Baltic Blue
      "#12538e",
      "#10487b",
      "#0d3d68",
      "#0b3255",
    ],
    // Yale Blue - secondary accent
    yale: [
      "#ebf1f3",
      "#cedce2",
      "#a7c0ca",
      "#7ba0b0",
      "#4a7d92",
      "#0B4F6C", // Yale Blue
      "#0a4761",
      "#093e54",
      "#073447",
      "#062b3a",
    ],
    // Charcoal Brown - dark mode background / text
    charcoal: [
      "#edeeed",
      "#d2d4d1",
      "#aeb1ad",
      "#868a84",
      "#595f56",
      "#1F271B", // Charcoal Brown
      "#1c2318",
      "#181e15",
      "#141a12",
      "#11150f",
    ],
    // Pale Oak - light mode neutral
    paleOak: [
      "#fbf9f8",
      "#f5f1ee",
      "#ece6e0",
      "#e3d9d0",
      "#d9cbbf",
      "#CBB9A8", // Pale Oak
      "#b7a797",
      "#9e9083",
      "#867a6f",
      "#6e645b",
    ],
    // Almond Silk - light mode surface
    almond: [
      "#fcfbfa",
      "#f8f4f2",
      "#f2ebe8",
      "#ece1dc",
      "#e5d6cf",
      "#DCC7BE", // Almond Silk
      "#c6b3ab",
      "#ac9b94",
      "#91837d",
      "#776b67",
    ],
    dark: [
      "#FFFFFF", // dark-0
      // '#C1C2C5', // dark-0
      "#A6A7AB", // dark-1
      "#909296", // dark-2
      "#5C5F66", // dark-3
      "#595f56", // dark-4
      "#3a4136", // dark-5
      "#242b1f", // dark-6
      "#1F271B", // dark-7 Default Dark Mode Background (Charcoal Brown)
      "#181e15", // dark-8
      "#11150f", // dark-9
    ],
  },
  headings: {
    fontFamily: "Figtree, sans-serif",
    sizes: {
      h1: { fontSize: rem(64), fontWeight: "700" },
      h2: { fontSize: rem(48), fontWeight: "700" },
      h3: { fontSize: rem(34), fontWeight: "700" },
      h4: { fontSize: rem(22), fontWeight: "700" },
      h5: { fontSize: rem(16), fontWeight: "600" },
    },
  },
  fontFamily: "Figtree, sans-serif",
  fontSizes: {
    xs: rem(12),
    sm: rem(14),
    md: rem(16), // Body text size
    lg: rem(20),
    xl: rem(24),
  },
  lineHeights: {
    xs: "1.4",
    sm: "1.45",
    md: "1.55",
    lg: "1.6",
    xl: "1.65",
  },
  radius: {
    xs: rem(3),
    sm: rem(6),
    md: rem(10),
    lg: rem(18),
    xl: rem(34),
  },
  spacing: {
    xs: rem(10),
    sm: rem(12),
    md: rem(16),
    lg: rem(20),
    xl: rem(32),
  },
  breakpoints: {
    xs: "36em", // 576px - Mobile
    sm: "48em", // 768px - Half Screen Default Mac?
    md: "62em", // 992px - Half Screen 1920x1080p
    lg: "75em", // 1200px
    xl: "88em", // 1408px - Anything not fullscreen
  },
  other: {
    codepulseGray: "#2E3033",
    patinaGreenDark: "#046655",
    patinaGreenLight: "#33E1C4",
    patinaBlueDark: "#1F4FCF",
    patinaBlueLight: "#5CC8FF",
    patinaRedDark: "#BB352C",
    patinaRedLight: "#FF5148",
    contentContainerWidth: 1200,
  },
} as const satisfies MantineThemeOverride;

// mantine types suck :(
export const theme = mergeMantineTheme(
  DEFAULT_THEME,
  themeOverride,
) as unknown as MergeDeep<typeof DEFAULT_THEME, typeof themeOverride>;

/**
 * Add custom CSS variables to keep consistency across the codebase.
 * Generally these should be used instead of raw values.
 * Usage: color: var(--mantine-color-patina-green-light)
 */
export const resolver: CSSVariablesResolver = (computedTheme) => ({
  variables: {
    "--mantine-color-patina-green-light": computedTheme.other.patinaGreenLight,
    "--mantine-color-patina-blue-light": computedTheme.other.patinaBlueLight,
    "--mantine-color-patina-red-light": computedTheme.other.patinaRedLight,
    "--mantine-color-patina-green-dark": computedTheme.other.patinaGreenDark,
    "--mantine-color-patina-blue-dark": computedTheme.other.patinaBlueDark,
    "--mantine-color-patina-red-dark": computedTheme.other.patinaRedDark,
  },
  light: {
    "--mantine-color-body": computedTheme.colors.almond[0],
    "--mantine-color-text": computedTheme.colors.charcoal[5],
    "--codepulse-surface": computedTheme.colors.almond[1],
    "--codepulse-header-bg": computedTheme.colors.paleOak[1],
  },
  dark: {
    "--mantine-color-body": computedTheme.colors.charcoal[7],
    "--mantine-color-text": "#FFFFFF",
    "--codepulse-surface": computedTheme.colors.charcoal[6],
    "--codepulse-header-bg": computedTheme.other.codepulseGray,
  },
});
