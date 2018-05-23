package com.luangeng.slivertrans.http.interceptor;

import java.util.LinkedList;
import java.util.List;

public class InterceptorChain {

    private List<Interceptor> list = new LinkedList<Interceptor>();

    public void add(Interceptor i) {
        list.add(i);
    }

    public void work() {
        for (Interceptor interceptor : list) {
            try {
                interceptor.intercept();
            } catch (Exception e) {

            }
        }
    }
}
