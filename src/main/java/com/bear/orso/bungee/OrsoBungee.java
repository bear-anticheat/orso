package com.bear.orso.bungee;

import com.bear.orso.bungee.listen.BungeePluginMessageListener;
import net.md_5.bungee.api.plugin.Plugin;

public class OrsoBungee extends Plugin {

    public static OrsoBungee INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        getProxy().registerChannel("orso");
        getProxy().getPluginManager().registerListener(this, new BungeePluginMessageListener());
    }

    @Override
    public void onDisable() {
        getProxy().unregisterChannel("orso");
    }
}
