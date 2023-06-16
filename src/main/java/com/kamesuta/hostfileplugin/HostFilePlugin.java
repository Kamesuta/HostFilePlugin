package com.kamesuta.hostfileplugin;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * ホストファイルプラグイン
 */
public final class HostFilePlugin extends JavaPlugin {
    /** インスタンス */
    public static HostFilePlugin instance;

    /** ロガー */
    public static Logger logger;

    /** HTTPサーバー */
    private HostHttpServer server;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        logger = getLogger();

        // コンフィグ読み込み
        saveDefaultConfig();
        Configuration config = getConfig();
        int port = config.getInt("port", 8080);

        // ファイルのマッピングを読み込み
        ConfigurationSection fileConfig = config.getConfigurationSection("file");
        // ファイルのマッピングが存在しない場合はプラグインを無効化
        if (fileConfig == null) {
            logger.warning("コンフィグが不正です。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // ファイルのマッピングを読み込み
        Map<String, File> fileMap = new HashMap<>();
        // ファイルのマッピングを読み込み
        for (String key : fileConfig.getKeys(false)) {
            String path = config.getString("file." + key);
            if (path == null || path.isEmpty()) {
                logger.warning("ファイルマップの読み込みに失敗: " + key);
                continue;
            }
            File file = new File(path);
            // 追加
            fileMap.put(key, file);
        }

        // MIMEのマッピングを読み込み
        ConfigurationSection mimeConfig = config.getConfigurationSection("mime");
        // MIMEのマッピングが存在しない場合はプラグインを無効化
        if (mimeConfig == null) {
            logger.warning("コンフィグが不正です。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // MIMEのマッピングを読み込み
        Map<String, String> mimeMap = new HashMap<>();
        // MIMEのマッピングを読み込み
        for (String key : mimeConfig.getKeys(false)) {
            String mime = config.getString("mime." + key);
            if (mime == null || mime.isEmpty()) {
                logger.warning("MIMEマップの読み込みに失敗: " + key);
                continue;
            }
            // 追加
            mimeMap.put(key, mime);
        }

        try {
            server = new HostHttpServer(port, fileMap, mimeMap);
            server.start();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (server != null) {
            server.stop();
        }
    }
}
