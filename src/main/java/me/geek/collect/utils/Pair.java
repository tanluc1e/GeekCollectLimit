package me.geek.collect.utils;

import java.io.Serializable;

/**
 * GeekCollectLimit
 * me.geek.collect.api
 *
 * @author 老廖
 * @since 2023/10/3 9:09
 */
public class Pair<K, V> implements Serializable {

    public K first;
    public V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "("+ first +", "+ second + ")";
    }
}
