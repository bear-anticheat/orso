package com.bear.orso.velocity;

import com.bear.bjornsdk.BjornSDK;
import com.bear.bjornsdk.object.Configuration;
import com.bear.bjornsdk.response.impl.ConfigResponse;
import com.bear.orso.velocity.config.VelocityConfigUtil;
import com.bear.orso.velocity.listen.VelocityPluginMessageListener;
import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.nio.file.Path;

@Getter
@Plugin(id = "orso-velocity", name = "Orso", version = "1.0.0-SNAPSHOT",
        url = "https://bear.com", description = "Network-wide alerts for Bear Anticheat", authors = {"Bear Development Team"})
public class OrsoVelocity {

    public static OrsoVelocity INSTANCE;

    private final Path dataDirectory;

    private final ProxyServer server;
    private final Logger logger;

    private Configuration cloudConfig;
    private Toml config;

    @Inject
    public OrsoVelocity(final ProxyServer server, final Logger logger, @DataDirectory final Path dataDirectory) {
        INSTANCE = this;

        this.dataDirectory = dataDirectory;
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onInitialize(final ProxyInitializeEvent event) {
        config = VelocityConfigUtil.load(getClass(), dataDirectory, "config");

        initBjorn();

        server.getChannelRegistrar().register(new LegacyChannelIdentifier("orso"));
        server.getEventManager().register(this, new VelocityPluginMessageListener());
    }

    private void initBjorn() {
        final String licenseKey = config.getString("auth.license_key");
        final String hostname = config.getString("bjorn.hostname");

        try {
            // we don't need an api key to fetch configs when we have a license key
            final BjornSDK bjornSDK = new BjornSDK(hostname, "");

            final ConfigResponse configResponse = bjornSDK.fetchConfig(licenseKey);

            if (!configResponse.isSuccess()) {
                logger.error("Couldn't fetch cloud config!");
                return;
            }

            cloudConfig = configResponse.getConfiguration();
        } catch (Exception e) {
            logger.error("An error occurred while connecting to Bjorn.");
            e.printStackTrace();
        }
    }
}
