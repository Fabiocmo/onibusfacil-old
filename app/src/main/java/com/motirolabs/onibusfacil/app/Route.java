package com.motirolabs.onibusfacil.app;

import java.io.Serializable;

public class Route implements Serializable {

    private static final long serialVersionUID = -6344816106482150083L;

    private int type;
    private String code;
    private String name;
    private String time;

    public Route() {

        super();

    }

    public Route(int type, String name) {

        super();

        this.type = type;
        this.code = "";
        this.name = name;
        this.time = "";

    }

    public Route(int type, String code, String name, String time) {

        super();

        this.type = type;
        this.code = code;
        this.name = name;
        this.time = time;

    }

    public int getType() {

        return type;

    }

    public void setType(int type) {

        this.type = type;

    }

    public String getCode() {

        return code;

    }

    public void setCode(String code) {

        this.code = code;

    }

    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }

    public String getTime() {

        return time;

    }

    public void setTime(String time) {

        this.time = time;

    }

}
