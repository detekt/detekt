import React from "react";
import Link from "@docusaurus/Link";
import styles from "./styles.module.css";

function MarketplaceCard(input) {
  const ruleset = input.ruleset;
  return (
    <li key={ruleset.title} className="card shadow--md">
      <div className="card__body">
        <div className={styles.marketplaceCardHeader}>
          <h3 className={styles.marketplaceCardTitle}>
            <Link href={ruleset.repo} className={styles.marketplaceCardLink}>
              {ruleset.title}
            </Link>
          </h3>
        </div>
        <div className={styles.marketplaceCardBody}>
          <p>{ruleset.description}</p>
          <h5>Coordinates</h5>
          <p>
            <code>detektPlugins("{ruleset.mavenCoordinates}")</code> on{" "}
            <strong>{ruleset.mavenRepo}</strong>
          </p>
          <h5>Rules</h5>
          <p>
            Uses type resolution:{" "}
            <strong>{ruleset.usesTypeResolution.toString()}</strong>
          </p>
          <p>
            <ul>
              {ruleset.rules.map((rule) => (
                <li>
                  <code>{rule}</code>
                </li>
              ))}
            </ul>
          </p>
        </div>
      </div>
    </li>
  );
}

export default React.memo(MarketplaceCard);
