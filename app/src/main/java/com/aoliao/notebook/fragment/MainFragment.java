package com.aoliao.notebook.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aoliao.notebook.R;
import com.aoliao.notebook.adapter.MainBottomRecyclerAdapter;
import com.aoliao.notebook.contract.MainContract;
import com.aoliao.notebook.model.OnItemClickListener;
import com.aoliao.notebook.model.entity.Post;
import com.aoliao.notebook.presenter.MainFragPresenter;
import com.aoliao.notebook.ui.EditorActivity;
import com.aoliao.notebook.ui.MainActivity;
import com.aoliao.notebook.utils.GlideImageLoader;
import com.aoliao.notebook.utils.LogUtil;
import com.aoliao.notebook.utils.MaterialRippleHelper;
import com.aoliao.notebook.utils.ScreenUtils;
import com.aoliao.notebook.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;

/**
 * Created by 你的奥利奥 on 2017/5/13.
 */

public class MainFragment extends BaseRefreshFragment<Post, MainFragPresenter> implements MainContract.MainFragView {

    @BindView(R.id.mainBottomRecycler)
    RecyclerView mainBottomRecycler;
    @BindView(R.id.imgScrollInfo)
    ImageView imgScrollInfo;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
//    @BindView(R.id.status)
//    View status;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.appbar)
    View appbar;
    @BindView(R.id.dl3)
    ViewGroup dl3;
    private MainFragHandler handler;
    private Runnable runnableTop, runnableBottom;
    private boolean scrollRunning;//底部view是否正在滚动
    private int oldTopMargin;//底部view最开始的topMargin值
    private RelativeLayout.LayoutParams params = null;//底部view的layoutParams
    private int minTopMargin;//底部view最小topMargin值
    private int bottomViewNowStatus;


    /**
     * 底部布局到达顶部
     */
    public static final int BOTTOM_VIEW_STATUS_TOP = 1;
    /**
     * 底部布局到达底部
     */
    public static final int BOTTOM_VIEW_STATUS_BOTTOM = 0;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    protected void onInit() {
        super.onInit();
        handler = new MainFragHandler(this);
        MaterialRippleHelper.ripple(imgScrollInfo);
//        initStatusHeight();
        initBanner();
        swipeLayout.setRefreshing(true);
        fab.setImageResource(R.mipmap.ic_plus_skittle);
    }

    private void initBanner() {
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
    }

    @Override
    protected void onListener() {
        super.onListener();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                Bundle bd = new Bundle();
                bd.putInt("class", 0x2);
                intent.putExtras(bd);
                startActivity(intent);
//                getFragmentManager().beginTransaction().add(R.id.container, new EditorActivity()).commit();
            }
        });

        mainBottomRecycler.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void SimpleOnItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                LogUtil.e2("position = " + i);
                LogUtil.e3(view.toString());
                switch (view.getId()) {
                    case R.id.tvUserName:
                        //跳转到用户信息查看
//                        goLookUser(baseQuickAdapter, i);
                        break;
                    case R.id.imgHead:
                        //跳转到用户信息查看
//                        goLookUser(baseQuickAdapter, i);
                        break;
                    case R.id.layoutBaseArticle:
//                        saveData(C.data.KEY_POST, baseQuickAdapter.getData().get(i));
//                        MainActivity.startFragment(C.fragment.READ_ARTICLE);
                        break;
                    case R.id.btnLike:
                        Post post = (Post) baseQuickAdapter.getData().get(i);
                        presenter.requestLike(post);
                        break;
                    case R.id.btnFollow:
                        Post postF = (Post) baseQuickAdapter.getData().get(i);
                        presenter.requestFollow(postF.getAuthor());
                        break;
                }
            }
        });

        mainBottomRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int dyDiff = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                LogUtil.e1("newState = " + newState + "; dyDiff = " + dyDiff);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dyDiff <= 0 && newState == 0 && manager.findFirstVisibleItemPosition() == 0) {
                    contentLayoutToDown();
                }

                if (newState == 0) {
                    dyDiff = 0;
//                    LogUtil.e1("set newState == 0");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                LogUtil.e1("dy = " + dy);
                dyDiff += dy;
                if (dy > 0) {
                    contentLayoutToTop();
                }
            }
        });

        banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                presenter.requestOpenBannerLink(getActivity(), position - 1);
            }
        });
    }


    @Override
    protected BaseQuickAdapter<Post, BaseViewHolder> getAdapter() {
        return new MainBottomRecyclerAdapter();
    }

    @Override
    protected SwipeRefreshLayout getSwipeLayout() {
        return swipeLayout;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mainBottomRecycler;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (banner != null) {
            banner.stopAutoPlay();
            banner.removeAllViews();
            banner = null;
        }
    }

