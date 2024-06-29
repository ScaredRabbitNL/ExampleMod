package io.github.scaredsmods.examplemod.block;

import io.github.scaredsmods.examplemod.ExampleMod;
import io.github.scaredsmods.examplemod.block.custom.ExampleBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block EXAMPLE_BLOCK = registerBlock("example_block",
            new ExampleBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(ExampleMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(ExampleMod.MOD_ID, name), block);
    }

    public static void registerModBlocks() {

    }
}
