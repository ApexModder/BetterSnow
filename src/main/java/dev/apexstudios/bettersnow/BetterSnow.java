/*
 * Copyright (c) ApexStudios
 * All Rights Reserved
 */

package dev.apexstudios.bettersnow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BetterSnow.ID)
public final class BetterSnow
{
    public static final String ID = "bettersnow";
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER.comment("Whether or not the \"Better\" Snow FX is enabled").define("enabled", true);

    public BetterSnow(IEventBus bus)
    {
        // do nothing server side
        if(!FMLEnvironment.dist.isClient())
            return;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BUILDER.build());

        NeoForge.EVENT_BUS.addListener(RenderLevelStageEvent.class, event -> {
            // quit out early if disabled
            if(!ENABLED.get())
                return;

            var level = Minecraft.getInstance().level;

            if(level == null)
                return;
            // add particles after weather
            if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER)
                return;

            var partialTick = event.getPartialTick();
            var pos = event.getCamera().getBlockPosition();

            // copied from ClientLevel#animateTick (same entry point for Biome AmbientFX)
            var x = pos.getX();
            var y = pos.getY();
            var z = pos.getZ();

            var mutablePos = new BlockPos.MutableBlockPos();

            for(var j = 0; j < 667; j++)
            {
                addParticle(level, x, y, z, mutablePos, 16, partialTick);
                addParticle(level, x, y, z, mutablePos, 32, partialTick);
            }
        });
    }

    // Copied from ClientLevel#doAnimateTick
    private void addParticle(ClientLevel level, int x, int y, int z, BlockPos.MutableBlockPos pos, int range, float partialTick)
    {
        pos.set(
                x + level.random.nextInt(range) - level.random.nextInt(range),
                y + level.random.nextInt(range) - level.random.nextInt(range),
                z + level.random.nextInt(range) - level.random.nextInt(range)
        );

        // only if raining and biome should be snowfall not rainfall
        if(level.getRainLevel(partialTick) <= 0F)
            return;
        if(level.getBiome(pos).value().getPrecipitationAt(pos) != Biome.Precipitation.SNOW)
            return;
        // only if pos can see sky, (not inside of caves, houses, under trees etc)
        if(!level.canSeeSky(pos))
            return;

        var blockState = level.getBlockState(pos);

        // not inside full blocks
        if(blockState.isCollisionShapeFullBlock(level, pos))
            return;
        // same check nether biome uses to determine if ambient FX can play
        if(level.random.nextFloat() > .00625F)
            return;

        // add particle
        level.addParticle(
                ParticleTypes.WHITE_ASH,
                pos.getX() + level.random.nextDouble(),
                pos.getY() + level.random.nextDouble(),
                pos.getZ() + level.random.nextDouble(),
                0D, 0D, 0D
        );
    }
}
