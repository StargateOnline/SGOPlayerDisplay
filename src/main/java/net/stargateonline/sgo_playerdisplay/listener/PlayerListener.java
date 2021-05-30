package net.stargateonline.sgo_playerdisplay.listener;

import io.github.nucleuspowered.nucleus.api.core.event.NucleusFirstJoinEvent;
import io.github.nucleuspowered.nucleus.api.module.nickname.event.NucleusChangeNicknameEvent;
import net.stargateonline.sgo_playerdisplay.PlayerDisplay;
import net.stargateonline.sgo_playerdisplay.PlayerDisplayTokens;
import net.stargateonline.sgo_playerdisplay.config.Config;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;
import org.spongepowered.api.text.serializer.TextSerializers;

public class PlayerListener {

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death deathEvent) {
        Living targetEntity = deathEvent.getTargetEntity();

        if(!deathEvent.isMessageCancelled()) {
            //clear entity properties
            if(targetEntity.getType() == EntityTypes.PLAYER) {
                Player targetPlayer = (Player) targetEntity;
                Scoreboard playerScoreboard = targetPlayer.getScoreboard();

                playerScoreboard.getTeam(targetPlayer.getName()).ifPresent(Team::unregister);
            }
        }
    }

    @Listener
    public void onPlayerFirstJoin(NucleusFirstJoinEvent event, @Getter("getTargetEntity") Player player) {
        if(Config.showFirstTimeMessage() && !Config.getFirstTimeMessage().isEmpty()) {
            MessageChannel.TO_ALL.send(PlayerDisplay.getPluginContainer(), getFormattedConnectionMessage(Config.getFirstTimeMessage(), player));
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join playerJoinEvent, @Root Player player) {
        if(Config.showLoginMessage()) {
            if(Config.getLoginMessage().isEmpty()) {
                playerJoinEvent.setMessageCancelled(true);
            } else {
                playerJoinEvent.setMessage(getFormattedConnectionMessage(Config.getLoginMessage(), player));
            }
        }
        PlayerDisplay.getInstance().refreshTabList(player);
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect disconnectEvent, @Root Player player) {
        if(Config.showLogoutMessage()) {
            if(Config.getLogoutMessage().isEmpty()) {
                disconnectEvent.setMessageCancelled(true);
            } else {
                disconnectEvent.setMessage(getFormattedConnectionMessage(Config.getLogoutMessage(), player));
            }
        }
    }

    @Listener
    public void onNicknameChange(NucleusChangeNicknameEvent.Post changeNicknameEvent) {
        changeNicknameEvent.getUser().getPlayer().ifPresent(player -> {
            PlayerDisplay.getInstance().refreshTabList(player);
        });
    }

    public static Text getFormattedConnectionMessage(String message, Player player) {
        PlaceholderParser parser = Sponge.getRegistry().getType(PlaceholderParser.class, PlayerDisplayTokens.PLUGIN_ID + ":playergroup").get();
        PlaceholderContext context = PlaceholderContext.builder()
                .setAssociatedObject(player)
                .build();
        Text playerDispname = parser.parse(context);

        return TextSerializers.FORMATTING_CODE.deserialize(message).replace("%PLAYER%", playerDispname);
    }

}
