import { DefaultMantineColor } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { ReactNode, useEffect, useRef } from "react";

/**
 * A custom React function that shows a notification in a React way.
 * The alternative would be to pollute the codebase with useEffects and repetitive checks, which are
 * unfavorable and hard to maintain.
 */
export default function Toast({
  message,
  color,
}: {
  message: ReactNode;
  color?: DefaultMantineColor;
}) {
  // Guards against firing more than once per mount (React 18 StrictMode double-invokes
  // effects in dev). The stable id below is the more important guard here: multiple
  // independent widgets on the same page (e.g. several leaderboard-dependent components)
  // can each render their own <Toast> with the same message when they all fail for the
  // same underlying reason — the id makes Mantine update/replace the existing toast
  // instead of stacking a separate one per widget.
  const firedRef = useRef(false);

  useEffect(() => {
    if (firedRef.current) {
      return;
    }
    firedRef.current = true;

    notifications.show({
      id: typeof message === "string" ? `toast-${message}` : undefined,
      message,
      color,
    });
  }, [color, message]);

  return <></>;
}
