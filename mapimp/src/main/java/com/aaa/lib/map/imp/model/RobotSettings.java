package com.aaa.lib.map.imp.model;

public class RobotSettings {
    /**
     * 清洁设置
     * <p>
     * 吸力
     * 水速
     * 拖布
     * 断点续扫
     * 地毯增压
     * 勿扰 （时间段）
     * 精擦
     * 音量
     * 语音
     * 是否正在定位机器
     * 自动集尘
     */
    private int suction;
    private int waterSpeed;
    private boolean hasMop;
    private boolean breakpointResume;
    private boolean CarpetPressurize;
    private boolean doNotDisturb;
    private boolean fineBrush;
    private int volume;
    private String voiceType;
    private boolean searchRobot;
    private boolean autoDust;

    public int getSuction() {
        return suction;
    }

    public void setSuction(int suction) {
        this.suction = suction;
    }

    public int getWaterSpeed() {
        return waterSpeed;
    }

    public void setWaterSpeed(int waterSpeed) {
        this.waterSpeed = waterSpeed;
    }

    public boolean hasMop() {
        return hasMop;
    }

    public void setHasMop(boolean hasMop) {
        this.hasMop = hasMop;
    }

    public boolean isBreakpointResume() {
        return breakpointResume;
    }

    public void setBreakpointResume(boolean breakpointResume) {
        this.breakpointResume = breakpointResume;
    }

    public boolean isCarpetPressurize() {
        return CarpetPressurize;
    }

    public void setCarpetPressurize(boolean carpetPressurize) {
        CarpetPressurize = carpetPressurize;
    }

    public boolean isDoNotDisturb() {
        return doNotDisturb;
    }

    public void setDoNotDisturb(boolean doNotDisturb) {
        this.doNotDisturb = doNotDisturb;
    }

    public boolean isFineBrush() {
        return fineBrush;
    }

    public void setFineBrush(boolean fineBrush) {
        this.fineBrush = fineBrush;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

    public boolean isSearchRobot() {
        return searchRobot;
    }

    public void setSearchRobot(boolean searchRobot) {
        this.searchRobot = searchRobot;
    }

    public boolean isAutoDust() {
        return autoDust;
    }

    public void setAutoDust(boolean autoDust) {
        this.autoDust = autoDust;
    }
}
