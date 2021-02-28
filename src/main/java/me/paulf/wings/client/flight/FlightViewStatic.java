package me.paulf.wings.client.flight;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.client.apparatus.FlightApparatusView;
import me.paulf.wings.client.apparatus.FlightApparatusViews;
import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class FlightViewStatic implements FlightView {
	private final LivingEntity entity;

	private Item item = Items.AIR;

	private final Consumer<Consumer<FormRenderer>> emptyState = consumer -> {};

	private Consumer<Consumer<FormRenderer>> state = this.emptyState;

	public FlightViewStatic(final LivingEntity entity) {
		this.entity = entity;
	}

	@Override
	public void ifFormPresent(final Consumer<FormRenderer> consumer) {
		final ItemStack stack = FlightApparatuses.find(this.entity);
		if (!this.item.equals(stack.getItem())) {
			this.item = stack.getItem();
			this.state = FlightApparatusViews.get(stack)
				.<Consumer<Consumer<FormRenderer>>>map(view -> new PresentState<>(view.getForm()))
				.orElse(this.emptyState);
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
		public void render(final MatrixStack matrixStack, final IVertexBuilder buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha, final float delta) {
			this.form.getModel().render(this.animator, delta, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}

		@Override
		public void accept(final Consumer<FormRenderer> consumer) {
			consumer.accept(this);
		}
	}
}
