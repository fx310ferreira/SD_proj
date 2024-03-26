# SD

### Things to solve
 - [ ] If a downloader dies the page it is indexing stops getting indexed
 - [ ] Currently, we are not indexing the same this URL more than once, this is only partially correct we should only index it once,
 but we should send it to the barrels for metrics
 - [ ] Words
 - [x] Data storage: start by storing all the multicast messages sent
 - [x] Should we search small words: yes
 - [ ] Wierd things happen when we close programs ask the teacher
 - [x] How big are the messages we can send: 1400B / 64KB
 - [ ] An actually useful readme
 - [ ] Add barrel_id to subscribe to make it impossible that 2 barrels have the same id at the same time
 - [ ] Add possibility for auto ID, the id can be defined iterating all currently opened barrels and the first barrelX available
 - [x] Add protection for possible sql injection in barrelID
 - [x] On dispatcher push don't add if the link does not start
 - [ ] Dispatcher precessed queue behaviour
 - [ ] Think in a way for us not to have inactive barrels inside the array list
