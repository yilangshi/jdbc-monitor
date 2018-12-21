package org.jdbc.monitor.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 简单实现有界队列：当队列超过最大长度时，舍弃最先插入的元素
 * @author: shi rui
 * @create: 2018-12-21 16:31
 */
public class LRUList<E> extends ArrayList<E> implements Serializable{

    private final int maxSize;

    public LRUList(int maxSize){
        this.maxSize = maxSize;
    }


    @Override
    public boolean add(E e){
        synchronized (this) {
            if (this.size() >= maxSize) {
                this.remove(0);
            }
            return super.add(e);
        }
    }

}
