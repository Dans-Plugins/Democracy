# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Changed

- The usage-reporting "Details" link (startup notice, `config.yml` and the docs) now points at https://danielstephenson.dev/usage-reporting, a public page; the previous link led to a private repository and returned 404 for everyone. The vendored trace client is now 0.6.1, which carries the same link in the `plugins/trace/config.yml` header it writes. Details: https://github.com/Stephenson-Software/trace-client-java/releases/tag/0.6.1.
- **Works with Medieval Factions 5.x and 6.x.** Democracy was built against Medieval Factions 4.6.2 and bundled a copy of it in its own jar, so on any current server it called that copy instead of the running plugin and failed to enable (#37). It now uses the installed Medieval Factions and bundles none. Starting an election needs the permission to disband the faction, which the Owner role has by default; elections are still kept per faction name, so existing election data is unchanged.
- Usage reporting is now disclosed on every start: the console says whether it is on, what is sent and where, and how to turn it off, or why it is off. A server-wide switch is added — `enabled: false` in `plugins/trace/config.yml` (written by the first reporting plugin to start) turns reporting off for every plugin that reports to trace — as are the environment variables `TRACE_USAGE_REPORTING=off` and `DO_NOT_TRACK=1`. The `usage-reporting` block is now written into `plugins/Democracy/config.yml` the first time the plugin starts without it, so the opt-out is visible on upgraded servers too. What is sent is unchanged. Details: https://danielstephenson.dev/usage-reporting
- The vendored trace client is now 0.3.0. `plugins/trace/config.yml` can now carry a `tags:` block whose entries are added to every usage event the plugin sends, so a test server can mark its own events (the release gates write `ci: "true"`) and be left out of the figures for real installations. Nothing changes for a server whose `plugins/trace/config.yml` has no `tags:` block.

### Added

- The plugin now reports usage events — `startup` on enable, `command` on each of its commands — to the author's trace server so it is known which plugins are in use. Events carry the plugin name, the event name, and the plugin version or command name; nothing about players or the server. Reporting runs off the main thread, never delays a tick, drops silently when the server is unreachable, and is turned off with `usage-reporting.enabled: false` in `config.yml`. The default config carries the plugin's key, so reporting is active out of the box unless turned off — including on servers upgraded from a version before the `usage-reporting` block existed, whose `config.yml` is never rewritten: the plugin reads the bundled defaults for any key the file lacks
- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get democracy --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

### Removed
- The `A` and `C` placeholder config options carried over from the Ponder example plugin have been dropped from `ConfigService`. Neither was ever written to `config.yml`, and the `A` branch would have coerced its value to an integer
- The placeholder message sent to every player on join ("This message was sent by ExamplePonderPlugin.") has been removed, along with the empty listener that sent it

### Fixed
- `/d vote <candidate>` now works when the candidate is offline. The name was looked up among online players only, so a vote for a candidate who had logged off was refused with "That player is not a candidate in this election." The name is now matched, ignoring case, against the election's own candidates, and that message is only sent when the named player really is not standing
- `/d help` now lists `/d vote <candidate>` instead of `/d vote`, matching the command's usage message and `COMMANDS.md`. A player who typed the command as the help menu showed it was sent a usage error
- The plugin now loads. The Medieval Factions API was looked up when the jar was loaded, before Medieval Factions had been enabled, so the lookup failed with a `NullPointerException` and Democracy was never enabled. It is now looked up in `onEnable()`, after Medieval Factions has started; if Medieval Factions is still not available there, the console says so and Democracy disables itself
- An election in progress now survives a server restart. Elections, candidates and votes are written to `elections.json`, `candidates.json` and `voters.json` in `plugins/Democracy/` as soon as `/d start`, `/d run`, `/d dropout` or `/d vote` changes them, and again when the plugin is disabled, and read back when it is enabled; previously every `save()` returned nothing and every `load()` was empty, so a restart silently discarded the election and all its votes. If a file cannot be read, the failure is reported in the console and the plugin disables itself rather than run on state it could not keep; the files are left untouched so they can be repaired, and nothing is written over them. Each file is written to a temporary sibling and moved into place, so a crash mid-write leaves the previous file intact. Because each change is saved as it is made, a server that crashes or is killed rather than stopping loses nothing a player was told had succeeded, unless that save itself failed, which is reported in the console. Because there is not yet a command to end an election (#6), an election that is started now stays in progress across restarts until its entry is removed from `elections.json` while the server is stopped — a restart no longer clears it
- `/d vote` and `/d dropout` now report a failure instead of miscounting or crashing when an election's participant list and the stored candidate/voter records disagree. `/d vote` checks that the candidate record exists and that the voter record was actually created before it confirms the vote, and `/d dropout` checks that the candidate record exists before removing it
- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.
- `/d start` now confirms a successfully started election in green. The confirmation was previously sent in the red every failure message uses, so a faction owner could not tell success from failure by colour
- Debug output is now prefixed with `[Democracy]` instead of `[ExamplePonderPlugin]`, and the `debugMode` config option finally has a consumer — enabling it logs the plugin being enabled and disabled
- A player who took part in an earlier election is now recorded correctly in a later one. Candidate and voter records are matched by election as well as by player, so a player who has already voted somewhere else can still run and vote in their current faction's election — and is once again limited to a single vote in it

## [0.2.0-SNAPSHOT-8-8-2026] – 2026-08-08

### Changed
- Democracy is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `0.2.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.

### Added
- `/d run`, `/d dropout`, `/d vote <candidate>`, and `/d info` are now implemented against the faction's active election, instead of replying "not implemented yet"
- `/d start` now rejects starting a second election in a faction that already has one in progress

### Fixed
- Config existence check on startup now looks at the plugin's own data folder instead of a leftover template path, so version-mismatch repair and config reload actually run on restart

## [Initial Release]

### Added
- Faction election system integrated with Medieval Factions
- `/d run`, `/d dropout`, `/d vote`, `/d start`, `/d info` commands
