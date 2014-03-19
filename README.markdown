## Muuuuu (working title)
_a soulseek-inspired p2p music sharing web app_

### Why?
Simply because WebRTC and Soulseek are cool, I wanted to learn ClojureScript and
I had some time off because of some profitable bitcoin fluctuations. It's purely
a demo app of what is possible with html5 features and ClojureScript with the
excellent Om library. I have no means turning it into a business.

### Soulseek
A think I valued most of Soulseek is it's peer recommendations. Being able to
talk about music, browsing each others libraries and sending each other music 
which the other might like. I believe there is more value in the recommendation
from peers than from automated engines.

Also I believe there is no value with having a huge, lets say a 1m+ catalogue,
when you don't know what to search for. By slimming it down to a curated (by
peers) library where people choose to include only their favourites or simply
the stuff they have, functions as a good funnel. It only puts music on display
which people actually listen to and thus makes it easier to browse for new
music.

### Feedback please
This is my first Clojure(script) app. Any feedback from tricks to architectural
tips are more than welcome! Pull requests, email, twitter whatsoever!:)

### Current status
Finishing UI events, next up WebRTC

### Run

```make run```

### Compile css
Sorry, this requires node and stylus to be installed. Would love to have a
Clojure alternative.

```make build css```
