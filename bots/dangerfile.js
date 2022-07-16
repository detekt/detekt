import { danger, fail, markdown, warn, message } from "danger";

// API reference: https://danger.systems/js/reference.html

const pr = danger.github.pr

// Find out if some particular files have changed in this PR
const changelog = danger.git.fileMatch("CHANGELOG.md")

// Rules

// When there are app-changes and it us not a PR marked as trivial, expect
// there to be CHANGELOG changes.
const trivialPR = pr.body.includes("trivial")
if (!changelog.edited && !trivialPR) {
  fail("No CHANGELOG added.")
}
