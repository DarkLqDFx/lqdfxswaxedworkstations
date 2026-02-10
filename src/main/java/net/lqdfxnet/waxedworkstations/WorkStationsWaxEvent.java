package net.lqdfxnet.waxedworkstations;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;
import java.util.Set;

@EventBusSubscriber(modid = "lqdfxswaxedworkstations")
public class WorkStationsWaxEvent {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();

        if (level.isClientSide) return;
        if (!player.isShiftKeyDown()) return;

        ItemStack held = event.getItemStack();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        ServerLevel serverLevel = (ServerLevel) level;

        // Only workstation blocks have a POI type
        if (PoiTypes.forState(state).isEmpty()) return;

        PoiManager poiManager = serverLevel.getPoiManager();
        Holder<PoiType> poiType = PoiTypes.forState(state).get();
        ResourceKey<PoiType> poiKey = poiType.getKey();
        boolean poiExists = poiManager.existsAtPosition(Objects.requireNonNull(poiType.getKey()), pos);

        // blocked POIs that don't make sense?!
        final Set<ResourceKey<PoiType>> poiBlocked = Set.of(
                PoiTypes.NETHER_PORTAL,
                PoiTypes.LIGHTNING_ROD,
                PoiTypes.LODESTONE,
                PoiTypes.HOME
        );

        //  blocked so do nothing!
        if (poiBlocked.contains(poiKey)) return;

        // remove POI by waxing
        if (held.getItem() instanceof HoneycombItem && poiExists) {
            // remove POI at this position
            poiManager.remove(pos);
            serverLevel.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
            serverLevel.sendParticles(ParticleTypes.WAX_ON, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 8, 0.2, 0.2, 0.2, 0.0);
            player.displayClientMessage(Component.translatable("lqdfxswaxedworkstations.display.wax_on").withStyle(ChatFormatting.GOLD), true);
            if (!player.getAbilities().instabuild) held.shrink(1);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        // strip wax and add POI back to world
        if (held.getItem() instanceof AxeItem) {
            if (PoiTypes.forState(state).isPresent() && !poiExists) {
                poiManager.add(pos, poiType);
                serverLevel.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
                serverLevel.sendParticles(ParticleTypes.WAX_OFF, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 8, 0.2, 0.2, 0.2, 0.0);
                player.displayClientMessage(Component.translatable("lqdfxswaxedworkstations.display.wax_off").withStyle(ChatFormatting.GREEN), true);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
