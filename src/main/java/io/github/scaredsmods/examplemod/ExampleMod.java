package io.github.scaredsmods.examplemod;

import io.github.scaredsmods.examplemod.block.ModBlocks;
import io.github.scaredsmods.examplemod.block.entity.ModBlockEntities;
import io.github.scaredsmods.examplemod.recipe.ModRecipes;
import io.github.scaredsmods.examplemod.screenhandler.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {

	public static final String MOD_ID = "example-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModBlockEntities.registerBlockEntities();
		ModBlocks.registerModBlocks();
		ModRecipes.registerRecipes();
		ModScreenHandlers.registerScreenHandlers();


		LOGGER.info("Hello Fabric world!");
	}
}