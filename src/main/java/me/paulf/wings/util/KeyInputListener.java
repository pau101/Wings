package me.paulf.wings.util;

import com.google.common.collect.ImmutableListMultimap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public final class KeyInputListener {
	private final ImmutableListMultimap<KeyBinding, Runnable> bindings;

	private KeyInputListener(ImmutableListMultimap<KeyBinding, Runnable> bindings) {
		this.bindings = bindings;
	}

	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent event) {
		this.bindings.asMap().entrySet().stream()
			.filter(e -> e.getKey().isPressed())
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

		private BuilderRoot(ImmutableListMultimap.Builder<KeyBinding, Runnable> bindings) {
			this.bindings = bindings;
		}

		@Override
		public CategoryBuilder category(String category) {
			return new CategoryBuilderRoot(this, category);
		}

		@Override
		public KeyInputListener build() {
			return new KeyInputListener(this.bindings.build());
		}
	}

	private static abstract class ChildBuilder<P extends Builder> implements Builder {
		final P parent;

		private ChildBuilder(P parent) {
			this.parent = parent;
		}

		@Override
		public final CategoryBuilder category(String category) {
			return this.parent.category(category);
		}

		@Override
		public final KeyInputListener build() {
			return this.parent.build();
		}
	}

	private static final class CategoryBuilderRoot extends ChildBuilder<BuilderRoot> implements CategoryBuilder {
		private final String category;

		private CategoryBuilderRoot(BuilderRoot delegate, String category) {
			super(delegate);
			this.category = category;
		}

		@Override
		public BindingBuilder key(String desc, IKeyConflictContext context, KeyModifier modifier, int keyCode) {
			KeyBinding binding = new KeyBinding(desc, context, modifier, keyCode, this.category);
			ClientRegistry.registerKeyBinding(binding);
			return new BindingBuilder(this, binding);
		}
	}

	public static final class BindingBuilder extends ChildBuilder<CategoryBuilderRoot> implements CategoryBuilder {
		private final KeyBinding binding;

		private BindingBuilder(CategoryBuilderRoot delegate, KeyBinding binding) {
			super(delegate);
			this.binding = binding;
		}

		public BindingBuilder onPress(Runnable runnable) {
			this.parent.parent.bindings.put(this.binding, runnable);
			return this;
		}

		@Override
		public BindingBuilder key(String desc, IKeyConflictContext context, KeyModifier modifier, int keyCode) {
			return this.parent.key(desc, context, modifier, keyCode);
		}
	}
}
