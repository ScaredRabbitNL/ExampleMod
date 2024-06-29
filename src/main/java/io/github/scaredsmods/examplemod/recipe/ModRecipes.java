package io.github.scaredsmods.examplemod.recipe;


import io.github.scaredsmods.examplemod.ExampleMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModRecipes {

    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ExampleMod.MOD_ID, ExampleRecipe.Serializer.ID),
                ExampleRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(ExampleMod.MOD_ID, ExampleRecipe.Type.ID),
                ExampleRecipe.Type.INSTANCE);
    }
}
