import { langNameToIcon } from "@/components/ui/langname-to-icon/LangNameToIcon";
import { Card, SimpleGrid, Text, Title, UnstyledButton } from "@mantine/core";
import { BiCode } from "react-icons/bi";
import { useNavigate } from "react-router-dom";

const LANGUAGES = [
  { label: "C", value: "c", icon: BiCode },
  { label: "C++", value: "cpp", icon: langNameToIcon.cpp },
  { label: "Python", value: "python", icon: langNameToIcon.python },
  { label: "Java", value: "java", icon: langNameToIcon.java },
  { label: "JavaScript", value: "javascript", icon: langNameToIcon.javascript },
  { label: "TypeScript", value: "typescript", icon: langNameToIcon.typescript },
];

export default function LanguageCards() {
  const navigate = useNavigate();

  return (
    <>
      <Title order={3} ta="center" mb="lg">
        Practice by Language
      </Title>
      <SimpleGrid cols={{ base: 2, sm: 3, md: 6 }} spacing="md">
        {LANGUAGES.map(({ label, value, icon: Icon }) => (
          <UnstyledButton
            key={value}
            onClick={() =>
              navigate(`/problem/all?category=dsa&language=${value}`)
            }
          >
            <Card
              className="transition-all hover:scale-105"
              ta="center"
              py="lg"
            >
              <Icon
                width={40}
                height={40}
                style={{ margin: "0 auto" }}
              />
              <Text fw={600} mt="sm">
                {label}
              </Text>
            </Card>
          </UnstyledButton>
        ))}
      </SimpleGrid>
    </>
  );
}
