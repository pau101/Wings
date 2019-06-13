package me.paulf.wings.client.flight;

import me.paulf.wings.client.apparatus.FlightApparatusView;
import me.paulf.wings.client.apparatus.FlightApparatusViews;
import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class FlightViewStatic implements FlightView {
	private final EntityLivingBase entity;

	private Item item = Items.AIR;

	private final Consumer<Consumer<FormRenderer>> emptyState = consumer -> {};

	private Consumer<Consumer<FormRenderer>> state = this.emptyState;

	public FlightViewStatic(final EntityLivingBase entity) {
		this.entity = entity;
	}

	@Override
	public void ifFormPresent(final Consumer<FormRenderer> consumer) {
		final ItemStack stack = FlightApparatuses.find(this.entity);
		if (!this.item.equals(stack.getItem())) {
			this.item = stack.getItem();
			final FlightApparatusView view = FlightApparatusViews.get(stack);
			if (view == null) {
				this.state = this.emptyState;
			} else {
				this.state = new PresentState<>(view.getForm());
			}
		}
		this.state.accept(consumer);
	}

	@Override
	public void tick(final ItemStack wings) {}

	@Override
	public void tickEyeHeight(final float value, final float delta, final FloatConsumer valueOut) {}

	private static final class PresentState<T extends Animator> implements FormRenderer, Consumer<Consumer<FormRenderer>> {
		final WingForm<T> form;

		final T animator;

		PresentState(final WingForm<T> form) {
			this.form = form;
			this.animator = form.createAnimator();
		}

		@Override
		public ResourceLocation getTexture() {
			return this.form.getTexture();
		}

		@Override
		public void render(final float delta, final float scale) {
			this.form.getModel().render(this.animator, delta, scale);
		}

		@Override
		public void accept(final Consumer<FormRenderer> consumer) {
			consumer.accept(this);
		}
	}
}
