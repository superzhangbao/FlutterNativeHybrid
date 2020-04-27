package com.xiaolan.nativeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.BasicMessageChannel;

public class FlutterAppActivity extends AppCompatActivity implements View.OnClickListener, BasicMessageChannel.Reply<String> {
    public final static String INIT_PARAMS = "initParams";
    private BasicMessageChannelPlugin basicMessageChannelPlugin;
    private EventChannelPlugin eventChannelPlugin;

    Activity activity;
    TextView textShow;
    EditText input;
    String title;
    boolean useEventChannel;

    public static void start(Context context, String initParams) {
        Intent intent = new Intent(context, FlutterAppActivity.class);
        intent.putExtra(INIT_PARAMS, initParams);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flutter);
        String initParams = getIntent().getStringExtra(INIT_PARAMS);
        // 已经没有这个API了
//        FlutterView flutterView = Flutter.createView(this, getLifecycle(), initParams);
        // 通过FlutterView引入Flutter编写的页面
        FlutterView flutterView = new FlutterView(this);
        FrameLayout.LayoutParams layout =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout flutter = findViewById(R.id.fl_flutter);
        flutter.addView(flutterView,layout);
        FlutterEngine flutterEngine = new FlutterEngine(this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        // 关键代码，将Flutter页面显示到FlutterView中
        flutterEngine.getNavigationChannel().setInitialRoute(initParams != null ? initParams : "null");
        flutterView.attachToFlutterEngine(flutterEngine);

        eventChannelPlugin = EventChannelPlugin.registerWith(flutterEngine.getDartExecutor());
        //注册Flutter plugin
        MethodChannelPlugin.registerWith(flutterEngine.getDartExecutor(),this);
        basicMessageChannelPlugin = BasicMessageChannelPlugin.registerWith(flutterEngine.getDartExecutor(),this);
//        uiPresenter = new UIPresenter(this, "通信与混合开发", this);

        textShow = findViewById(R.id.textShow);
        input = findViewById(R.id.input);
        TextView titleView = findViewById(R.id.title);
        titleView.setText(title);
        findViewById(R.id.btnSend).setOnClickListener(this);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.mode_basic_message_channel) {
                useEventChannel = false;
            } else if (checkedId == R.id.mode_event_channel) {
                useEventChannel = true;
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendMessage(charSequence.toString(),useEventChannel);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void sendMessage(String message, boolean useEventChannel) {
        if (useEventChannel) {
            eventChannelPlugin.send(message);
        } else {
            basicMessageChannelPlugin.send(message, this);
        }
    }

    public void showDartMessage(String message) {
        textShow.setText("收到Dart消息:" + message);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSend) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            //隐藏软键盘
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

            String data = input.getText().toString();
            //            sendEvent(data);
        }
    }

    @Override
    public void reply(String reply) {
        showDartMessage(reply);
    }
}
