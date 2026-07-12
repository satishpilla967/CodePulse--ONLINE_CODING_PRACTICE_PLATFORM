import {
  Card,
  type CardProps,
  createPolymorphicComponent,
} from "@mantine/core";
import { forwardRef } from "react";

export type CodePulseCardProps = CardProps;

const _CodePulseCard = forwardRef<HTMLDivElement, CodePulseCardProps>(
  (
    {
      children,
      withBorder = true,
      padding = "md",
      radius = "md",
      shadow = "sm",
      ...props
    },
    ref,
  ) => (
    <Card
      ref={ref}
      withBorder={withBorder}
      padding={padding}
      radius={radius}
      shadow={shadow}
      {...props}
    >
      {children}
    </Card>
  ),
);

_CodePulseCard.displayName = "CodePulseCard";

const CodePulseCard = createPolymorphicComponent<"div", CodePulseCardProps>(
  _CodePulseCard,
);
export default CodePulseCard;
