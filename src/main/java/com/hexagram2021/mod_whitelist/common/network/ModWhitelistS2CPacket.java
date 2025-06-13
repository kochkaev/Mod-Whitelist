package com.hexagram2021.mod_whitelist.common.network;

import com.hexagram2021.mod_whitelist.ModWhitelist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ModWhitelistS2CPacket() implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModWhitelist.MODID, "s2c");
    public static final Type<ModWhitelistS2CPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ModWhitelistS2CPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {},
            buf -> new ModWhitelistS2CPacket()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
