package com.smart.gateway.wrapper;

import jodd.io.StreamUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Request 操作类
 *
 * @author wf
 * @since 2022-08-06
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    /**
     * 用于保存读取body中数据
     */
    private byte[] body;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        //读取请求的数据保存到本类当中
        body = StreamUtil.readBytes(request.getReader(), "UTF-8");
    }

    /**
     * 覆盖（重写）父类的方法
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 覆盖（重写）父类的方法
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 获取body中的数据
     *
     * @return
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * 把处理后的参数放到body里面
     *
     * @param body
     */
    public void setBody(byte[] body) {
        this.body = body;
    }
}