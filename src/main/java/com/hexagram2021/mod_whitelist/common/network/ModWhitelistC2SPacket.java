package com.hexagram2021.mod_whitelist.common.network;

import com.hexagram2021.mod_whitelist.ModWhitelist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ModWhitelistC2SPacket(List<String> mods) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModWhitelist.MODID, "c2s");
    public static final Type<ModWhitelistC2SPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ModWhitelistC2SPacket> CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeCollection(packet.mods(), FriendlyByteBuf::writeUtf),
            buf -> new ModWhitelistC2SPacket(buf.readList(FriendlyByteBuf::readUtf))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
