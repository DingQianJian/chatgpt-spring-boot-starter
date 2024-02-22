# ガイドライン

### アーティファクトの使い方

* ダウンロードしてローカルのmavenレポジトリーにインストールしてください。
* 次のコードをpomファイルに付けてください。

```
<dependency>
  <groupId>com.dqj</groupId>
  <artifactId>chatgpt-spring-boot-stater</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

* サービスを完成したら、プログラムを起動しテストしてください。

### application.yaml中の設定変更

```
gpt:
  config:
    key: xxxx
    model: gpt-4
```

* key: openAIから取得したTOKEN.
* model: ChatGptのエンジン. 以下の値を選んでください:
    - gpt-4 (default)
    - gpt-3.5-turbo
