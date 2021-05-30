package net.stargateonline.sgo_playerdisplay.config;

import net.stargateonline.sgo_playerdisplay.PlayerDisplay;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class Config {
    public static CommentedConfigurationNode configNode;

    public static void buildConfig(ConfigurationLoader<CommentedConfigurationNode> loader, File configFile) {
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
                configNode = loader.load();
                setupConfig();
                loader.save(configNode);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                configNode = loader.load();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setupConfig() {
        configNode.getNode("InitialValue", "Prefix").setValue("");
        configNode.getNode("InitialValue", "Suffix").setValue("");
        configNode.getNode("InitialValue").setComment("If players' prefix is not set yet, plugin will use initial value instead");

        configNode.getNode("ShowPrefix").setValue("true");
        configNode.getNode("ShowSuffix").setValue("true");
        configNode.getNode("ShowDisplayName").setValue("true");
        configNode.getNode("ShowDisplayName").setComment("Allows to display a formatted Minecraft username");

        configNode.getNode("ShowNickName").setValue("true");
        configNode.getNode("ShowNickName").setComment("If true and a nickname has been specified, " +
                "it will be displayed instead of the default Minecraft username");

        configNode.getNode("UpdateInterval", "NameList").setValue(15);
        configNode.getNode("UpdateInterval", "NameList").setComment("How long, in seconds, should plugin update tablist's playernames");

        configNode.getNode("RefreshDelay").setValue(5);
        configNode.getNode("RefreshDelay").setComment("How long, in ticks, should plugin wait before refresh tablist when player join server, set to 0 to disable delay");

        configNode.getNode("ConnnectionMessages", "ShowFirstTimeMessage").setValue("true");
        configNode.getNode("ConnnectionMessages", "ShowLoginMessage").setValue("true");
        configNode.getNode("ConnnectionMessages", "ShowLogoutMessage").setValue("true");

        configNode.getNode("ConnnectionMessages", "FirstTimeMessage").setValue("Comtrya %PLAYER%! Welcome to Stargate Online");
        configNode.getNode("ConnnectionMessages", "LoginMessage").setValue("%PLAYER% steps through the wormhole");
        configNode.getNode("ConnnectionMessages", "LogoutMessage").setValue("%PLAYER% left");
    }

    public static String getInitPrefix(){
        return configNode.getNode("InitialValue", "Prefix").getString("");
    }

    public static String getInitSuffix(){
        return configNode.getNode("InitialValue", "Suffix").getString("");
    }

    public static boolean showPrefix(){
        return configNode.getNode("ShowPrefix").getBoolean(true);
    }

    public static boolean showSuffix(){
        return configNode.getNode("ShowSuffix").getBoolean(true);
    }

    public static boolean showDisplayName(){
        return configNode.getNode("ShowDisplayName").getBoolean(true);
    }

    public static boolean showNickName() {
        return configNode.getNode("ShowNickName").getBoolean(true);
    }

    public static boolean showFirstTimeMessage() {
        return configNode.getNode("ConnnectionMessages", "ShowFirstTimeMessage").getBoolean(true);
    }

    public static boolean showLoginMessage() {
        return configNode.getNode("ConnnectionMessages", "ShowLoginMessage").getBoolean(true);
    }

    public static boolean showLogoutMessage() {
        return configNode.getNode("ConnnectionMessages", "ShowLogoutMessage").getBoolean(true);
    }

    public static String getFirstTimeMessage() {
        return configNode.getNode("ConnnectionMessages", "FirstTimeMessage").getString("");
    }

    public static String getLoginMessage() {
        return configNode.getNode("ConnnectionMessages", "LoginMessage").getString("");
    }

    public static String getLogoutMessage() {
        return configNode.getNode("ConnnectionMessages", "LogoutMessage").getString("");
    }

    public static int getNameUpdateInterval(){
        return configNode.getNode("UpdateInterval", "NameList").getInt(60);
    }

    public static int getDelay(){
        return configNode.getNode("RefreshDelay").getInt();
    }

    public static void saveConfig(){
        try{
            PlayerDisplay.getInstance().getLoader().save(configNode);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void reloadConfig(){
        try{
            configNode = PlayerDisplay.getInstance().getLoader().load();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
