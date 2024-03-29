import { danger, fail, markdown, warn, message } from "danger";

// API reference: https://danger.systems/js/reference.html
const pr = danger.github.pr;

const functionalChanges = danger.git.fileMatch("**/src/main/kotlin/**/*.kt");
const testChanges = danger.git.fileMatch("**/src/test/kotlin/**/*.kt");
const docsChanges = danger.git.fileMatch("**/*.md");
const rulesChanges = danger.git.fileMatch(
  "detekt-rules-*/src/main/kotlin/**/*.kt"
);
const ruleTestChanges = danger.git.fileMatch(
  "detekt-rules-*/src/test/kotlin/**/*.kt"
);
const detektConfigFileChanges = danger.git.fileMatch(
  "**/default-detekt-config.yml"
);
const websiteUnversionedChanges = danger.git.fileMatch(
    "website/docs/**/*.md",
    "website/docs/**/*.mdx");
const websiteVersionedChanges = danger.git.fileMatch(
    "website/versioned_docs/**/*.md",
    "website/versioned_docs/**/*.mdx");
const milestone = danger.github.pr.milestone;
const prReviews = danger.github.reviews;

const optsCheckmark = {
  icon: ":white_check_mark:",
};

// Warn if the PR contains a functional change without a test
if (functionalChanges.edited && !testChanges.edited) {
  warn(
    "It looks like this PR contains functional changes without a corresponding test."
  );
}

// Handle PRs for new detekt rules.
if (rulesChanges.created) {
  message("Thanks for adding a new rule to detekt :heart:");
  if (!ruleTestChanges.edited) {
    warn(
      "It looks like your new rule doesn't comes with tests. Make sure you include them."
    );
  } else {
    message(
      "We detekted that you added tests, to your rule, that's awesome!",
      optsCheckmark
    );
  }
  if (!detektConfigFileChanges.edited) {
    warn(
      "It looks like you haven't updated the `default-detekt-config.yml` file in your PR. Make sure you run `./gradlew generateDocumentation`"
    );
  } else {
    message(
      "We detekted that you updated the `default-detekt-config.yml` file, that's awesome!",
      optsCheckmark
    );
  }
}

// If `default-detekt-config.yml` changes without a rule change, warn the user
if (detektConfigFileChanges.length > 0 && rulesChanges.length === 0) {
  warn(
    "It looks like you're touching the `default-detekt-config.yml` file without updating the rules code. This file is automatically generated with the `./gradlew generateDocumentation` task, so your changes will be overridden."
  );
}

// Say thank you to Website edits.
if (docsChanges.edited) {
  message(
    "Thank you very much for making [our website](https://detekt.dev/) better :heart:!"
  );
}

// Warn if the PR has been accepted but has no milestone set.
if (!milestone && prReviews.some((review) => review.state === "APPROVED")) {
  warn(
    "This PR is approved with no milestone set. If merged, it won't appear in the detekt release notes."
  );
}

// Warn if docs changes are either on the un-versioned copy or the versioned copy only.
if (websiteUnversionedChanges.edited && !websiteVersionedChanges.edited) {
  warn(
    "It looks like you're editing the **un-versioned copy** of our website. This affects only users on the 'next' version of detekt, and it's correct only if you intend to document a **future change or feature**. " +
      "If you intended to make a change also for the **current** version of detekt, please make sure you edit the equivalent file inside `website/versioned_docs/` as well."
  );
} else if (!websiteUnversionedChanges.edited && websiteVersionedChanges.edited) {
  warn(
    "It looks like you're editing the **versioned copy** of our website. This affects only users on the 'current' version of detekt, and it's correct only if you intend to fix the documentation for an **already released** version of detekt. " +
      "Most of the time you want to update also the docs inside `website/docs/` as well, so this change will reflect also for documentation on **future versions** of detekt."
  );
}
