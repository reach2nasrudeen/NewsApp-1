package com.example.sridharjajoo.newsapp.core.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.sridharjajoo.newsapp.R;
import com.example.sridharjajoo.newsapp.Utils.Utils;
import com.example.sridharjajoo.newsapp.core.Headline.HeadlineAdapter;
import com.example.sridharjajoo.newsapp.data.Headline.Articles;
import com.example.sridharjajoo.newsapp.data.Headline.HeadlineService;
import com.example.sridharjajoo.newsapp.di.Injectable;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchFragment extends Fragment implements Injectable{

    @Inject
    HeadlineService headlineService;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.custom_search)
    SearchView searchView;

    @BindView(R.id.cutom_search_recycler)
    RecyclerView customSearchRecycler;

    private List<Articles> articlesList;
    private HeadlineAdapter headlineAdapter;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                customSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    private void setRecyclerView(List<Articles> articlesList) {
        customSearchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        headlineAdapter = new HeadlineAdapter(articlesList, getActivity());
        customSearchRecycler.setAdapter(headlineAdapter);
        loadArticles(articlesList);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        customSearchRecycler.addItemDecoration(itemDecorator);
    }

    private void loadArticles(List<Articles> articlesList) {
        headlineAdapter.setArticlesList(articlesList);
    }

    private void customSearch(String s) {
        headlineService.getCustomSearchReponse(s)
                .doOnSubscribe(disposable -> progressBar.setVisibility(View.VISIBLE))
                .doFinally(() -> progressBar.setVisibility(View.GONE))
                .subscribe(status -> {
                    this.articlesList = status.articles;
                    setRecyclerView(articlesList);
                    Utils.hideKeyboard(view);
                });
    }
}