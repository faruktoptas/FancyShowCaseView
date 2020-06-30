Change Log
==========

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
*   New: #155 Disable enter/exit animations by setting null
*   Fix: #154 roundRectRadius can't be set 0

1.2.0 (23-07-2019)
----------------------------
*   New: Migrated to androidx. This release and all future releases are only compatible with projects that have been migrated to androidx.
