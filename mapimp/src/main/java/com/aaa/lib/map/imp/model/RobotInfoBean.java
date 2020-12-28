package com.aaa.lib.map.imp.model;

public class RobotInfoBean {
    /**
     * "botName"	string		机器名
     * "audioVolume"	int		音量
     * "battery" 			电量
     * "audioBag"	enum		语音包
     * "fanSuction"	int		吸力
     * "compileVerions"	string		固件版本
     * "mcuVersion"			MCU版本
     * "uuid"			uuid
     * "serialNumber"			序列号
     * "macAddr"			Mac地址
     * "ipAddr"			IP地址
     * "connectedSsid"			连接热点名称
     */

    private String botName;
    private int audioVolume;
    private int battery;
    private int audioBag;
    private int fanSuction;
    private String compileVerions;
    private String mcuVersion;
    private String uuid;
    private String serialNumber;
    private String macAddr;
    private String ipAddr;
    private String connectedSsid;
    private int wifiSignalLevel;

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public int getAudioVolume() {
        return audioVolume;
    }

    public void setAudioVolume(int audioVolume) {
        this.audioVolume = audioVolume;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getAudioBag() {
        return audioBag;
    }

    public void setAudioBag(int audioBag) {
        this.audioBag = audioBag;
    }

    public int getFanSuction() {
        return fanSuction;
    }

    public void setFanSuction(int fanSuction) {
        this.fanSuction = fanSuction;
    }

    public String getCompileVerions() {
        return compileVerions;
    }

    public void setCompileVerions(String compileVerions) {
        this.compileVerions = compileVerions;
    }

    public String getMcuVersion() {
        return mcuVersion;
    }

    public void setMcuVersion(String mcuVersion) {
        this.mcuVersion = mcuVersion;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getConnectedSsid() {
        return connectedSsid;
    }

    public void setConnectedSsid(String connectedSsid) {
        this.connectedSsid = connectedSsid;
    }

    public int getWifiSignalLevel() {
        return wifiSignalLevel;
    }

    public void setWifiSignalLevel(int wifiSignalLevel) {
        this.wifiSignalLevel = wifiSignalLevel;
    }

    @Override
    public String toString() {
        return "{" +
                "\"botName\":\'" + botName + "\'" +
                ", \"audioVolume\":" + audioVolume +
                ", \"battery\":" + battery +
                ", \"audioBag\":" + audioBag +
                ", \"fanSuction\":" + fanSuction +
                ", \"compileVerions\":\'" + compileVerions + "\'" +
                ", \"mcuVersion\":\'" + mcuVersion + "\'" +
                ", \"uuid\":\'" + uuid + "\'" +
                ", \"serialNumber\":\'" + serialNumber + "\'" +
                ", \"macAddr\":\'" + macAddr + "\'" +
                ", \"ipAddr\":\'" + ipAddr + "\'" +
                ", \"connectedSsid\":\'" + connectedSsid + "\'" +
                ", \"wifiSignalLevel\":" + wifiSignalLevel +
                '}';
    }
}
