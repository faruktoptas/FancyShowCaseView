package me.toptas.fancyshowcase;

/**
 * Listener for dismissing of one FancyShowCaseView
 */
public interface DismissListener {
    /**
     * is called when a {@link FancyShowCaseView} is dismissed
     *
     * @param id the one shot id of the dismissed view
     */
    void onDismiss(String id);
    /**
     * is called when a {@link FancyShowCaseView} is skipped because of it's one shot id
     *
     * @param id the one shot id of the dismissed view
     */
    void onSkipped(String id);
}
