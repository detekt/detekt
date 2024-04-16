import Link from "@docusaurus/Link";
import {useActiveVersion} from "@docusaurus/plugin-content-docs/client";

export function DefaultConfigurationFile() {
    const activeVersion = useActiveVersion();
    const versionTag = activeVersion.name === "current" ? "main" : `v${activeVersion.name}`;
    const location = `https://github.com/detekt/detekt/blob/${versionTag}/detekt-core/src/main/resources/default-detekt-config.yml`;

    return <Link href={location}>default-detekt-config.yml</Link>;
}
