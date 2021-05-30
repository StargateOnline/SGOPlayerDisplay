package net.stargateonline.sgo_playerdisplay.listener;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.stargateonline.sgo_playerdisplay.commands.CommandRefresh;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;

public class GroupListener {

    public GroupListener(LuckPerms lp) {
        EventBus eventbus = lp.getEventBus();
        eventbus.subscribe(GroupDataRecalculateEvent.class, e-> {
            ArrayList<Player> group = new ArrayList<Player>();

            for(Player player : Sponge.getServer().getOnlinePlayers()) {
                User user = lp.getUserManager().getUser(player.getUniqueId());
                if(user == null) continue;
                if(user.getPrimaryGroup().equals(e.getGroup().getName())) {
                    group.add(player);
                }
                CommandRefresh.updateTab4All(group);
            }
        });
    }
}
