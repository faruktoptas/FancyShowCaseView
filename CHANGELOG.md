# 1.3.0
*   Builder parameters move to Properties data class
*   Move logic part to a separate Presenter class with a testable way
*   Add unit tests to Presenter
*   Remove Calculator class


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

# 1.2.1

### Bug Fixes
* Fix #154 roundRectRadius can't be set 0
### Features and Improvements
* Fix #155 Disable enter/exit animations by setting null

# 1.2.0
### Features and Improvements
* Migrated to androidx. This release and all future releases are only compatible with projects that have been migrated to androidx.
