# Web + API test runner. The official Playwright Java image already contains a JDK, Maven and the
# browsers, so nothing is installed here. Keep the tag in sync with playwright.version in pom.xml.
FROM mcr.microsoft.com/playwright/java:v1.63.0-noble

# The repository is mounted here by docker-compose.yml (results and reports land on the host)
WORKDIR /workspace

# Browsers ship with the image; skip Playwright's download at test time
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

# -fae: if one module fails (e.g. reqres.in rate limit), still run the other
CMD ["sh", "./mvnw", "-B", "-ntp", "-fae", "test", "-pl", "api-tests,web-tests", "-am"]
