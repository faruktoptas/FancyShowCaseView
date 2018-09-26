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

package me.toptas.fancyshowcase

import java.util.LinkedList
import java.util.Queue

/**
 * Handles queues of [FancyShowCaseView] so that they are shown one after another
 * takes care to skip views that should not be shown because of their one shot id
 */
class FancyShowCaseQueue : DismissListener {

    private val queue: Queue<FancyShowCaseView> = LinkedList()
    var current: FancyShowCaseView? = null
    var completeListener: OnCompleteListener? = null


    /**
     * Adds a FancyShowCaseView to the queue
     *
     * @param showCaseView the view that should be added to the queue
     * @return Builder
     */
    fun add(showCaseView: FancyShowCaseView): FancyShowCaseQueue {
        queue.add(showCaseView)
        return this
    }

    /**
     * Starts displaying all views in order of their insertion in the queue, one after another
     */
    fun show() {
        if (!queue.isEmpty()) {
            current = queue.poll().apply {
                dismissListener = this@FancyShowCaseQueue
                show()
            }
        } else {
            completeListener?.onComplete()
        }
    }

    /**
     * Cancels the queue
     * @param hideCurrent hides current FancyShowCaseView
     */
    fun cancel(hideCurrent: Boolean) {
        if (hideCurrent) {
            current?.hide()
        }
        if (!queue.isEmpty()) {
            queue.clear()
        }
    }

    override fun onDismiss(id: String?) {
        show()
    }

    override fun onSkipped(id: String?) {
        show()
    }

}
