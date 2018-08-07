package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.Animator;
import me.paulf.wings.server.flight.AnimatorFactory;
import me.paulf.wings.server.flight.StandardAnimatorFactory;
import me.paulf.wings.server.flight.WingType;
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

	private static final String ID_FORMAT = "%s_wings";

	private static final String TEXTURE_FORMAT = "textures/entity/wings/%s.png";

	private static final StandardWing[] TYPES = values();

	private final AnimatorFactory animatorFactory;

	private final ResourceLocation id;

	private final ResourceLocation texture;

	StandardWing(AnimatorFactory animatorFactory, String name) {
		this(
			animatorFactory,
			new ResourceLocation(WingsMod.ID, String.format(ID_FORMAT, name)),
			new ResourceLocation(WingsMod.ID, String.format(TEXTURE_FORMAT, name))
		);
	}

	StandardWing(AnimatorFactory animatorFactory, ResourceLocation id, ResourceLocation texture) {
		this.animatorFactory = animatorFactory;
		this.id = id;
		this.texture = texture;
	}

	public ResourceLocation getId() {
		return id;
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

}
