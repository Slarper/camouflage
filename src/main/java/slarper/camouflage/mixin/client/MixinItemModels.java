package slarper.camouflage.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slarper.camouflage.Camouflage;

@Environment(EnvType.CLIENT)
@Mixin(ItemModels.class)
public abstract class MixinItemModels {
    @Shadow @Nullable public abstract BakedModel getModel(Item item);

    @Shadow public abstract BakedModelManager getModelManager();

    @Inject(
            method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getModelByNbt(ItemStack stack, CallbackInfoReturnable<BakedModel> cir){
        NbtCompound nbt = stack.getNbt();
        if (stack.hasNbt() && nbt!=null && nbt.contains(Camouflage.MODEL_KEY)){
            String model = nbt.getString(Camouflage.MODEL_KEY);
            if (!model.equals("")){
                BakedModel bakedModel = null;
                // if the model is an item model
                // its form is "<namespace>:<item_id>"
                // if the model is an extra model
                // its form is "<namespace>:item/<item_id>"
                try {
                    Identifier id = new Identifier(model);
                    if (Registry.ITEM.containsId(id)){
                        bakedModel = this.getModel(Registry.ITEM.get(id));
                    }else {
                        bakedModel = BakedModelManagerHelper.getModel(this.getModelManager(), id);
                    }
                } catch (InvalidIdentifierException e){
                    nbt.remove(Camouflage.MODEL_KEY);
                    Camouflage.LOGGER.error("Invalid model id.",e);
                }
                cir.setReturnValue(bakedModel == null? this.getModelManager().getMissingModel() : bakedModel);
            }else {
                nbt.remove(Camouflage.MODEL_KEY);
                Camouflage.LOGGER.info("Improper model specification");
            }
        }
    }
}