import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";
import { backend } from "utils/run-backend-instance";
import { db } from "utils/run-local-db";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

await yargs(hideBin(process.argv))
  .option("actionUrl", {
    type: "string",
    demandOption: true,
  })
  .strict()
  .parse();

async function main() {
  const ciAppEnv = await getEnvVariables(["ci-app"]);

  try {
    //type-gen
    try {
      const dbEnv = await db.start();
      const env = { ...process.env, ...ciAppEnv, ...dbEnv };
      await backend.start(env);
      await $.env(env)`pnpm --dir js i --frozen-lockfile`;
      await $.env(env)`pnpm --dir js run generate`;
      await $.env(env)`pnpm --dir js run typecheck`;
    } finally {
      await backend.end();
      await db.end();
    }

    const dbEnv = await db.start();

    const c$ = $.env({
      ...process.env,
      ...dbEnv,
      ...ciAppEnv,
      CI: "true",
    });

    await c$`pnpm --dir e2e i --frozen-lockfile`;
    await c$`pnpm --dir e2e e2e`;
  } finally {
    await db.end();
  }
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
