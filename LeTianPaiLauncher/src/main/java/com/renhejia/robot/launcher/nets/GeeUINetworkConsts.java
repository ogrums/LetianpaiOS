package com.renhejia.robot.launcher.nets;

/**
 * Paths served by the local mock on http://10.0.2.2:8080.
 * The original production paths were removed before open source.
 * Bodies for the todo entries are stubs; see third_party_demo/APIS.md.
 */
public class GeeUINetworkConsts {

    /** Device bind status. */
    public final static String BIND_INFO = "/robot_api/v1/device/bindInfo";

    /** Calendar events. */
    public final static String CALENDAR_LIST = "/robot_api/v1/device/calendar";

    /** Countdown events. */
    public final static String COUNTDOWN_LIST = "/robot_api/v1/device/countdown";

    /** Fan accounts. */
    public final static String FANS_INFO_LIST = "/robot_api/v1/device/fans";

    /** Weather plus calendar count shown on the home screen. */
    public final static String GENERAL_INFO = "/robot_api/v1/device/general";

    /** Weather detail. */
    public final static String WEATHER_INFO = "/robot_api/v1/device/weather";

    /** Alarm list. */
    public final static String CLOCK_LIST = "/robot_api/v1/device/clock";

    /** Full robot configuration. Stub. */
    public final static String GET_ALL_CONFIG = "/robot_api/v1/device/allConfig";

    /** Status upload. Stub. */
    public final static String UPLOAD_STATUS = "/robot_api/v1/device/uploadStatus";

    /** Shared robot configuration. Stub. */
    public final static String GET_COMMON_CONFIG = "/robot_api/v1/device/commonConfig";

    /** Serial and hardcode from the MAC. Same path as LtpNetWork. */
    public final static String GET_SN_BY_MAC = "/robot_api/v1/bind/getSnByMac";

    /** Server clock. */
    public final static String GET_SERVER_TIME_STAMP = "/robot_api/v1/device/serverTime";

    /** Channel logos. */
    public static final String GET_DEVICE_CHANNELLOGO = "/robot_api/v1/device/logo";

}
