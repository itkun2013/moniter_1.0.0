// IKonsungAidlInterface.aidl
package com.konsung.aidl;

// Declare any non-default types here with import statements

interface IKonsungAidlInterface {
    int trendValue(int param);
	void saveTrend(int param, int value);

    int getEcgConfig(int id);

    int getNibpConfig(int id);

    int getSpo2Config(int id);

    int getT1();
        // t2
    int getT2();
        // td
    int getTd();

    //耳温
    int getIrtempTrend();

    void sendNibpConfig(int id, int value);

    int getParamStatus(int param);
    byte[] getWave(int param);

    int savedTrendValue(int param);
    void saveToDb();        // 保存到数据库

    void savedWave(int param,String value);

    void setWaveNum(int num);//设置导联数

    void resetWave();

}
