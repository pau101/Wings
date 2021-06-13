package me.paulf.wings.server.config;

import com.google.common.collect.ImmutableMap;
import me.paulf.wings.server.item.WingSettings;
import me.paulf.wings.server.item.WingsItems;
import net.minecraft.util.ResourceLocation;

import java.util.stream.Stream;

//@Config(modid = WingsMod.ID, name = WingsMod.ID + "/items", category = "") FIXME: config
public final class WingsItemsConfig {
    private WingsItemsConfig() {
    }

    //	@Config.LangKey("config.wings.items.entry.angel")
//	@Config.LangKey("config.wings.items.entry.angel")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings ANGEL = new ConfigWingSettings(WingsItems.Names.ANGEL);

    //	@Config.LangKey("config.wings.items.entry.slime")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings SLIME = new ConfigWingSettings(WingsItems.Names.SLIME);

    //	@Config.LangKey("config.wings.items.entry.blue_butterfly")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings BLUE_BUTTERFLY = new ConfigWingSettings(WingsItems.Names.BLUE_BUTTERFLY);

    //	@Config.LangKey("config.wings.items.entry.monarch_butterfly")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings MONARCH_BUTTERFLY = new ConfigWingSettings(WingsItems.Names.MONARCH_BUTTERFLY);

    //	@Config.LangKey("config.wings.items.entry.fire")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings FIRE = new ConfigWingSettings(WingsItems.Names.FIRE);

    //	@Config.LangKey("config.wings.items.entry.bat")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings BAT = new ConfigWingSettings(WingsItems.Names.BAT);

    //	@Config.LangKey("config.wings.items.entry.fairy")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings FAIRY = new ConfigWingSettings(WingsItems.Names.FAIRY);

    //	@Config.LangKey("config.wings.items.entry.evil")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings EVIL = new ConfigWingSettings(WingsItems.Names.EVIL);

    //	@Config.LangKey("config.wings.items.entry.dragon")
//	@Config.RequiresMcRestart
    public static final ConfigWingSettings DRAGON = new ConfigWingSettings(WingsItems.Names.DRAGON);

    public static ImmutableMap<ResourceLocation, WingSettings> createWingAttributes() {
        return Stream.of(ANGEL, SLIME, BLUE_BUTTERFLY, MONARCH_BUTTERFLY, FIRE, BAT, FAIRY, EVIL, DRAGON)
            .collect(ImmutableMap.toImmutableMap(ConfigWingSettings::getKey, ConfigWingSettings::toImmutable));
    }
}
