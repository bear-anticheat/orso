package com.bear.orso.bungee;

import com.bear.bjornsdk.BjornSDK;
import com.bear.bjornsdk.object.Configuration;
import com.bear.bjornsdk.response.impl.ConfigResponse;
import com.bear.orso.bungee.listen.BungeePluginMessageListener;
import com.bear.orso.common.config.OrsoConfig;
import com.moandjiezana.toml.Toml;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
public class OrsoBungee extends Plugin {

    public static OrsoBungee INSTANCE;

    private Configuration cloudConfig;
    private Toml config;

    @Override
    public void onEnable() {
        INSTANCE = this;

        config = OrsoConfig.load(getClass(), getDataFolder().toPath(), "config");

        initBjorn();

        getProxy().registerChannel("orso");
        getProxy().getPluginManager().registerListener(this, new BungeePluginMessageListener());
    }

    @Override
    public void onDisable() {
        getProxy().unregisterChannel("orso");
    }

    private void initBjorn() {
        final String licenseKey = config.getString("auth.license_key");
        final String hostname = config.getString("bjorn.hostname");

        try {
            // we don't need an api key to fetch configs when we have a license key
            final BjornSDK bjornSDK = new BjornSDK(hostname, "");

            final ConfigResponse configResponse = bjornSDK.fetchConfig(licenseKey);

            if (!configResponse.isSuccess()) {
                getLogger().severe("Couldn't fetch cloud config!");
                return;
            }

            cloudConfig = configResponse.getConfiguration();
        } catch (Exception e) {
            getLogger().severe("An error occurred while connecting to Bjorn.");
            e.printStackTrace();
        }
    }
}
