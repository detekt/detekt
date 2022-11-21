import React from "react";
import clsx from "clsx";
import Link from "@docusaurus/Link";
import Layout from "@theme/Layout";
import { extensions } from "@site/src/data/marketplace";
import MarketplaceCard from "./_components/MarketplaceCard";
import styles from "./styles.module.css";

const TITLE = "Detekt 3rd-party Marketplace";
const DESCRIPTION =
  "List of Detekt Rules that have been built by the community 🎉";
const SUBMIT_URL =
  "https://github.com/detekt/detekt/blob/main/website/src/data/marketplace.js";
const DOCS_URL = "https://detekt.dev/docs/introduction/extensions/";
const SEARCH_RULES_URL = "https://github.com/topics/detekt-rules";

function MarketplaceHeader() {
  return (
    <section className="margin-top--lg margin-bottom--lg text--center">
      <h1>{TITLE}</h1>
      <p>
        List of <Link href={DOCS_URL}>Detekt Rules, Extensions</Link> & Plugins that have been built by the community.
      </p>
      <Link
        className={clsx(
          "button",
          "button--primary",
          styles.marketplaceHeaderButton
        )}
        href={SUBMIT_URL}
      >
        🙏 Please add your ruleset
      </Link>
      <Link
        className={clsx(
          "button",
          "button--secondary",
          styles.marketplaceHeaderButton
        )}
        href={SEARCH_RULES_URL}
      >
        Find more rules on Github
      </Link>
    </section>
  );
}

function MarketplaceCards() {
  // No Results scenario
  if (extensions.length === 0) {
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
          <h2 className={styles.marketplaceHeader}>All extensions</h2>
          <ul className={clsx("clean-list", styles.marketplaceList)}>
            {extensions.map((extension) => (
              <MarketplaceCard key={extension.title} extension={extension} />
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
