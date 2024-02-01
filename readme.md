# Rock-Scissors-Paper Game Server
jdk-17, gradle 8.4
## Запуск 
### 1. Скриптами sh или powershell (сборка + запуск)
- `chmod +x runServer.sh & ./runServer.sh`
- `.\runServer.ps1`
### 2. Gradle
- `./gradlew build & java -jar ./build/libs/rsp-game-server-1.0-all.jar`
- `.\gradlew build && java -jar .\build\libs\rsp-game-server-1.0-all.jar`

Можно указать порт в аргументе при запуске jar. Например

`java -jar ./build/libs/rsp-game-server-1.0-all.jar -p 8080`

или 

`java -jar ./build/libs/rsp-game-server-1.0-all.jar --port 8080`. 

Порт по дефолту - 5050

На всякий случай скомпилированный jar опубликован в релизе https://github.com/massenzio-p/rsp-multiplayer/releases/tag/no-tag

