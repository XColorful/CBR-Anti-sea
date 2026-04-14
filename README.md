# 自定义大逃杀防刷海 | CBR Anti-sea  
  
[中文](#自定义大逃杀防刷海) | [English](#cbr-anti-sea)

# 自定义大逃杀防刷海

😎[wiki](https://github.com/XColorful/CBR-Anti-sea/wiki) | 📄[docs](https://github.com/XColorful/CBR-Anti-sea/tree/HEAD/docs)

本模组为[自定义大逃杀](https://github.com/XColorful/BattleRoyale)特定场景的功能补丁。

`该模组需要安装在服务端`

## 功能补丁

修复特定地图下第一个区域中心刷在海洋的问题：
- 默认情况下，[全局偏移](https://github.com/XColorful/BattleRoyale/wiki/Game-command#全局偏移)在 _(0, 0)_ 或 _(0, -8192)_ 时触发
- 将 _256x256_ 的掩码图片映射到 _8192x8192_

|地图1 _(0, 0)_|地图2 _(0, -8192)_|
|---|---|
|![地图1掩码图](pic/map1_256x256.png)|![地图2掩码图](pic/map2_256x256.png)|

# CBR Anti-sea

😎[wiki](https://github.com/XColorful/CBR-Anti-sea/wiki#English) | 📄[docs](https://github.com/XColorful/CBR-Anti-sea/tree/HEAD/docs)

This mod serves as a functional patch for [Custom BattleRoyale](https://github.com/XColorful/BattleRoyale) in specific scenarios.

`This mod needs to be installed on the server.`

## Feature patch

Resolves issues where the first zone center spawns in the ocean under specific maps:
- Triggered when [Global offset](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Global-offset) is at _(0, 0)_ or _(0, -8192)_ by default
- Maps a _256x256_ mask image to an _8192x8192_ area

|Map 1 _(0, 0)_|Map 2 _(0, -8192)_|
|---|---|
|![Map 1 mask image](pic/map1_256x256.png)|![Map 2 mask image](pic/map2_256x256.png)|