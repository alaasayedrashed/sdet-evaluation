#!/usr/bin/env bash
# Runs the mobile suite on CI. Called from the reactivecircus/android-emulator-runner step, so the
# emulator is already booted. The runner executes its `script` line by line, which is why the
# logic lives in this file.
#
#  1. Start the Appium server (chromedriver autodownload enabled for the WebView scenario).
#  2. Run the regular mobile scenarios          -> their result is the step's exit code.
#  3. Run the intentional-failure (@negative) scenarios -> expected to fail; reported, not fatal.
# Both runs write to the same ./allure-results, so the report shows all 9 scenarios.
set -uo pipefail

APPIUM_URL="http://127.0.0.1:4723"
mkdir -p mobile-tests/target

echo "::group::Start Appium server"
appium --address 127.0.0.1 --port 4723 \
  --allow-insecure "*:chromedriver_autodownload" \
  --log-timestamp --log mobile-tests/target/appium.log &
APPIUM_PID=$!
# Always stop Appium when this script exits. A running Appium keeps adb connections (logcat,
# UiAutomator2 port forwarding) open to the emulator, which makes the runner's "Terminate
# Emulator" phase hang after the tests have finished.
# shellcheck disable=SC2329 # invoked by the EXIT trap below
stop_appium() {
  kill "$APPIUM_PID" 2>/dev/null && wait "$APPIUM_PID" 2>/dev/null
  echo "Appium server stopped"
}
trap stop_appium EXIT
# Poll /status until the server is ready (no fixed sleeps)
curl --silent --fail --retry 30 --retry-delay 2 --retry-connrefused "${APPIUM_URL}/status" \
  || { echo "::error::Appium server did not start"; exit 1; }
echo
echo "::endgroup::"

echo "::group::Regular mobile scenarios (must pass)"
./mvnw -B -ntp test -pl mobile-tests
regular=$?
echo "::endgroup::"

echo "::group::Intentional-failure scenarios (@negative, expected to fail)"
./mvnw -B -ntp test -pl mobile-tests -Dcucumber.filter.tags="@negative"
negative=$?
echo "::endgroup::"

{
  echo "### Mobile test results"
  echo
  echo "| Suite | Expectation | Outcome |"
  echo "|---|---|---|"
  echo "| Regular scenarios (1-7) | pass | $([ "$regular" -eq 0 ] && echo 'passed' || echo 'FAILED') |"
  echo "| Intentional crashes (8-9, \`@negative\`) | fail by design | $([ "$negative" -ne 0 ] && echo 'failed as expected' || echo 'UNEXPECTEDLY PASSED') |"
} >> "${GITHUB_STEP_SUMMARY:-/dev/stdout}"

if [ "$negative" -eq 0 ]; then
  echo "::warning::The @negative scenarios were expected to fail (the app crashes) but passed"
fi

exit "$regular"
