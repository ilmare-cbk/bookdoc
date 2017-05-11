package com.kr.bookdoc.bookdoc.bookdocmainfragment;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.BookdocBookSearchActivity;
import com.kr.bookdoc.bookdoc.BookdocSimplePrescriptionDetailActivity;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocLoadMoreScrollListener;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.SinglePrescriptionDel;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singleline.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singleline.Singlelines;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlielineedit.SingleLineEdit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SimplePrescriptionFragment extends Fragment implements OnRefreshListener {
    RecyclerView simplePrescriptionRecyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    SimplePrescriptionAdapter simplePrescriptionAdapter;
    SwipeRefreshLayout simplePrescriptionSwipeRefreshLayout;
    BookdocProgressDialog bookdocProgressDialog;
    int totalCount;
    private BookdocLoadMoreScrollListener bookdocLoadMoreScrollListener;
    Typeface typeNanumR,typeMyeongjo,typeBrush;
    private ArrayList<Integer> delItemId = new ArrayList<>();


    public SimplePrescriptionFragment() {
    }

    public static SimplePrescriptionFragment newInstance(){
        SimplePrescriptionFragment simplePrescriptionFragment = new SimplePrescriptionFragment();
        return simplePrescriptionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        bookdocProgressDialog = new BookdocProgressDialog(getContext());
        typeNanumR = Typeface.createFromAsset(getContext().getAssets(), "NanumBarunGothic.ttf");
        typeMyeongjo = Typeface.createFromAsset(getContext().getAssets(), "NanumMyeongjo.otf");
        typeBrush = Typeface.createFromAsset(getContext().getAssets(), "NanumBrush.otf");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.fragment_simple_prescription, container, false);

        simplePrescriptionSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simple_prescription_swipe_refresh_layout);
        simplePrescriptionSwipeRefreshLayout.setOnRefreshListener(this);

        simplePrescriptionRecyclerView = (RecyclerView) view.findViewById(R.id.simple_prescription_rv);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        simplePrescriptionAdapter = new SimplePrescriptionAdapter();
        simplePrescriptionRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        simplePrescriptionRecyclerView.addItemDecoration(new SimplePrescriptionItemDecoration(2,6));
        simplePrescriptionRecyclerView.setAdapter(simplePrescriptionAdapter);
        bookdocLoadMoreScrollListener = new BookdocLoadMoreScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int skip = (page-1)*9;
                new BookSinglelinePrescriptionAsyncTask().execute(skip,9);
            }

            @Override
            public void onLoadMore(int page) {
            }

            @Override
            public void setVisibleTopButton() {

            }

            @Override
            public void setInvisibleTopButton() {

            }
        };
        simplePrescriptionRecyclerView.addOnScrollListener(bookdocLoadMoreScrollListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new BookSinglelinePrescriptionAsyncTask().execute(0,9);
    }

    @Override
    public void onRefresh() {
        bookdocLoadMoreScrollListener.initData();
        simplePrescriptionAdapter.removeItems();
        new BookSinglelinePrescriptionAsyncTask().execute(0,9);
        simplePrescriptionSwipeRefreshLayout.setRefreshing(false);
    }


    private class SimplePrescriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final int CREATE_SIMPLE_PRESCRIPTON_ITEM = 0;
        private final int SIMPLE_PRESCRIPTON_ITEM = 1;
        DisplayMetrics displayMetrics;


        ArrayList<Data> singlineData = new ArrayList<>();
        ArrayList<String> imageList = new ArrayList<>();
        ArrayList<String> commentList = new ArrayList<>();
        ArrayList<Integer> fontPositionList = new ArrayList<>();

        private int contentWidth;

        public SimplePrescriptionAdapter() {
            displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            contentWidth = displayMetrics.widthPixels/2;
        }

        @Override
        public int getItemViewType(int position) {
            if(CREATE_SIMPLE_PRESCRIPTON_ITEM == position){
                return CREATE_SIMPLE_PRESCRIPTON_ITEM;
            }else{
                return SIMPLE_PRESCRIPTON_ITEM;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch(viewType){
                case CREATE_SIMPLE_PRESCRIPTON_ITEM:
                    View createSimplePrescriptionItem = inflater.inflate(R.layout.create_simple_prescription_item, parent, false);
                    viewHolder = new CreateSimplePrescriptionViewHolder(createSimplePrescriptionItem);
                    break;
                case SIMPLE_PRESCRIPTON_ITEM:
                    View simplePrescriptionItem = inflater.inflate(R.layout.simple_prescription_item, parent, false);
                    viewHolder = new SimplePrescriptionViewHolder(simplePrescriptionItem);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            switch (holder.getItemViewType()){
                case CREATE_SIMPLE_PRESCRIPTON_ITEM:
                    CreateSimplePrescriptionViewHolder createSimplePrescriptionViewHolder = (CreateSimplePrescriptionViewHolder) holder;
                    createSimplePrescriptionViewHolder.createSimplePrescriptionItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent bookSearchIntent = new Intent(getContext(), BookdocBookSearchActivity.class);
                            bookSearchIntent.putExtra("singleline",true);
                            startActivity(bookSearchIntent);
                        }
                    });
                    break;
                case SIMPLE_PRESCRIPTON_ITEM:
                    final SimplePrescriptionViewHolder simplePrescriptionViewHolder = (SimplePrescriptionViewHolder) holder;
                    final Data data = singlineData.get(position-1);
                    try{
                        if(data != null){
                            imageList.add(data.getImagePath());
                            commentList.add(data.getDescription());
                            fontPositionList.add(data.getFont());

                            int imageType = data.getImageType();
                            if(imageType != 0){
                                Glide.with(getContext())
                                        .load(BookdocImageList.getBookdocImage(imageType).intValue())
                                        .into(simplePrescriptionViewHolder.bookSingleLineBackgroundImg);
                                simplePrescriptionViewHolder.bookSingleLineComment.setText(data.getDescription());
                            }else{
                                Glide.with(getContext())
                                        .load(data.getImagePath())
                                        .into(simplePrescriptionViewHolder.bookSingleLineBackgroundImg);
                                simplePrescriptionViewHolder.bookSingleLineComment.setText(data.getDescription());
                            }
                        }
                        switch(data.getFont()){
                            case 1:
                                simplePrescriptionViewHolder.bookSingleLineComment.setTypeface(typeNanumR);
                                break;
                            case 2:
                                simplePrescriptionViewHolder.bookSingleLineComment.setTypeface(typeMyeongjo);
                                break;
                            case 3:
                                simplePrescriptionViewHolder.bookSingleLineComment.setTypeface(typeBrush);
                                break;
                        }
                        switch (data.getAlignment()){
                            case 1:
                                simplePrescriptionViewHolder.bookSingleLineComment.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                                break;
                            case 2:
                                simplePrescriptionViewHolder.bookSingleLineComment.setGravity(Gravity.CENTER);
                                break;
                            case 3:
                                simplePrescriptionViewHolder.bookSingleLineComment.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
                                break;
                        }
                    }catch (NullPointerException e){

                    }

                    simplePrescriptionViewHolder.simplePrescriptionBlur.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("position", String.valueOf(position-1));
                            Intent simplePrescriptionDetailIntent = new Intent(getContext(), BookdocSimplePrescriptionDetailActivity.class);
                            simplePrescriptionDetailIntent.putExtra("currentPosition",position-1);
                            startActivity(simplePrescriptionDetailIntent);
                        }
                    });

            }
        }

        @Override
        public int getItemCount() {
            return singlineData.size()+1;
        }
        public void addItems(ArrayList<Data> datas){
            singlineData.addAll(datas);
            notifyDataSetChanged();
        }

        public void addItem(Data data){
            singlineData.add(0, data);
            notifyDataSetChanged();
        }

        public void removeItems(){
            singlineData.clear();
            notifyDataSetChanged();
        }


        public void updateItem(int position, Data data){
            singlineData.set(position, data);
            notifyDataSetChanged();
        }


        public class CreateSimplePrescriptionViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout createSimplePrescriptionItem;
            ViewGroup.LayoutParams layoutParams;

            public CreateSimplePrescriptionViewHolder(View itemView) {
                super(itemView);
                createSimplePrescriptionItem = (RelativeLayout) itemView.findViewById(R.id.create_simple_prescription_item);
                layoutParams = createSimplePrescriptionItem.getLayoutParams();
                layoutParams.width = contentWidth;
                layoutParams.height = contentWidth;
            }
        }

        public class SimplePrescriptionViewHolder extends RecyclerView.ViewHolder{
            LinearLayout simplePrescriptionBlur;
            ViewGroup.LayoutParams backgroundLayoutParams;
            ViewGroup.LayoutParams blurLayoutParams;
            TextView bookSingleLineComment;
            ImageView bookSingleLineBackgroundImg;

            public SimplePrescriptionViewHolder(View itemView) {
                super(itemView);
                bookSingleLineBackgroundImg= (ImageView) itemView.findViewById(R.id.simple_prescription_item_container);
                backgroundLayoutParams = bookSingleLineBackgroundImg.getLayoutParams();
                backgroundLayoutParams.width = contentWidth;
                backgroundLayoutParams.height = contentWidth;
                bookSingleLineComment = (TextView) itemView.findViewById(R.id.textview_singline_comment);
                simplePrescriptionBlur = (LinearLayout) itemView.findViewById(R.id.simple_prescription_item_blur);
                blurLayoutParams = simplePrescriptionBlur.getLayoutParams();
                blurLayoutParams.width = contentWidth;
                blurLayoutParams.height = contentWidth;
            }
        }
    }

    private class SimplePrescriptionItemDecoration extends RecyclerView.ItemDecoration{
        private int spanCount;
        private int spacing;

        public SimplePrescriptionItemDecoration(int spanCount, int spacing) {
            this.spanCount = spanCount;
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if(position >= spanCount){
                outRect.top = spacing;
            }
            outRect.left = column*spacing;
        }
    }

    public class BookSinglelinePrescriptionAsyncTask extends AsyncTask<Integer, String, Singlelines> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.show();
            }
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(),message  ,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Singlelines doInBackground(Integer... integers) {
            int skip = integers[0];
            int count = integers[1];
            Response response = null;
            OkHttpClient toServer;
            Singlelines singlelines = new Singlelines();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_SINGLELINES,skip, count))
                        .header("Accept", "application/json")
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                if(response.isSuccessful()){
                    Log.d("SimplePrescription", "SimplePrescription: "+responseMessage);
                    singlelines.setData(new JSONObject(responseMessage));
                }else{
                    Log.e("요청에러", response.message().toString());
                }

            }catch(Exception e){
                Log.e("파싱에러", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            }finally{
                if(response != null){
                    response.close();
                }
            }
            return singlelines;
        }
        @Override
        public void onPostExecute(Singlelines singlelines) {
            super.onPostExecute(singlelines);
            try {
                simplePrescriptionAdapter.addItems(singlelines.getData());
                totalCount = singlelines.getTotaled();
            }catch (Exception e){

            }
            if(bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.dismiss();
            }
        }
    }


    @Subscribe
    public void addSinglePrescription(Data data){
        simplePrescriptionAdapter.addItem(data);
    }

    @Subscribe
    public void removeSinglePrescription(SinglePrescriptionDel singlePrescriptionDelId){
        delItemId.add(singlePrescriptionDelId.getSinglePrescriptionDelId());
    }

    @Subscribe
    public void updateSingleLine(SingleLineEdit singleLineEdit){
        simplePrescriptionAdapter.updateItem(singleLineEdit.getPosition(), singleLineEdit.getData());
        simplePrescriptionRecyclerView.scrollToPosition(singleLineEdit.getPosition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bookdocProgressDialog != null) {
            bookdocProgressDialog.dismiss();
            bookdocProgressDialog = null;
        }
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(delItemId.size() > 0){
            simplePrescriptionAdapter.removeItems();
            new BookSinglelinePrescriptionAsyncTask().execute(0,9);
            delItemId.clear();
        }
    }
}
