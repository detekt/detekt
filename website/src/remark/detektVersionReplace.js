import { visit } from "unist-util-visit";

// Remark plugin that is replacing the [detekt_version] with the latest
// released version. Please note that this field is updated automatically
// by the `applyDocVersion` task.
const detektVersion = "2.0.0-alpha.1";

const getVersionToUse = filePath => {
  if (!filePath.includes("versioned_docs/")) return detektVersion;

  const match = filePath.match(/version-(.*?)\//);
  if (!match) throw new Error(`Could not extract version from path: ${filePath}`);
  return match[1];
};

const detektVersionReplacePlugin = (options) => {
  const transformer = async (ast, file) => {
    visit(ast, "code", (node) => {
      if (node.value.includes("[detekt_version]")) {
        node.value = node.value.replaceAll("[detekt_version]", getVersionToUse(file.path));
      }
    });
  };
  return transformer;
};

module.exports = {
  detektVersionReplacePlugin: detektVersionReplacePlugin,
  detektVersion: detektVersion
};
