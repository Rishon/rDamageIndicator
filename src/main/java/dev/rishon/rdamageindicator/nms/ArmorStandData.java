package dev.rishon.rdamageindicator.nms;

import dev.rishon.rdamageindicator.Utils;
import dev.rishon.rdamageindicator.handler.NMSHandler;
import lombok.Data;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@Data
public class ArmorStandData {

    private final NMSHandler handler;

    private final Entity entity;
    private final double damage;
    private Entity damager;
    private EntityArmorStand armorStand;
    private int task;

    public ArmorStandData(NMSHandler handler, Entity entity, @Nullable Entity damager, double damage) {
        this.entity = entity;
        this.damager = damager;
        this.damage = damage;
        this.handler = handler;
    }

    public void createIndicator() {
        Location location = entity.getLocation();
        CraftWorld world = (CraftWorld) location.getWorld();
        armorStand = new EntityArmorStand(EntityTypes.d, world.getHandle());
        armorStand.a(
                location.getX() + Math.random() * 0.5 - 0.25,
                location.getY() + 0.3,
                location.getZ() + Math.random() * 0.5 - 0.25
        );
        ArmorStand stand = (ArmorStand) armorStand.getBukkitEntity();
        stand.setCustomName(colored(damage));
        stand.setCustomNameVisible(true);
        stand.setInvisible(true);
        stand.setArms(false);
        stand.setBasePlate(false);
        stand.setSmall(true);
        stand.setMarker(false);
    }

    public void spawnIndicator() {
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(armorStand, 78);
        DataWatcher watcher = armorStand.ai();
        watcher.b(DataWatcherRegistry.a.a(0), (byte) 32);

        if (damager == null && entity != null) damager = entity;
        CompletableFuture.runAsync(() -> {
            ((CraftPlayer) damager).getHandle().b.a(packet);
            ((CraftPlayer) damager).getHandle().b.a(new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true));
        });
        handler.getArmorStandData().put(entity, this);
    }

    public void removeIndicator() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armorStand.ae());
        if (damager == null && entity != null) damager = entity;
        CompletableFuture.runAsync(() -> ((CraftPlayer) damager).getHandle().b.a(packet));
        handler.getArmorStandData().remove(entity);
    }

    public void syncDeleteIndicator() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armorStand.ae());
        ((CraftPlayer) damager).getHandle().b.a(packet);
        handler.getArmorStandData().remove(entity);
    }

    public void updateIndicator(double damage) {
        Location location = entity.getLocation();
        ArmorStand stand = (ArmorStand) armorStand.getBukkitEntity();
        stand.setCustomName(colored(damage));
        armorStand.a(
                location.getX() + Math.random() * 0.5 - 0.25,
                location.getY() + 0.3,
                location.getZ() + Math.random() * 0.5 - 0.25
        );

        if (damager == null) damager = entity;

        DataWatcher watcher = armorStand.ai();
        watcher.b(DataWatcherRegistry.a.a(0), (byte) 32);
        CompletableFuture.runAsync(() -> {
            ((CraftPlayer) damager).getHandle().b.a(new PacketPlayOutEntityTeleport(armorStand));
            ((CraftPlayer) damager).getHandle().b.a(new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true));
        });
    }

    private String colored(double damage) {
        String str;
        if (damage > 10) {
            str = "&#f44336" + damage + " ✦";
        } else if (damage > 5) {
            str = "&#ffd966" + damage + " ✪";
        } else {
            str = "&#8fce00" + damage + " ✫";
        }
        return Utils.translate(str);
    }
}