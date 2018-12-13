package com.flyco.dialog.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.internal.BaseAlertDialog;


/**
 * Created by hnvfh on 2017/7/17.
 */

public class InputDialog extends BaseAlertDialog<InputDialog> {

    /**
     * title underline
     */
    private View mVLineTitle;
    /**
     * vertical line between btns
     */
    private View mVLineVertical;
    /**
     * vertical line between btns
     */
    private View mVLineVertical2;
    /**
     * horizontal line above btns
     */
    private View mVLineHorizontal;
    /**
     * title underline color(标题下划线颜色)
     */
    private int mTitleLineColor = Color.parseColor("#61AEDC");
    /**
     * title underline height(标题下划线高度)
     */
    private float mTitleLineHeight = 1f;
    /**
     * btn divider line color(对话框之间的分割线颜色(水平+垂直))
     */
    private int mDividerColor = Color.parseColor("#DCDCDC");

    public static final int STYLE_ONE = 0;
    public static final int STYLE_TWO = 1;
    private int mStyle = STYLE_ONE;
    /**
     * 输入框
     */
    protected EditText mETContent;

    private onInputListener mOnInputListener;
    public InputDialog(Context context) {
        super(context);
        /** default value*/
        mTitleTextColor = Color.parseColor("#61AEDC");
        mTitleTextSize = 22f;
        mContentTextColor = Color.parseColor("#383838");
        mContentTextSize = 17f;
        mLeftBtnTextColor = Color.parseColor("#8a000000");
        mRightBtnTextColor = Color.parseColor("#8a000000");
        mMiddleBtnTextColor = Color.parseColor("#8a000000");
        /** default value*/
    }

    @Override
    public View onCreateView() {
        mETContent=new EditText(mContext);
        /** title */
        mTvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mLlContainer.addView(mTvTitle);

        /** title underline */
        mVLineTitle = new View(mContext);
        mLlContainer.addView(mVLineTitle);

        /** content */
        LinearLayout.LayoutParams mLinearLayout=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLinearLayout.leftMargin=30;
        mLinearLayout.rightMargin=30;
        mETContent.setLayoutParams(mLinearLayout);
        mLlContainer.addView(mETContent);

        mVLineHorizontal = new View(mContext);
        mVLineHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        mLlContainer.addView(mVLineHorizontal);

        /** btns */
        mTvBtnLeft.setLayoutParams(new LinearLayout.LayoutParams(0, dp2px(45), 1));
        mLlBtns.addView(mTvBtnLeft);

        mVLineVertical = new View(mContext);
        mVLineVertical.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
        mLlBtns.addView(mVLineVertical);

        mTvBtnMiddle.setLayoutParams(new LinearLayout.LayoutParams(0, dp2px(45), 1));
        mLlBtns.addView(mTvBtnMiddle);

        mVLineVertical2 = new View(mContext);
        mVLineVertical2.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
        mLlBtns.addView(mVLineVertical2);

        mTvBtnRight.setLayoutParams(new LinearLayout.LayoutParams(0, dp2px(45), 1));
        mLlBtns.addView(mTvBtnRight);

        mLlContainer.addView(mLlBtns);

        return mLlContainer;
    }
    public void setMaxLength(int mMaxLength){
        mETContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(mMaxLength) });
    }
    public void setMaxLines(int mMaxLines){
        mETContent.setMaxLines(mMaxLines);
    }
    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        /** title */
        mTvTitle.setGravity(Gravity.CENTER);
        mTvTitle.setPadding(dp2px(0), dp2px(15), dp2px(0), dp2px(0));


        /** title underline */
        mVLineTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                dp2px(mTitleLineHeight)));
        mVLineTitle.setBackgroundColor(mTitleLineColor);
        mVLineTitle.setVisibility(mIsTitleShow && mStyle == STYLE_ONE ? View.VISIBLE : View.GONE);

        /** content */

        mETContent.setPadding(dp2px(15), dp2px(7), dp2px(15), dp2px(20));
        mETContent.setMinHeight(dp2px(56));
        mETContent.setGravity(Gravity.CENTER);

        /** btns */
        mVLineHorizontal.setBackgroundColor(mDividerColor);
        mVLineVertical.setBackgroundColor(mDividerColor);
        mVLineVertical2.setBackgroundColor(mDividerColor);


        mTvBtnMiddle.setVisibility(View.GONE);
        mVLineVertical.setVisibility(View.GONE);


        /**set background color and corner radius */
        float radius = dp2px(mCornerRadius);
        mLlContainer.setBackgroundDrawable(CornerUtils.cornerDrawable(mBgColor, radius));
        mTvBtnLeft.setBackgroundDrawable(CornerUtils.btnSelector(radius, mBgColor, mBtnPressColor, 0));
        mTvBtnRight.setBackgroundDrawable(CornerUtils.btnSelector(radius, mBgColor, mBtnPressColor, 1));
        mTvBtnMiddle.setBackgroundDrawable(CornerUtils.btnSelector(mBtnNum == 1 ? radius : 0, mBgColor, mBtnPressColor, -1));


        mTvBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnInputListener!=null){
                    mOnInputListener.onComplete(mETContent.getText().toString());
                }
            }
        });
    }
    public void setInputListener(onInputListener mInputListener){
        mOnInputListener=mInputListener;
    }
    public interface onInputListener {
        void onComplete(String ms);
    }
}
