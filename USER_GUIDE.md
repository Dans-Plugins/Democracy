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
2. Any faction member can run `/d run` to declare their candidacy.
3. Faction members vote using `/d vote`.
4. The candidate with the most votes becomes the new faction leader.
5. Use `/d info` to check the current election status.

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
