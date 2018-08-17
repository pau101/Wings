package me.paulf.wings.server.dreamcatcher;

import me.paulf.wings.util.NBTSerializer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;
import java.util.function.Consumer;

public final class Playable {
	private State state;

	public Playable() {
		this(AbsentState.INSTANCE);
	}

	private Playable(State state) {
		this.state = state;
	}

	public void setPlayer(UUID player) {
		state = new PresentState(player);
	}

	public void ifPlayerPresent(Consumer<UUID> consumer) {
		state.ifPresent(consumer);
	}

	private interface State {
		void ifPresent(Consumer<UUID> consumer);
	}

	private static final class AbsentState implements State {
		private static final State INSTANCE = new AbsentState();

		@Override
		public void ifPresent(Consumer<UUID> consumer) {}
	}

	private static final class PresentState implements State {
		private final UUID player;

		private PresentState(UUID player) {
			this.player = player;
		}

		@Override
		public void ifPresent(Consumer<UUID> consumer) {
			consumer.accept(player);
		}
	}

	public static final class Serializer implements NBTSerializer<Playable, NBTTagCompound> {
		private static final String PLAYER_UUID = "PlayerUUID";

		@Override
		public NBTTagCompound serialize(Playable instance) {
			NBTTagCompound compound = new NBTTagCompound();
			instance.ifPlayerPresent(playerId -> compound.setUniqueId(PLAYER_UUID, playerId));
			return compound;
		}

		@Override
		public Playable deserialize(NBTTagCompound compound) {
			Playable playable = new Playable();
			if (compound.hasUniqueId(PLAYER_UUID)) {
				playable.setPlayer(compound.getUniqueId(PLAYER_UUID));
			}
			return playable;
		}
	}
}
