package dansplugins.democracy.services;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent
    - setConfigOption()
    - sendConfigList()
 */

import dansplugins.democracy.Democracy;
import dansplugins.democracy.trace.UsageReportingSetting;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

/**
 * @author Daniel McCoy Stephenson
 */
public class ConfigService {
    private static final String USAGE_REPORTING_ENABLED_KEY = "usage-reporting.enabled";
    private static final String USAGE_REPORTING_ENDPOINT_KEY = "usage-reporting.endpoint";
    private static final String USAGE_REPORTING_KEY_KEY = "usage-reporting.key";
    private static final String DEFAULT_USAGE_REPORTING_ENDPOINT = "https://trace.danielstephenson.dev";

    private final Democracy democracy;

    private boolean altered = false;

    public ConfigService(Democracy democracy) {
        this.democracy = democracy;
    }

    public void saveMissingConfigDefaultsIfNotPresent() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", democracy.getVersion());
        } else {
            getConfig().set("version", democracy.getVersion());
        }

        // save config options
        if (!isSet("debugMode")) { getConfig().set("debugMode", false); }

        getConfig().options().copyDefaults(true);
        democracy.saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {
        if (getConfig().isSet(option)) {
            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("debugMode")) {
                getConfig().set(option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else {
                getConfig().set(option, value);
                sender.sendMessage(ChatColor.GREEN + "String set.");
            }

            // save
            democracy.saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + getConfig().getString("version")
                + ", debugMode: " + getString("debugMode"));
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return democracy.getConfig();
    }

    public boolean isSet(String option) {
        return getConfig().isSet(option);
    }

    public int getInt(String option) {
        return getConfig().getInt(option);
    }

    public int getIntOrDefault(String option, int defaultValue) {
        int toReturn = getInt(option);
        if (toReturn == 0) {
            return defaultValue;
        }
        return toReturn;
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(option);
    }

    public double getDoubleOrDefault(String option, double defaultValue) {
        double toReturn = getDouble(option);
        if (toReturn == 0) {
            return defaultValue;
        }
        return toReturn;
    }

    public String getString(String option) {
        return getConfig().getString(option);
    }

    public String getStringOrDefault(String option, String defaultValue) {
        String toReturn = getString(option);
        if (toReturn == null) {
            return defaultValue;
        }
        return toReturn;
    }

    // The one-argument getters, deliberately. Bukkit registers the jar's config.yml
    // as the defaults for the file on disk, and the one-argument getters fall
    // through to them -- but the two-argument getters return their explicit
    // fallback instead, which for the key would be "" and would turn reporting
    // off wherever the block is missing from disk. Democracy writes the block out
    // on enable when it is missing (see onEnable), so this only matters for a
    // hand-trimmed config.yml or a plugins directory that could not be written
    // to. Verified against YamlConfiguration, not assumed.

    /**
     * Fails closed: only true, on or yes turns reporting on, so a quoted "false" or a typo
     * turns it off (getBoolean() would return the bundled true for them), and an opt-out in
     * config.yml on disk wins over the copy loaded at startup.
     */
    public boolean isUsageReportingEnabled() {
        return UsageReportingSetting.isEnabled(getConfig(), getConfigFile());
    }

    /** The plugin's config.yml, which may not exist yet. */
    public File getConfigFile() {
        return new File(democracy.getDataFolder(), "config.yml");
    }

    public String getUsageReportingEndpoint() {
        return getStringOrDefault(USAGE_REPORTING_ENDPOINT_KEY, DEFAULT_USAGE_REPORTING_ENDPOINT);
    }

    /** Empty when no key is configured or bundled, which the client treats as "off". */
    public String getUsageReportingKey() {
        return getStringOrDefault(USAGE_REPORTING_KEY_KEY, "");
    }
}