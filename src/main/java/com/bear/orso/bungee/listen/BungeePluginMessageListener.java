package com.bear.orso.bungee.listen;

import com.bear.bjornsdk.object.Configuration;
import com.bear.orso.bungee.OrsoBungee;
import com.bear.orso.common.handler.UnifiedHandler;
import com.bear.orso.common.util.Color;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BungeePluginMessageListener implements Listener {

    @EventHandler
    public void onPluginMessage(final PluginMessageEvent event) {
        final String channelName = event.getTag();

        if (!(event.getSender() instanceof Connection)) {
            return;
        }

        final Configuration configuration = OrsoBungee.INSTANCE.getCloudConfig();

        if (channelName.equals("orso")) {
            final String _json = new String(event.getData(), StandardCharsets.UTF_8);

            final BiConsumer<String, String> alertConsumer = (formatted, data) -> {
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
            };

            final Consumer<String> banConsumer = (commandName) -> {
                final ProxyServer proxyServer = OrsoBungee.INSTANCE.getProxy();

                proxyServer.getPluginManager().dispatchCommand(proxyServer.getConsole(), commandName);
            };

            UnifiedHandler.handle(configuration, _json, alertConsumer, banConsumer);
        }
    }
}
