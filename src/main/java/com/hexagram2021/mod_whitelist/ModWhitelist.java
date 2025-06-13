package com.hexagram2021.mod_whitelist;

import com.hexagram2021.mod_whitelist.common.network.ModWhitelistC2SPacket;
import com.hexagram2021.mod_whitelist.common.network.ModWhitelistS2CPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class ModWhitelist implements ModInitializer {
	public static final String MODID = "mod_whitelist";
	public static final String MOD_NAME = "Mod Whitelist";
	public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(MODID).orElseThrow().getMetadata().getVersion().getFriendlyString();
	
	@Override
	public void onInitialize() {
	}

	public static void registerPackets() {
		PayloadTypeRegistry.playS2C().register(ModWhitelistS2CPacket.TYPE, ModWhitelistS2CPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(ModWhitelistC2SPacket.TYPE, ModWhitelistC2SPacket.CODEC);
	}
}
