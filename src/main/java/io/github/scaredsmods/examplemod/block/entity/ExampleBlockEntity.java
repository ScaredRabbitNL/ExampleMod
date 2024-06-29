package io.github.scaredsmods.examplemod.block.entity;

import io.github.scaredsmods.examplemod.recipe.ExampleRecipe;
import io.github.scaredsmods.examplemod.screenhandler.ExampleScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExampleBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory{

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT_1 = 1;
    private static final int OUTPUT_SLOT_2 = 2;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;

    public ExampleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXAMPLE_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ExampleBlockEntity.this.progress;
                    case 1 -> ExampleBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ExampleBlockEntity.this.progress = value;
                    case 1 -> ExampleBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }


    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }


    @Override
    public Text getDisplayName() {
        return Text.literal("Gem Polishing Station");
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("gem_polishing_station.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("gem_polishing_station.progress");
    }


    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ExampleScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.isClient()) {
            return;
        }

        if(areOutputSlotsEmptyOreReceivable()) {
            if(this.hasRecipe()) {
                this.increaseCraftProgress();
                markDirty(world, pos, state);

                if(hasCraftingFinished()) {
                    this.craftItem();
                    this.resetProgress();
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        Optional<RecipeEntry<ExampleRecipe>> recipe = getCurrentRecipe();

        this.removeStack(INPUT_SLOT, 1);

        this.setStack(OUTPUT_SLOT_1, new ItemStack(recipe.get().value().getResult(null).getItem(),
                getStack(OUTPUT_SLOT_1).getCount() + recipe.get().value().getResult(null).getCount()));

        this.setStack(OUTPUT_SLOT_2, new ItemStack(recipe.get().value().getSecondResult(null).getItem(),
                getStack(OUTPUT_SLOT_2).getCount() + recipe.get().value().getSecondResult(null).getCount()));
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<ExampleRecipe>> recipe = getCurrentRecipe();

        return recipe.isPresent() && canInsertAmountIntoOutputSlots(recipe.get().value().getResult(null))
                && canInsertItemIntoOutputSlots(recipe.get().value().getResult(null).getItem());
    }

    private boolean canInsertItemIntoOutputSlots(Item item) {
        return (this.getStack(OUTPUT_SLOT_1).getItem() == item || this.getStack(OUTPUT_SLOT_1).isEmpty())
                &&
                (this.getStack(OUTPUT_SLOT_2).getItem() == item || this.getStack(OUTPUT_SLOT_2).isEmpty());
    }

    private boolean canInsertAmountIntoOutputSlots(ItemStack result) {
        return (this.getStack(OUTPUT_SLOT_1).getCount() + result.getCount() <= getStack(OUTPUT_SLOT_1).getMaxCount())
                &&
                (this.getStack(OUTPUT_SLOT_2).getCount() + result.getCount() <= getStack(OUTPUT_SLOT_2).getMaxCount());
    }

    private boolean areOutputSlotsEmptyOreReceivable() {
        return (this.getStack(OUTPUT_SLOT_1).isEmpty() || this.getStack(OUTPUT_SLOT_1).getCount() < this.getStack(OUTPUT_SLOT_1).getMaxCount())
                &&
                (this.getStack(OUTPUT_SLOT_2).isEmpty() || this.getStack(OUTPUT_SLOT_2).getCount() < this.getStack(OUTPUT_SLOT_2).getMaxCount());
    }

    private Optional<RecipeEntry<ExampleRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());
        for(int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }

        return getWorld().getRecipeManager().getFirstMatch(ExampleRecipe.Type.INSTANCE, inv, getWorld());
    }

}