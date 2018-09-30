/*
 * Copyright (c) 2018. Faruk Toptaş
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.toptas.fancyshowcase.listener

/**
 * Listener for dismissing of one FancyShowCaseView
 */
interface DismissListener {
    /**
     * is called when a [FancyShowCaseView] is dismissed
     *
     * @param id the show once id of the dismissed view
     */
    fun onDismiss(id: String?)

    /**
     * is called when a [FancyShowCaseView] is skipped because of it's show once id
     *
     * @param id the show once id of the dismissed view
     */
    fun onSkipped(id: String?)
}
