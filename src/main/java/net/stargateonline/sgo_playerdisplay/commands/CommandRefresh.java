package net.stargateonline.sgo_playerdisplay.commands;

import net.stargateonline.sgo_playerdisplay.PlayerDisplayTokens;
import net.stargateonline.sgo_playerdisplay.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;

import java.util.ArrayList;
import java.util.Optional;

public class CommandRefresh {

    /*On Player Join Event, Update Other players' tab list, simply insert*/
    public static void refreshOthers(Player player) {
        PlaceholderParser parser = Sponge.getRegistry().getType(PlaceholderParser.class, PlayerDisplayTokens.PLUGIN_ID + ":playergroup").get();

        for(Player p : Sponge.getServer().getOnlinePlayers()) {
            if(p.equals(player)) continue;
            TabList tabList = p.getTabList();
            Optional<TabListEntry> tabListEntry = tabList.getEntry(player.getUniqueId()); //minecraft thread added this entry?

            PlaceholderContext context = PlaceholderContext.builder()
                    .setAssociatedObject(player)
                    .build();
            Text name = parser.parse(context);

            if(tabListEntry.isPresent()) {
                // Update TabList
                tabListEntry.get().setDisplayName(name);
            }

            Util.setPlayerNametag(player);
        }
    }

    /* On Player Join Event, Update player, get all online players' data */
    public static void refreshSelf(Player player) {
        if(player.isOnline()) {
            PlaceholderParser parser = Sponge.getRegistry().getType(PlaceholderParser.class, PlayerDisplayTokens.PLUGIN_ID + ":playergroup").get();
            TabList tabList = player.getTabList();

            for(Player p : Sponge.getServer().getOnlinePlayers()) {
                PlaceholderContext context = PlaceholderContext.builder()
                    .setAssociatedObject(p)
                    .build();
                Text name = parser.parse(context);
                Optional<TabListEntry> tabListEntry = tabList.getEntry(p.getUniqueId());

                // Update TabList and set player's name tag
                if(tabListEntry.isPresent()) {
                    tabListEntry.get().setDisplayName(name);
                }

                Util.setPlayerNametag(p);
            }
        }
    }

    public static void updateTab4All(ArrayList<Player> groupplayer) {
        PlaceholderParser parser = Sponge.getRegistry().getType(PlaceholderParser.class, PlayerDisplayTokens.PLUGIN_ID + ":playergroup").get();

        for(Player p : Sponge.getServer().getOnlinePlayers()) {
            TabList tabList = p.getTabList();
            for(Player groplayer : groupplayer) {
                Optional<TabListEntry> tabListEntry = tabList.getEntry(groplayer.getUniqueId());
                PlaceholderContext context = PlaceholderContext.builder()
                        .setAssociatedObject(groplayer)
                        .build();
                Text name = parser.parse(context);

                if(tabListEntry.isPresent()) {
                    // Update TabList and set player's name tag
                    tabListEntry.get().setDisplayName(name);
                }

                Util.setPlayerNametag(p);
            }
        }
    }

    public static void updateAllTab() {
        PlaceholderParser parser = Sponge.getRegistry().getType(PlaceholderParser.class, PlayerDisplayTokens.PLUGIN_ID + ":playergroup").get();

        for(Player p : Sponge.getServer().getOnlinePlayers()) {
            TabList tabList = p.getTabList();
            for(Player player : Sponge.getServer().getOnlinePlayers()) {
                Optional<TabListEntry> tabListEntry = tabList.getEntry(player.getUniqueId());
                PlaceholderContext context = PlaceholderContext.builder()
                        .setAssociatedObject(player)
                        .build();
                Text name = parser.parse(context);

                if(tabListEntry.isPresent()) {
                    // Update TabList and set player's name tag
                    tabListEntry.get().setDisplayName(name);
                }

                Util.setPlayerNametag(p);
            }
        }
    }

}
