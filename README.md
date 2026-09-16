# Democracy
This expansion for Medieval Factions is intended to allow nations to hold democratic elections.

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
