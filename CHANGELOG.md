# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Fixed
- Config existence check on startup now looks at the plugin's own data folder instead of a leftover template path, so version-mismatch repair and config reload actually run on restart

## [Initial Release]

### Added
- Faction election system integrated with Medieval Factions
- `/d run`, `/d dropout`, `/d vote`, `/d start`, `/d info` commands
