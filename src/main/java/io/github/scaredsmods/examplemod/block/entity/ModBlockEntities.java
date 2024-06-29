package io.github.scaredsmods.examplemod.block.entity;

import io.github.scaredsmods.examplemod.ExampleMod;
import io.github.scaredsmods.examplemod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<ExampleBlockEntity> EXAMPLE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ExampleMod.MOD_ID, "example_be"),
                    BlockEntityType.Builder.create(ExampleBlockEntity::new,
                            ModBlocks.EXAMPLE_BLOCK).build());




    public static void registerBlockEntities() {
        ExampleMod.LOGGER.info("Registering Block Entities for " + ExampleMod.MOD_ID);
    }
}