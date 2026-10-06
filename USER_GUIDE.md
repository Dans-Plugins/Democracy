# Democracy User Guide

## What is Democracy?

Democracy is a Spigot plugin that adds democratic elections to Medieval Factions servers. Faction members can run for leadership, vote for candidates, and determine their faction leader through an in-game election process.

## Requirements

- [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) must be installed on the server.

## Installation

1. Download the latest `Democracy-<version>.jar` from the [Releases](https://github.com/Dans-Plugins/Democracy/releases) page.
2. Place the JAR (and the Medieval Factions JAR) in your server's `plugins/` folder.
3. Restart the server.

## How Elections Work

1. The current faction leader runs `/d start` to begin an election.
2. Any faction member can run `/d run` to declare their candidacy, or `/d dropout` to withdraw it.
3. Faction members vote using `/d vote <candidate>`.
4. Use `/d info` to check the current election status and vote tallies.

An election in progress survives a server restart: elections, candidates and votes are saved to `elections.json`, `candidates.json` and `voters.json` in `plugins/Democracy/` each time `/d start`, `/d run`, `/d dropout` or `/d vote` changes them, and again when the server stops, and loaded again when it starts. A server that crashes rather than stopping cleanly therefore keeps every change a player was told had succeeded; if a save fails, the cause is reported in the console. If one of those files cannot be read, the plugin reports it in the console and disables itself, leaving the files untouched so they can be repaired. There is not yet a command to end an election, so a faction's election stays in progress until its entry is removed from `elections.json` while the server is stopped.

### Who Can Start, Run and Vote

- Only the owner of a faction can start an election in it. A member who is not the owner, and a player in no faction, are refused.
- A player can only take part in their own faction's election: running and voting both apply to the election of the faction the player is in. Members of other factions, allies included, cannot run or vote in it; if their own faction has no election, they are told so.
- Players in no faction can neither run nor vote.
- Each player can vote once per election, for a player who is running in it; a second vote is refused and the first one stands.

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `d.help` | `true` | View the help menu. |
| `d.info` | `true` | View election information. |
| `d.run` | `true` | Run as a candidate. |
| `d.dropout` | `true` | Drop out of an election. |
| `d.vote` | `true` | Vote in an election. |
| `d.start` | `true` | Start a faction election. |

## Support

Ask questions in the [Discord server](https://discord.gg/xXtuAQ2) or open a [GitHub issue](https://github.com/Dans-Plugins/Democracy/issues).
