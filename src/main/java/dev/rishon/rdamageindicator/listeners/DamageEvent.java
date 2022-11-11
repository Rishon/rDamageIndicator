package dev.rishon.rdamageindicator.listeners;

import dev.rishon.rdamageindicator.handler.MainHandler;
import dev.rishon.rdamageindicator.nms.ArmorStandData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEvent implements Listener {

    private final MainHandler handler;

    public DamageEvent(MainHandler handler) {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityByEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity attacker = event.getDamager();
        double damage = Math.round(event.getFinalDamage() * 100.0) / 100.0;

        if (attacker.getType() != EntityType.PLAYER) return;

        ArmorStandData data = handler.getNmsHandler().getArmorStandData().get(entity);

        if (data == null) {
            data = new ArmorStandData(handler.getNmsHandler(), entity, attacker, damage);
            data.createIndicator();
            data.spawnIndicator();
            data.setTask(entity.getServer().getScheduler().runTaskLaterAsynchronously(this.handler.getPlugin(), data::removeIndicator, 20L).getTaskId());
        } else {
            entity.getServer().getScheduler().cancelTask(data.getTask());
            data.updateIndicator(damage);
            data.setTask(entity.getServer().getScheduler().runTaskLaterAsynchronously(this.handler.getPlugin(), data::removeIndicator, 20L).getTaskId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || entity.getType() != EntityType.PLAYER)
            return;
        double damage = Math.round(event.getFinalDamage() * 100.0) / 100.0;

        ArmorStandData data = handler.getNmsHandler().getArmorStandData().get(entity);

        if (data == null) {
            data = new ArmorStandData(this.handler.getNmsHandler(), entity, null, damage);
            data.createIndicator();
            data.spawnIndicator();
            data.setTask(entity.getServer().getScheduler().runTaskLaterAsynchronously(this.handler.getPlugin(), data::removeIndicator, 20L).getTaskId());
        } else {
            entity.getServer().getScheduler().cancelTask(data.getTask());
            data.updateIndicator(damage);
            data.setTask(entity.getServer().getScheduler().runTaskLaterAsynchronously(this.handler.getPlugin(), data::removeIndicator, 20L).getTaskId());
        }
    }

}
