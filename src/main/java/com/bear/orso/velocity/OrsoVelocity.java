package com.bear.orso.velocity;

import com.bear.orso.velocity.listen.VelocityPluginMessageListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
@Plugin(id = "orso-velocity", name = "Orso", version = "1.0.0-SNAPSHOT",
        url = "https://bear.com", description = "Network-wide alerts for Bear Anticheat", authors = {"Bear Development Team"})
public class OrsoVelocity {

    public static OrsoVelocity INSTANCE;

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public OrsoVelocity(final ProxyServer server, final Logger logger) {
        INSTANCE = this;

        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onInitialize(final ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(new LegacyChannelIdentifier("orso"));

        server.getEventManager().register(this, new VelocityPluginMessageListener());
    }
}
