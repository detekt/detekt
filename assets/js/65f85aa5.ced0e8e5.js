"use strict";(self.webpackChunk_detekt_website=self.webpackChunk_detekt_website||[]).push([[2842],{156:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>d,contentTitle:()=>o,default:()=>c,frontMatter:()=>r,metadata:()=>a,toc:()=>l});const a=JSON.parse('{"id":"gettingstarted/mavenanttask","title":"Run detekt using Maven Ant Task","description":"1. Add following lines to your pom.xml.","source":"@site/versioned_docs/version-1.23.6/gettingstarted/mavenanttask.md","sourceDirName":"gettingstarted","slug":"/gettingstarted/mavenanttask","permalink":"/docs/1.23.6/gettingstarted/mavenanttask","draft":false,"unlisted":false,"editUrl":"https://github.com/detekt/detekt/edit/main/website/versioned_docs/version-1.23.6/gettingstarted/mavenanttask.md","tags":[],"version":"1.23.6","sidebarPosition":4,"frontMatter":{"title":"Run detekt using Maven Ant Task","keywords":["maven","anttask"],"sidebar":null,"permalink":"mavenanttask.html","folder":"gettingstarted","summary":null,"sidebar_position":4},"sidebar":"defaultSidebar","previous":{"title":"Run detekt using Gradle Task","permalink":"/docs/1.23.6/gettingstarted/gradletask"},"next":{"title":"Using Type Resolution","permalink":"/docs/1.23.6/gettingstarted/type-resolution"}}');var s=t(74848),i=t(28453);const r={title:"Run detekt using Maven Ant Task",keywords:["maven","anttask"],sidebar:null,permalink:"mavenanttask.html",folder:"gettingstarted",summary:null,sidebar_position:4},o=void 0,d={},l=[];function u(e){const n={code:"code",li:"li",ol:"ol",pre:"pre",...(0,i.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsxs)(n.ol,{children:["\n",(0,s.jsx)(n.li,{children:"Add following lines to your pom.xml."}),"\n",(0,s.jsxs)(n.li,{children:["Run ",(0,s.jsx)(n.code,{children:"mvn verify"})," (when using the verify phase as we are doing here)"]}),"\n"]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-xml",children:'<build>\n    <plugins>\n        <plugin>\n            <groupId>org.apache.maven.plugins</groupId>\n            <artifactId>maven-antrun-plugin</artifactId>\n            <version>1.8</version>\n            <executions>\n                <execution>\n                    \x3c!-- This can be run separately with mvn antrun:run@detekt --\x3e\n                    <id>detekt</id>\n                    <phase>verify</phase>\n                    <configuration>\n                        <target name="detekt">\n                            <java taskname="detekt" dir="${basedir}"\n                                  fork="true" \n                                  failonerror="true"\n                                  classname="io.gitlab.arturbosch.detekt.cli.Main"\n                                  classpathref="maven.plugin.classpath">\n                                <arg value="--input"/>\n                                <arg value="${basedir}/src/main/kotlin"/>\n                                <arg value="--excludes"/>\n                                <arg value="**/special/package/internal/**"/>\n                                <arg value="--report"/>\n                                <arg value="xml:${basedir}/reports/detekt.xml"/>\n                                <arg value="--baseline"/>\n                                <arg value="${basedir}/reports/baseline.xml"/>\n                            </java>\n                        </target>\n                    </configuration>\n                    <goals><goal>run</goal></goals>\n                </execution>\n            </executions>\n            <dependencies>\n                <dependency>\n                    <groupId>io.gitlab.arturbosch.detekt</groupId>\n                    <artifactId>detekt-cli</artifactId>\n                    <version>1.23.6</version>\n                </dependency>\n            </dependencies>\n        </plugin>\n    </plugins>\n</build>\n'})})]})}function c(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,s.jsx)(n,{...e,children:(0,s.jsx)(u,{...e})}):u(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>r,x:()=>o});var a=t(96540);const s={},i=a.createContext(s);function r(e){const n=a.useContext(i);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:r(e.components),a.createElement(i.Provider,{value:n},e.children)}}}]);