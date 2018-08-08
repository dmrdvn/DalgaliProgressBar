# WaveLoadingView

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-WaveLoadingView-green.svg?style=true)](https://android-arsenal.com/details/1/2908)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)  

**WaveLoadingView** - Gerçekçi bir dalga yükleme efekti sağlayan bir Android kitaplığı

## Örnek Görünüm

<img src="http://7xikfc.com1.z0.glb.clouddn.com/waveloadingview.png" alt="örnek" title="örnek" width="400" height="680" />



## Kullanım

**For a working implementation of this project see the `sample/` folder.**

### Adım 1

Kütüphaneyi bir yerel kütüphane projesi olarak ekleyin veya build.gradle'daki bağımlılığı ekleyin.

```groovy
dependencies {
    implementation 'me.itangqi.waveloadingview:library:0.3.5'
    // I have uploaded v0.3.5 on 2017-01-06, if it doesn't take effect or your
    // gradle cannot find it in maven central, you may try v0.3.4.
}
```	
veya

Kütüphaneyi içe aktarıp, daha sonra /settings.gradle ve /app/build.gradle adresine ekleyin.. Bunu nasıl yapacağınızı bilmiyorsanız, mail adresim üzerinden bana ulaşabilirsiniz.

### Adım 2

Planınıza WaveLoadingView widget'ını dahil edin. Ve aşağıdaki satırları özelleştirebilirsiniz.
   
```xml
<me.itangqi.waveloadingview.WaveLoadingView
    android:id="@+id/waveLoadingView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:wlv_borderColor="@color/colorAccent"
    app:wlv_borderWidth="3dp"
    app:wlv_progressValue="40"
    app:wlv_shapeType="circle"
    app:wlv_round_rectangle="true"
    app:wlv_triangle_direction="north"
    app:wlv_titleCenterStrokeColor="@android:color/holo_blue_dark"
    app:wlv_titleCenterStrokeWidth="3dp"
    app:wlv_titleCenter="Center Title"
    app:wlv_titleCenterColor="@android:color/white"
    app:wlv_titleCenterSize="24sp"
    app:wlv_waveAmplitude="70"
    app:wlv_waveColor="@color/colorAccent"/>   
```

### Adım 3

Aktivitenizde setOnCheckedChangeListener, onProgressChanged vb. gibi geri çağrılara bazı animasyon kodları yazabilirsiniz.


```java
    WaveLoadingView mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
    mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
    mWaveLoadingView.setTopTitle("Top Title");
    mWaveLoadingView.setCenterTitleColor(Color.GRAY);
    mWaveLoadingView.setBottomTitleSize(18);
    mWaveLoadingView.setProgressValue(80);
    mWaveLoadingView.setBorderWidth(10);
    mWaveLoadingView.setAmplitudeRatio(60);
    mWaveLoadingView.setWaveColor(Color.GRAY);
    mWaveLoadingView.setBorderColor(Color.GRAY);
    mWaveLoadingView.setTopTitleStrokeColor(Color.BLUE);
    mWaveLoadingView.setTopTitleStrokeWidth(3);
    mWaveLoadingView.setAnimDuration(3000);
    mWaveLoadingView.pauseAnimation();
    mWaveLoadingView.resumeAnimation();
    mWaveLoadingView.cancelAnimation();
    mWaveLoadingView.startAnimation();
```

## Özelleştirme

Lütfen çekinmeyin :)

|isim|format|açıklama|
|:---:|:---:|:---:|
| wlv_borderWidth | dimension |Kenarlık kalınlığı, varsayılan 0
| wlv_borderColor | color | Kenarlık rengi
| wlv_progressValue | integer | Progress değeri, varsayılan 50
| wlv_shapeType | enum | Şekil türü, varsayılan daire
| wlv_triangle_direction | enum | Üçgen yönü, varsayılan kuzey
| wlv_round_rectangle | boolean | Yuvarlak dikdörtgen mi, varsayılan false
| wlv_round_rectangle_x_and_y | integer | Yuvarlak Dikdörtgenin köşeleri, varsayılan 30
| wlv_waveColor | color | Dalga efekti rengi
| wlv_wave_background_Color | color | Dalga efekti arka plan rengi
| wlv_waveAmplitude | float | Dalga yoğunluğu
| wlv_titleTop | string | Üst başlık içeriği, varsayılan null
| wlv_titleCenter | string | Merkez başlık içeriği, varsayılan null
| wlv_titleBottom | string | Alt başlık içeriği, varsayılan null
| wlv_titleTopSize | dimension | Üst başlık boyutu, varsayılan 18 
| wlv_titleCenterSize | dimension | Orta başlık boyutu, varsayılan 22
| wlv_titleBottomSize | dimension | Alt başlık boyutu, varsayılan 18
| wlv_titleTopColor | color | Üst başlık rengi
| wlv_titleCenterColor | color | Orta başlık rengi 
| wlv_titleBottomColor | color | Alt başlık rengi
| wlv_titleTopStrokeColor | color | Üst başlık kontur rengi 
| wlv_titleCenterStrokeColor | color | Merkez başlık kontur rengi 
| wlv_titleBottomStrokeColor | color | Alt başlık kontur rengi
| wlv_titleTopStrokeWidth | dimension | Üst başlık kontur genişliği 
| wlv_titleCenterStrokeWidth | dimension | Merkez başlık strok genişliği 
| wlv_titleBottomStrokeWidth | dimension | Alt başlık kontur genişliği  


