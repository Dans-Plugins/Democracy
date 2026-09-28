# Democracy
This expansion for Medieval Factions is intended to allow nations to hold democratic elections.

## Requirements
Medieval Factions **5.x or 6.x** (tested with 6.1.0). Democracy uses the Medieval Factions installed on the server; it does not bundle its own copy.

## Supported Minecraft Versions
This plugin is supported on the Minecraft versions listed in [`minecraft-versions.json`](minecraft-versions.json): currently **1.19.4**, **1.21.11** and **26.2** (Spigot and its forks). Every stable release is booted on a real server of each of these versions before it is published, and every build checks that the plugin only uses Bukkit API that exists on all of them. Other versions from 1.19.4 onwards are expected to work but are not tested. To support another version, add it to the file: both checks pick it up.

## Usage reporting

Usage reporting is on by default: each time the plugin is enabled, and each time one of its commands is run, it sends its name, its version and the command's name to the author's trace server at https://trace.danielstephenson.dev, so it is known which plugins are actually in use. Nothing about players, worlds, IP addresses or the server is sent, and nothing typed after a command is.

To turn it off:

- for this plugin: set `usage-reporting.enabled: false` in `plugins/Democracy/config.yml`
- for every plugin on the server that reports to trace: set `enabled: false` in `plugins/trace/config.yml` (written on first start)
- for the whole server process: set the environment variable `TRACE_USAGE_REPORTING=off` (or `DO_NOT_TRACK=1`)

The plugin says on every start whether reporting is on. Details: https://github.com/Stephenson-Software/trace#usage-reporting

## Authors and acknowledgement
### Developers
Name | Main Contributions
------------ | -------------
Daniel Stephenson | Creator
