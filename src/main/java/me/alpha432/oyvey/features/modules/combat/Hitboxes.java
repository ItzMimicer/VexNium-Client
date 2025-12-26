package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;

public class Hitboxes extends Module {

    // Store original hitboxes so we can restore them
    private final Map<Integer, AABB> originalBoxes = new HashMap<>();

    // ===== SETTINGS (replace with your setting system if you have one) =====
    public double size = 0.4D;      // How much to expand (blocks)
    public boolean playersOnly = true;
    public boolean mobsOnly = false;
    // =====================================================================

    public Hitboxes() {
        super("Hitboxes", "Expands entity hitboxes (anti-cheat test)", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.level == null || mc.player == null) return;

        for (Entity entity : mc.level.entitiesForRendering()) {

            if (!(entity instanceof LivingEntity living)) continue;
            if (entity == mc.player) continue;
            if (entity instanceof EndCrystal) continue;
            if (!entity.isAlive()) continue;

            // === Filters ===
            if (playersOnly && !(entity instanceof Player)) continue;
            if (mobsOnly && !(entity instanceof Mob)) continue;

            int id = entity.getId();

            // Save original bounding box once
            originalBoxes.putIfAbsent(id, entity.getBoundingBox());

            AABB box = originalBoxes.get(id);

            // Expand hitbox
            AABB expanded = new AABB(
                    box.minX - size,
                    box.minY,
                    box.minZ - size,
                    box.maxX + size,
                    box.maxY + size,
                    box.maxZ + size
            );

            entity.setBoundingBox(expanded);
        }
    }

    @Override
    public void onDisable() {
        if (mc.level == null) return;

        // Restore original hitboxes
        for (Entity entity : mc.level.entitiesForRendering()) {
            AABB original = originalBoxes.get(entity.getId());
            if (original != null) {
                entity.setBoundingBox(original);
            }
        }

        originalBoxes.clear();
    }

    @Override
    public String getDisplayInfo() {
        return "Size: " + size;
    }
}

