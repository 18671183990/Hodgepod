package com.alan.hodgepod.module.bean;

import java.util.List;

/**
 * Created by Alan on 2016/7/9.
 */
public class WeatherInfoBean {

    public int error_code;
    public String reason;
    public ResultBean result;

    public static class ResultBean {

        public DataBean data;

        public static class DataBean {

            public int isForeign;
            public LifeBean life;
            public PM25InfoBean pm25info;
            public RealtimeBean realtime;
            public List<WeatherBean> weather;

            public static class LifeBean {

                public String date;
                public InfoBean info;

                public static class InfoBean {

                    public List<String> chuanyi;
                    public List<String> ganmao;
                    public List<String> kongtiao;
                    public List<String> wuran;
                    public List<String> xiche;
                    public List<String> yundong;
                    public List<String> ziwaixian;
                }
            }

            public static class PM25InfoBean {

                public String cityName;
                public String dateTime;
                public String key;
                public PM25 pm25;
                public int show_desc;

                public static class PM25 {

                    public String curPm;
                    public String des;
                    public int level;
                    public String pm10;
                    public String pm25;
                    public String quality;
                }
            }

            public static class RealtimeBean {

                public String city_code;
                public String city_name;
                public long dataUptime;
                public String date;
                public String moon;
                public String time;

                public WeatherBean weather;
                public int week;
                public WindBean wind;


                public static class WeatherBean {

                    public String humidity;
                    public String img;
                    public String info;
                    public String temperature;
                }

                public static class WindBean {

                    public String direct;
                    public String power;
                    public String windspeed;
                }
            }

            public static class WeatherBean {

                public String date;
                public InfoBean info;
                public String nongli;
                public String week;

                public static class InfoBean {

                    public List<String> day;
                    public List<String> night;
                }
            }
        }
    }
}
