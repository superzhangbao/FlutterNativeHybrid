package com.xiaolan.nativeapp;

import io.flutter.plugin.common.EventChannel;
import io.flutter.view.FlutterView;

/**
 * author : zhangbao
 * date : 2020/4/27 5:02 PM
 * description :EventChannelPlugin
 * 用于数据流（event streams）的通信，持续通信，常用于Native向Dart的通信，如：
 * 手机电量变化，网络连接变化，陀螺仪，传感器等等
 *
 */
public class EventChannelPlugin implements EventChannel.StreamHandler {
    private EventChannel.EventSink eventSink;

    static EventChannelPlugin registWith(FlutterView flutterView) {
        EventChannelPlugin eventChannelPlugin = new EventChannelPlugin();
        new EventChannel(flutterView,"EventChannelPlugin").setStreamHandler(eventChannelPlugin);
        return  eventChannelPlugin;
    }

    void send(Object params) {
        if (eventSink!=null) {
            eventSink.success(params);
        }
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
    }
}
