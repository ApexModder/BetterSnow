/*
 * Copyright (c) ApexStudios
 * All Rights Reserved
 */

package dev.apexstudios.bettersnow;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(BetterSnow.ID)
public final class BetterSnow
{
    public static final String ID = "bettersnow";

    public BetterSnow(IEventBus bus)
    {
    }

    public static ResourceLocation identifier(String path)
    {
        return new ResourceLocation(ID, path);
    }
}
