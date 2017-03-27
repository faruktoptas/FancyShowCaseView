package me.toptas.fancyshowcase;

/**
 * Listener for dismissing of one FancyShowCaseView
 */
public interface DismissListener {
    /**
     * is called when a {@link FancyShowCaseView} is dismissed
     *
     * @param id the show once id of the dismissed view
     */
    void onDismiss(String id);
    /**
     * is called when a {@link FancyShowCaseView} is skipped because of it's show once id
     *
     * @param id the show once id of the dismissed view
     */
    void onSkipped(String id);
}
