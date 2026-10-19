## Planning
### Players should be able to
- view the current/last election — the current one can be viewed with `/d info`; a cancelled election is discarded, so there is no last election to view
- vote for a candidate during an active election — done (`/d vote <candidate>`)
- run as a candidate — done (`/d run`, and `/d dropout` to withdraw)
- start an election — done (`/d start`)
- end an election — an election can be cancelled without a winner (`/d cancel`); concluding one and installing the winner as faction leader is not built yet (#6)

### Domain Objects
- Election
- Candidate
- Voter