package com.bear.orso.common.handler;

import com.bear.bjornsdk.object.Configuration;
import com.bear.orso.common.alert.MessageParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@UtilityClass
public class UnifiedHandler {

    private static final String ALERT_FORMAT =
            "&b&l⚡ &r&7/ &b%player% &7has failed &b%name% &7(&b%type%&7) &3x%vl%";

    public void handle(final Configuration configuration, final String _json,
                       final BiConsumer<String, String> alertConsumer, final Consumer<String> banConsumer) {
        final JsonObject json = JsonParser.parseString(_json).getAsJsonObject();
        final String type = json.get("type").getAsString();

        final boolean isOnline = configuration != null;

        switch (type) {
            case "alert": {
                if (isOnline && !configuration.isProxyAlerts()) break;

                final String formatted = MessageParser.fromJson(json, isOnline ? configuration.getAlertFormat() : ALERT_FORMAT);
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
}
