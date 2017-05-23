/*
 * Copyright 2016 XuJiaji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aoliao.notebook.presenter;

import com.aoliao.notebook.R;
import com.aoliao.notebook.contract.EditorContract;
import com.aoliao.notebook.helper.AppController;
import com.aoliao.notebook.model.NetRequest;
import com.aoliao.notebook.xmvp.XBasePresenter;

import java.io.File;



/**
 * Created by jiana on 16-7-27.
 */
public class EditorPresenter extends XBasePresenter<EditorContract.View> implements EditorContract.Presenter{

    public EditorPresenter(EditorContract.View view) {
        super(view);
    }

    @Override
    public void uploadPic(String path) {
        view.showUploadPicProgress();
        NetRequest.Instance().uploadPic(new File(path), new NetRequest.UploadPicListener<String>() {
            @Override
            public void compressingPic() {
                view.showCompressingPicDialog();
            }

            @Override
            public void compressedPicSuccess() {
                view.compressedPicSuccess();
            }

            @Override
            public void progress(int i) {
                view.uploadPicProgress(i);
            }

            @Override
            public void success(String s) {
                view.uploadedPicLink(s);
            }

            @Override
            public void error(String err) {
                view.uploadPicFail(err);
            }
        });
    }

    @Override
    public void uploadArticle(String coverPicture, String title, String article) {
        view.showUploadArticleProgress();
        if ("".equals(title)) {
            view.uploadArticleFail(AppController.getAppContext().getString(R.string.please_input_title));
            return;
        }
        if ("".equals(article)) {
            view.uploadArticleFail(AppController.getAppContext().getString(R.string.please_input_content));
        }

        NetRequest.Instance().uploadArticle(coverPicture, title, article, new NetRequest.RequestListener<String>() {
            @Override
            public void success(String s) {
                view.uploadArticleSuccess();
            }

            @Override
            public void error(String err) {
                view.uploadArticleFail(err);
            }
        });


    }
}
