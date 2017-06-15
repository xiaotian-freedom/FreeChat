package in.srain.cube.views.ptr.loadmore;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.srain.cube.views.ptr.R;

/**
 * Created by tianshutong on 16/3/16.
 */
public class LoadMoreDefaultFooterView extends RelativeLayout implements LoadMoreUIHandler {

    private ImageView progressBar;
    private TextView mTextView;
    private AnimationDrawable animationDrawable;

    public LoadMoreDefaultFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupViews();
    }

    private void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.cube_views_load_more_default_footer, this);
        progressBar = (ImageView) findViewById(R.id.ptr_load_more_progress);
        mTextView = (TextView) findViewById(R.id.cube_views_load_more_default_footer_text_view);
        animationDrawable = (AnimationDrawable) progressBar.getBackground();
    }

    @Override
    public void setLoadMoreText(String s) {
        if (null != s && !TextUtils.isEmpty(s)) {
            mTextView.setText(s);
        }
    }

    @Override
    public void onLoading(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        progressBar.setVisibility(VISIBLE);
        animationDrawable.start();
        mTextView.setText(R.string.cube_views_load_more_loading);
    }

    @Override
    public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore) {
        animationDrawable.stop();
        progressBar.setVisibility(View.GONE);
        if (!hasMore) {
            setVisibility(VISIBLE);
            if (empty) {
                mTextView.setText(R.string.cube_views_load_more_loaded_empty);
            } else {
                mTextView.setText(R.string.cube_views_load_more_loaded_no_more);
            }
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void onWaitToLoadMore(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        animationDrawable.stop();
        progressBar.setVisibility(View.INVISIBLE);
        mTextView.setText(R.string.cube_views_load_more_click_to_load_more);
    }

    @Override
    public void onLoadError(LoadMoreContainer container, int errorCode, String errorMessage) {
        animationDrawable.stop();
        progressBar.setVisibility(View.GONE);
        mTextView.setText(R.string.cube_views_load_more_error);
    }
}
