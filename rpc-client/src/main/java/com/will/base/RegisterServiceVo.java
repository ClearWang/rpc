package com.will.base;

import java.io.Serializable;
import java.util.Objects;

/**
 * 类说明：注册中心注册服务的实体类
 * @author clewill
 */
public class RegisterServiceVo implements Serializable {
    private final String host;
    private final int port;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterServiceVo that = (RegisterServiceVo) o;
        return port == that.port && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    public RegisterServiceVo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
