package com.fz.Utils;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Collections;

public class MyProperties extends Properties {

    private static final long serialVersionUID = 1L;

    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    @Override
    public synchronized Object put(Object key, Object value) {
        // TODO Auto-generated method stub
        keys.add(key);
        return super.put(key, value);
    }

    @Override
    public Set<String> stringPropertyNames() {
        // TODO Auto-generated method stub
        Set<String> set = new LinkedHashSet<String>();
        for (Object obj : keys) {
            set.add((String) obj);
        }
        return set;
    }

    @Override
    public Set<Object> keySet() {
        // TODO Auto-generated method stub
        return this.keys;
    }

    public Enumeration<Object> keys() {
        return Collections.<Object>enumeration(keys);
    }

}