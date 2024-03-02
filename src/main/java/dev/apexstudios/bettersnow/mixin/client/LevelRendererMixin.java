/*
 * Copyright (c) ApexStudios
 * All Rights Reserved
 */

package dev.apexstudios.bettersnow.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.apexstudios.bettersnow.BetterSnow;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// @Debug(export = true)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin
{
    @ModifyExpressionValue(
            method = "renderSnowAndRain",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/biome/Biome$Precipitation;SNOW:Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation BetterSnow$renderSnowAndRain(Biome.Precipitation precipitation)
    {
        // return expected value if disabled
        if(!BetterSnow.ENABLED.get())
            return precipitation;

        // shouldnt be required due to the @At injecting us into the 'if(precipitation == SNOW)' check
        // but just incase its anything else, return the value they are expecting
        if(precipitation != Biome.Precipitation.SNOW)
            return precipitation;

        // replace the 'if(precipitation == SNOW)' check with a 'if(precipitation == NONE)' check
        // to stop the original snow from rendering
        return Biome.Precipitation.NONE;
    }
}
