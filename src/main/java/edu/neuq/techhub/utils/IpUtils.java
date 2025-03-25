/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.neuq.techhub.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import edu.neuq.techhub.constant.GlobalConstant;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    private static final String LOCAL_IP = "127.0.0.1";

    private static final String RESOURCE_NAME = "ip2region.xdb";

    private static Searcher searcher = null;

    static {
        initSearcher();
    }

    private static void initSearcher() {
        try (InputStream inputStream = IpUtils.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            if (inputStream == null) {
                logger.error("Failed to find resource: {}", RESOURCE_NAME);
                return;
            }

            // 直接从输入流读取字节数组，无需创建临时文件
            byte[] cBuff = IoUtil.readBytes(inputStream);
            searcher = Searcher.newWithBuffer(cBuff);
            logger.info("IP region searcher initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize IP region searcher: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取当前请求
     * @return 当前HTTP请求，如果不在请求上下文中返回null
     */
    public static HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return requestAttributes != null ? requestAttributes.getRequest() : null;
        } catch (Exception e) {
            logger.debug("Failed to get current request: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前请求的IP地址
     * @return IP地址字符串
     */
    public static String getIp() {
        HttpServletRequest request = getRequest();
        return getIp(request);
    }

    /**
     * 获取当前请求的IP地址
     * @return IP地址字符串
     */
    public static String getIp(HttpServletRequest request) {
        return JakartaServletUtil.getClientIP(request);
    }

    /**
     * 使用ip2region库解析IP地址
     * @param ip IP地址
     * @return 解析后的地理位置信息
     */
    public static String getIp2region(String ip) {
        if (StringUtils.isBlank(ip)) {
            return GlobalConstant.UNKNOWN;
        }

        if (searcher == null) {
            logger.error("IP region searcher is not initialized");
            return GlobalConstant.UNKNOWN;
        }

        try {
            String ipInfo = searcher.search(ip);
            if (StringUtils.isNotBlank(ipInfo)) {
                return ipInfo.replace("|0", "").replace("0|", "");
            }
        } catch (Exception e) {
            logger.error("Failed to search IP {} in region database: {}", ip, e.getMessage());
        }

        return GlobalConstant.UNKNOWN;
    }

    /**
     * 获取访问设备信息
     * @param request HTTP请求
     * @return 用户代理信息
     */
    public static UserAgent getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
    }

    /**
     * 获取本地主机IP地址
     * @return 本地IP地址
     */
    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("Failed to get host IP: {}", e.getMessage());
            return LOCAL_IP;
        }
    }

    /**
     * 获取本地主机名
     * @return 本地主机名
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.warn("Failed to get host name: {}", e.getMessage());
            return "未知";
        }
    }

}
