package com.kamesuta.hostfileplugin;

import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Level;

/**
 * HTTPサーバー
 */
public class HostHttpServer extends NanoHTTPD {
    /**
     * URI→ファイルのマッピング
     */
    private final Map<String, File> fileMap;

    /**
     * 拡張子→MIMEのマッピング
     */
    private final Map<String, String> mimeMap;

    public HostHttpServer(int port, Map<String, File> fileMap, Map<String, String> mimeMap) {
        super(port);
        this.fileMap = fileMap;
        this.mimeMap = mimeMap;
    }

    @Override
    public Response serve(IHTTPSession session) {
        // リクエストされたURIを取得
        String uri = session.getUri().substring(1);
        // 名前から対応するファイル名を取得
        File file = fileMap.get(uri);
        // ファイルが存在しない場合は404を返す
        if (file == null || !file.exists()) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found");
        }
        // 拡張子からMIMEを取得
        String mime = mimeMap.get(uri.substring(uri.lastIndexOf('.') + 1));
        // MIMEが存在しない場合はtext/plainを使用
        if (mime == null) {
            mime = MIME_PLAINTEXT;
        }
        // ファイルを返す
        try {
            return newChunkedResponse(Response.Status.OK, mime, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // ログを出力
            HostFilePlugin.logger.log(Level.WARNING, "ファイルが見つかりませんでした。", e);
            // 500を返す
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "500 Internal Server Error");
        }
    }
}