//    private void initStatusHeight() {
//        int statusHeight = ScreenUtils.getStatusHeight(getActivity());
//        ViewGroup.LayoutParams statusParams = status.getLayoutParams();
//        statusParams.height = statusHeight;
//        status.setLayoutParams(statusParams);
//
//        ViewGroup.LayoutParams params = appbar.getLayoutParams();
//        params.height = params.height + statusHeight;
//        appbar.setLayoutParams(params);
//
//        RelativeLayout.LayoutParams dl2Params = (RelativeLayout.LayoutParams) dl2.getLayoutParams();
//        dl2Params.topMargin = dl2Params.topMargin + statusHeight;
//        dl2.setLayoutParams(dl2Params);
//    }

    /**
     * 布局到顶部
     */
    @Override
    public void contentLayoutToTop() {
        if (scrollRunning) {
            return;
        }
        if (runnableTop != null) {
            contentLayoutToTopListener(true);
            handler.post(runnableTop);
            return;
        }
        runnableTop = new Runnable() {
            @Override
            public void run() {
                if (params == null) {
                    params = (RelativeLayout.LayoutParams) dl3.getLayoutParams();
                    oldTopMargin = params.topMargin;
                    minTopMargin = appbar.getHeight();
                }

                if (params.topMargin > minTopMargin) {
                    params.topMargin -= 30;
                    dl3.setLayoutParams(params);
                    handler.post(this);
                } else {
                    imgScrollInfo.setImageResource(R.drawable.ic_scroll_down);
                    handler.removeCallbacks(this);
                    contentLayoutToTopListener(false);
                }
            }
        };
        contentLayoutToTopListener(true);
        handler.post(runnableTop);
    }

    /**
     * 布局到达顶部监听
     */
    @Override
    public void contentLayoutToTopListener(boolean start) {
        scrollRunning = start;
        if (!start) {
            bottomViewNowStatus = BOTTOM_VIEW_STATUS_TOP;
        }
    }

    @Override
    public void contentLayoutToDown() {
        if (scrollRunning) {
            return;
        }
        if (runnableBottom != null) {
            contentLayoutToDownListener(true);
            handler.post(runnableBottom);
            return;
        }
        runnableBottom = new Runnable() {
            @Override
            public void run() {
                if (params == null) {
                    params = (RelativeLayout.LayoutParams) dl3.getLayoutParams();
                    oldTopMargin = params.topMargin;
                    minTopMargin = appbar.getHeight();
                }

                if (params.topMargin < oldTopMargin) {
                    params.topMargin += 30;
                    dl3.setLayoutParams(params);
                    handler.post(this);
                } else {
                    imgScrollInfo.setImageResource(R.drawable.ic_scroll_top);
                    handler.removeCallbacks(this);
                    contentLayoutToDownListener(false);
                }
            }
        };
        contentLayoutToDownListener(true);
        handler.post(runnableBottom);
    }

    @Override
    public void contentLayoutToDownListener(boolean start) {
        scrollRunning = start;
        if (!start) {
            bottomViewNowStatus = BOTTOM_VIEW_STATUS_BOTTOM;
        }
    }

    @Override
    public void pullBannerDataSuccess(List<String> titles, List<String> images) {
        //设置图片集合
        banner.setImages(images);
        banner.setBannerTitles(titles);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    @Override
    public void pullBannerDataFail(String err) {
        ToastUtil.getInstance().showShortT(err);
    }


    @Override
    public void likePostSuccess() {
        ToastUtil.getInstance().showShortT(R.string.like_cuccess);
    }

    @Override
    public void likePostFail(String err) {
        ToastUtil.getInstance().showShortT(err);
    }

    @Override
    public void followUserSuccess() {
        ToastUtil.getInstance().showShortT(R.string.follow_success);
    }

    @Override
    public void followUserFail(String err) {
        ToastUtil.getInstance().showShortT(err);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }


    public static final class MainFragHandler extends Handler {
        /**
         * 打开菜单
         */
        public static final int OPEN_MENU = 0X22;
        private WeakReference<MainFragment> wr;

        public MainFragHandler(MainFragment mf) {
            wr = new WeakReference<>(mf);
        }

        @Override
        public void handleMessage(Message msg) {
            MainFragment mf = wr.get();
            if (mf == null) {
                return;
            }

        }
    }

}
