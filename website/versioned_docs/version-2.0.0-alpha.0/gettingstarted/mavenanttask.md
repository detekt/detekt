---
title: "Run detekt using Maven Ant Task"
keywords: [maven, anttask]
sidebar: 
permalink: mavenanttask.html
folder: gettingstarted
summary:
sidebar_position: 4
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
                                  classname="dev.detekt.cli.Main"
                                  classpathref="maven.plugin.classpath">
                                <arg value="--input"/>
                                <arg value="${basedir}/src/main/kotlin"/>
                                <arg value="--excludes"/>
                                <arg value="**/special/package/internal/**"/>
                                <arg value="--report"/>
                                <arg value="xml:${basedir}/reports/detekt.xml"/>
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
                    <groupId>dev.detekt</groupId>
                    <artifactId>detekt-cli</artifactId>
                    <version>[detekt_version]</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```
