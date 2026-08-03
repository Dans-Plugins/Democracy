# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- `/d run`, `/d dropout`, `/d vote <candidate>`, and `/d info` are now implemented against the faction's active election, instead of replying "not implemented yet"
- `/d start` now rejects starting a second election in a faction that already has one in progress

### Fixed
- Config existence check on startup now looks at the plugin's own data folder instead of a leftover template path, so version-mismatch repair and config reload actually run on restart

## [Initial Release]

### Added
- Faction election system integrated with Medieval Factions
- `/d run`, `/d dropout`, `/d vote`, `/d start`, `/d info` commands
