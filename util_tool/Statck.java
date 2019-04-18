package com.secmask.web.common.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;

/**
 * 简易栈工具类
 * @author wd
 * @Program DBMaskerServer
 * @create 2018-09-28 17:47
 */
public class Statck<E extends Object> {
    private int poolSize;
    private LinkedList<E> pool ;

    //初始化无限栈
    public Statck() {
        pool = new LinkedList<>();
    }

    //初始化有限栈
    public Statck(int poolSize) {
        this.poolSize = poolSize;
        this.pool = new LinkedList<>();
    }

    public void clear() {
        pool.clear();
    }

    public boolean isEmpty() {
        return pool.isEmpty();
    }

    /**
     * 获取栈顶元素
     * */
    public E getTopObjcet() {
        if (isEmpty()) {return null;}
        return pool.get(0);
    }

    /**
     * 弹出栈操作
     * */
    public E pop() {
        if (isEmpty()) {throw new EmptyStackException();}
        return pool.pop();
    }

    /**
     * 压入栈
     * */
    public void push(E e) {
        if (isEmpty()) {throw new EmptyStackException();}
        pool.push(e);
    }

    /**
     * 插入栈
     * @param e
     */
    public void add(E e) {
        //如果为有限栈，则先弹出，再插入
        if (poolSize != 0 && getStatckSize()>= poolSize) {
            pop();
        }
        pool.add(e);
    }

    /**
     * 获取当前栈大小
     * */
    public int getStatckSize() {
        //if (isEmpty()) {throw new EmptyStackException();}
        return pool.size();
    }

    /**
     * 获取栈内数据
     */
    public List getData() {
        return pool;
    }

}