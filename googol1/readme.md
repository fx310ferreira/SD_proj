# SD1

### Things to solve
 - [/] If a downloader dies the page it is indexing stops getting indexed: this can be fixed in the second delivery
 - [x] Currently, we are not indexing the same this URL more than once, this is only partially correct we should only index it once,
 but we should send it to the barrels for metrics
 - [x] Words
 - [x] Data storage: start by storing all the multicast messages sent
 - [x] Should we search small words: yes
 - [x] How big are the messages we can send: 1400B / 64KB
 - [x] An actually useful readme ✅ 2024-04-10
 - [x] Add barrel_id to subscribe to make it impossible that 2 barrels have the same id at the same time
 - [x] Add protection for possible sql injection in barrelID
 - [x] On dispatcher push don't add if the link does not start
 - [x] Dispatcher processed queue behaviour
 - [x] Think in a way for us not to have inactive barrels inside the array list
 - [x] Search words: order
 - [x] Barrel metrics
 - [x] Add feedback to the barrel when it fails to connect
 - [x] Add rollback and commits on every database operation ✅ 2024-04-10
 - [ ] Change all system.out.println to system.err.println and make them more compreensible, also make them do system.exit() when necessary
 - [x] Javadoc ✅ 2024-04-10
 - [ ] Text is missing when link is searched
 - [x] Correct ascii
 - [x] Search urls
 - [x] Double ended queue to allow user to insert url
 - [x] Add client callback to update admin page ✅ 2024-04-10
 - [ ] Change client to not crash on server crash