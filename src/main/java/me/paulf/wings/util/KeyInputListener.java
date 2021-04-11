package me.paulf.wings.util;

import com.google.common.collect.ImmutableListMultimap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public final class KeyInputListener {
	private final ImmutableListMultimap<KeyBinding, Runnable> bindings;

	private KeyInputListener(final ImmutableListMultimap<KeyBinding, Runnable> bindings) {
		this.bindings = bindings;
	}

	@SubscribeEvent
	public void onKey(final InputEvent.KeyInputEvent event) {
		this.bindings.asMap().entrySet().stream()
			.filter(e -> e.getKey().consumeClick())
			.flatMap(e -> e.getValue().stream())
			.forEach(Runnable::run);
	}

	public static Builder builder() {
		return new BuilderRoot();
	}

	public interface Builder {
		CategoryBuilder category(String category);

		KeyInputListener build();
	}

	public interface CategoryBuilder extends Builder {
		BindingBuilder key(String desc, IKeyConflictContext context, KeyModifier modifier, int keyCode);
	}

	public static final class BuilderRoot implements Builder {
		private final ImmutableListMultimap.Builder<KeyBinding, Runnable> bindings;

		private BuilderRoot() {
			this(ImmutableListMultimap.builder());
		}

		private BuilderRoot(final ImmutableListMultimap.Builder<KeyBinding, Runnable> bindings) {
			this.bindings = bindings;
		}

		@Override
		public CategoryBuilder category(final String category) {
			return new CategoryBuilderRoot(this, category);
		}

		@Override
		public KeyInputListener build() {
			return new KeyInputListener(this.bindings.build());
		}
	}

	private static abstract class ChildBuilder<P extends Builder> implements Builder {
		final P parent;

		private ChildBuilder(final P parent) {
			this.parent = parent;
		}

		@Override
		public final CategoryBuilder category(final String category) {
			return this.parent.category(category);
		}

		@Override
		public final KeyInputListener build() {
			return this.parent.build();
		}
	}

	private static final class CategoryBuilderRoot extends ChildBuilder<BuilderRoot> implements CategoryBuilder {
		private final String category;

		private CategoryBuilderRoot(final BuilderRoot delegate, final String category) {
			super(delegate);
			this.category = category;
		}

		@Override
		public BindingBuilder key(final String desc, final IKeyConflictContext context, final KeyModifier modifier, final int keyCode) {
			final KeyBinding binding = new KeyBinding(desc, context, modifier, InputMappings.Type.KEYSYM, keyCode, this.category);
			ClientRegistry.registerKeyBinding(binding);
			return new BindingBuilder(this, binding);
		}
	}

	public static final class BindingBuilder extends ChildBuilder<CategoryBuilderRoot> implements CategoryBuilder {
		private final KeyBinding binding;

		private BindingBuilder(final CategoryBuilderRoot delegate, final KeyBinding binding) {
			super(delegate);
			this.binding = binding;
		}

		public BindingBuilder onPress(final Runnable runnable) {
			this.parent.parent.bindings.put(this.binding, runnable);
			return this;
		}

		@Override
		public BindingBuilder key(final String desc, final IKeyConflictContext context, final KeyModifier modifier, final int keyCode) {
			return this.parent.key(desc, context, modifier, keyCode);
		}
	}
}
