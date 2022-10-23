import React from "react";
import clsx from "clsx";
import Link from "@docusaurus/Link";
import styles from "./styles.module.css";
import MarketplaceCardTag from "../MarketplaceCardTag";

function MarketplaceCard(input) {
  const extension = input.extension;
  return (
    <li key={extension.title} className="card shadow--md">
      <div className="card__body">
        <div className={styles.marketplaceCardHeader}>
          <h3 className={styles.marketplaceCardTitle}>{extension.title}</h3>
          <ul className={styles.tagContainer}>
            {extension.tags.map((tag) => (
              <MarketplaceCardTag tag={tag} />
            ))}
          </ul>
          <Link
            href={extension.repo}
            className={clsx(
              "button",
              "button--secondary",
              "button--sm",
              styles.marketplaceHeaderButton
            )}
          >
            Source
          </Link>
        </div>
        <div className={styles.marketplaceCardBody}>
          <Link href={extension.repo}>{extension.repo}</Link>
          <p>{extension.description}</p>
          {extension.rules && (
            <p>
              <h5>Rules</h5>
              <p>
                Uses type resolution:{" "}
                <strong>{extension.usesTypeResolution.toString()}</strong>
              </p>
              <p>
                <ul>
                  {extension.rules.map((rule) => (
                    <li>
                      <code>{rule}</code>
                    </li>
                  ))}
                </ul>
              </p>
            </p>
          )}
        </div>
      </div>
    </li>
  );
}

export default React.memo(MarketplaceCard);
