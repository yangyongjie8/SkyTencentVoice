package com.skyworthdigital.voice.tencent_module.domains.recipe;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.model.TemplateItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class RecipeActivity extends Activity implements AdapterView.OnItemClickListener {
    private ArrayList<RecipeListItem> mBeanList;
    private RecipeAdapter mListAdapter;
    private WebView mWebView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hookWebView();
        setContentView(R.layout.activity_recipe);
        mListView = (ListView) findViewById(R.id.lv_imagetext);
        mWebView = (WebView) findViewById(R.id.web);
        mWebView.getSettings().setJavaScriptEnabled(true);

        // 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        mWebView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        //web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebView.getSettings().setLoadWithOverviewMode(true);

        Intent intent = getIntent();
        if (intent.hasExtra("url")) {
            String url = intent.getStringExtra("url");
            refreshWebView(url);
            //mWebView.loadUrl(url);
        }
        mWebView.getSettings().setJavaScriptEnabled(true);  //加上这一行网页为响应式的
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;   //返回true， 立即跳转，返回false,打开网页有延时
            }
        });

        List<TemplateItem> list = (List<TemplateItem>) getIntent().getSerializableExtra("list");
        mBeanList = getAllNews(list);
        //2.找到控件
        //3.创建一个adapter设置给listview
        mListAdapter = new RecipeAdapter(this, mBeanList);

        mListView.setSelected(true);
        mListAdapter.setPlayPos(0);
        mListView.setAdapter(mListAdapter);
        //4.设置listview条目的点击事件
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        RecipeListItem bean = (RecipeListItem) parent.getItemAtPosition(position);
        //mWebView.loadUrl(bean.news_url);
        updateItem(mListAdapter.getPlayPos(), false);
        mListAdapter.setPlayPos(position);
        updateItem(position, true);
        refreshWebView(bean.news_url);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("url")) {
            String url = intent.getStringExtra("url");
            mWebView.loadUrl(url);
        }
        List<TemplateItem> list = (List<TemplateItem>) intent.getSerializableExtra("list");
        mBeanList = getAllNews(list);
        //3.创建一个adapter设置给listview
        mListAdapter = new RecipeAdapter(this, mBeanList);

        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
    }

    private void refreshWebView(String url) {
        mWebView.loadUrl(url);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.setBackgroundColor(Color.TRANSPARENT);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

        });
    }

    private void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                return;
            }
            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> providerConstructor = providerClass.getConstructor(delegateClass);
            if (providerConstructor != null) {
                providerConstructor.setAccessible(true);
                Constructor<?> declaredConstructor = delegateClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                sProviderInstance = providerConstructor.newInstance(declaredConstructor.newInstance());
                field.set("sProviderInstance", sProviderInstance);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RecipeListItem> getAllNews(List<TemplateItem> list) {

        ArrayList<RecipeListItem> arrayList = new ArrayList<RecipeListItem>();

        for (TemplateItem item : list) {
            RecipeListItem newsBean = new RecipeListItem();
            newsBean.title = item.mTitle;
            newsBean.des = item.mDescription;
            newsBean.news_url = item.mDestURL;
            newsBean.icon = item.mContentURL;
            arrayList.add(newsBean);
        }
        return arrayList;
    }

    private void updateItem(int position, boolean focus) {
        View view = mListView.getChildAt(position);
        if (view != null) {
            RecipeAdapter.ViewHolder holder = (RecipeAdapter.ViewHolder) view.getTag();
            if (holder != null) {
                if (focus) {
                    holder.item_tv_title.setBackgroundColor(getResources().getColor(R.color.new_gold));
                } else {
                    holder.item_tv_title.setBackground(getResources().getDrawable(R.drawable.bg_color_selector));
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ImagePipeline mImagePipeline = Fresco.getImagePipeline();
        if (mImagePipeline != null) {
            mImagePipeline.clearMemoryCaches();
        }
        MLog.i("recipe", "onDestroy");
    }
}
