![FancyShowCaseView](https://user-images.githubusercontent.com/1595227/56747421-9aafc580-6786-11e9-9344-e3216f7aa660.png)
<p align="center">An easy-to-use customizable show case view with circular reveal animation.</p>
<p align="center">
        <a href="https://github.com/faruktoptas/FancyShowCaseView/actions"><img src="https://github.com/faruktoptas/FancyShowCaseView/workflows/build/badge.svg" alt="Status"/></a>
        <a href="https://jitpack.io/#faruktoptas/FancyShowCaseView"><img src="https://jitpack.io/v/faruktoptas/FancyShowCaseView.svg" alt="Download"/></a>
        <a href="https://www.codacy.com/manual/faruktoptas/FancyShowCaseView?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=faruktoptas/FancyShowCaseView&amp;utm_campaign=Badge_Grade"><img src="https://app.codacy.com/project/badge/Grade/47050961b2d947d3a58292f50d74e110" alt=""/></a>
        <a href="https://android-arsenal.com/details/1/5440"><img src="https://img.shields.io/badge/Android%20Arsenal-FancyShowCaseView-brightgreen.svg?style=flat" alt=""/></a>
        <a href="https://android-arsenal.com/api?level=10"><img src="https://img.shields.io/badge/API-14%2B-blue.svg?style=flat" alt=""/></a>
        <a href="https://gitter.im/faruktoptas/FancyShowCaseView?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge"><img src="https://badges.gitter.im/faruktoptas/FancyShowCaseView.svg" alt=""/></a>
</p>

<p align="center">
        <img src="https://cloud.githubusercontent.com/assets/1595227/24761426/f80dbc64-1af3-11e7-9c99-bca3dd836a8e.png"/>
</p>
<p align="center">
        <a href="https://material.uplabs.com/posts/fancyshowcaseview">@MaterialUp Best of the Day</a>
</p>

## Features
*   Circular reveal animation (API Level 21+)
*   Focus on a specific view or position
*   Background color
*   Circle and Rounded Rectangle focus shapes
*   Title style and position 
*   Custom view inflation
*   Custom enter/exit animations
*   Chaining multiple FancyShowCaseView instances
*   Showing only one time

![!gif](https://cloud.githubusercontent.com/assets/1595227/24331187/ad143b80-1237-11e7-919c-36111c1ce559.gif)![!gif](https://cloud.githubusercontent.com/assets/1595227/24331189/afec8d9e-1237-11e7-986d-0ab7c44db7c7.gif)

## Download
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
    implementation 'com.github.faruktoptas:FancyShowCaseView:1.3.9'
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

## Supported Properties

| Command | Description |
| --- | --- |
| `focusOn` |  |
| `title` |  |
| `typeface` |  |
| `titleStyle` |  |
| `titleGravity` |  |
| `titleSize` |  |
| `enableAutoTextPosition` | Center text position vertically. |
| `backgroundColor` |  |
| `fitSystemWindows` |  |
| `focusShape` |  |
| `focusBorderColor` |  |
| `focusBorderSize` |  |
| `focusDashedBorder` |  |
| `roundRectRadius` |  |
| `showOnce` |  |
| `clickableOn` |  |
| `focusCircleRadiusFactor` |  |
| `focusRectSizeFactor` |  |
| `customView` |  |
| `closeOnTouch` |  |
| `enableTouchOnFocusedView` |  |
| `enterAnimation` |  |
| `exitAnimation` |  |
| `animationListener` |  |
| `disableFocusAnimation` |  |
| `focusAnimationMaxValue` | Focus animation max value. Bigger value makes larger focus area. |
| `focusAnimationStep` | Step for focus animation. Default value is 1. |
| `focusRectAtPosition` |  |
| `focusCircleAtPosition` |  |
| `dismissListener` |  |
| `delay` | Shows the FancyShowCaseView after a delay. |

Please see [wiki](https://github.com/faruktoptas/FancyShowCaseView/wiki) for more samples.  

## Already in use in following apps
(feel free to send me new projects)

*   [News - Newspaper & Magazine](https://play.google.com/store/apps/details?id=com.moblino.countrynews)
*   [NN Senin Mobilin](https://play.google.com/store/apps/details?id=com.eteration.ing.mobile.android)
*   [Umíme česky](https://play.google.com/store/apps/details?id=cz.muni.fi.umimecesky)
*   [DHIS2 Capture](https://play.google.com/store/apps/details?id=com.dhis2)
*   [Travel Weather - Forecast plan for your trip](https://play.google.com/store/apps/details?id=pozzo.apps.travelweather)
*   [muSync for Spotify](https://play.google.com/store/apps/details?id=com.kloczl.musync.android)

## Sample App
 [Latest Release](https://github.com/faruktoptas/FancyShowCaseView/releases)

## Xamarin Port
Thanks to [DigitalSa1nt](https://github.com/DigitalSa1nt) for the Xamarin ported version [Xamarin.ShowcaseView](https://github.com/DigitalSa1nt/Xamarin.ShowcaseView)

## Contribute
You can contribute by opening a pull request to **dev** branch.
Please try to push one feature in one commit for a clean commit history.

## Buy Me a Coffee

<a href="https://www.buymeacoffee.com/faruktoptas" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>

## License
[Apache License 2.0](https://github.com/faruktoptas/FancyShowCaseView/blob/master/LICENSE)
