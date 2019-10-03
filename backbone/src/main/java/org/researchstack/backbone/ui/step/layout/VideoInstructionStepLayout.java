package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.VideoInstructionStep;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.AssetVideoView;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;

import java.io.File;

public class VideoInstructionStepLayout extends FixedSubmitBarLayout implements StepLayout {

    private static final String LOG_TAG = VideoInstructionStepLayout.class.getCanonicalName();


    protected StepCallbacks callbacks;

    protected VideoInstructionStep videoInstructionStep;
    protected Step step;

    protected TextView titleTextView;
    protected TextView  textTextView;
    protected ImageView imageView;
    protected TextView  moreDetailTextView;

    public VideoInstructionStepLayout(Context context) {
        super(context);
    }

    public VideoInstructionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoInstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public VideoInstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetStep(step);
        connectStepUi(
                R.id.rsb_video_instruction_title,
                R.id.rsb_video_instruction_text,
                R.id.rsb_video_image_view,
                R.id.rsb_video_instruction_more_detail_text);
        refreshStep();
    }

    protected void validateAndSetStep(Step step) {
        if (!(step instanceof VideoInstructionStep)) {
            throw new IllegalStateException("InstructionStepLayout only works with InstructionStep");
        }
        this.videoInstructionStep = (VideoInstructionStep)step;
        this.step = step;
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public @LayoutRes
    int getContentResourceId() {
        return R.layout.rsb_step_layout_video_instruction;
    }


    public void connectStepUi(@IdRes int titleRId, @IdRes int textRId, @IdRes int imageRId, @IdRes int detailRId) {
        titleTextView       = findViewById(titleRId);
        textTextView        = findViewById(textRId);
        imageView           = findViewById(imageRId);
        moreDetailTextView  = findViewById(detailRId);
    }

    public void refreshStep() {
        if (step != null) {
            String title = step.getTitle();
            String text  = step.getText();

            // Set Title
            if (! TextUtils.isEmpty(title)) {
                titleTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(title);
            }

            // Set Summary
            if(! TextUtils.isEmpty(text)) {
                textTextView.setVisibility(View.VISIBLE);

                // There is an odd bug where endlines do not show up with Html.fromHtml correctly,
                // so we should use the old school text when we find one and assume it is not html,
                // because html does not use "\n" it uses line breaks
                if (text.contains("\n")) {
                    textTextView.setText(text);
                } else {
                    textTextView.setText(Html.fromHtml(text));
                    final String htmlDocTitle = title;
                    textTextView.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String url) {
                            String path = ResourcePathManager.getInstance().
                                    generateAbsolutePath(ResourcePathManager.Resource.TYPE_HTML, url);
                            Intent intent = ViewWebDocumentActivity.newIntentForPath(
                                    getContext(), htmlDocTitle, path);
                            getContext().startActivity(intent);
                        }
                    });
                }
            }

            // Set Next / Skip
            if (submitBar != null) {
                submitBar.setVisibility(View.VISIBLE);
                submitBar.setPositiveTitle(R.string.rsb_next);
                submitBar.setPositiveAction(v -> onComplete());

                if (videoInstructionStep.getSubmitBarNegativeActionSkipRule() != null) {
                    final VideoInstructionStep.SubmitBarNegativeActionSkipRule rule =
                            videoInstructionStep.getSubmitBarNegativeActionSkipRule();
                    submitBar.setNegativeTitle(rule.getTitle());
                    submitBar.setNegativeAction(v -> {
                        StepResult stepResult = new StepResult(step);
                        rule.onNegativeActionClicked(videoInstructionStep, stepResult);
                        if (callbacks != null) {
                            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, stepResult);
                        }
                    });
                } else if (step.isOptional()) {
                    submitBar.setNegativeTitle(R.string.rsb_step_skip);
                    submitBar.setNegativeAction(v -> {
                        if (callbacks != null) {
                            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
                        }
                    });
                } else {
                    submitBar.getNegativeActionView().setVisibility(View.GONE);
                }
            }

            refreshImage(videoInstructionStep.getImage());
            if (moreDetailTextView != null) {
                refreshDetailText(videoInstructionStep.getMoreDetailText(), moreDetailTextView.getCurrentTextColor());
            }
        }
    }

    protected void refreshImage(String imageName) {
        // Setup the Imageview, is compatible with normal, vector, and animated drawables
        if (imageName != null) {
            int drawableInt = ResUtils.getDrawableResourceId(getContext(), imageName);
            if (drawableInt != 0) {

                // https://developer.android.com/guide/topics/graphics/vector-drawable-resources.html
                imageView.setImageResource(drawableInt);

                if (videoInstructionStep.scaleType != null) {
                    imageView.setScaleType(videoInstructionStep.scaleType);
                }

                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        playVideo(v);
                    }
                });
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    protected void refreshDetailText(String detailText, int detailTextColor) {
        moreDetailTextView.setVisibility(detailText == null ? View.GONE : View.VISIBLE);
        if (detailText != null) {
            moreDetailTextView.setText(detailText);
        }
        moreDetailTextView.setTextColor(detailTextColor);
    }


    /**
     * Play video when the image is clicked.
     */
    public void playVideo(View view) {
        Log.d(LOG_TAG, "Playing video!");

        String path = videoInstructionStep.getVideo();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        intent.setDataAndType(Uri.parse(path), "video/*");
        view.getContext().startActivity(intent);


    }

    protected void onComplete() {
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
    }

}
