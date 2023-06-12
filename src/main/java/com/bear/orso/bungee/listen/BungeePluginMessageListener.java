package com.bear.orso.bungee.listen;

import com.bear.orso.bungee.OrsoBungee;
import com.bear.orso.common.alert.MessageParser;
import com.bear.orso.common.util.Color;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.nio.charset.StandardCharsets;

public class BungeePluginMessageListener implements Listener {

    public static final String ALERT_FORMAT =
            "&b&l⚡ &r&7/ &b%player% &7has failed &b%name% &7(&b%type%&7) &3x%vl%";

    @EventHandler
    public void onPluginMessage(final PluginMessageEvent event) {
        final String channelName = event.getTag();

        if (channelName.equals("orso")) {
            final String _json = new String(event.getData(), StandardCharsets.UTF_8);
            final JsonObject json = JsonParser.parseString(_json).getAsJsonObject();

            final String formatted = MessageParser.fromJson(json, ALERT_FORMAT);
            final String data = json.get("data").getAsString();

            final TextComponent component = new TextComponent(new ComponentBuilder(formatted)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Color.translate("&7" + data)).create()))
                    .create()
            );

            for (final ProxiedPlayer player : OrsoBungee.INSTANCE.getProxy().getPlayers()) {
                if (player.hasPermission("bear.alerts")) {
                    player.sendMessage(component);
                }
            }
        }
    }
}
