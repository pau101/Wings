package com.pau101.wings.server.item;

import java.util.stream.Stream;

import com.pau101.wings.WingsMod;
import com.pau101.wings.server.flight.Animator;
import com.pau101.wings.server.flight.AnimatorFactory;
import com.pau101.wings.server.flight.WingType;
import com.pau101.wings.util.Mth;
import com.pau101.wings.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import static com.pau101.wings.server.flight.StandardAnimatorFactory.AVIAN;
import static com.pau101.wings.server.flight.StandardAnimatorFactory.INSECTOID;

public enum StandardWing implements WingType {
	ANGEL(AVIAN, "angel"),
	SLIME(INSECTOID, "slime"),
	BLUE_BUTTERFLY(INSECTOID, "blue_butterfly"),
	MONARCH_BUTTERFLY(INSECTOID, "monarch_butterfly"),
	FIRE(AVIAN, "fire"),
	BAT(AVIAN, "bat"),
	FAIRY(INSECTOID, "fairy"),
	EVIL(AVIAN, "evil"),
	DRAGON(AVIAN, "dragon");

	private static final String TRANSLATION_KEY_FORMAT = "wing.%s.name";

	private static final String TEXTURE_FORMAT = "textures/entity/wings/%s.png";

	private static final StandardWing[] TYPES = values();

	private final AnimatorFactory animatorFactory;

	private final String name;

	private final String translationKey;

	private final ResourceLocation texture;

	StandardWing(AnimatorFactory animatorFactory, String name) {
		this(animatorFactory, name, String.format(TRANSLATION_KEY_FORMAT, Util.underScoreToCamel(name)), new ResourceLocation(WingsMod.ID, String.format(TEXTURE_FORMAT, name)));
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
