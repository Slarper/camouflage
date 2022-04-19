package slarper.camouflage.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import slarper.camouflage.Camouflage;

public class NickItem extends Item {
    public NickItem(Settings settings) {
        super(settings);
    }
    public static void putNickName(String id, ItemStack stack){
        if (!(id == null) && !(id.equals(""))){
            stack.getOrCreateNbt().putString(Camouflage.NAME_KEY,id);
        }
    }

    public static String getNickName(ItemStack stack){
        if (!stack.hasNbt()){
            return "";
        }

        NbtCompound nbt = stack.getNbt();
        assert nbt != null;
        return nbt.getString(Camouflage.NAME_KEY);
    }


    @Override
    public String getTranslationKey(ItemStack stack) {
        return getNickName(stack).equals("") ? super.getTranslationKey(stack) : getNickName(stack);
    }

}
