/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.album.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yanzhenjie.album.R;
import com.yanzhenjie.album.adapter.AlbumFolderAdapter;
import com.yanzhenjie.album.entity.AlbumFolder;
import com.yanzhenjie.album.impl.OnCompatItemClickListener;
import com.yanzhenjie.album.util.SelectorUtils;

import java.util.List;

/**
 * <p>Folder preview.</p>
 * Created by Yan Zhenjie on 2016/10/18.
 */
public class AlbumFolderDialog extends BottomSheetDialog {

    private int mCurrentPosition = 0;
    private BottomSheetBehavior bottomSheetBehavior;
    private OnCompatItemClickListener mItemClickListener;

    public AlbumFolderDialog(@NonNull Context context,
                             @ColorInt int toolbarColor,
                             @ColorInt int navigationColor,
                             @Nullable List<AlbumFolder> albumFolders,
                             @Nullable OnCompatItemClickListener itemClickListener) {
        super(context, R.style.album_DialogStyle_Folder);
        setContentView(R.layout.album_dialog_floder);

        setWindowBarColor(toolbarColor, navigationColor);
        fixRestart();

        mItemClickListener = itemClickListener;

        RecyclerView rvContentList = (RecyclerView) findViewById(R.id.rv_content_list);
        assert rvContentList != null;
        rvContentList.setLayoutManager(new LinearLayoutManager(getContext()));

        ColorStateList stateList = SelectorUtils.createColorStateList(
                ContextCompat.getColor(context, R.color.album_ColorPrimaryBlack),
                toolbarColor);
        rvContentList.setAdapter(new AlbumFolderAdapter(stateList, albumFolders, new OnCompatItemClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                behaviorHide();
                if (mItemClickListener != null && mCurrentPosition != position) {
                    mCurrentPosition = position;
                    mItemClickListener.onItemClick(view, position);
                }
            }
        }));
    }

    /**
     * Set window bar color.
     *
     * @param statusColor     status bar color.
     * @param navigationColor navigation bar color.
     */
    private void setWindowBarColor(@ColorInt int statusColor, @ColorInt int navigationColor) {
        if (Build.VERSION.SDK_INT >= 21) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(statusColor);
                window.setNavigationBarColor(navigationColor);
            }
        }
    }

    /**
     * Fix reshow.
     */
    private void fixRestart() {
        View view = findViewById(android.support.design.R.id.design_bottom_sheet);
        if (view == null) return;
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }


    /**
     * Dismiss dialog.
     */
    public void behaviorHide() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
