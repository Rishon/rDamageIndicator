package dev.rishon.rdamageindicator;

import dev.rishon.rdamageindicator.handler.MainHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Main extends JavaPlugin {

    // Handlers
    public MainHandler handler;
    // Instance
    private Main plugin;

    @Override
    public void onEnable() {
        this.plugin = this;
        this.handler = new MainHandler(this);
        this.getLogger().info(this.getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        this.handler.end();
        this.getLogger().info(this.getName() + " has been disabled!");
    }
}
