// Allure 3 report configuration (https://allurereport.org/docs/v3/configure/).
// Used by `./mvnw -N exec:exec@allure-report` (local and CI). A plain object is exported (no
// `import { defineConfig } from "allure"`) because the CLI runs via npx from its own cache.

const env = process.env;

export default {
  name: "SDET Evaluation - Test Report",
  output: "./allure-report",

  // One report, one tab per module: each test is routed by the Cucumber tag of its feature.
  environments: {
    api: {
      name: "API (REST Assured)",
      matcher: ({ labels }) => labels.some(({ name, value }) => name === "tag" && value === "api"),
    },
    web: {
      name: "Web (Playwright)",
      matcher: ({ labels }) => labels.some(({ name, value }) => name === "tag" && value === "web"),
    },
    mobile: {
      name: "Mobile (Appium)",
      matcher: ({ labels }) => labels.some(({ name, value }) => name === "tag" && value === "mobile"),
    },
  },

  // Shown on the report overview.
  variables: {
    "Java": "21 (Temurin)",
    "Test stack": "Cucumber 7 + TestNG",
    "Branch": env.GITHUB_REF_NAME ?? "local",
    "Commit": env.GITHUB_SHA?.slice(0, 7) ?? "local",
    "CI run": env.GITHUB_RUN_ID
      ? `${env.GITHUB_SERVER_URL}/${env.GITHUB_REPOSITORY}/actions/runs/${env.GITHUB_RUN_ID}`
      : "local run",
  },

  // Failure triage: the first matching rule wins.
  categories: [
    {
      // Mobile scenarios 8 & 9 crash the app on purpose to demonstrate failure reporting.
      name: "Intentional failures (app crash demo)",
      matchedStatuses: ["failed"],
      messageRegex: ".*most likely crashed.*",
    },
    {
      name: "Product defects (assertion failed)",
      matchedStatuses: ["failed"],
    },
    {
      name: "Test or environment errors (timeouts, driver, network)",
      matchedStatuses: ["broken"],
    },
  ],
};
