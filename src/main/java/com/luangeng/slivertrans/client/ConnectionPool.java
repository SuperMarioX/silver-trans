package com.luangeng.slivertrans.client;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionPool {

    private static Map<String, List<Channel>> map = new HashMap();
}
