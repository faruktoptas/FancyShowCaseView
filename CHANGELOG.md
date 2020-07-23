Change Log
==========

1.3.1 (24-07-2020)
----------------------------
*   Fix: Wrong focus for scaled views. [#201](https://github.com/faruktoptas/FancyShowCaseView/issues/201)
*   Fix: Animations won't "turn" unless parameters match. [#197](https://github.com/faruktoptas/FancyShowCaseView/issues/197)
*   New: Add custom typeface support. [#179](https://github.com/faruktoptas/FancyShowCaseView/issues/179)

1.3.0 (26-01-2020)
----------------------------
*   New: Builder parameters move to Properties data class
*   New: Move logic part to a separate Presenter class with a testable way
*   New: Add unit tests to Presenter
*   New: Remove Calculator class

### API Changes
Old:
```kotlin
fancyShowCaseView.calculator.circleCenterX
fancyShowCaseView.calculator.circleCenterY
fancyShowCaseView.calculator.focusWidth
fancyShowCaseView.calculator.focusHeight
fancyShowCaseView.calculator.focusShape
```

New:
```kotlin
fancyShowCaseView.focusCenterX
fancyShowCaseView.focusCenterY
fancyShowCaseView.focusWidth
fancyShowCaseView.focusHeight
fancyShowCaseView.focusShape
```

1.2.1 (06-08-2019)
----------------------------
*   New: Disable enter/exit animations by setting null [#155](https://github.com/faruktoptas/FancyShowCaseView/issues/155)
*   Fix: roundRectRadius can't be set 0 [#154](https://github.com/faruktoptas/FancyShowCaseView/issues/154)

1.2.0 (23-07-2019)
----------------------------
*   New: Migrated to androidx. This release and all future releases are only compatible with projects that have been migrated to androidx.
