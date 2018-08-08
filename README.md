# Dalgalı Progress Bar - Wave Loading View


[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)  

**WaveLoadingView** - Gerçekçi bir dalga yükleme efekti sağlayan bir Android kitaplığı

## Örnek Görünüm

<img src="http://7xikfc.com1.z0.glb.clouddn.com/waveloadingview.png" alt="örnek" title="örnek" width="400" height="680" />



## Kullanım

** Bu projenin örnek dosyası için `sample/` klasörüne bakın. **

### Adım 1

Kütüphaneyi bir yerel kütüphane projesi olarak ekleyin veya build.gradle'daki bağımlılığı ekleyin.

```groovy
dependencies {
    implementation 'me.itangqi.waveloadingview:library:0.3.5'
/...
}
```	
veya

Kütüphaneyi içe aktarıp, daha sonra /settings.gradle ve /app/build.gradle adresine ekleyin.. Bunu nasıl yapacağınızı bilmiyorsanız, mail adresim üzerinden bana ulaşabilirsiniz.

### Adım 2

Layout dosyanıza WaveLoadingView widget'ını dahil edin. Ve aşağıdaki satırları özelleştirebilirsiniz.
   
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

|İsim|Format|Açıklama|
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


## Güncellemeler

### v1.0

#### İlk versiyon:

- İlk versiyon yayınlandı.


## Demo

[Download](https://github.com/tangqi92/WaveLoadingView/releases/download/v0.2.1/sample-release-unsigned.apk)



## Topluluk

Katkıda bulunmaktan çekinmeyin!

Kütüphaneyi uygulamada kullandığınızı belirtirseniz sevinirim.



## Teşekkürler

İlham alınanlar 

- [WaveView](https://github.com/gelitenight/WaveView) created by [gelitenight](https://github.com/gelitenight)
- [CircularFillableLoaders](https://github.com/lopspower/CircularFillableLoaders) created by [lopspower](https://github.com/lopspower)


## İletişim


Herhangi bir sorunuz olursa, benimle iletişime geçebilirsiniz : [demirdoven.hh#gmail.com](mailto:demirdoven.hh#gmail.com "İletişime geçtiğin için teşekkürler")


## Lisans

    Copyright 2018 drdvn

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

