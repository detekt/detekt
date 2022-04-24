// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Detekt',
  tagline: 'A static code analyzer for Kotlin',
  url: 'https://detekt.dev/',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'throw',
  onDuplicateRoutes: 'throw',
  favicon: '/img/favicon.svg',
  organizationName: 'detekt',
  projectName: 'detekt',

  presets: [
    [
      '@docusaurus/preset-classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: 'https://github.com/detekt/detekt/edit/main/docs/',
        },
        blog: {
          showReadingTime: true,
          editUrl:
            'https://github.com/detekt/detekt/edit/main/docs/blog/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  plugins: [
    [
      '@docusaurus/plugin-client-redirects',
      {
        redirects: [
          { to: '/docs/introduction/changelog', from: '/changelog.html' },
          { to: '/docs/introduction/changelog-rc', from: '/changelog-rc.html' },
          { to: '/docs/introduction/configurations', from: '/configurations.html' },
          { to: '/docs/introduction/reporting', from: '/reporting.html' },
          { to: '/docs/introduction/suppressing-rules', from: '/suppressing-rules.html' },
          { to: '/docs/introduction/baseline', from: '/baseline.html' },
          { to: '/docs/introduction/extensions', from: '/extensions.html' },
          { to: '/docs/introduction/snapshots', from: '/snapshots.html' },
          { to: '/docs/introduction/compatibility', from: '/compatibility.html' },
          { to: '/docs/gettingstarted/cli', from: '/cli.html' },
          { to: '/docs/gettingstarted/gradle', from: '/gradle.html' },
          { to: '/docs/gettingstarted/gradle', from: '/groovydsl.html' },
          { to: '/docs/gettingstarted/gradle', from: '/kotlindsl.html' },
          { to: '/docs/gettingstarted/gradletask', from: '/gradletask.html' },
          { to: '/docs/gettingstarted/mavenanttask', from: '/mavenanttask.html' },
          { to: '/docs/gettingstarted/type-resolution', from: '/type-resolution.html' },
          { to: '/docs/gettingstarted/type-resolution', from: '/type-and-symbol-solving.html' },
          { to: '/docs/gettingstarted/git-pre-commit-hook', from: '/git-pre-commit-hook.html' },
          { to: '/docs/rules/comments', from: '/comments.html' },
          { to: '/docs/rules/complexity', from: '/complexity.html' },
          { to: '/docs/rules/coroutines', from: '/coroutines.html' },
          { to: '/docs/rules/empty-blocks', from: '/empty-blocks.html' },
          { to: '/docs/rules/exceptions', from: '/exceptions.html' },
          { to: '/docs/rules/formatting', from: '/formatting.html' },
          { to: '/docs/rules/naming', from: '/naming.html' },
          { to: '/docs/rules/performance', from: '/performance.html' },
          { to: '/docs/rules/potential-bugs', from: '/potential-bugs.html' },
          { to: '/docs/rules/style', from: '/style.html' },
        ],
      },
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'Detekt',
        logo: {
          alt: 'Detekt Logo',
          src: 'img/favicon.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'intro',
            position: 'left',
            label: 'Docs',
          },
          {
            to: '/blog', 
            label: 'Blog', 
            position: 'left'
          },
          {
            href: 'https://detekt.dev/kdoc/',
            position: 'left',
            label: 'API',
          },
          {
            href: 'https://github.com/detekt/detekt',
            label: 'GitHub',
            position: 'right',
            className: 'header-github-link',
            'aria-label': 'GitHub repository',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Getting Started with Gradle',
                to: '/docs/gettingstarted/gradle',
              },
              {
                label: 'Getting Started with the CLI',
                to: '/docs/gettingstarted/gradle',
              },
              {
                label: 'Rules Documentation',
                to: '/docs/rules/comments',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Slack',
                href: 'https://kotlinlang.slack.com/archives/C88E12QH4',
              },
              {
                label: 'Stack Overflow',
                href: 'https://stackoverflow.com/questions/tagged/detekt',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'Blog',
                to: '/blog',
              },
              {
                label: 'GitHub',
                href: 'https://github.com/detekt/detekt',
              },
              {
                label: 'KDoc',
                href: 'https://detekt.dev/kdoc',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Detekt team - Built with Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ['kotlin', 'groovy', 'java'],
      },
    }),

  customFields: {
    detektVersion: '1.20.0'
  },
};

module.exports = config;
