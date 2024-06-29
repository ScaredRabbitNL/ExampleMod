package io.github.scaredsmods.examplemod;

import io.github.scaredsmods.examplemod.screenhandler.ExampleScreen;
import io.github.scaredsmods.examplemod.screenhandler.ExampleScreenHandler;
import io.github.scaredsmods.examplemod.screenhandler.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.EXAMPLE_SCREEN_HANDLER, ExampleScreen::new);
    }
}
