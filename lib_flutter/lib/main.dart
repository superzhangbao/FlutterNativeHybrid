import 'dart:async';

import 'package:flutter/material.dart';
import 'dart:ui';

import 'package:flutter/services.dart';

void main() => runApp(MyApp(initParams: window.defaultRouteName));

class MyApp extends StatelessWidget {
  final String initParams;

  const MyApp({Key key, this.initParams}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter 混合开发',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter 混合开发',initParams:initParams),
    );
  }
}

class MyHomePage extends StatefulWidget {

  const MyHomePage({Key key, this.initParams, this.title}) : super(key: key);

  final String initParams;
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const EventChannel _eventChannel = EventChannel("EventChannelPlugin");
  static const MethodChannel _methodChannel =
      MethodChannel("MethodChannelPlugin");
  static const BasicMessageChannel<String> _basicMessageChannel =
      const BasicMessageChannel("BasicMessageChannelPlugin", StringCodec());
  String showMessage = "";
  StreamSubscription _streamSubscription;
  bool _isMethodChannel = false;

  @override
  void initState() {
    _streamSubscription = _eventChannel
        .receiveBroadcastStream("123")
        .listen(_onToDart, onError: _onToDartError);
    //使用BasicMessageChannel接受来自native的消息，并向native回复
    _basicMessageChannel
        .setMessageHandler((String message) => Future<String>(() {
              setState(() {
                showMessage = "BasicMessageChannel:" + message;
              });
              return "收到Native消息：" + message;
            }));
    super.initState();
  }

  @override
  void dispose() {
    _streamSubscription.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Container(
        alignment: Alignment.topCenter,
        decoration: BoxDecoration(color: Colors.lightBlueAccent),
        margin: EdgeInsets.only(top: 70),
        child: Column(
          children: <Widget>[
            SwitchListTile(
                value: _isMethodChannel,
                onChanged: _onChangeChannel,
                title: Text(_isMethodChannel?"MethodChannelPlugin" : "BasicMessageChannelPlugin"),
            ),
            TextField(
              onChanged: _onTextChange,
              decoration: InputDecoration(
                focusedBorder: UnderlineInputBorder(
                  borderSide: BorderSide(color: Colors.white)
                ),
                enabledBorder: UnderlineInputBorder(
                    borderSide: BorderSide(color: Colors.white)
                ),
              ),
            ),
            Text("收到初始化参数initParams:${widget.initParams}"),
            Text("Native传来的数据:$showMessage"),
          ],
        ),
      ),
    );
  }

  void _onToDart(message) {
    setState(() {
      showMessage = message;
    });
  }

  void _onToDartError(error) {
    print(error);
  }

  void _onChangeChannel(bool value){
    setState(() {
      _isMethodChannel = value;
    });
  }

  void _onTextChange(value) async {
    String response;
    try {
      if(_isMethodChannel) {
            //使用MessageChannel接受来自native的消息，并向native回复
            response = await _methodChannel.invokeMethod("send",value);
          }else{
        response = await _basicMessageChannel.send(value);
      }
    } on PlatformException catch (e) {
      print(e);
    }
  }
}
