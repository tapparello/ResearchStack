package org.researchstack.backbone.step;

import android.util.Log;
import android.widget.ImageView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.ui.step.layout.VideoInstructionStepLayout;

import java.util.List;
import java.util.Map;


/**
 * A VideoInstructionStep object presents a video to the participant.
 * <p>
 * You can use video instruction steps to present videos during a task
 */

public class VideoInstructionStep extends Step implements NavigableOrderedTask.NavigationRule {

    private static final String LOG_TAG = VideoInstructionStep.class.getCanonicalName();

    /*
     * Additional detailed text to display
     */
    String moreDetailText;

    /**
     Additional text to display for the step in a localized string at the bottom of the view.

     The footnote is displayed in a smaller font below the continue button. It is intended to be used
     in order to include disclaimer, copyright, etc. that is important to display in the step but
     should not distract from the main purpose of the step.
     */
    String footnote;

    /**
     An image that provides a thumbnail for the video.

     The image is displayed with aspect fit. Depending on the device, the screen area
     available for this image can vary. For exact
     metrics, see `ORKScreenMetricIllustrationHeight`.
     */
    String image;

    /**
     A URL (or URI) to the video to be displayed.

     */
    String video;

    /**
     Optional icon image to show above the title and text.
     */
    String iconImage;

    /**
     * Image scale type
     */
    public ImageView.ScaleType scaleType;

    /**
     * Pointer to the next step to show after this one. If nil, then the next step
     * is determined by the navigation rules setup by NavigableOrderedTask.
     */
    String nextStepIdentifier;

    /**
     * If not null, the InstructionStepLayout will use this class to add a negative skip action
     * that when pressed, will skip this step to another step specified by SubmitBarNegativeActionSkipRule
     */
    private SubmitBarNegativeActionSkipRule submitBarSkipRule;

    /* Default constructor needed for serialization/deserialization of object */
    public VideoInstructionStep() {
        super();
    }

    public VideoInstructionStep(String identifier, String title, String detailText)
    {
        super(identifier, title);
        setText(detailText);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass() {
        return VideoInstructionStepLayout.class;
    }

    public void setMoreDetailText(String detailText) {
        moreDetailText = detailText;
    }
    public String getMoreDetailText() {
        return moreDetailText;
    }

    public void setFootnote(String newFootnote) {
        footnote = newFootnote;
    }
    public String getFootnote() {
        return footnote;
    }

    public void setVideo(String newVideo) {
        Log.d(LOG_TAG, this + " - Video URL set to: " + newVideo);
        video = newVideo;
    }
    public String getVideo() {
        return video;
    }

    public void setImage(String newImage) {
        image = newImage;
    }
    public String getImage() {
        return image;
    }

    public void setIconImage(String image) {
        iconImage = image;
    }
    public String getIconImage() {
        return iconImage;
    }

    public void setNextStepIdentifier(String identifier) {
        nextStepIdentifier = identifier;
    }
    public String getNextStepIdentifier() {
        return nextStepIdentifier;
    }

    public void setSubmitBarNegativeActionSkipRule(String taskIdentifier, String title, String skipIdentifier) {
        submitBarSkipRule = new SubmitBarNegativeActionSkipRule(taskIdentifier, title, skipIdentifier);
    }

    public SubmitBarNegativeActionSkipRule getSubmitBarNegativeActionSkipRule() {
        return submitBarSkipRule;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        return nextStepIdentifier;
    }

    public static class SubmitBarNegativeActionSkipRule {

        public static final String SKIP_RESULT_KEY = "skip";

        private String taskIdentifier;
        private String title;
        private String skipToStepIdentifier;

        public SubmitBarNegativeActionSkipRule(String taskIdentifier, String title, String skipToStepIdentifier) {
            this.taskIdentifier = taskIdentifier;
            this.title = title;
            this.skipToStepIdentifier = skipToStepIdentifier;
        }

        public void onNegativeActionClicked(VideoInstructionStep step, StepResult stepResult) {
            // Set the next step identifier
            step.setNextStepIdentifier(skipToStepIdentifier);

            // add a result to this step view controller to mark that the task was skipped
            Map<String, Object> stepResultMap = stepResult.getResults();
            stepResultMap.put(SKIP_RESULT_KEY, taskIdentifier);
        }

        public String getTitle() {
            return title;
        }
    }

}
