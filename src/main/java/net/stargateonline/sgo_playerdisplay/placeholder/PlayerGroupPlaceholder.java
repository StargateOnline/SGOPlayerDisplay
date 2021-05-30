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

public class PlayerGroupPlaceholder implements PlaceholderParser {

    @Override
    public String getId() {
        return PlayerDisplayTokens.PLUGIN_ID + ":playergroup";
    }

    @Override
    public String getName() {
        return "Player Name + LuckPerms Group Placeholder";
    }

    @Override
    public Text parse(PlaceholderContext placeholderContext) {
        return placeholderContext.getAssociatedObject()
            .filter( p -> p instanceof Player )
            .map( p -> genDisplayname((Player) p) )
            .filter( Objects::nonNull ) // ignore offline players
            .map( TextSerializers.FORMATTING_CODE::deserialize )
            .orElse( Text.EMPTY );
    }

    private static String genDisplayname(Player player) {
        LuckPerms lp = PlayerDisplay.getInstance().getLuckPerms();

        // Get prefix and suffix
        User user = Util.loadUser(player);
        if(user == null) return null; // user is not online

        Group group = lp.getGroupManager().getGroup(user.getPrimaryGroup());
        if(group == null) return null;

        ContextManager cm = lp.getContextManager();
        CachedMetaData usermeta = user.getCachedData().getMetaData(cm.getStaticQueryOptions());
        CachedMetaData groupmeta = group.getCachedData().getMetaData(cm.getStaticQueryOptions());

        // Nickname support
        Text nicknameText = Util.retrieveNickname(player);

        String prefix = (usermeta.getPrefix() != null) ? usermeta.getPrefix() : groupmeta.getPrefix();
        String suffix = (usermeta.getSuffix() != null) ? usermeta.getSuffix() : groupmeta.getSuffix();
        String displayname = TextSerializers.FORMATTING_CODE.serialize(player.getDisplayNameData().displayName().get());
        String nickname = TextSerializers.FORMATTING_CODE.serialize(nicknameText);

        if(prefix == null) prefix = Config.getInitPrefix();
        if(suffix == null) suffix = Config.getInitSuffix();
        if(displayname.isEmpty()) displayname = player.getName();

        if(Util.isStringOverSizeLimit(prefix)) prefix = "";
        if(Util.isStringOverSizeLimit(suffix)) suffix = "";


        String formattedDisplayname = "";
        if(Config.showPrefix() && !prefix.isEmpty()) formattedDisplayname += prefix + PlayerDisplayTokens.SPACE_SEPARATOR;
        if(Config.showNickName() && !nickname.isEmpty()) formattedDisplayname += nickname;
        else if(Config.showDisplayName()) formattedDisplayname += displayname;
        else formattedDisplayname += player.getName();
        if(Config.showSuffix() && !suffix.isEmpty()) formattedDisplayname += PlayerDisplayTokens.SPACE_SEPARATOR + suffix;

        return formattedDisplayname;
    }
}
