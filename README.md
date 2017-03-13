# FancyShowCaseView [![Status](https://travis-ci.org/faruktoptas/FancyShowCaseView.svg?branch=master)](https://travis-ci.org/faruktoptas/FancyShowCaseView) [![](https://jitpack.io/v/faruktoptas/FancyShowCaseView.svg)](https://jitpack.io/#faruktoptas/FancyShowCaseView)
An easy-to-use customizable show case view with circular reveal animation.


## Features
- Circular reveal animation (API Level 21+)
- Background color
- Title style and position 
- Custom view inflation
- Custom enter/exit animations

![!gif](https://github.com/faruktoptas/FancyShowCaseView/blob/master/art/fancyshowcase.gif?raw=true)

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
    com.github.faruktoptas:FancyShowCaseView:latest.release.here
}
```

## Sample Usage
```
new FancyShowCaseView.Builder(this)
        .focusOn(view)
        .title("Focus on View")
        .build()
        .show();
```
## Customize
```
new FancyShowCaseView.Builder(this)
        .focusOn(view)
        .focusCircleRadiusFactor(1.5)
        .title("Focus on View with larger circle")
        .titleStyle(0, Gravity.BOTTOM | Gravity.CENTER)
        .enterAnimation(enterAnimation)
        .exitAnimation(exitAnimation)
        .showOnce(false)
        .backgroundColor(Color.parseColor("#CC00FF00"))
        .build()
        .show();
```
## Inflating Custom View
```
new FancyShowCaseView.Builder(this)
        .customView(R.layout.layout_my_custom_view, null)
        .build()
        .show();
```
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
