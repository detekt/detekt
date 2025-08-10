import { visit } from "unist-util-visit";

// Remark plugin that is replacing the [detekt_version] with the latest
// released version. Please note that this field is updated automatically
// by the `applyDocVersion` task.
const detektVersion = "2.0.0-alpha.0";

const detektVersionReplacePlugin = (options) => {
  const transformer = async (ast) => {
    visit(ast, "code", (node) => {
      if (node.value.includes("[detekt_version]")) {
        node.value = node.value.replaceAll("[detekt_version]", detektVersion);
      }
    });
  };
  return transformer;
};

module.exports = {
  detektVersionReplacePlugin: detektVersionReplacePlugin,
  detektVersion: detektVersion
};
