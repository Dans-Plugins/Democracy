# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added

- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get democracy --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

### Fixed
- A player who took part in an earlier election is now recorded correctly in a later one. Candidate and voter records are matched by election as well as by player, so a player who has already voted somewhere else can still run and vote in their current faction's election — and is once again limited to a single vote in it

### Removed
- The placeholder message sent to every player on join ("This message was sent by ExamplePonderPlugin.") has been removed, along with the empty listener that sent it

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
