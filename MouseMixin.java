package org.kin.invrework.client.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.kin.invrework.InventoryRework;
import org.kin.invrework.client.InventoryReworkClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.player.Inventory;

@Mixin(MouseHandler.class)
public class MouseMixin {
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true) //el cancellable es necesario
    private void onScroll(long window, double Horizontal, double vertical, CallbackInfo ci){
        Minecraft client = Minecraft.getInstance(); //crea una instancia de jugador
        if(client.player != null && !client.player.isSpectator()){ //siempre que no se este en modo espectador y el jugador exista
            if(InventoryReworkClient.keyPress && vertical != 0 && client.screen ==null){ //se presiono la tecla
                int step = vertical > 0 ? -1 : 1;
                int next = Math.floorMod(
                        InventoryReworkClient.selectedSlot - InventoryReworkClient.MAIN_START + step,
                        InventoryReworkClient.MAIN_END - InventoryReworkClient.MAIN_START
                ) + InventoryReworkClient.MAIN_START;
                InventoryReworkClient.selectedSlot = next;
                int totalSlots = InventoryReworkClient.MAIN_END - InventoryReworkClient.MAIN_START;
                int currentOff = InventoryReworkClient.selectedSlot - InventoryReworkClient.MAIN_START;
                Inventory inventory = client.player.getInventory();
                for(int i = 1; i <= totalSlots; i++){
                    int nextOff = Math.floorMod(currentOff + (i * step), totalSlots);
                    int cantidadSlot = nextOff + InventoryReworkClient.MAIN_START;
                    if(!inventory.getItem(cantidadSlot).isEmpty()){
                        InventoryReworkClient.selectedSlot = cantidadSlot;
                        break;
                    }
                }
                ci.cancel(); //este se encarga de desactivar el slot de la mano, como no se
            }
        }
    }
}
