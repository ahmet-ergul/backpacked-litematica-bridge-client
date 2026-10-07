package com.ahmet.bplbridge;

import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Backpacked does not sync equipped backpack contents to the client.
 * The only time the client sees them is while the backpack screen (B) is open,
 * so we snapshot the contents every tick while it is open and remember them.
 */
public final class BackpackCache {
    private static final Map<Integer, List<ItemStack>> BY_INDEX = new HashMap<>();
    private static volatile List<ItemStack> flat = Collections.emptyList();
    private static Object session;

    private BackpackCache() {}

    public static void tick(Minecraft mc) {
        Object current = mc.getConnection();
        if (current != session) {
            session = current;
            BY_INDEX.clear();
            flat = Collections.emptyList();
        }
        if (mc.player == null || !(mc.player.containerMenu instanceof BackpackContainerMenu menu) || !menu.isOwner()) {
            return;
        }
        Container inv = menu.getBackpackInventory();
        List<ItemStack> snap = new ArrayList<>(inv.getContainerSize());
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (!s.isEmpty()) snap.add(s.copy());
        }
        List<ItemStack> old = BY_INDEX.get(menu.getBackpackIndex());
        if (old != null && sameContents(old, snap)) return;
        BY_INDEX.put(menu.getBackpackIndex(), snap);
        List<ItemStack> all = new ArrayList<>();
        for (List<ItemStack> l : BY_INDEX.values()) all.addAll(l);
        flat = Collections.unmodifiableList(all);
    }

    private static boolean sameContents(List<ItemStack> a, List<ItemStack> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (!ItemStack.matches(a.get(i), b.get(i))) return false;
        }
        return true;
    }

    /** Copies of every stack seen in the player's own backpacks this session. */
    public static List<ItemStack> getAll() {
        return flat;
    }
}
