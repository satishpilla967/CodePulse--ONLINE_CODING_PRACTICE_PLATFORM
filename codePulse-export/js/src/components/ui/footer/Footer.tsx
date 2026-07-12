import { GotoAdminPageButton } from "@/components/ui/admin-button/AdminButton";
import styles from "@/components/ui/footer/Footer.module.css";
import { Text, Anchor, Flex, Box, Stack } from "@mantine/core";
import { motion, useScroll, useTransform } from "motion/react";
import { ReactNode, useRef } from "react";

export function Footer() {
  const containerRef = useRef<HTMLDivElement>(null);
  const { scrollYProgress } = useScroll({
    target: containerRef,
    offset: ["start end", "end end"],
  });
  const missionText = "Solve problems. Compete. Level up.";

  const y = useTransform(scrollYProgress, [0, 1], ["-100%", "0%"]);

  return (
    <Box ref={containerRef} className={styles.footerWrapper}>
      <motion.div className={styles.footer} style={{ y }}>
        <Box className={styles.footerContents}>
          <Box
            visibleFrom={"sm"}
            data-testid={"footer-logo-mission-desktop"}
            className={styles.footerLeft}
          >
            <Flex align={"center"}>
              <Text fw={550} size={"md"}>
                CodePulse
              </Text>
            </Flex>
            <Flex>
              <Text pl={"xs"} c={"dimmed"} size={"sm"}>
                {missionText}
              </Text>
            </Flex>
            <GotoAdminPageButton />
          </Box>
          <Box className={styles.footerRight}>
            <Box hiddenFrom={"sm"} data-testid={"footer-logo-mission-mobile"}>
              <Flex align={"center"}>
                <Text fw={550} size={"md"}>
                  CodePulse
                </Text>
              </Flex>
              <Flex>
                <Text c={"dimmed"} size={"sm"}>
                  {missionText}
                </Text>
              </Flex>
              <GotoAdminPageButton />
            </Box>
            <Box data-testid={"footer-links-section"}>
              <Stack gap={4}>
                <Text fw={550}>About</Text>
                <AnchorLink href={"/privacy"} ariaLabel={"Privacy Policy"}>
                  Privacy Policy
                </AnchorLink>
              </Stack>
            </Box>
          </Box>
        </Box>
      </motion.div>
    </Box>
  );
}

function AnchorLink({
  href,
  children,
  ariaLabel,
}: {
  href: string;
  children: ReactNode;
  ariaLabel: string;
}) {
  return (
    <Anchor
      href={href}
      c={"dimmed"}
      size={"sm"}
      variant={"subtle"}
      target={"_blank"}
      aria-label={ariaLabel}
      rel={"noopener noreferrer"}
    >
      {children}
    </Anchor>
  );
}
