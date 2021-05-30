package net.stargateonline.sgo_playerdisplay.placeholder;

import net.stargateonline.sgo_playerdisplay.PlayerDisplayTokens;
import net.stargateonline.sgo_playerdisplay.config.Config;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Objects;


public class PlayerDisplaynamePlaceholder implements PlaceholderParser {
    @Override
    public String getId() {
        return PlayerDisplayTokens.PLUGIN_ID + ":playerdispname";
    }

    @Override
    public String getName() {
        return "Player Minecraft displayname";
    }

    @Override
    public Text parse(PlaceholderContext placeholderContext) {
        return placeholderContext.getAssociatedObject()
                .filter( p -> p instanceof Player)
                .map( p -> genDisplayname((Player) p) )
                .filter( Objects::nonNull ) // ignore offline players
                .map( TextSerializers.FORMATTING_CODE::deserialize )
                .orElse( Text.EMPTY );
    }

    private static String genDisplayname(Player player) {
        String displayname = TextSerializers.FORMATTING_CODE.serialize(player.getDisplayNameData().displayName().get());

        if(displayname.isEmpty()) displayname = player.getName();

        String formattedDisplayname = "";
        if(Config.showDisplayName()) formattedDisplayname += displayname;
        else formattedDisplayname += player.getName();

        return formattedDisplayname;
    }
}
