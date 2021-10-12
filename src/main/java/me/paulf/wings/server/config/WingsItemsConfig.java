package me.paulf.wings.server.config;

import com.google.common.collect.ImmutableMap;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.item.WingSettings;
import net.minecraft.util.ResourceLocation;

import java.util.stream.Stream;

//@Config(modid = WingsMod.ID, name = WingsMod.ID + "/items", category = "") FIXME: config
public final class WingsItemsConfig {
    private WingsItemsConfig() {
    }

    //	@Config.LangKey("config.wings.items.entry.angel")
//	@Config.LangKey("config.wings.items.entry.angel")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings ANGEL = new ConfigWingSettings(WingsMod.Names.ANGEL);

    //	@Config.LangKey("config.wings.items.entry.slime")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings SLIME = new ConfigWingSettings(WingsMod.Names.SLIME);

    //	@Config.LangKey("config.wings.items.entry.blue_butterfly")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings BLUE_BUTTERFLY = new ConfigWingSettings(WingsMod.Names.BLUE_BUTTERFLY);

    //	@Config.LangKey("config.wings.items.entry.monarch_butterfly")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings MONARCH_BUTTERFLY = new ConfigWingSettings(WingsMod.Names.MONARCH_BUTTERFLY);

    //	@Config.LangKey("config.wings.items.entry.fire")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings FIRE = new ConfigWingSettings(WingsMod.Names.FIRE);

    //	@Config.LangKey("config.wings.items.entry.bat")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings BAT = new ConfigWingSettings(WingsMod.Names.BAT);

    //	@Config.LangKey("config.wings.items.entry.fairy")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings FAIRY = new ConfigWingSettings(WingsMod.Names.FAIRY);

    //	@Config.LangKey("config.wings.items.entry.evil")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings EVIL = new ConfigWingSettings(WingsMod.Names.EVIL);

    //	@Config.LangKey("config.wings.items.entry.dragon")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings DRAGON = new ConfigWingSettings(WingsMod.Names.DRAGON);

    public static ImmutableMap<ResourceLocation, WingSettings> createWingAttributes() {
        return Stream.of(ANGEL, SLIME, BLUE_BUTTERFLY, MONARCH_BUTTERFLY, FIRE, BAT, FAIRY, EVIL, DRAGON)
            .collect(ImmutableMap.toImmutableMap(ConfigWingSettings::getKey, ConfigWingSettings::toImmutable));
    }
}
