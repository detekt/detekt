---
title: "Run detekt using Maven Ant Task"
keywords: maven anttask
tags: [getting_started, maven]
sidebar: 
permalink: mavenanttask.html
folder: gettingstarted
summary:
---

1. Add following lines to your pom.xml.
2. Run `mvn verify` (when using the verify phase as we are doing here)

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-antrun-plugin</artifactId>
            <version>1.8</version>
            <executions>
                <execution>
                    <!-- This can be run separately with mvn antrun:run@detekt -->
                    <id>detekt</id>
                    <phase>verify</phase>
                    <configuration>
                        <target name="detekt">
                            <java taskname="detekt" dir="${basedir}"
                                  fork="true" 
                                  failonerror="true"
                                  classname="io.gitlab.arturbosch.detekt.cli.Main"
                                  classpathref="maven.plugin.classpath">
                                <arg value="--input"/>
                                <arg value="${basedir}/src/main/kotlin"/>
                                <arg value="--filters"/>
                                <arg value=".*/target/.*,.*/resources/.*"/>
                                <arg value="--output"/>
                                <arg value="${basedir}/reports"/>
                                <arg value="--outputname"/>
                                <arg value="detekt-report"/>
                                <arg value="--baseline"/>
                                <arg value="${basedir}/reports/baseline.xml"/>
                            </java>
                        </target>
                    </configuration>
                    <goals><goal>run</goal></goals>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>io.gitlab.arturbosch.detekt</groupId>
                    <artifactId>detekt-cli</artifactId>
                    <version>1.0.0.[version]</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>

<!-- You need this repository as detekt is not yet on MavenCentral -->
<pluginRepositories>
  <pluginRepository>
    <id>arturbosch-code-analysis</id>
    <name>arturbosch-code-analysis (for detekt)</name>
    <url>https://dl.bintray.com/arturbosch/code-analysis/</url>
    <layout>default</layout>
    <releases>
      <enabled>true</enabled>
      <updatePolicy>never</updatePolicy>
    </releases>
    <snapshots>
      <enabled>false</enabled>
      <updatePolicy>never</updatePolicy>
    </snapshots>
  </pluginRepository>
</pluginRepositories>
```
