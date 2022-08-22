#!/bin/sh

echo ＊＊＊＊＊ PcapPlusPlusのライブラリを取得しています ＊＊＊＊＊
curl -SL --output pcapplusplus-android.tar.gz https://github.com/seladb/PcapPlusPlus/releases/download/v21.11/pcapplusplus-21.11-android.tar.gz
tar -xf pcapplusplus-android.tar.gz
rm -f pcapplusplus-android.tar.gz
if [ ! -d ./app/libs ]; then mkdir -p ./app/libs; fi
mv -f ./pcapplusplus-21.11-android ./app/libs/pcapplusplus