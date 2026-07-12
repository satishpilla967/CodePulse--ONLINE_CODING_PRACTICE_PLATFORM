import { $ } from "bun";

import { brightMagenta } from "@/../utils/colors";

async function start() {
  try {
    console.log("Starting mysql container...");

    await $`docker rm -f codepulse-db`;

    await $`docker run -d \
        --name codepulse-db \
        -e MYSQL_ROOT_PASSWORD=mysql \
        -e MYSQL_DATABASE=codepulse \
        -e MYSQL_USER=mysql \
        -e MYSQL_PASSWORD=mysql \
        -p 5440:3306 \
        mirror.gcr.io/library/mysql:8`;

    console.log("Waiting for mysql to become ready.");

    let ready = false;
    const attempts = 30;

    for (let i = 1; i <= attempts; i++) {
      const check = await $`docker exec codepulse-db mysqladmin ping -h 127.0.0.1 -u root -pmysql --silent`
        .quiet()
        .nothrow();

      if (check.exitCode === 0) {
        console.log("mysql is ready!");
        ready = true;
        break;
      }

      console.log(`Waiting for mysql... (${i}/${attempts})`);
      await Bun.sleep(2000);
    }

    if (!ready) {
      console.error("mysql failed to start in time.");
      await end();
      process.exit(1);
    }

    const env = {
      DATABASE_HOST: "localhost",
      DATABASE_PORT: "5440",
      DATABASE_NAME: "codepulse",
      DATABASE_USER: "root",
      DATABASE_PASSWORD: "mysql",
    };

    console.log("mysql started, running migrations...");

    await $.env(env)`./mvnw flyway:migrate -Dflyway.locations=filesystem:./db`;

    console.log("mysql ready");

    return env;
  } catch (e) {
    console.error(e);
    end();
  }
}

async function end() {
  console.log("Stopping and removing mysql container...");

  console.log(brightMagenta("=== DB LOGS ==="));
  const logs = await $`docker logs codepulse-db`.text();
  logs
    .split("\n")
    .filter((s) => s.length > 0)
    .forEach((line) => console.log(brightMagenta(line)));
  console.log(brightMagenta("=== DB LOGS END ==="));

  await $`docker stop codepulse-db`.quiet().nothrow();
  await $`docker rm codepulse-db`.quiet().nothrow();

  delete process.env.DATABASE_HOST;
  delete process.env.DATABASE_PORT;
  delete process.env.DATABASE_NAME;
  delete process.env.DATABASE_USER;
  delete process.env.DATABASE_PASSWORD;
}

export const db = {
  start,
  end,
};
