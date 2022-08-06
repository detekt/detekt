import React from "react";
import clsx from "clsx";
import Layout from "@theme/Layout";
import { rulesets } from "@site/src/data/rulesmarketplace";
import MarketplaceCard from "./_components/MarketplaceCard";
import styles from "./styles.module.css";

const TITLE = "Detekt 3rd Party Rules Marketplace";
const DESCRIPTION =
  "List of Detekt Rules that have been built by the community 🎉";
const SUBMIT_URL =
  "https://github.com/detekt/detekt/blob/main/website/src/data/rulesmarketplace.js";
const SEARCH_RULES_URL = "https://github.com/topics/detekt-rules";

function MarketplaceHeader() {
  return (
    <section className="margin-top--lg margin-bottom--lg text--center">
      <h1>Detekt 3rd Party Rules Marketplace</h1>
      <p>List of Detekt Rules that have been built by the community.</p>
      <a
        className={clsx(
          "button",
          "button--primary",
          styles.marketplaceHeaderButton
        )}
        href={SUBMIT_URL}
        target="_blank"
        rel="noreferrer"
      >
        🙏 Please add your ruleset
      </a>
      <a
        className={clsx(
          "button",
          "button--secondary",
          styles.marketplaceHeaderButton
        )}
        href={SEARCH_RULES_URL}
        target="_blank"
        rel="noreferrer"
      >
        Find more rules on Github
      </a>
    </section>
  );
}

function MarketplaceCards() {
  // No Results scenario
  if (rulesets.length === 0) {
    return (
      <section className="margin-top--lg margin-bottom--xl">
        <div className="container padding-vert--md text--center">
          <h2>No results</h2>
        </div>
      </section>
    );
  }

  return (
    <section className="margin-top--lg margin-bottom--xl">
      <>
        <div className="container margin-top--lg">
          <h2 className={styles.marketplaceHeader}>All rulesets</h2>
          <ul className={clsx("clean-list", styles.marketplaceList)}>
            {rulesets.map((ruleset) => (
              <MarketplaceCard key={ruleset.title} ruleset={ruleset} />
            ))}
          </ul>
        </div>
      </>
    </section>
  );
}

export default function Marketplace() {
  return (
    <Layout title={TITLE} description={DESCRIPTION}>
      <main className="margin-vert--lg">
        <MarketplaceHeader />
        <MarketplaceCards />
      </main>
    </Layout>
  );
}
