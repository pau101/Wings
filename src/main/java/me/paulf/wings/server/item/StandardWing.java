package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.Animator;
import me.paulf.wings.server.flight.AnimatorFactory;
import me.paulf.wings.server.flight.WingType;
import me.paulf.wings.util.Mth;
import me.paulf.wings.util.Util;
import me.paulf.wings.server.flight.StandardAnimatorFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.stream.Stream;

public enum StandardWing implements WingType {
	ANGEL(StandardAnimatorFactory.AVIAN, "angel"),
	SLIME(StandardAnimatorFactory.INSECTOID, "slime"),
	BLUE_BUTTERFLY(StandardAnimatorFactory.INSECTOID, "blue_butterfly"),
	MONARCH_BUTTERFLY(StandardAnimatorFactory.INSECTOID, "monarch_butterfly"),
	FIRE(StandardAnimatorFactory.AVIAN, "fire"),
	BAT(StandardAnimatorFactory.AVIAN, "bat"),
	FAIRY(StandardAnimatorFactory.INSECTOID, "fairy"),
	EVIL(StandardAnimatorFactory.AVIAN, "evil"),
	DRAGON(StandardAnimatorFactory.AVIAN, "dragon");

	private static final String TRANSLATION_KEY_FORMAT = "wing.%s.name";

	private static final String TEXTURE_FORMAT = "textures/entity/wings/%s.png";

	private static final StandardWing[] TYPES = values();

	private final AnimatorFactory animatorFactory;

	private final String name;

	private final String translationKey;

	private final ResourceLocation texture;

	StandardWing(AnimatorFactory animatorFactory, String name) {
		this(
			animatorFactory,
			name,
			String.format(TRANSLATION_KEY_FORMAT, Util.underScoreToCamel(name)),
			new ResourceLocation(WingsMod.ID, String.format(TEXTURE_FORMAT, name))
		);
	}

	StandardWing(AnimatorFactory animatorFactory, String name, String translationKey, ResourceLocation texture) {
		this.animatorFactory = animatorFactory;
		this.name = name;
		this.translationKey = translationKey;
		this.texture = texture;
	}

	public String getName() {
		return name;
	}

	public String getTranslationKey() {
		return translationKey;
	}

	public int getMeta() {
		return ordinal();
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	@Override
	public boolean canFly() {
		return true;
	}

	@Override
	public Animator getAnimator(Animator animator) {
		return animatorFactory.provides(animator) ? animator : animatorFactory.create();
	}

	public static Stream<StandardWing> stream() {
		return Stream.of(TYPES);
	}

	public static StandardWing fromMeta(ItemStack stack) {
		return TYPES[Mth.mod(stack.getMetadata(), TYPES.length)];
	}
}
