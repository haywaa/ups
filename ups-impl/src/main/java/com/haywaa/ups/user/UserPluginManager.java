package com.haywaa.ups.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.haywaa.ups.user.plugin.UserPlugin;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 12:31
 */
public class UserPluginManager {
     private static final Map<String, UserPlugin> pluginMap = new ConcurrentHashMap<>();

     public static void addPlugin(String channel, UserPlugin plugin) {
          pluginMap.put(channel, plugin);
     }

     public static UserPlugin getPlugin(String channel) {
          return pluginMap.get(channel);
     }
}
