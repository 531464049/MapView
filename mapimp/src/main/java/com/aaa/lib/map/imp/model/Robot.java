package com.aaa.lib.map.imp.model;


public class Robot {

    private long deviceId;
    /**
     * 机器状态：
     * <p>
     * 清扫模式
     * 状态
     * 电量
     * 水量
     */
    private RobotStatus robotStatus;

    /**
     * 清洁设置
     * <p>
     * 风力
     * 水量
     * 拖布
     * 断点续扫
     * 地毯增压
     * 勿扰 （时间段）
     * 精擦
     */
    private RobotSettings cleaningSettings;

    /**
     * 机器信息
     * <p>
     * "botName"	    string	机器名
     * "audioVolume"	int		音量
     * "battery" 		int	    电量
     * "audioBag"	    enum	语音包
     * "fanSuction"	    int		吸力
     * "compileVerions"	string	固件版本
     * "mcuVersion"		string  MCU版本
     * "uuid"			string  uuid
     * "serialNumber"	string	序列号
     * "macAddr"		string  Mac地址
     * "ipAddr"			string  IP地址
     * "connectedSsid"	string  连接热点名称
     */
    private RobotInfoBean robotInfo;

    /**
     * "cleanTime"	int	minute	当前清扫时间
     * "historyTime"			当前清扫面积
     * "cleanArea"	float	㎡	历史清扫时间
     * "historyArea"			历史清扫面积
     * "cleanCount"	int		历史清扫次数
     */
    private CleanStatistics cleanStatistics;

    /**
     * 耗材损耗情况
     * "mainBrush"	    int 主刷使用时间
     * "mainBrushLife"	int	主刷寿命
     * "sideBrush"		int	边刷使用时间
     * "sideBrushLife"	int	边刷寿命
     * "strainer"		int	滤网使用时间
     * "strainerLife"	int	滤网寿命
     * "sensor"			int 传感器使用时间
     * "sensorLife"		int	传感器寿命
     */
    private ConsumptionBean consumption;

    //地图数据
    private LDMapBean map;
    //路径数据
    private LDPathBean path;
    //禁区、分区、定点、虚拟墙等数据
    private LDAreaBean area;

    public static Robot get() {
        return InstanceHolder.robot;
    }

    private Robot() {
        this.cleaningSettings = new RobotSettings();
        this.robotInfo = new RobotInfoBean();
        this.consumption = new ConsumptionBean();
        this.map = new LDMapBean();
        this.path = new LDPathBean();
        this.area = new LDAreaBean();
        this.robotStatus = new RobotStatus();
    }

    private static class InstanceHolder {
        static final Robot robot = new Robot();
    }

    public RobotStatus getRobotStatus() {
        return robotStatus;
    }

    public RobotSettings getRobotSettings() {
        return cleaningSettings;
    }

    public RobotInfoBean getRobotInfo() {
        return robotInfo;
    }

    public ConsumptionBean getConsumption() {
        return consumption;
    }

    public LDMapBean getMapData() {
        return map;
    }

    public LDPathBean getPathData() {
        return path;
    }

    public LDAreaBean getAreaData() {
        return area;
    }

    public void setRobotStatus(RobotStatus robotStatus) {
        this.robotStatus = robotStatus;
    }


    public void setRobotSettings(RobotSettings cleaningSettings) {
        this.cleaningSettings = cleaningSettings;
    }

    public void setRobotInfo(RobotInfoBean robotInfo) {
        this.robotInfo = robotInfo;
    }

    public void setConsumption(ConsumptionBean consumption) {
        this.consumption = consumption;
    }

    public void setMap(LDMapBean map) {
        this.map = map;
    }

    public void setPath(LDPathBean path) {
        this.path = path;
    }

    public CleanStatistics getCleanStatistics() {
        return cleanStatistics;
    }

    public void setCleanStatistics(CleanStatistics cleanStatistics) {
        this.cleanStatistics = cleanStatistics;
    }

}
