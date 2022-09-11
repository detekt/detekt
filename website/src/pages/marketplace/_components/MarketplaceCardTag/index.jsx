import React from "react";
import { tagTypes } from "../../../../data/rulesmarketplace";
import styles from "./styles.module.css";

// const TagComp = React.forwardRef<HTMLLIElement>(
//   ({label, color, description}, ref) => (
//     <li ref={ref} className={styles.tag} title={description}>
//       <span className={styles.textLabel}>{label.toLowerCase()}</span>
//       <span className={styles.colorLabel} style={{backgroundColor: color}} />
//     </li>
//   ),
// );

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
    // <Tooltip
    //   key={index}
    //   text={tagObject.description}
    //   anchorEl="#__docusaurus"
    //   id={id}
    // >
    //   <TagComp key={index} {...tagObject} />
    // </Tooltip>
  );
}

export default React.memo(MarketplaceCardTag);
