import React from 'react';
import clsx from 'clsx';
import styles from './HomepageFeatures.module.css';

const FeatureList = [
  {
    title: 'Your companion for cleaner Kotlin',
    image: 'img/home/detekt-logo.svg',
    description: (
      <>
        Detekt helps you write cleaner Kotlin code so you can focus on what
        matters the most <strong>building amazing software</strong>.
      </>
    ),
  },
  {
    title: 'Integrate in any project',
    image: 'img/home/gradle-logo.svg',
    description: (
      <>
        Detekt comes with a set of plugins that helps you configure it easily in your
        Gradle, Maven, Bazel, ... build. Enjoy static analysis on Android, JVM, JS, Native
        and Multiplatform projects out of the box.
      </>
    ),
  },
  {
    title: 'Easy to extend',
    image: 'img/home/plugin-logo.svg',
    description: (
      <>
        Detekt can be easily extended with custom rules that helps you track and
        fix anti-patterns in your codebase.
      </>
    ),
  },
  {
    title: 'Community Driven',
    image: 'img/home/github-logo.svg',
    description: (
      <>
        Detekt is entirely open-source and developed by the community. Join us
        on GitHub and help us shape the future of this tool.
      </>
    ),
  },
];

function Feature({title, image, description}) {
  return (
    <div className={clsx('col col--6')}>
      <div className="text--center">
        <img className={styles.featureImg} src={image} alt={image} />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
