package net.stargateonline.sgo_playerdisplay.listener;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.event.user.track.UserDemoteEvent;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import net.stargateonline.sgo_playerdisplay.PlayerDisplay;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class UserListener {
    private static PlayerDisplay plugin;

    public UserListener(PlayerDisplay _plugin, LuckPerms lp) {
        plugin = _plugin;
        EventBus eventbus = lp.getEventBus();

        eventbus.subscribe(UserDataRecalculateEvent.class, e-> {
            Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUniqueId());
            player.ifPresent(p -> plugin.refreshTabList(p));
        });

        eventbus.subscribe(UserDemoteEvent.class, e-> {
            Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUniqueId());
            player.ifPresent(p -> plugin.refreshTabList(p));
        });

        eventbus.subscribe(UserPromoteEvent.class, e-> {
            Optional<Player> player = Sponge.getServer().getPlayer(e.getUser().getUniqueId());
            player.ifPresent(p -> plugin.refreshTabList(p));
        });
    }
}
