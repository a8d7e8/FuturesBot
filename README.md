FuturesBot - 台指期當沖自動交易機器人
===========
# 請注意，交易規則不一定適合每個人，請量力而為！

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/tradingbot/FuturesBot?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
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
        1. 下載後，解壓縮
        2. 將解壓目錄 bin ，加到 Path 環境變數。 如: C:\apache-ant-1.9.4\bin
    2. 到 FuturesBot 目錄執行 ant ，就會產生好 newfutures.jar
    3. 將 newfutures.jar 和 lib 整個目錄複製到 VM 虛擬機上的 C:\ ，就完成程式改版佈署。
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

## 不想自行建置執行環境可直接下載 [VM 虛擬機映像檔](https://mega.co.nz/#!cVBFWbya!SQYisDMn7dSv-KvNUNoOR_gqKQv_udxI1LObM0fGXvk)
帳號：bot 密碼： tradingbot

匯入步驟： File -> Open ，選擇下載的 TradingBot.ova *** 記得改成 DHCP 或自家的網段 ***

![VMware Player Import](https://cloud.githubusercontent.com/assets/664465/5545133/aca0c798-8b54-11e4-9ec3-e37a00759574.png "VMware Player Import")
## 已將Java交易程式(java -jar newfutures.jar)和MQTT報價(node tradingbot.js)程式排程，亦可手動執行，先執行 C:\\run.bat 後再執行 C:\\MQTT.bat

### 其中交易規則參數，需定期 WFA 回測後參數調整，不包含在 VM 內(因需歷史資料)。可透過 Dropbox 自動更新，歡迎來信訂閱。

歡迎大家加入討論程式交易，[TradingBot 粉絲團](http://www.facebook.com/tradingbot)或是[Coco-in討論區 - TradingBot程式交易機器人](http://www.coco-in.net/forum-140-1.html)

歡迎發 Pull Request 協助修改永續發展此 TradingBot 。感謝！

若需要支援服務或教學顧問付費服務，歡迎來信！聯絡資訊：[service@tradingbot.com.tw](service@tradingbot.com.tw)
