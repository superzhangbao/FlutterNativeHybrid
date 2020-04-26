package com.xiaolan.nativeapp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn->{
                // 通过FlutterView引入Flutter编写的页面
                val flutterView = FlutterView(this)
                val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                val flContainer: FrameLayout = findViewById(R.id.fl_container)
                flContainer.addView(flutterView, lp)
                // 关键代码，将Flutter页面显示到FlutterView中
                val flutterEngine = FlutterEngine(this)
                flutterEngine.dartExecutor.executeDartEntrypoint(
                        DartExecutor.DartEntrypoint.createDefault()
                )
                flutterEngine.navigationChannel.setInitialRoute("route1")
                flutterView.attachToFlutterEngine(flutterEngine)
            }
        }
    }
}
