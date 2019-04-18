package com.secmask.util.tool;

import java.io.Serializable;
import java.util.Vector;

public class Serie implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Vector<Object> data;

    public Serie() {
    }

    public Serie(String name, Vector<Object> data) {
        this.name = name;
        this.data = data;
    }

    public Serie(String name, Object[] array) {
        this.name = name;
        if (array != null) {
            this.data = new Vector(array.length);

            for(int i = 0; i < array.length; ++i) {
                this.data.add(array[i]);
            }
        }

    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector<Object> getData() {
        return this.data;
    }

    public void setData(Vector<Object> data) {
        this.data = data;
    }
}
