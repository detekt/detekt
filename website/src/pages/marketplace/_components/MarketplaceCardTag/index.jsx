import React from "react";
import { tagTypes } from "../../../../data/rulesmarketplace";
import styles from "./styles.module.css";

function MarketplaceCardTag(input) {

  const tag = input.tag;

  function getColorForTag(tag) {
    if (tag in tagTypes) {
      return tagTypes[tag].color;
    } else {
      return null;
    }
  }

  function getDescriptionForTag(tag) {
    if (tag in tagTypes) {
      return tagTypes[tag].description;
    } else {
      return null;
    }
  }

  return (
    <li title={getDescriptionForTag(tag)} className={styles.tag}>
      <span className={styles.textLabel}>{input.tag.toLowerCase()}</span>
      <span
        className={styles.colorLabel}
        style={{ backgroundColor: getColorForTag(tag) }}
      />
    </li>
  );
}

export default React.memo(MarketplaceCardTag);
