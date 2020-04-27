package com.xiaolan.nativeapp;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.StringCodec;

/**
 * author : zhangbao
 * date : 2020/4/27 5:15 PM
 * description :
 */
public class BasicMessageChannelPlugin implements BasicMessageChannel.MessageHandler<String> {
    private Activity activity;
    private BasicMessageChannel<String> messageChannel;

    static BasicMessageChannelPlugin registerWith(DartExecutor flutterView,Activity activity) {
        return new BasicMessageChannelPlugin(flutterView,activity);
    }

    public BasicMessageChannelPlugin(DartExecutor flutterView,Activity activity) {
        this.activity = activity;
        this.messageChannel = new BasicMessageChannel<>(flutterView, "BasicMessageChannelPlugin", StringCodec.INSTANCE);
        //设置消息处理器，处理来自dart的消息
        messageChannel.setMessageHandler(this);
    }

    @Override
    public void onMessage(String message, BasicMessageChannel.Reply<String> reply) {
        reply.reply("Native_BasicMessageChannel收到:" + message);
        FlutterAppActivity activity = (FlutterAppActivity) this.activity;
        activity.showDartMessage(message);
        Toast.makeText(this.activity,"Native_BasicMessageChannel收到"+ message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 向dart发送消息，并接收dart的反馈
     *
     * @param message  要给dart发送的消息内容
     * @param callback 来自dart的回调信息
     */
    void send(String message, BasicMessageChannel.Reply<String> callback) {
        messageChannel.send(message,callback);
    }


}
