package com.bear.orso.common.handler;

import com.bear.bjornsdk.object.Configuration;
import com.bear.orso.common.alert.MessageParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnifiedHandler {

    private static final String ALERT_FORMAT =
        "&b&l⚡ &r&7/ &b%player% &7has failed &b%name% &7(&b%type%&7) &3x%vl% {{&c&o(Experimental)}}";

    private static final Pattern EXPERIMENTAL_CHECK_PATTERN = Pattern.compile("(?<=[{]{2})(.*)(?=[}]{2})");

    private static final Map<String, String> ALERT_MAP = new HashMap<>();

    static {
        ALERT_MAP.put("null_false", computeAlertRegex(ALERT_FORMAT, false));
        ALERT_MAP.put("null_true", computeAlertRegex(ALERT_FORMAT, true));
    }

    public static void handle(final Configuration configuration, String _json,
                              final BiConsumer<String, String> alertConsumer, final Consumer<String> banConsumer) {
        _json = _json.substring(2);

        final JsonObject json = JsonParser.parseString(_json).getAsJsonObject();
        final String type = json.get("type").getAsString();

        final boolean isOnline = configuration != null;

        switch (type) {
            case "alert": {
                if (isOnline && !configuration.isProxyAlerts()) break;

                final String alertKey = (isOnline ? "bjorn" : "null") + "_" + json.get("check_developer").getAsBoolean();

                if (isOnline && !ALERT_MAP.containsKey(alertKey)) {
                    final String bjornFormat = new String(configuration.getAlertFormat().getBytes(), StandardCharsets.UTF_8);

                    ALERT_MAP.put("bjorn_true", computeAlertRegex(bjornFormat, true));
                    ALERT_MAP.put("bjorn_false", computeAlertRegex(bjornFormat, false));
                }

                final String formatted = MessageParser.fromJson(json, ALERT_MAP.get(alertKey));
                final String data = json.get("data").getAsString();

                alertConsumer.accept(formatted, data);

                break;
            }
            case "ban": {
                if (!isOnline || !configuration.isProxyBans()) break;

                final String username = json.get("username").getAsString();
                final String uuid = json.get("uuid").getAsString();

                final String commandName = configuration.getBanCommand()
                        .replace("%player%", username)
                        .replace("%uuid%", uuid);

                banConsumer.accept(commandName);

                break;
            }
        }
    }

    private static String computeAlertRegex(String alertFormat, final boolean developer) {
        final Matcher match = EXPERIMENTAL_CHECK_PATTERN.matcher(alertFormat);

        if (match.find()) {
            final String experimentalMessage = match.group();

            if (!developer) {
                alertFormat = alertFormat.replace("{{" + experimentalMessage + "}}", "");
            } else {
                alertFormat = alertFormat.replace("{{", "").replace("}}", "");
            }
        }

        return alertFormat;
    }
}
