/*
 * Copyright Ningbo Qishan Technology Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mds.group.purchase.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * 高德地图工具类
 * 依赖开源项目 com.github.kevinsawicki(http-request)
 *
 * @author whh
 */
public class GeoCodeUtil {

    /**
     * 高德地图秘钥
     */
    private static final String KEY = "xxx";

    /**
     * 获取地理/逆地理编码 API
     */
    private static String ADDRESSENCODED_URL = "https://restapi.amap.com/v3/geocode/geo";

    /**
     * 距离测量API
     */
    private static String RANGEMEASUREMENT_URL = "https://restapi.amap.com/v3/distance";

    /**
     * 联级请求
     */
    private static String DISTRICT_URL = "http://restapi.amap.com/v3/config/district";
    /**
     * 联级请求
     */
    private static String REGEO_URL = "http://restapi.amap.com/v3/geocode/regeo";
    /**
     * 获取某个地点所在的坐标
     */
    private static String PLACE_TEXT = "http://restapi.amap.com/v3/place/text";


    private static Map<String, Object> params = new HashMap<>();

    static {
        params.put("key", KEY);
    }

    /**
     * 获取某个地点所在的经纬度
     *
     * @param name 指定地点
     * @return longitude and latitude
     */
    public static String getLongitudeAndLatitude(String name) {
        params.put("types", "");
        params.put("city", name);
        params.put("children", 1);
        params.put("offset", 1);
        params.put("page", 1);
        params.put("extensions", "base");
        String body = HttpUtil.get(PLACE_TEXT, params);
        params.remove("types");
        params.remove("city");
        params.remove("children");
        params.remove("offset");
        params.remove("page");
        params.remove("extensions");
        return JSON.parseObject(body).getJSONArray("pois").getJSONObject(0).getString("location");
    }

    /**
     * 获取地理/逆地理编码
     *
     * @param address 结构化地址信息
     * @param batch   是否批量查询控制
     * @return address encoded
     * @url https ://lbs.amap.com/api/webservice/guide/api/georegeo
     */
    public static String getAddressEncoded(String address, boolean batch) {
        params.put("address", address);
        params.put("batch", batch);
        String body = HttpUtil.get(ADDRESSENCODED_URL, params);
        params.remove("address");
        params.remove("batch");
        return body;
    }

    /**
     * 距离测量
     *
     * @param origins     出发点
     * @param destination 目的地
     * @param type        路径计算的方式和方法  默认1
     * @return range measurement
     * @url https ://lbs.amap.com/api/webservice/guide/api/direction
     */
    public static String getRangeMeasurement(String origins, String destination, Integer type) {
        params.put("origins", origins);
        params.put("destination", destination);
        if (type != null) {
            params.put("type", type);
        }
        String body = HttpUtil.get(RANGEMEASUREMENT_URL, params);
        params.remove("origins");
        params.remove("destination");
        return body;
    }

    /**
     * 获取联级请求
     *
     * @return district
     */
    public static String getDistrict() {
        params.put("subdistrict", 3);
        String body = HttpUtil.get(DISTRICT_URL, params);
        params.remove("subdistrict");
        return body;
    }

    /**
     * 经纬度获取地区
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param batch     batch=true为批量查询。batch=false为单点查询
     * @return area
     */
    public static String getArea(String longitude, String latitude, Boolean batch) {
        params.put("location", longitude + "," + latitude);
        params.put("batch", batch);
        String body = HttpUtil.get(REGEO_URL, params);
        params.remove("location");
        params.remove("batch");
        return body;
    }

    /**
     * 生成以中心点为中心的四方形经纬度
     *
     * @param lat    纬度
     * @param lon    精度
     * @param raidus 半径（以米为单位）
     * @return double [ ]
     */
    public static double[] getAround(double lat, double lon, int raidus) {

        Double latitude = lat;
        Double longitude = lon;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        return new double[]{minLat, minLng, maxLat, maxLng};
    }


    /**
     * Distance double.
     *
     * @param centerLocation 中心经纬度
     * @param targetLocation 需要计算的经纬度
     * @return 米 double
     */
    public static double distance(String centerLocation, String targetLocation) {

        double centerLon = Double.parseDouble(centerLocation.substring(0, centerLocation.indexOf(",")));
        double centerLat = Double.parseDouble(centerLocation.substring(1 + centerLocation.indexOf(",")));
        double targetLon = Double.parseDouble(targetLocation.substring(0, targetLocation.indexOf(",")));
        double targetLat = Double.parseDouble(targetLocation.substring(1 + targetLocation.indexOf(",")));
        // 每经度单位米;
        double jl_jd = 102834.74258026089786013677476285;
        // 每纬度单位米;
        double jl_wd = 111712.69150641055729984301412873;
        double b = Math.abs((centerLat - targetLat) * jl_jd);
        double a = Math.abs((centerLon - targetLon) * jl_wd);
        return Math.sqrt((a * a + b * b));
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
    }

}
