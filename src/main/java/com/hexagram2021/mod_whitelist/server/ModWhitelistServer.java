package com.hexagram2021.mod_whitelist.server;

import com.hexagram2021.mod_whitelist.ModWhitelist;
import com.hexagram2021.mod_whitelist.common.network.ModWhitelistC2SPacket;
import com.hexagram2021.mod_whitelist.common.network.ModWhitelistS2CPacket;
import com.hexagram2021.mod_whitelist.common.utils.MWLogger;
import com.hexagram2021.mod_whitelist.server.config.MWServerConfig;
import com.hexagram2021.mod_whitelist.server.config.MismatchType;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ModWhitelistServer implements DedicatedServerModInitializer {

	private static final Map<UUID, AtomicInteger> pendingPlayers = new HashMap<>();
	private static final int TIMEOUT_TICKS = 100;
	@Override
	public void onInitializeServer() {
		MWServerConfig.hello();
		ModWhitelist.registerPackets();
		registerPackets();
	}

	public static void registerPackets() {
		ServerPlayConnectionEvents.JOIN.register((ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) -> {
			ServerPlayNetworking.send(handler.player, new ModWhitelistS2CPacket());
			pendingPlayers.put(handler.player.getUUID(), new AtomicInteger(0));
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			pendingPlayers.entrySet().removeIf(entry -> {
				UUID playerId = entry.getKey();
				var requestTick = entry.getValue();
				if (requestTick.getAndAdd(1) >= TIMEOUT_TICKS) {
					var player = server.getPlayerList().getPlayer(playerId);
					if (player != null) {
						MWLogger.LOGGER.info("Player {} failed to respond to mod whitelist check, disconnecting.", player.getName().getString());
						player.connection.disconnect(Component.literal("Failed to check your mod list. Please, install \"Mod Whitelist\" mod."));
					}
					return true;
				}
				return false;
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(ModWhitelistC2SPacket.TYPE, (payload, context) -> {
			pendingPlayers.remove(context.player().getUUID());
			ServerGamePacketListenerImpl handler = context.player().connection;
			List<String> mods = payload.mods();
			List<Pair<String, MismatchType>> mismatches = MWServerConfig.test(mods);
			if (!mismatches.isEmpty()) {
				MutableComponent reason = Component.translatable("multiplayer.disconnect.mod_whitelist.modlist_mismatch");
				for (Pair<String, MismatchType> mod : mismatches) {
					switch (mod.getRight()) {
						case UNINSTALLED_BUT_SHOULD_INSTALL ->
								reason.append(Component.translatable("multiplayer.disconnect.mod_whitelist.misc.to_install", mod.getLeft()));
						case INSTALLED_BUT_SHOULD_NOT_INSTALL ->
								reason.append(Component.translatable("multiplayer.disconnect.mod_whitelist.misc.to_uninstall", mod.getLeft()));
					}
				}
				handler.disconnect(reason);
			}
		});
	}
}
