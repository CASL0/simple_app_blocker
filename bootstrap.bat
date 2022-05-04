@echo off
chcp 65001

echo ＊＊＊＊＊ PcapPlusPlusのライブラリを取得しています ＊＊＊＊＊
curl -SL --output pcapplusplus-android.tar.gz https://github.com/seladb/PcapPlusPlus/releases/download/v21.11/pcapplusplus-21.11-android.tar.gz
tar -xf pcapplusplus-android.tar.gz
del /q pcapplusplus-android.tar.gz
if not exist .\app\libs\ mkdir .\app\libs\
move /Y .\pcapplusplus-21.11-android .\app\libs\pcapplusplus
