// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

import { themes as prismThemes } from "prism-react-renderer";
const { detektVersionReplacePlugin, detektVersion } = require("./src/remark/detektVersionReplace");

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: "detekt",
  tagline: "A static code analyzer for Kotlin",
  url: "https://detekt.dev/",
  baseUrl: "/",
  onBrokenLinks: "throw",
  onBrokenMarkdownLinks: "throw",
  onDuplicateRoutes: "throw",
  favicon: "/img/favicon.svg",
  organizationName: "detekt",
  projectName: "detekt",

  presets: [
    [
      "@docusaurus/preset-classic",
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve("./sidebars.js"),
          editUrl: "https://github.com/detekt/detekt/edit/main/website/",
          remarkPlugins: [detektVersionReplacePlugin],
        },
        blog: {
          showReadingTime: true,
          editUrl: "https://github.com/detekt/detekt/edit/main/website/",
        },
        theme: {
          customCss: require.resolve("./src/css/custom.css"),
        },
      }),
    ],
  ],

  plugins: [
    [
      "@docusaurus/plugin-client-redirects",
      {
        redirects: [
          { to: "/changelog", from: "/docs/introduction/changelog" },
          { to: "/changelog-rc", from: "/docs/introduction/changelog-rc" },
          { to: "/changelog", from: "/changelog.html" },
          { to: "/changelog-rc", from: "/changelog-rc.html" },
          {
            to: "/docs/introduction/configurations",
            from: "/configurations.html",
          },
          { to: "/docs/introduction/reporting", from: "/reporting.html" },
          {
            to: "/docs/introduction/suppressing-rules",
            from: "/suppressing-rules.html",
          },
          { to: "/docs/introduction/baseline", from: "/baseline.html" },
          { to: "/docs/introduction/extensions", from: "/extensions.html" },
          { to: "/docs/introduction/snapshots", from: "/snapshots.html" },
          {
            to: "/docs/introduction/compatibility",
            from: "/compatibility.html",
          },
          { to: "/docs/introduction/compose", from: "/compose.html" },
          { to: "/docs/gettingstarted/cli", from: "/cli.html" },
          { to: "/docs/gettingstarted/gradle", from: "/gradle.html" },
          { to: "/docs/gettingstarted/gradle", from: "/groovydsl.html" },
          { to: "/docs/gettingstarted/gradle", from: "/kotlindsl.html" },
          { to: "/docs/gettingstarted/gradletask", from: "/gradletask.html" },
          {
            to: "/docs/gettingstarted/mavenanttask",
            from: "/mavenanttask.html",
          },
          {
            to: "/docs/gettingstarted/type-resolution",
            from: "/type-resolution.html",
          },
          {
            to: "/docs/gettingstarted/type-resolution",
            from: "/type-and-symbol-solving.html",
          },
          {
            to: "/docs/gettingstarted/git-pre-commit-hook",
            from: "/git-pre-commit-hook.html",
          },
          { to: "/docs/rules/comments", from: "/comments.html" },
          { to: "/docs/rules/complexity", from: "/complexity.html" },
          { to: "/docs/rules/coroutines", from: "/coroutines.html" },
          { to: "/docs/rules/empty-blocks", from: "/empty-blocks.html" },
          { to: "/docs/rules/exceptions", from: "/exceptions.html" },
          { to: "/docs/rules/formatting", from: "/formatting.html" },
          { to: "/docs/rules/naming", from: "/naming.html" },
          { to: "/docs/rules/performance", from: "/performance.html" },
          { to: "/docs/rules/potential-bugs", from: "/potential-bugs.html" },
          { to: "/docs/rules/style", from: "/style.html" },
          { to: "/docs/introduction/suppressors", from: "/suppressors.html" },
        ],
        createRedirects(existingPath) {
          if (existingPath.startsWith("/docs/rules/")) {
            return existingPath.replace("/docs/rules/", `/docs/${detektVersion}/rules/`)
          } else {
            return undefined;
          }
        },
      },
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: "detekt",
        logo: {
          alt: "detekt logo",
          src: "img/favicon.svg",
        },
        items: [
          {
            type: "doc",
            docId: "intro",
            position: "left",
            label: "Docs",
          },
          {
            to: "/blog",
            label: "Blog",
            position: "left",
          },
          {
            to: "https://detekt.dev/kdoc/",
            label: "APIs",
            position: "left",
          },
          {
            to: "/marketplace",
            label: "Marketplace",
            position: "left",
          },
          {
            type: "docsVersionDropdown",
            position: "right",
            dropdownActiveClassDisabled: true,
            dropdownItemsAfter: [
              {
                to: "/changelog",
                label: "All changelogs",
              },
            ],
          },
          {
            href: "https://github.com/detekt/detekt",
            label: "GitHub",
            position: "right",
            className: "header-github-link",
            "aria-label": "GitHub repository",
          },
        ],
      },
      footer: {
        style: "dark",
        links: [
          {
            title: "Docs",
            items: [
              {
                label: "Getting Started with Gradle",
                to: "/docs/gettingstarted/gradle",
              },
              {
                label: "Getting Started with the CLI",
                to: "/docs/gettingstarted/cli",
              },
              {
                label: "Rules Documentation",
                to: "/docs/rules/comments",
              },
            ],
          },
          {
            title: "Community",
            items: [
              {
                label: "Slack",
                href: "https://kotlinlang.slack.com/archives/C88E12QH4",
              },
              {
                label: "Stack Overflow",
                href: "https://stackoverflow.com/questions/tagged/detekt",
              },
            ],
          },
          {
            title: "More",
            items: [
              {
                label: "Blog",
                to: "/blog",
              },
              {
                label: "GitHub",
                href: "https://github.com/detekt/detekt",
              },
              {
                label: "KDoc",
                href: "https://detekt.dev/kdoc",
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} detekt team - Built with Docusaurus.`,
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
        additionalLanguages: ["kotlin", "groovy", "java"],
      },
      algolia: {
        appId: "5PZNXB7M3G",
        apiKey: "6f23d0811156d77c936736893b97c5fd",
        indexName: "detekt",
        contextualSearch: true,
      },
    }),
};

module.exports = config;
