package com.bear.orso.velocity.listen;

import com.bear.bjornsdk.object.Configuration;
import com.bear.orso.common.alert.MessageParser;
import com.bear.orso.velocity.OrsoVelocity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.charset.StandardCharsets;

public class VelocityPluginMessageListener {

    public static final String ALERT_FORMAT =
            "&b&l⚡ &r&7/ &b%player% &7has failed &b%name% &7(&b%type%&7) &3x%vl%";

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event) {
        final ChannelIdentifier channelIdentifier = event.getIdentifier();
        final String channelName = channelIdentifier.getId();

        if (event.getSource() instanceof Player) {
            return;
        }

        final Configuration configuration = OrsoVelocity.INSTANCE.getCloudConfig();

        final boolean isOnline = configuration != null;

        if (channelName.equals("orso")) {
            final String _json = new String(event.getData(), StandardCharsets.UTF_8);
            final JsonObject json = JsonParser.parseString(_json).getAsJsonObject();

            final String formatted = MessageParser.fromJson(json, isOnline ? configuration.getAlertFormat() : ALERT_FORMAT);
            final String data = json.get("data").getAsString();

            final Component component = Component.text(formatted)
                    .hoverEvent(HoverEvent.showText(Component.text(data, NamedTextColor.GRAY)));

            for (final Player player : OrsoVelocity.INSTANCE.getServer().getAllPlayers()) {
                if (player.hasPermission("bear.alerts")) {
                    player.sendMessage(component);
                }
            }
        }
    }
}
