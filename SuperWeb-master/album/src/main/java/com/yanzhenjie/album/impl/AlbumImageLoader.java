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
package com.yanzhenjie.album.impl;

import android.widget.ImageView;

/**
 * <p>Picture loader, you can determine the url, load the local image and network pictures.</p>
 * Created by Yan Zhenjie on 2017/3/31.
 */
public interface AlbumImageLoader {

    /**
     * According to the specified width high loading pictures, wide high, the greater the picture clearer, more memory.
     *
     * @param imageView {@link ImageView}.
     * @param imagePath path from local SDCard.
     * @param width     target width.
     * @param height    target height.
     */
    void loadImage(ImageView imageView, String imagePath, int width, int height);

}
