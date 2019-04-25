![FancyShowCaseView](https://user-images.githubusercontent.com/1595227/56747421-9aafc580-6786-11e9-9344-e3216f7aa660.png)
An easy-to-use customizable show case view with circular reveal animation.

[![Status](https://travis-ci.org/faruktoptas/FancyShowCaseView.svg?branch=master)](https://travis-ci.org/faruktoptas/FancyShowCaseView) [ ![Download](https://api.bintray.com/packages/faruktoptas/fancyshowcaseview/fancyshowcaseview/images/download.svg) ](https://bintray.com/faruktoptas/fancyshowcaseview/fancyshowcaseview) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FancyShowCaseView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5440) [![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=10) [![Join the chat at https://gitter.im/faruktoptas/FancyShowCaseView](https://badges.gitter.im/faruktoptas/FancyShowCaseView.svg)](https://gitter.im/faruktoptas/FancyShowCaseView?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

![materialup](https://cloud.githubusercontent.com/assets/1595227/24761426/f80dbc64-1af3-11e7-9c99-bca3dd836a8e.png)

[@MaterialUp Best of the Day](https://material.uplabs.com/posts/fancyshowcaseview)

## Features
- Circular reveal animation (API Level 21+)
- Background color
- Circle and Rounded Rectangle focus shapes
- Title style and position 
- Custom view inflation
- Custom enter/exit animations
- Chaining multiple FancyShowCaseView instances
- Showing only one time

![!gif](https://cloud.githubusercontent.com/assets/1595227/24331187/ad143b80-1237-11e7-919c-36111c1ce559.gif)![!gif](https://cloud.githubusercontent.com/assets/1595227/24331189/afec8d9e-1237-11e7-986d-0ab7c44db7c7.gif)

## Download
```gradle
implementation 'me.toptas.fancyshowcase:fancyshowcaseview:1.1.5'
```

## Sample Usage
```java
new FancyShowCaseView.Builder(this)
        .focusOn(view)
        .title("Focus on View")
        .build()
        .show();
```
Please see [wiki](https://github.com/faruktoptas/FancyShowCaseView/wiki) for more samples. 

## Already in use in following apps
(feel free to send me new projects)
* [News - Newspaper & Magazine](https://play.google.com/store/apps/details?id=com.moblino.countrynews)
* [XMusic Pro](https://play.google.com/store/apps/details?id=com.riseapps.xmusic)
* [Muqabla -Free Online Live Quiz Game Show](https://play.google.com/store/apps/details?id=com.es.triv)
* [Umíme česky](https://play.google.com/store/apps/details?id=cz.muni.fi.umimecesky)
* [Get Off Your Phone](https://play.google.com/store/apps/details?id=com.nephi.getoffyourphone)

## Sample App
 [Latest Release](https://github.com/faruktoptas/FancyShowCaseView/releases)
 
## Xamarin Port
Thanks to [DigitalSa1nt](https://github.com/DigitalSa1nt) for the Xamarin ported version [Xamarin.ShowcaseView](https://github.com/DigitalSa1nt/Xamarin.ShowcaseView)

## Contribute
You can contribute by opening a pull request to **dev** branch.
Please try to push one feature in one commit for a clean commit history.

## Buy Me a Coffee

<a href="https://www.buymeacoffee.com/faruktoptas" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>

License
=======

    Copyright 2019 Faruk Toptaş

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
