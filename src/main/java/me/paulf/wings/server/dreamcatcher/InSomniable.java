package me.paulf.wings.server.dreamcatcher;

import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.util.NBTSerializer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.function.IntConsumer;

public final class InSomniable {
	private State state;

	public InSomniable() {
		this(new SearchState());
	}

	private InSomniable(final State state) {
		this.state = state;
	}

	public void onPlay(final World world, final PlayerEntity player, final BlockPos pos, final int note) {
		this.state = this.state.onPlay(world, player, pos, note);
	}

	public void clone(final InSomniable other) {
		this.state = other.state.copy();
	}

	private interface State {
		State onPlay(World world, PlayerEntity player, BlockPos pos, int note);

		State copy();

		void ifSearching(IntConsumer consumer);
	}

	private static final class SearchState implements State {
		private final int[] mask = {
			0xBFBE,
			0xFFFD,
			0xFFFF,
			0xCD43,
			0xFFFF,
			0x7EFF,
			0xFFFF,
			0xF7FF,
			0xFBFF
		};

		private final String[] members = {
			"wings.dreamcatcher.jiu",
			"wings.dreamcatcher.sua",
			"wings.dreamcatcher.siyeon",
			"wings.dreamcatcher.handong",
			"wings.dreamcatcher.yoohyeon",
			"wings.dreamcatcher.dami",
			"wings.dreamcatcher.gahyeon",
		};

		private int state;

		private SearchState() {
			this(0x1FFFE);
		}

		private SearchState(final int state) {
			this.state = state;
		}

		@Override
		public State onPlay(final World world, final PlayerEntity player, final BlockPos pos, final int note) {
			if (note >= 6 && note <= 14 && ((this.state = (this.state | this.mask[note - 6]) << 1) & 0x20000) == 0) {
				final ItemStack stack = new ItemStack(WingsItems.BLUE_BUTTERFLY_WINGS.get());
				stack.setHoverName(new TranslationTextComponent(this.members[world.random.nextInt(this.members.length)]));
				final ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.25D, pos.getZ() + 0.5D, stack);
				entity.setDefaultPickUpDelay();
				world.addFreshEntity(entity);
				return InSomniacState.INSTANCE;
			}
			return this;
		}

		@Override
		public State copy() {
			return new SearchState(this.state);
		}

		@Override
		public void ifSearching(final IntConsumer consumer) {
			consumer.accept(this.state);
		}
	}

	private static final class InSomniacState implements State {
		private static final State INSTANCE = new InSomniacState();

		@Override
		public State onPlay(final World world, final PlayerEntity player, final BlockPos pos, final int note) {
			return this;
		}

		@Override
		public State copy() {
			return this;
		}

		@Override
		public void ifSearching(final IntConsumer consumer) {}
	}

	public static final class Serializer implements NBTSerializer<InSomniable, CompoundNBT> {
		private static final String SEARCH_STATE = "SearchState";

		@Override
		public CompoundNBT serialize(final InSomniable instance) {
			final CompoundNBT compound = new CompoundNBT();
			instance.state.ifSearching(state -> compound.putInt(SEARCH_STATE, state));
			return compound;
		}

		@Override
		public InSomniable deserialize(final CompoundNBT compound) {
			final State state;
			if (compound.contains(SEARCH_STATE, Constants.NBT.TAG_INT)) {
				state = new SearchState(compound.getInt(SEARCH_STATE));
			} else {
				state = InSomniacState.INSTANCE;
			}
			return new InSomniable(state);
		}
	}
}
