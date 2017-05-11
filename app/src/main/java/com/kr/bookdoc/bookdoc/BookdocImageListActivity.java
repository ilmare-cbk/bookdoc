package com.kr.bookdoc.bookdoc;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookdocImage;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookdocImageSingle;

public class BookdocImageListActivity extends AppCompatActivity {
    private Toolbar bookdocImageListToolbar;
    private RecyclerView bookdocImageListRv;
    private GridLayoutManager gridLayoutManager;
    private BookdocImageListAdapter bookdocImageListAdapter;
    private boolean checkFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_image_list);

        checkFrom = getIntent().getBooleanExtra("checkFrom", true);

        bookdocImageListToolbar = (Toolbar) findViewById(R.id.bookdoc_image_list_toolbar);
        setSupportActionBar(bookdocImageListToolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        bookdocImageListRv = (RecyclerView) findViewById(R.id.bookdoc_image_list_rv);
        gridLayoutManager = new GridLayoutManager(this, 3);
        bookdocImageListAdapter = new BookdocImageListAdapter(BookdocImageList.getBookdocImageArrayList());
        bookdocImageListRv.setLayoutManager(gridLayoutManager);
        bookdocImageListRv.setAdapter(bookdocImageListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    private class BookdocImageListAdapter extends RecyclerView.Adapter<BookdocImageListAdapter.BookdocImageListViewHolder> {
        private ArrayList<Integer> imageList;
        private DisplayMetrics displayMetrics;
        private int contentWidth;

        public BookdocImageListAdapter(ArrayList<Integer> imageList) {
            this.imageList = imageList;
            displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            contentWidth = displayMetrics.widthPixels / 3;
        }

        @Override
        public BookdocImageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookdoc_image_list_item, parent, false);
            return new BookdocImageListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookdocImageListViewHolder holder, final int position) {
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(imageList.get(position).intValue())
                    .into(holder.bookdocImageListiv);
            holder.bookdocImageListiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkFrom) {
                        BusProvider.getInstance().post(new BookdocImage(Uri.parse(String.valueOf(position + 1)), false));
                    } else {
                        BusProvider.getInstance().post(new BookdocImageSingle(Uri.parse(String.valueOf(position + 1)), false));
                    }
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        public class BookdocImageListViewHolder extends RecyclerView.ViewHolder {
            ImageView bookdocImageListiv;
            ViewGroup.LayoutParams layoutParams;

            public BookdocImageListViewHolder(View itemView) {
                super(itemView);
                bookdocImageListiv = (ImageView) itemView.findViewById(R.id.bookdoc_image_list_item_iv);
                layoutParams = bookdocImageListiv.getLayoutParams();
                layoutParams.width = contentWidth;
                layoutParams.height = contentWidth;
            }
        }
    }
}
