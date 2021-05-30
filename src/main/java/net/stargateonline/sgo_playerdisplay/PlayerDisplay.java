package net.stargateonline.sgo_playerdisplay;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.module.nickname.NucleusNicknameService;
import net.luckperms.api.LuckPerms;
import net.stargateonline.sgo_playerdisplay.commands.CommandRefresh;
import net.stargateonline.sgo_playerdisplay.commands.ExecutorRefresh;
import net.stargateonline.sgo_playerdisplay.commands.ExecutorReload;
import net.stargateonline.sgo_playerdisplay.config.Config;
import net.stargateonline.sgo_playerdisplay.listener.GroupListener;
import net.stargateonline.sgo_playerdisplay.listener.PlayerListener;
import net.stargateonline.sgo_playerdisplay.listener.UserListener;
import net.stargateonline.sgo_playerdisplay.placeholder.PlayerDisplaynamePlaceholder;
import net.stargateonline.sgo_playerdisplay.placeholder.PlayerGroupPlaceholder;
import net.stargateonline.sgo_playerdisplay.placeholder.PrefixPlaceholder;
import net.stargateonline.sgo_playerdisplay.placeholder.SuffixPlaceholder;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.placeholder.PlaceholderParser;

import java.io.File;
import java.util.Optional;

@Plugin(
        id = PlayerDisplayTokens.PLUGIN_ID,
        name = PlayerDisplayTokens.PLUGIN_NAME,
        version = PlayerDisplayTokens.PLUGIN_VERSION,
        description = PlayerDisplayTokens.PLUGIN_DESCRIPTION,
        url = PlayerDisplayTokens.PLUGIN_WEBSITE,
        authors = {"DrJackson"},
        dependencies = {
                @Dependency(id = "luckperms", optional = false, version = "[5.3,)"),
                @Dependency(id = "nucleus", optional = true, version = "[2.3.3,)")
        }
)
public class PlayerDisplay {

    @Inject
    private Logger logger;

    private static PlayerDisplay instance;
    private static PluginContainer pluginContainer;
    private static LuckPerms luckperms;
    private boolean isNucleusPresent = true;

    @Inject
    Game game;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File configFile;

    public static PlayerDisplay getInstance() {
        return instance;
    }

    public static PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Listener //During this state, the plugin gets ready for initialization. Logger and config
    public void onPreInit(GamePreInitializationEvent preInitEvent) {
        pluginContainer = Sponge.getPluginManager().fromInstance(this).get();
        Config.buildConfig(configLoader, configFile);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        Optional<ProviderRegistration<LuckPerms>> lpProvider = Sponge.getServiceManager().getRegistration(LuckPerms.class);
        Optional<NucleusNicknameService> nucleusNicknameProvider = NucleusAPI.getNicknameService();

        if(lpProvider.isPresent()) {
            luckperms = lpProvider.get().getProvider();
        } else {
            logger.error("LuckPerms 5 is not present");
        }

        if(!nucleusNicknameProvider.isPresent()) {
            isNucleusPresent = false;
            logger.warn("Unable to load Nucleus Nickname Service");
        }

        // Register and specify child Commands
        CommandSpec commands = CommandSpec.builder()
                .child(
                        CommandSpec.builder()
                        .description(Text.of("Reload config"))
                        .permission(PlayerDisplayTokens.PLUGIN_ID + ".reload")
                        .executor(new ExecutorReload())
                        .build(),
                        "reload"
                )
                .child(
                       CommandSpec.builder()
                       .description(Text.of("Refresh TabList"))
                       .permission(PlayerDisplayTokens.PLUGIN_ID + ".refresh")
                       .executor(new ExecutorRefresh())
                       .build(),
                       "refresh"
                )
                .build();
        game.getCommandManager().register(instance, commands, "playerdisplay");

        registerEventListeners();

        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                CommandRefresh.updateAllTab();
            }
        }).intervalTicks((long)(Config.getNameUpdateInterval()*20L)).submit(instance);

        logger.info("**********************************************");
        logger.info("* SGO Player Display has successfully loaded *");
        logger.info("**********************************************");
    }

    public void registerEventListeners() {
        new UserListener(instance, luckperms);
        new GroupListener(luckperms);
        Sponge.getEventManager().registerListeners(this, new PlayerListener());
    }

    // Placeholders
    @Listener
    public void registerTokensEvent(GameRegistryEvent.Register<PlaceholderParser> event) {
        event.register(new PlayerGroupPlaceholder());
        event.register(new PrefixPlaceholder());
        event.register(new SuffixPlaceholder());
        event.register(new PlayerDisplaynamePlaceholder());
    }

    public void refreshTabList(Player player) {
        int delay = Config.getDelay();
        if(delay > 0) {
            Task.builder().execute(new Runnable() {
                @Override
                public void run() {
                    CommandRefresh.refreshOthers(player);
                    CommandRefresh.refreshSelf(player);
                }
            }).delayTicks((long)delay).submit(instance);
        } else {
            CommandRefresh.refreshOthers(player);
            CommandRefresh.refreshSelf(player);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public LuckPerms getLuckPerms() {
        return luckperms;
    }

    public boolean isNucleusAvailable() {
        return isNucleusPresent;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getLoader(){
        return configLoader;
    }
}
