package com.paulniu.imagetextview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 前后带图片的TextView
 * Created by shixiaoming on 17/8/12.
 */

public class QuoteTextView extends AppCompatTextView {
    private String leftHtml = "";//文本左边html
    private String rightHtml = "";//文本右边html

    private String ELLIPSIS = "...";//自定义省略符号
    private int placeholderCount = 1;//图片及省略号占位

    public interface EllipsizeListener {
        void ellipsizeStateChanged(boolean ellipsized);
    }

    private final List<EllipsizeListener> ellipsizeListeners = new ArrayList<EllipsizeListener>();
    private boolean isEllipsized;
    private boolean isStale = true;
    private boolean programmaticChange;
    private String fullText;//原始文本
    private float lineSpacingMultiplier = 1.0f;
    private float lineAdditionalVerticalPadding = 0.0f;

    public QuoteTextView(Context context) {
        super(context);
    }

    public QuoteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuoteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addEllipsizeListener(EllipsizeListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        ellipsizeListeners.add(listener);
    }

    public void removeEllipsizeListener(EllipsizeListener listener) {
        ellipsizeListeners.remove(listener);
    }

    public boolean isEllipsized() {
        return isEllipsized;
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        isStale = true;
    }

    @Override
    public int getMaxLines() {   //兼容TextView 该方法小于API16不可用bug
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            return super.getMaxLines();
        }else{
            return TextViewCompat.getMaxLines(this);
        }
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        this.lineAdditionalVerticalPadding = add;
        this.lineSpacingMultiplier = mult;
        super.setLineSpacing(add, mult);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        if (!programmaticChange) {
            fullText = text.toString();//拿到原始文本
            isStale = true;
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (isStale) {
            super.setEllipsize(null);//避免重复绘制
            resetText();//自定义省略
        }
        super.onDraw(canvas);
    }
    public void setHtmlFromString(String html) {
        Html.ImageGetter imgGetter;

        imgGetter = new LocalImageGetter(getContext());

        // this uses Android's Html class for basic parsing, and HtmlTagHandler
//        setText(Html.fromHtml(html, imgGetter, new HtmlTagHandler()));
        Spanned spanned = Html.fromHtml(html, imgGetter, null);
        if(spanned instanceof SpannableStringBuilder){
            ImageSpan[] imageSpans = spanned.getSpans(0, spanned.length(), ImageSpan.class);
            for(ImageSpan imageSpan : imageSpans){
                int start = spanned.getSpanStart(imageSpan);
                int end = spanned.getSpanEnd(imageSpan);
                Drawable d = imageSpan.getDrawable();
                AlignTextSpan newImageSpan = new AlignTextSpan(d, ImageSpan.ALIGN_BASELINE);
                ((SpannableStringBuilder) spanned).setSpan(newImageSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                ((SpannableStringBuilder) spanned).removeSpan(imageSpan);
            }
        }
        setText(spanned, BufferType.SPANNABLE);

        // make links work
        setMovementMethod(LinkMovementMethod.getInstance());
//        setTextColor(getResources().getColor(android.R.color.w));
    }
    public void setQuoteTextView(String text, String imgLeft, String imgRight, String ellipse, int count){
        this.leftHtml = imgLeft;//文本左边图片资源名，img01
        this.rightHtml = imgRight;//文本右边图片资源名
        this.ELLIPSIS = ellipse;//自定义省略符号
        this.placeholderCount = count;//图片及省略号占位
        super.setText(text);
    }
    public void setQuoteTextView(String text, String imgRight, int count){
        setQuoteTextView(text,"",imgRight, "",count);
    }


    private void resetText() {
        if(TextUtils.isEmpty(fullText)){
            return;
        }
        int maxLines = getMaxLines();
        String workingText = fullText;
        boolean ellipsized = false;
        if (maxLines != -1) {
            Layout layout = createWorkingLayout(workingText);
            if (layout.getLineCount() > maxLines) {
                workingText = fullText.substring(0, layout.getLineEnd(maxLines - 1)).trim();
                workingText = workingText.substring(0, workingText.length() - placeholderCount) + ELLIPSIS;
                ellipsized = true;
            }
        }
        if (!workingText.equals(getText())) {
            programmaticChange = true;
            try {
                setHtmlFromString(toHtml(workingText));
            } finally {
                programmaticChange = false;
            }
        }else {
            setHtmlFromString(toHtml(workingText));
        }
        isStale = false;
        if (ellipsized != isEllipsized) {
            isEllipsized = ellipsized;
            for (EllipsizeListener listener : ellipsizeListeners) {
                listener.ellipsizeStateChanged(ellipsized);
            }
        }
    }
    private String toHtml(String text){
        return leftHtml + text + rightHtml;
    }
    private String getHtmlImg(String src){
        if(TextUtil.isEmpty(src)){
            return "";
        }
        return "<img src='" + src + "'/>";
    }
    private Layout createWorkingLayout(String workingText) {
        return new StaticLayout(workingText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(),
                Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
    }
    @Override
    public void setEllipsize(TextUtils.TruncateAt where) {
        super.setEllipsize(where);
        // Ellipsize settings are not respected
    }
}
