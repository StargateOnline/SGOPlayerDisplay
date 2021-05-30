package net.stargateonline.sgo_playerdisplay.placeholder;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.stargateonline.sgo_playerdisplay.PlayerDisplay;
import net.stargateonline.sgo_playerdisplay.PlayerDisplayTokens;
import net.stargateonline.sgo_playerdisplay.Util;
import net.stargateonline.sgo_playerdisplay.config.Config;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Objects;

public class SuffixPlaceholder implements PlaceholderParser {
    @Override
    public String getId() {
        return PlayerDisplayTokens.PLUGIN_ID + ":suffix";
    }

    @Override
    public String getName() {
        return "LuckPerms Group Suffix Placeholder";
    }

    @Override
    public Text parse(PlaceholderContext placeholderContext) {
        return placeholderContext.getAssociatedObject()
                .filter( p -> p instanceof Player)
                .map( p -> genSuffix((Player) p) )
                .filter( Objects::nonNull ) // ignore offline players
                .map( TextSerializers.FORMATTING_CODE::deserialize )
                .orElse( Text.EMPTY );
    }

    private static String genSuffix(Player player) {
        LuckPerms lp = PlayerDisplay.getInstance().getLuckPerms();

        // Get prefix and suffix
        User user = Util.loadUser(player);
        if(user == null) return null; // user is not online

        Group group = lp.getGroupManager().getGroup(user.getPrimaryGroup());
        if(group == null) return null;

        ContextManager cm = lp.getContextManager();
        CachedMetaData usermeta = user.getCachedData().getMetaData(cm.getStaticQueryOptions());
        CachedMetaData groupmeta = group.getCachedData().getMetaData(cm.getStaticQueryOptions());

        String suffix = (usermeta.getSuffix() != null) ? usermeta.getSuffix() : groupmeta.getSuffix();

        if(suffix == null) suffix = Config.getInitSuffix();
        if(Util.isStringOverSizeLimit(suffix)) suffix = "";

        return suffix;
    }
}
