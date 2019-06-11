package me.paulf.wings.server.net.clientbound;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import me.paulf.wings.server.item.ImmutableWingSettings;
import me.paulf.wings.server.item.ItemWings;
import me.paulf.wings.server.item.WingSettings;
import me.paulf.wings.server.net.Message;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Map;

public final class MessageSetWingSettings extends Message {
	private ImmutableMap<ResourceLocation, WingSettings> settings;

	public MessageSetWingSettings() {}

	public MessageSetWingSettings(final ImmutableMap<ResourceLocation, WingSettings> settings) {
		this.settings = settings;
	}

	@Override
	protected void serialize(final PacketBuffer buf) {
		final ImmutableSet<Map.Entry<ResourceLocation, WingSettings>> entries = this.settings.entrySet();
		buf.writeInt(entries.size());
		for (final Map.Entry<ResourceLocation, WingSettings> entry : entries) {
			buf.writeResourceLocation(entry.getKey());
			final WingSettings value = entry.getValue();
			buf.writeInt(value.getRequiredFlightSatiation());
			buf.writeFloat(value.getFlyingExertion());
			buf.writeInt(value.getRequiredLandSatiation());
			buf.writeFloat(value.getLandingExertion());
			buf.writeShort(value.getItemDurability());
		}
	}

	@Override
	protected void deserialize(final PacketBuffer buf) {
		final ImmutableMap.Builder<ResourceLocation, WingSettings> builder = ImmutableMap.builder();
		for (int remaining = buf.readInt(); remaining --> 0; ) {
			builder.put(
				buf.readResourceLocation(),
				ImmutableWingSettings.of(
					buf.readInt(),
					buf.readFloat(),
					buf.readInt(),
					buf.readFloat(),
					buf.readShort()
				)
			);
		}
		this.settings = builder.build();
	}

	@Override
	protected void process(final MessageContext ctx) {
		for (final Map.Entry<ResourceLocation, WingSettings> entry : this.settings.entrySet()) {
			final Item item = ForgeRegistries.ITEMS.getValue(entry.getKey());
			if (item instanceof ItemWings) {
				((ItemWings) item).setSettings(entry.getValue());
			}
		}
	}
}
