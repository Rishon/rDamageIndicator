package dev.rishon.rdamageindicator.handler;

import dev.rishon.rdamageindicator.nms.ArmorStandData;
import lombok.Getter;
import org.bukkit.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param handler MainHandler
 */
@Getter
public record NMSHandler(MainHandler handler) implements Handler {

    // Map of all spawned armor stands
    private static Map<Entity, ArmorStandData> armorStandData;

    public NMSHandler(MainHandler handler) {
        this.handler = handler;
        init();
    }

    @Override
    public void init() {
        armorStandData = new ConcurrentHashMap<>();
        this.handler.getPlugin().getLogger().info("NMSHandler has been initialized!");
    }

    @Override
    public void end() {
        for (Entity entity : this.getArmorStandData().keySet()) {
            ArmorStandData data = this.getArmorStandData().get(entity);
            data.syncDeleteIndicator();
        }
        this.handler.getPlugin().getLogger().info("NMSHandler has been ended!");
    }

    public Map<Entity, ArmorStandData> getArmorStandData() {
        return armorStandData;
    }
}
