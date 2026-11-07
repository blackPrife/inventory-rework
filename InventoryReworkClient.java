package org.kin.invrework.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ClickType;

public class InventoryReworkClient implements ClientModInitializer {
	public static KeyMapping testeKey;
	public static boolean keyPress = false;
	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath("inventory-rework", "main")
	);
	public static final Identifier OFFHAND_SPRITE = Identifier.withDefaultNamespace("hud/hotbar_offhand_left");
	public static final int MAIN_START = 9;
	public static final int MAIN_END = 36;
	public static int selectedSlot = MAIN_START;
	boolean wasPressed = false;
	@Override
	public void onInitializeClient() {
		testeKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.inventory-rework.test",
				GLFW.GLFW_KEY_V,
				CATEGORY
		));
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.HOTBAR,
				Identifier.fromNamespaceAndPath("inventory-rework", "offhand_slot"),
				(graphics, deltaTracker) -> {
					if(keyPress) {
						Minecraft client = Minecraft.getInstance();
						if(client == null) return;
						ItemStack stack = client.player.getInventory().getItem(selectedSlot);
						int x = graphics.guiWidth() / 2 - 11;
						int y = graphics.guiHeight() - 22 - 24 - 8;
						graphics.blitSprite(RenderPipelines.GUI_TEXTURED, OFFHAND_SPRITE, x, y, 29, 24);
						if(stack.isEmpty()) return;
						int itemX = x + 3;
						int itemY = y + 4;
						graphics.renderItem(stack, itemX, itemY);
						graphics.renderItemDecorations(client.font, stack, itemX, itemY);
					}
				}
		);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(client.player == null) {
				keyPress = false;
				wasPressed = false;
				selectedSlot = MAIN_START;
				return;
			}
			Inventory inventory= client.player.getInventory();
			keyPress = testeKey.isDown();
			if(wasPressed && !keyPress){
				client.gameMode.handleInventoryMouseClick(
						client.player.inventoryMenu.containerId,
						selectedSlot,
						inventory.getSelectedSlot(),
						ClickType.SWAP,
						client.player
				);
				selectedSlot = MAIN_START;
			}
			wasPressed = keyPress;
		});
	}
}