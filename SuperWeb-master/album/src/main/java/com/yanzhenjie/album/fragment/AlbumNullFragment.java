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

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.album.AlbumWrapper;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.util.SelectorUtils;

/**
 * <p>Display when there is no picture in the multimedia library.</p>
 * Created by Yan Zhenjie on 2017/3/28.
 */
public class AlbumNullFragment extends BasicCameraFragment {

    private static final String KEY_OUTPUT_IMAGE_PATH = "KEY_OUTPUT_IMAGE_PATH";

    /**
     * Resolve the image path at the time of success.
     *
     * @param bundle {@link #onFragmentResult(int, int, Bundle)}.
     * @return image path.
     */
    public static String parseImagePath(Bundle bundle) {
        return bundle.getString(KEY_OUTPUT_IMAGE_PATH);
    }

    private AppCompatButton mBtnCamera;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.album_fragment_null, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setToolbar((Toolbar) view.findViewById(R.id.toolbar));
        displayHomeAsUpEnabled(R.drawable.album_ic_back_white);
        setTitle(R.string.album_title_not_found_image);

        mBtnCamera = (AppCompatButton) view.findViewById(R.id.btn_camera);
        mBtnCamera.setOnClickListener(mCameraClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle argument = getArguments();
        int toolBarColor = argument.getInt(
                AlbumWrapper.KEY_INPUT_TOOLBAR_COLOR,
                ContextCompat.getColor(getContext(), R.color.album_ColorPrimary));
        int statusBarColor = argument.getInt(
                AlbumWrapper.KEY_INPUT_STATUS_COLOR,
                ContextCompat.getColor(getContext(), R.color.album_ColorPrimaryBlack));

        // noinspection ConstantConditions
        getToolbar().setBackgroundColor(toolBarColor);

        ColorStateList stateList = SelectorUtils.createColorStateList(toolBarColor, statusBarColor);
        //noinspection RestrictedApi
        mBtnCamera.setSupportBackgroundTintList(stateList);
    }

    /**
     * Camera click.
     */
    private View.OnClickListener mCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraUnKnowPermission(randomJPGPath());
        }
    };

    @Override
    protected void onCameraBack(String imagePath) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_OUTPUT_IMAGE_PATH, imagePath);
        setResult(RESULT_OK, bundle);
        finish();
    }
}
