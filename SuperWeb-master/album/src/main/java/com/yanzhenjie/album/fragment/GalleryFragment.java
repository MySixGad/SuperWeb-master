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
package com.yanzhenjie.album.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.album.GalleryWrapper;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.adapter.BasicPreviewAdapter;
import com.yanzhenjie.album.adapter.PathPreviewAdapter;
import com.yanzhenjie.album.impl.GalleryCallback;
import com.yanzhenjie.album.util.SelectorUtils;
import com.yanzhenjie.fragment.NoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Gallery.</p>
 * Created by yanzhenjie on 17-3-29.
 */
public class GalleryFragment extends NoFragment {

    private GalleryCallback mCallback;

    private int mToolBarColor;

    private MenuItem mFinishMenuItem;

    private View mCheckParent;
    private AppCompatCheckBox mCheckBox;

    private int mCurrentItemPosition;
    private ViewPager mViewPager;

    private List<String> mCheckedPaths;
    private boolean[] mCheckedList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (GalleryCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mCallback = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.album_fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mCheckParent = view.findViewById(R.id.layout_gallery_preview_bottom);
        mCheckBox = (AppCompatCheckBox) view.findViewById(R.id.cb_album_check);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);

        setToolbar((Toolbar) view.findViewById(R.id.toolbar));
        displayHomeAsUpEnabled(R.drawable.album_ic_back_white);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle argument = getArguments();
        mToolBarColor = argument.getInt(
                GalleryWrapper.KEY_INPUT_TOOLBAR_COLOR,
                ContextCompat.getColor(getContext(), R.color.album_ColorPrimary));

        // noinspection ConstantConditions
        getToolbar().setBackgroundColor(mToolBarColor);
        getToolbar().getBackground().mutate().setAlpha(200);

        this.mCurrentItemPosition = argument.getInt(GalleryWrapper.KEY_INPUT_CURRENT_POSITION, 0);
        if (mCurrentItemPosition >= mCheckedPaths.size()) mCurrentItemPosition = 0;

        boolean hasCheck = argument.getBoolean(GalleryWrapper.KEY_INPUT_CHECK_FUNCTION, false);
        if (!hasCheck) mCheckParent.setVisibility(View.GONE);

        initializeCheckBox();
        initializeViewPager();

        setCheckedCountUI(getCheckCount());
    }

    /**
     * Bind the preview picture collection.
     *
     * @param imagePaths image list of local.
     */
    public void bindImagePaths(List<String> imagePaths) {
        this.mCheckedPaths = imagePaths;
        int length = mCheckedPaths.size();
        mCheckedList = new boolean[length];
        for (int i = 0; i < length; i++) {
            mCheckedList[i] = true;
        }
    }

    private void initializeCheckBox() {
        //noinspection RestrictedApi
        mCheckBox.setSupportButtonTintList(SelectorUtils.createColorStateList(Color.WHITE, mToolBarColor));
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mCheckBox.isChecked();
                mCheckedList[mCurrentItemPosition] = isChecked;
                setCheckedCountUI(getCheckCount());
            }
        });
    }

    private void initializeViewPager() {
        if (mCheckedPaths.size() > 2)
            mViewPager.setOffscreenPageLimit(2);

        BasicPreviewAdapter previewAdapter = new PathPreviewAdapter(mCheckedPaths);
        mViewPager.setAdapter(previewAdapter);
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentItemPosition = position;
                mCheckBox.setChecked(mCheckedList[position]);
                // noinspection ConstantConditions
                getToolbar().setTitle(mCurrentItemPosition + 1 + " / " + mCheckedPaths.size());
            }
        };
        mViewPager.addOnPageChangeListener(pageChangeListener);
        mViewPager.setCurrentItem(mCurrentItemPosition);
        // Forced call.
        pageChangeListener.onPageSelected(mCurrentItemPosition);
    }

    /**
     * Set the number of selected pictures.
     *
     * @param count number.
     */
    private void setCheckedCountUI(int count) {
        String finishStr = getString(R.string.album_menu_finish);
        finishStr += "(" + count + " / " + mCheckedPaths.size() + ")";
        mFinishMenuItem.setTitle(finishStr);
    }

    /**
     * Get check item count.
     *
     * @return number.
     */
    private int getCheckCount() {
        int i = 0;
        for (boolean b : mCheckedList) {
            if (b) i++;
        }
        return i;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.album_menu_preview, menu);
        mFinishMenuItem = menu.findItem(R.id.album_menu_finish);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.album_menu_finish) {
            ArrayList<String> patList = new ArrayList<>();
            for (int i = 0; i < mCheckedList.length; i++) {
                if (mCheckedList[i]) {
                    patList.add(mCheckedPaths.get(i));
                }
            }
            mCallback.onGalleryResult(patList);
        }
        return true;
    }

}
