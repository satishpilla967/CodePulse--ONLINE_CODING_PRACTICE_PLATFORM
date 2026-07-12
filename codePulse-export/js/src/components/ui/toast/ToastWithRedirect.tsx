import { DefaultMantineColor } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { ReactNode, useEffect, useRef } from "react";
import { useNavigate } from "react-router";

/**
 * A custom React function that shows a notification and handles the redirect in a React way.
 * The alternative would be to pollute the codebase with useEffects and repetitive checks, which are
 * unfavorable and hard to maintain.
 */
export default function ToastWithRedirect({
  to,
  message,
  color,
}: {
  to: string | number;
  message: ReactNode;
  color?: DefaultMantineColor;
}) {
  const navigate = useNavigate();
  // Guards against firing more than once per mount: React 18 StrictMode double-invokes
  // effects in dev, and redirect chains can transiently mount this component more than once
  // across a couple of renders before the final route settles — without this, both of those
  // cause the same toast to stack multiple times.
  const firedRef = useRef(false);

  useEffect(() => {
    if (firedRef.current) {
      return;
    }
    firedRef.current = true;

    notifications.show({
      // A stable id (derived from the message when it's plain text) makes repeated calls with
      // the same message update/replace the existing toast instead of stacking a new one, as a
      // second line of defense against duplicate mounts across different routes/components.
      id: typeof message === "string" ? `toast-redirect-${message}` : undefined,
      message,
      color,
    });
    navigate(to);
  }, [color, message, navigate, to]);

  return <></>;
}
