FuturesBot - 台指期當沖自動交易機器人
===========
作者：[Philipz](http://blog.everfine.com.tw/)

網站：[TradingBot 開放原始碼程式交易系統](http://www.tradingbot.com.tw/)、[Facebook粉絲團](http://www.facebook.com/tradingbot)

軟體授權：Apache License, Version 2.0，請見license.txt

1. 使用環境建置

    1. 安裝 [Java Runtime Environment](https://java.com/zh_TW/download/manual_java7.jsp)
    2. 安裝 [NodeJS](http://nodejs.org/download/)
    3. 安裝接收報價必要 library
    ```
    npm install net mqtt
    ```
    
2. 使用方法 - 修改程式包裝成 Jar 檔

    1. 安裝 [Ant build](http://ant.apache.org/)
    2. 到 FuturesBot 目錄執行 ant ，就會產生好 newfutures.jar
    3. 複製 newfutures.jar 到 VM 虛擬機上的 C:\ ，就完成程式改版佈署。
    4. 先執行 java -jar newfutures.jar
    5. 再啟動報價，執行 node tradingbot.js ，即可開始程式交易。

3. 下單機

    1. 建議使用下單大師，[http://moneyprinter.pixnet.net/blog](http://moneyprinter.pixnet.net/blog)
    2. 或者，請參閱[程式交易經驗分享系列(4) - 下單機設定及系列回顧](https://blog.everfine.com.tw/4/)

4. 程式簡易說明

    1. 主要接受TCP Socket程式為SocketServer.java
    2. 策略邏輯為NewDdeClient.java
    3. 目前設定需配合Dropbox使用，亦可自行修改不使用
    4. GetWednesday.java是檢查每個月台指期和摩台期結算日
    5. 請自行設定排程時間，於每日早上八點四十五分之前執行

## 不想自行建置執行環境可直接下載 [VM 虛擬機映像檔](https://mega.co.nz/#!VFgEmCTT!5XUpUyg9u64uLcmqZPm15AtRW9aEur0parr2de0vKiA)
匯入步驟： File -> Open ，選擇下載的 TradingBot.ova
![VMware Player Import](https://lh3.googleusercontent.com/DEoWF6k0oVYfLUED27xw0W8H7ZDRJhufOEKb22JZgN_-yt5zGa3Qten1liBUPL5JKujPQd6iVYc=w1342-h523 "VMware Player Import")

### 其中交易規則參數，需定期 WFA 回測後參數調整，不包含在 VM 內(因需歷史資料)。可透過 Dropbox 自動更新，歡迎來信訂閱。

歡迎大家加入討論程式交易，[TradingBot 粉絲團](http://www.facebook.com/tradingbot)或是[Coco-in討論區 - TradingBot程式交易機器人](http://www.coco-in.net/forum-140-1.html)

歡迎發 Pull Request 協助修改永續發展此 TradingBot 。感謝！

若需要支援服務或教學顧問付費服務，歡迎來信！聯絡資訊：[service@tradingbot.com.tw](service@tradingbot.com.tw)
