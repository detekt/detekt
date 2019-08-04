---
title:  "Calling it One Zero"
published: true
permalink: calling-it-one-zero.html
summary: "So we finally made it. One zero."
tags: [news]
---

Here are some metrics describing detekt's lifespan so far:

![lifespan](/images/one-zero/1-lifespan.png)

detekt is nearly **3** years old already!  
As GitHub tells us the project is pretty active.  
You may say that in 2018 it was more active telling by the number of commits, however that year 
we also changed our merge strategy from merge-with-rebase to squash-and-merge.
That said, it is much harder to achieve these high commit numbers now ;).

![numbers](/images/one-zero/2-numbers.png)

There are **2516** commits, **52** releases on GitHub and a total **93** contributors by the time of writing.
**20** out of the 93 authors contributed once or more in the last three months.

![numbers](/images/one-zero/3-bintray-absolute.png)

**~780k** downloads in the last **30 days** is a pretty high number ... three months ago it was like **500k**.
One can clearly see when weekends are ;).

This does however not mean "1 download = 1 user". There are like eight detekt modules each with a jar and pom which needs to be downloaded etc.
Most of the downloads should be coming from CI though it is pretty hard to calculate the number of users detekt actually has.

![numbers](/images/one-zero/4-bintray-percent.png)

What I also noticed is the high number of "early adopters" in the Kotlin world (or just detekt).
- RC09 was released in Sep 2018
- RC10 was released in Nov 2018
- RC11 was released in Nov 2018
- RC12 was released in Dec 2018
- RC14 was released in Feb 2019
- RC15 was released in Jun 2019
- RC16 was released in Jun 2019

**65%** of users are on a version published in 2019. But we clearly lost some users in the older versions due to breaking changes in RC13 and RC15.
I'm excited to see how many users will jump to the 1.x.x release train and how this numbers will look like.

Last but not least here is a worldmap of where detekt users are coming from.

![numbers](/images/one-zero/5-bintray-world.png)

References:
- https://bintray.com/arturbosch/code-analysis/detekt#statistics
- https://github.com/arturbosch/detekt/graphs/contributors
