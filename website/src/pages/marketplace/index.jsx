import React from "react";
import clsx from "clsx";
import Link from "@docusaurus/Link";
import Layout from "@theme/Layout";
import { extensions, tagTypes } from "@site/src/data/marketplace";
import MarketplaceCard from "./_components/MarketplaceCard";
import MarketplaceCardTag from "./_components/MarketplaceCardTag";
import styles from "./styles.module.css";

const TITLE = "Detekt 3rd-party Marketplace";
const DESCRIPTION =
  "List of detekt rules that have been built by the community 🎉";
const SUBMIT_URL =
  "https://github.com/detekt/detekt/blob/main/website/src/data/marketplace.js";
const DOCS_URL = "https://detekt.dev/docs/introduction/extensions/";

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
        href="#unpublished"
      >
        Find more on Github
      </Link>
    </section>
  );
}

function MarketplaceCards() {
  // No Results scenario
  if (extensions.length === 0) {
    return (
      <section className="margin-top--lg margin-bottom--lg">
        <div className="container padding-vert--md text--center">
          <h2>No results</h2>
        </div>
      </section>
    );
  }

  return (
    <section className="margin-top--lg margin-bottom--lg">
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

function MarketplaceFooter() {
  return (
    <section id="unpublished" className="margin-top--lg margin-bottom--xl">
      <div className="container margin-top--lg">
        <h2>Unpublished community resources</h2>
        <p>
          List of <Link href={DOCS_URL}>Detekt Rules, Extensions</Link> that may be hidden from sight.
        </p>
        <ul className={clsx("clean-list")}>
          {Object.keys(tagTypes).map((tag) => (
            <>
              <MarketplaceCardTag tag={tag} />
              <li>
                <ul>
                  {tagTypes[tag].communityUrls.map((url) => (
                    <li>
                      <Link href={url}>{url}</Link>
                    </li>
                  ))}
                </ul>
              </li>
            </>
          ))}
        </ul>
      </div>
    </section>
  );
}

export default function Marketplace() {
  return (
    <Layout title={TITLE} description={DESCRIPTION}>
      <main className="margin-vert--lg">
        <MarketplaceHeader />
        <MarketplaceCards />
        <MarketplaceFooter />
      </main>
    </Layout>
  );
}
