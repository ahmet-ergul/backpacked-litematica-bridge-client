package com.ahmet.bplbridge.mixin;

import com.ahmet.bplbridge.BackpackCache;
import fi.dy.masa.litematica.materials.MaterialListUtils;
import fi.dy.masa.malilib.util.data.ItemType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = MaterialListUtils.class, remap = false)
public abstract class MaterialListUtilsMixin {
    @Inject(method = "getInventoryItemCounts", at = @At("RETURN"))
    private static void bplbridge$addBackpackItems(Container inv, CallbackInfoReturnable<Object2IntOpenHashMap<ItemType>> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || inv != mc.player.getInventory()) return;
        List<ItemStack> stacks = BackpackCache.getAll();
        if (stacks.isEmpty()) return;
        // Re-use Litematica's own counting so shulkers/bundles inside the backpack are handled too.
        // A SimpleContainer is not the player inventory, so this does not recurse into us.
        Object2IntOpenHashMap<ItemType> extra =
                MaterialListUtils.getInventoryItemCounts(new SimpleContainer(stacks.toArray(new ItemStack[0])));
        Object2IntOpenHashMap<ItemType> result = cir.getReturnValue();
        extra.object2IntEntrySet().forEach(e -> result.addTo(e.getKey(), e.getIntValue()));
    }
}