**Tüm özelliklerin, çalışma zamanında bunları değiştirmek için kendi alıcıları ve ayarlayıcıları vardır.**


## Change Log

### 0.3.5（2017-01-06）

#### Update:

- Support change the frequency of the waves. [#13](https://github.com/tangqi92/WaveLoadingView/issues/13) / [#23](https://github.com/tangqi92/WaveLoadingView/issues/23)

### 0.3.4（2017-01-05）

#### Update:

- Support pause/resume wave. [#28](https://github.com/tangqi92/WaveLoadingView/issues/28) / [#26](https://github.com/tangqi92/WaveLoadingView/issues/26)

#### Plan:

- Support change the frequency of the waves


### 0.3.3（2016-10-14）

### 0.3.2（2016-07-29）

#### Update:

- Support change the background of waveview [#18](https://github.com/tangqi92/WaveLoadingView/issues/18). [by GreatGarlic](https://github.com/GreatGarlic)

### 0.3.1（2016-07-23）

#### Update:

- Added stroke feature to all titles. Strokes are disabled by default. [by shayanzoro](https://github.com/shayanzoro)

### 0.3.0 (2016-06-07)

#### Fixed bugs:

- How to set match_parent width? [#12](https://github.com/tangqi92/WaveLoadingView/issues/12)

### 0.2.3（2016-05-25）

#### Update:

- Support the `minSdkVersion` to 14 

### 0.2.2（2016-05-23）

#### Fixed bugs:

- Default amplitude value [#12](https://github.com/tangqi92/WaveLoadingView/issues/12)

### 0.2.1 (2016-05-21)

#### Update:

- Update more shape types like: Rectangle, Round Rectangle, Triangle...etc [#7](https://github.com/tangqi92/WaveLoadingView/issues/7)
- Update `build.gradle`
- Update Sample

#### Plan:

- Title position fit shape size

### 0.2.0 (2016-02-17)

#### Implemented enhancements:

- Prefix the attributes with "wlv"

#### Fixed bugs:

- setProgressValue() increase doesn't conform to logic [#8](https://github.com/tangqi92/WaveLoadingView/issues/8)

#### Update:

- Update `build.gradle`
- Update Sample

### 0.1.5 (2016-01-14)

#### Fixed bugs:

- IllegalArgumentException: width and height must be > 0 while loading Bitmap from View [#6](https://github.com/tangqi92/WaveLoadingView/issues/6)

### 0.1.4 (2015-12-17)

#### Fixed bugs:

- setProgressValue() doesn't change the value of mProgressValue [#4](https://github.com/tangqi92/WaveLoadingView/issues/4)


### 0.1.3

#### Fixed bugs:

- Attribute "borderWidth" has already been defined [#2](https://github.com/tangqi92/WaveLoadingView/issues/2)


## Demo

[Download](https://github.com/tangqi92/WaveLoadingView/releases/download/v0.2.1/sample-release-unsigned.apk)

## Apps using the WaveLoadingView

Feel free to send me new projects

- [Easy Fit Calorie Counter](https://play.google.com/store/apps/details?id=com.marioherzberg.swipeviews_tutorial1)
- [Kebthung](https://play.google.com/store/apps/details?id=skesw12.kebthung)


## Community

Looking for contributors, feel free to fork !

Tell me if you're using my library in your application, I'll share it in this README.


## Thanks

Inspired by 

- [WaveView](https://github.com/gelitenight/WaveView) created by [gelitenight](https://github.com/gelitenight)
- [CircularFillableLoaders](https://github.com/lopspower/CircularFillableLoaders) created by [lopspower](https://github.com/lopspower)


## Contact Me

Born in 1992, now a student of Southeast University, master of Software Engineering. Loving technology, programming, reading and sports.

I will graduate in June 2017, expect the internship or full-time job in Android or iOS.

If you have any questions or want to make friends with me, please feel free to contact me : [imtangqi#gmail.com](mailto:imtangqi@gmail.com "Welcome to contact me")


## License

    Copyright 2016 Qi Tang

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

