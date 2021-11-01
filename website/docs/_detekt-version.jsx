import React from "react";
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";

const DetektVersion = () => (
    <span>{useDocusaurusContext().siteConfig.customFields.detektVersion}</span>
);

export default DetektVersion