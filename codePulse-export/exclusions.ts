/**
 * See https://github.com/tahminator/codepulse/blob/main/.github/scripts/utils/upload.ts
 * for test exclusion usages.
 */

const backendBaseDir = "src/main/java/org/patinanetwork/codepulse";
const frontendBaseDir = "js/src";

export const backendExclusions = [
  `${backendBaseDir}/common/dto/**`,
  `${backendBaseDir}/playwright/PlaywrightProvider.java`,
  `${backendBaseDir}/CodePulseApplication.java`,
];
export const frontendExclusions = [
  `${frontendBaseDir}/**/*.test.ts`,
  `${frontendBaseDir}/**/*.test.tsx`,
];
