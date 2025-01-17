package com.smart.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.smart.common.utils.IPUtil;
import com.smart.common.utils.StringUtil;
import com.smart.gateway.print.PrintWriter;
import com.smart.common.gateway.provider.AuthProvider;
import com.smart.gateway.wrapper.RequestWrapper;
import com.smart.gateway.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.smart.common.constant.SmartConstant.DRUID;
import static com.smart.common.constant.SmartConstant.MULTIPART_FORM_DATA;
import static com.smart.gateway.constant.GatewayConstant.*;


/**
 * 过滤器
 *
 * @author wf
 * @since 2022-08-06
 */
@Order(1)
@Component
@WebFilter(urlPatterns = "/*", filterName = "smartFilter")
@Slf4j
public class SmartFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        cors(httpServletResponse);
        if (httpServletRequest.getContentType() != null && httpServletRequest.getContentType().contains(MULTIPART_FORM_DATA)) {
            // 文件上传
            chain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
            ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);
            String uri = httpServletRequest.getRequestURI();
            if (!StringUtil.containsAny(uri, AuthProvider.getSkipPrintUrl())) {
                print(requestWrapper);
            }
            //转换成代理类
            chain.doFilter(requestWrapper, responseWrapper);
            String result = responseWrapper.getResult();
            //后台数据编辑后，通过真正的response写到前台页⾯去
            if (uri.contains(DRUID)) {
                //判断是否有值
                if (StringUtil.isNotBlank(result)) {
                    result = formatDruid(result);
                }
            }
            response.getOutputStream().write(result.getBytes());
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * 打印请求
     *
     * @param request 请求
     */
    private synchronized void print(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String ip = IPUtil.getIpAddress(request);
        String method = request.getMethod();
        String query = request.getQueryString();
        if (StringUtil.isNotBlank(query)) {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            String[] arr = query.split("&");
            List<String> list = Arrays.asList(arr);
            List<String> format = new ArrayList<>();
            for (String param : list) {
                String[] item = param.split("=");
                if (item.length == 2) {
                    format.add("\"" + item[0] + "\":\"" + item[1] + "\"");
                }
            }
            builder.append(StringUtil.join(format, ","));
            builder.append("}");
            query = builder.toString();
        } else {
            query = "";
        }
        String data = getBody(request);
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));

        Map<String, String> map = new LinkedHashMap<>(0);
        map.put(URL_PREFIX, url);
        map.put(IP_PREFIX, ip);
        map.put(METHOD_PREFIX, method);
        map.put(QUERY_PREFIX, query);
        map.put(DATA_PREFIX, data);
        map.put(TIME_PREFIX, time);
        if (!isSkip(url)) {
            String token = request.getHeader("token");
            if (StringUtil.isNotBlank(token)) {
                JSONObject user = JSON.parseObject(JWT.decode(token).getClaim("user").asString());
                map.put(USER_PREFIX, user.getString("userId"));
            }
        }
        PrintWriter.print(PrintWriter.RED, BORDER_TOP, false, false);
        map.keySet().forEach(key -> {
            printMiddle(key, map.get(key));
        });
        PrintWriter.print(PrintWriter.RED, BORDER_BOTTOM, true, false);
    }

    private void printMiddle(String prefix, String msg) {
        PrintWriter.print(PrintWriter.BLUE, "        " + prefix, false, true);
        System.out.println(msg);
    }

    private String getBody(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            br = request.getReader();
            String str;
            StringBuilder wholeStr = new StringBuilder();
            while ((str = br.readLine()) != null) {
                wholeStr.append(str);
            }
            return wholeStr.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理druid数据
     *
     * @param body 页面body
     * @return String
     */
    private String formatDruid(String body) {
        if (body.contains(".footer {")) {
            body = body.replace(".footer {", ".footer { display:none;");
        }
        if (body.contains("console.log(")) {
            body = body.replace("console.log(", "// console.log(");
        }
        if (body.contains("alert alert-error clearfix")) {
            body = body.replace("alert alert-error clearfix", "");
            body = body.replace("(*) property for user to setup", "");
        }
        return body;
    }

    /**
     * 解决跨域
     *
     * @param response 响应
     */
    private void cors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }


    /**
     * 验证是否跳过鉴权
     *
     * @param path 需验证的路由
     */
    private boolean isSkip(String path) {
        return AuthProvider.getAllSkipUrl().stream().map(url -> url.replace(AuthProvider.TARGET, AuthProvider.REPLACEMENT)).anyMatch(path::contains);
    }
}
