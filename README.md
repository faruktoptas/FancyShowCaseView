# FancyShowCaseView [![Status](https://travis-ci.org/faruktoptas/FancyShowCaseView.svg?branch=master)](https://travis-ci.org/faruktoptas/FancyShowCaseView) [![](https://jitpack.io/v/faruktoptas/FancyShowCaseView.svg)](https://jitpack.io/#faruktoptas/FancyShowCaseView)  [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FancyShowCaseView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5440) [![API](https://img.shields.io/badge/API-10%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=10)
An easy-to-use customizable show case view with circular reveal animation.

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

![!gif](https://cloud.githubusercontent.com/assets/1595227/24331187/ad143b80-1237-11e7-919c-36111c1ce559.gif)

![!gif](https://cloud.githubusercontent.com/assets/1595227/24331189/afec8d9e-1237-11e7-986d-0ab7c44db7c7.gif)

# Gradle Dependency

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Then, add the library to your module `build.gradle`
```gradle
dependencies {
    compile 'com.github.faruktoptas:FancyShowCaseView:0.1.1'
}
```

## Sample Usage
```java
new FancyShowCaseView.Builder(this)
        .focusOn(view)
        .title("Focus on View")
        .build()
        .show();
```
## Rounded Rectangle
```java
new FancyShowCaseView.Builder(this)
        .focusOn(view)
        .title("Focus on View")
        .focusShape(FocusShape.ROUNDED_RECTANGLE)
        .build()
        .show();
```
## Customize
```java
new FancyShowCaseView.Builder(this)
        .focusOn(view)
        .focusCircleRadiusFactor(1.5)
        .title("Focus on View with larger circle")
        .titleStyle(0, Gravity.BOTTOM | Gravity.CENTER)
        .enterAnimation(enterAnimation)
        .exitAnimation(exitAnimation)
        .showOnce("fancy1")
        .backgroundColor(Color.parseColor("#CC00FF00"))
        .build()
        .show();
```
## Inflating Custom View
```java
new FancyShowCaseView.Builder(this)
        .customView(R.layout.layout_my_custom_view, null)
        .build()
        .show();
```
## Queuing multiple instances
```java
new FancyShowCaseQueue()
	    .add(fancyShowCaseView1)
	    .add(fancyShowCaseView2)
	    .add(fancyShowCaseView3)
	    .show();
```
## Already in use in following apps
(feel free to send me new projects)
* [News - Newspaper & Magazine](https://play.google.com/store/apps/details?id=com.moblino.countrynews)

## APK URL
FancyShowCaseView\app

## Contribute
You can contribute by opening a pull request to **dev** branch.
Please try to push one feature in one commit for a clean commit history.

License
=======

    Copyright 2017 Faruk Toptaş

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
