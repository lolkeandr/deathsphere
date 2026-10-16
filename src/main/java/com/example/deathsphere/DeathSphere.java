package com.example.deathsphere;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(DeathSphere.MODID)
public class DeathSphere {
    public static final String MODID = "deathsphere";

    public DeathSphere(IEventBus modBus) {
        ModRegistry.BLOCKS.register(modBus);
        ModRegistry.ITEMS.register(modBus);
        ModRegistry.BLOCK_ENTITIES.register(modBus);
    }
}
