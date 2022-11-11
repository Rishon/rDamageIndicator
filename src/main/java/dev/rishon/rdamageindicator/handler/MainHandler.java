package dev.rishon.rdamageindicator.handler;

import dev.rishon.rdamageindicator.Main;
import dev.rishon.rdamageindicator.listeners.DamageEvent;
import lombok.Data;
import org.bukkit.plugin.PluginManager;

@Data
public class MainHandler implements Handler {

    private final Main plugin;

    // NMS Handler
    private NMSHandler nmsHandler;

    public MainHandler(Main plugin) {
        this.plugin = plugin;
        init();
    }

    @Override
    public void init() {
        this.nmsHandler = new NMSHandler(this);

        registerListeners();
        this.plugin.getLogger().info("MainHandler has been initialized!");
    }

    @Override
    public void end() {
        this.nmsHandler.end();
        this.plugin.getLogger().info("MainHandler has been ended!");
    }

    private void registerListeners() {
        PluginManager manager = this.getPlugin().getServer().getPluginManager();
        manager.registerEvents(new DamageEvent(this), this.getPlugin());
    }

}
