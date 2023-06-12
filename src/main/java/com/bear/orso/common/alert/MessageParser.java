package com.bear.orso.common.alert;

import com.bear.orso.common.util.Color;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageParser {

    /**
     * Translates an alert json into an alert message, given a format.
     *
     * @param json alert json
     * @return alert message
     */
    public String fromJson(final JsonObject json, final String format) {
        final String username = json.get("username").getAsString();

        final String checkName = json.get("check_name").getAsString();
        final String checkType = json.get("check_type").getAsString();

        final int vl = json.get("vl").getAsInt();

        String formatted = format
                .replace("%player%", username)
                .replace("%name%", checkName)
                .replace("%type%", checkType)
                .replace("%vl%", vl + "");

        formatted = Color.translate(formatted);

        return formatted;
    }
}
