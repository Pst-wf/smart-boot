package com.smart.model.terminal;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.smart.common.utils.StringUtil;
import lombok.Getter;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * Ssh 模型
 *
 * @author wf
 * @since 2022/5/2
 */
@Getter
public class SshModel {

    private String name;
    private String host;
    private Integer port;
    private String user;
    private String password;
    /**
     * 编码格式
     */
    private String charset;

    /**
     * 文件目录
     */
    private String fileDirs;

    /**
     * ssh 私钥
     */
    private String privateKey;

    private String connectType;

    /**
     * 不允许执行的命令
     */
    private String notAllowedCommand;

    /**
     * 允许编辑的后缀文件
     */
    private String allowEditSuffix;

    public void setName(String name) {
        this.name = name;
    }

    public void setNotAllowedCommand(String notAllowedCommand) {
        this.notAllowedCommand = notAllowedCommand;
    }

    public ConnectType connectType() {
        return EnumUtil.fromString(ConnectType.class, this.connectType, ConnectType.PASS);
    }

    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setFileDirs(String fileDirs) {
        this.fileDirs = fileDirs;
    }

    public List<String> fileDirs() {
        return StringUtil.jsonConvertArray(this.fileDirs, String.class);
    }

    public void fileDirs(List<String> fileDirs) {
        if (fileDirs != null) {
            for (int i = fileDirs.size() - 1; i >= 0; i--) {
                String s = fileDirs.get(i);
                fileDirs.set(i, FileUtil.normalize(s));
            }
            this.fileDirs = JSONArray.toJSONString(fileDirs);
        } else {
            this.fileDirs = null;
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Charset getCharsetT() {
        Charset charset;
        try {
            charset = Charset.forName(this.getCharset());
        } catch (Exception e) {
            charset = CharsetUtil.CHARSET_UTF_8;
        }
        return charset;
    }

    public List<String> allowEditSuffix() {
        return StringUtil.jsonConvertArray(this.allowEditSuffix, String.class);
    }

    public void allowEditSuffix(List<String> allowEditSuffix) {
        if (allowEditSuffix == null) {
            this.allowEditSuffix = null;
        } else {
            this.allowEditSuffix = JSONArray.toJSONString(allowEditSuffix);
        }
    }

    public void setAllowEditSuffix(String allowEditSuffix) {
        this.allowEditSuffix = allowEditSuffix;
    }

    /**
     * 检查是否包含禁止命令
     *
     * @param sshItem   实体
     * @param inputItem 输入的命令
     * @return false 存在禁止输入的命令
     */
    public static boolean checkInputItem(SshModel sshItem, String inputItem) {
        // 检查禁止执行的命令
        String notAllowedCommand = StrUtil.emptyToDefault(sshItem.getNotAllowedCommand(), StrUtil.EMPTY).toLowerCase();
        if (StrUtil.isEmpty(notAllowedCommand)) {
            return true;
        }
        List<String> split = StrUtil.split(notAllowedCommand, StrUtil.COMMA);
        inputItem = inputItem.toLowerCase();
        List<String> commands = StrUtil.split(inputItem, StrUtil.CR);
        commands.addAll(StrUtil.split(inputItem, "&"));
        for (String s : split) {
            boolean anyMatch = commands.stream().anyMatch(item -> StrUtil.startWithAny(item, s + StrUtil.SPACE, ("&" + s + StrUtil.SPACE), StrUtil.SPACE + s + StrUtil.SPACE));
            if (anyMatch) {
                return false;
            }
            anyMatch = commands.stream().anyMatch(item -> StrUtil.equals(item, s));
            if (anyMatch) {
                return false;
            }
        }
        return true;
    }

    public enum ConnectType {
        /**
         * 账号密码
         */
        PASS,
        /**
         * 密钥
         */
        PUBLIC_KEY
    }
}