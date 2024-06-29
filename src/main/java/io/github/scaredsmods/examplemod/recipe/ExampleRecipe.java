package io.github.scaredsmods.examplemod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.scaredsmods.moreoutputapi.api.Output2Recipe;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;

public class ExampleRecipe implements Recipe<SimpleInventory>, Output2Recipe {


    private final ItemStack output_1;
    private final ItemStack output_2;

    private final List<Ingredient> recipeItems;

    public ExampleRecipe(List<Ingredient> ingredients, ItemStack itemStack, ItemStack output2) {
        this.output_1 = itemStack;
        this.recipeItems = ingredients;
        this.output_2 = output2;
    }

    @Override
    public ItemStack getSecondResult(DynamicRegistryManager registryManager) {
        return output_2;
    }

    @Override
    public ItemStack craftSecond(DynamicRegistryManager registryManager) {
        return output_2;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }

        return recipeItems.get(0).test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output_1;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return output_1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ExampleRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "example_recipe";
    }

    public static class Serializer implements RecipeSerializer<ExampleRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "example_recipe";

        public static final Codec<ExampleRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
                validateAmount(Ingredient.DISALLOW_EMPTY_CODEC, 9).fieldOf("ingredients").forGetter(ExampleRecipe::getIngredients),
                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output_1").forGetter(r -> r.output_1),
                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output_2").forGetter(r -> r.output_2)
        ).apply(in, ExampleRecipe::new));

        private static Codec<List<Ingredient>> validateAmount(Codec<Ingredient> delegate, int max) {
            return Codecs.validate(Codecs.validate(
                    delegate.listOf(), list -> list.size() > max ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)
            ), list -> list.isEmpty() ? DataResult.error(() -> "Recipe has no ingredients!") : DataResult.success(list));
        }

        @Override
        public Codec<ExampleRecipe> codec() {
            return CODEC;
        }

        @Override
        public ExampleRecipe read(PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for(int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output_1 = buf.readItemStack();
            ItemStack output_2 = buf.readItemStack();
            return new ExampleRecipe(inputs, output_1, output_2);
        }

        @Override
        public void write(PacketByteBuf buf, ExampleRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.getResult(null));
        }
    }
}