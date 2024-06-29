package io.github.scaredsmods.examplemod.screenhandler;

import io.github.scaredsmods.examplemod.ExampleMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static final ScreenHandlerType<ExampleScreenHandler> EXAMPLE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(ExampleMod.MOD_ID, "gem_polishing"),
                    new ExtendedScreenHandlerType<>(ExampleScreenHandler::new));

    public static void registerScreenHandlers() {
        ExampleMod.LOGGER.info("Registering Screen Handlers for " + ExampleMod.MOD_ID);
    }
}
