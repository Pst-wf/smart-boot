package com.smart.gateway.wrapper;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 返回值输出代理类
 *
 * @author wf
 * @since 2022-08-06
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
    /**
     * 定义打印流，servlet⾥拿到后，数据通过它存到缓冲区
     */
    private final PrintWriter cachedWriter;
    /**
     * 缓冲区，⽤来存放后台数据
     */
    private final CharArrayWriter bufferedWriter;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        bufferedWriter = new CharArrayWriter();
        cachedWriter = new PrintWriter(bufferedWriter);
    }

    @Override
    public PrintWriter getWriter() {
        return cachedWriter;
    }

    public String getResult() {
        byte[] bytes = bufferedWriter.toString().getBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}