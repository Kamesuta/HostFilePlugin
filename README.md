# HostFilePlugin

このプラグインは、サーバーのファイルをHTTPで公開することができます。

## 使い方

設定ファイルに書き込み、サーバーを起動してください。

## 設定ファイル

設定ファイルは、`plugins/HostFilePlugin/config.yml`にあります。

```yaml
# ポート
port: 8080
file:
  # ここに「URI: ファイルパス」の対応を記述
  eula: eula.txt
  properties: server.properties
mime:
  # ここに「拡張子: MIMEタイプ」の対応を記述
  yml: text/yaml
  json: application/json
  txt: text/plain
```
