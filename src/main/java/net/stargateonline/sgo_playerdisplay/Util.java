package net.stargateonline.sgo_playerdisplay;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.stargateonline.sgo_playerdisplay.config.Config;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

public class Util {

    final public static int MAX_FIELD_LENGTH = 16; // characters

    /* load user instance, if player is not online, return null */
    public static User loadUser(Player player){
        LuckPerms lp = PlayerDisplay.getInstance().getLuckPerms();
        if (!player.isOnline()) return null;
        else return lp.getUserManager().getUser(player.getUniqueId());
    }

    /**
     * Check the size of a string without taking into account formatting characters
     * @param str string
     * @return
     */
    public static boolean isStringOverSizeLimit(String str) {
        return TextSerializers.FORMATTING_CODE.deserialize(str).toPlain().length() > MAX_FIELD_LENGTH;
    }

    /**
     * Set name tag above player's head
     * @param player player entity
     */
    public static void setPlayerNametag(Player player) {
        Team team;
        Scoreboard playerScoreboard = player.getScoreboard();

        PlaceholderContext context = PlaceholderContext.builder()
                .setAssociatedObject(player)
                .build();

        // Retrieve prefix
        PlaceholderParser parserPrefix = Sponge.getRegistry().getType(PlaceholderParser.class, PlayerDisplayTokens.PLUGIN_ID + ":prefix").get();
        Text prefix = parserPrefix.parse(context);

        Optional<Team> optionalTeam = playerScoreboard.getTeam(player.getName());
        if(optionalTeam.isPresent()) {
            team = optionalTeam.get();
        } else {
            team = Team.builder().name(player.getName()).build();
            playerScoreboard.registerTeam(team);
        }

        team.getMembers().clear();
        team.addMember(TextSerializers.FORMATTING_CODE.deserialize("&r&f" + player.getName())); // player's display name (minecraft username)
        if( Config.showPrefix() && !prefix.toPlain().isEmpty() && !isStringOverSizeLimit(prefix.toPlain()) )
            team.setPrefix(prefix);
        if( Config.showSuffix() ) // display nucleus nickname as suffix, if a nickname has been specified by the player
            setNicknameAsSuffix(team, player);
    }

    public static void setNicknameAsSuffix(Team team, Player player) {
        String nickname = retrieveNickname(player).toPlain();
        String suffixStr = "";

        if(!nickname.isEmpty()) {
            suffixStr += "&r&8(";
            if(nickname.length() > MAX_FIELD_LENGTH - 2) {
                suffixStr += nickname.substring(0, MAX_FIELD_LENGTH - 4) + ".."; // shorten nickname
            } else {
                suffixStr += nickname;
            }
            suffixStr += ")";
        }

        team.setSuffix(TextSerializers.FORMATTING_CODE.deserialize(suffixStr));
    }

    public static Text retrieveNickname(Player player) {
        Text nickname = Text.EMPTY;
        if(PlayerDisplay.getInstance().isNucleusAvailable()) {
            Optional<Text> nick = NucleusAPI.getNicknameService().get().getNickname(player.getUniqueId());
            if(nick.isPresent()) nickname = nick.get();

            /*if(Util.isStringOverSizeLimit(nickname.toPlain())) {
                try {
                    NucleusAPI.getNicknameService().get().removeNickname(player);
                } catch(NicknameException e) {
                    PlayerDisplay.getInstance().getLogger().error("Nickname too long. Unable to remove nickname from player!");
                }
                nickname = Text.EMPTY;
            }*/
        }
        return nickname;
    }

    /**
     * Set player's "sub tag" (below the name tag)
     * @param player player entity
     * @param subtag text (formatting characters allowed)
     */
    public static void setPlayerSubtag(Player player, String subtag) {
        Scoreboard playerScoreboard = player.getScoreboard();
        Optional<Objective> optionalObjective = playerScoreboard.getObjective(DisplaySlots.BELOW_NAME);

        if(optionalObjective.isPresent()) {
            // Make sure we are targeting our own objective on the correct display slot
            // We don't want to override other scoreboards
            if (optionalObjective.get().getName().equals(player.getName())) {
                optionalObjective.get().getOrCreateScore(TextSerializers.FORMATTING_CODE.deserialize(subtag));
            }
        } else {
            Objective objective = Objective.builder()
                .name(Text.of(subtag).toPlain())
                .displayName(TextSerializers.FORMATTING_CODE.deserialize(subtag))
                .criterion(Criteria.DUMMY)
                .objectiveDisplayMode(ObjectiveDisplayModes.INTEGER)
                .build();
            playerScoreboard.addObjective(objective);
            playerScoreboard.updateDisplaySlot(objective, DisplaySlots.BELOW_NAME);
        }

    }

    public static void setPlayerSubtag(Player player, String subtag, int score) {
        Scoreboard playerScoreboard = player.getScoreboard();
        Optional<Objective> optionalObjective = playerScoreboard.getObjective(DisplaySlots.BELOW_NAME);

        optionalObjective.ifPresent(objective -> {
            // Make sure we are targeting our own objective on the correct display slot
            // We don't want to override other scoreboards
            if(objective.getName().equals(PlayerDisplayTokens.PLUGIN_ID)) {
                objective.getOrCreateScore(TextSerializers.FORMATTING_CODE.deserialize(subtag)).setScore(score);
            }
        });
    }
}
