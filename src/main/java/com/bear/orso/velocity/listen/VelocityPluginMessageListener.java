package com.bear.orso.velocity.listen;

import com.bear.bjornsdk.object.Configuration;
import com.bear.orso.common.handler.UnifiedHandler;
import com.bear.orso.velocity.OrsoVelocity;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VelocityPluginMessageListener {

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event) {
        final ChannelIdentifier channelIdentifier = event.getIdentifier();
        final String channelName = channelIdentifier.getId();

        if (event.getSource() instanceof Player) {
            return;
        }

        final Configuration configuration = OrsoVelocity.INSTANCE.getCloudConfig();

        if (channelName.equals("orso")) {
            final String _json = new String(event.getData(), StandardCharsets.UTF_8);

            final BiConsumer<String, String> alertConsumer = (formatted, data) -> {
                final Component component = Component.text(formatted)
                        .hoverEvent(HoverEvent.showText(Component.text(data, NamedTextColor.GRAY)));

                for (final Player player : OrsoVelocity.INSTANCE.getServer().getAllPlayers()) {
                    if (player.hasPermission("bear.alerts")) {
                        player.sendMessage(component);
                    }
                }
            };

            final Consumer<String> banConsumer = (commandName) -> {
                final ProxyServer proxyServer = OrsoVelocity.INSTANCE.getServer();

                proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), commandName);
            };

            UnifiedHandler.handle(configuration, _json, alertConsumer, banConsumer);
        }
    }
}
