package com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import com.kr.bookdoc.bookdoc.BookdocBookSearchActivity;
import com.kr.bookdoc.bookdoc.BookdocCreatePrescriptionActivity;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocImageSelectDialog;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocutils.OnSetBookInfoListener;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookdocImage;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.daumbookdata.DaumBookData;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptiondetail.PrescriptionMain;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;


public class BookPrescriptionCreateMainFragment extends Fragment implements View.OnClickListener, OnSetBookInfoListener {
    private final int SEARCH_BOOK_REQUEST_CODE = 0;
    EditText bookPrescriptionCreateMainEdit;
    LinearLayout bookPrescriptionCreateMainSearchBook;
    CardView bookPrescriptionCreateMainAddBackground;
    LinearLayout bookPrescriptionCreateMainBlur;
    ImageView bookPrescriptionCreateMainBackgroundImg;
    TextView bookPrescriptionCreateMainBookTitle, bookPrescriptionCreateMainBookAuthor,
            bookPrescriptionCreateMainBookPublisher, isbn;
    CustomImageView bookPrescriptionCreateMainBookImage;
    private Uri imageCaptureUri;
    private String s;
    private int imageType;
    PrescriptionMain prescriptionMain;
    DaumBookData daumBookData;

    public static BookPrescriptionCreateMainFragment newInstance() {
        BookPrescriptionCreateMainFragment bookPrescriptionCreateMainFragment = new BookPrescriptionCreateMainFragment();
        return bookPrescriptionCreateMainFragment;
    }

    public static BookPrescriptionCreateMainFragment newInstance(PrescriptionMain prescriptionMain) {
        BookPrescriptionCreateMainFragment bookPrescriptionCreateMainFragment = new BookPrescriptionCreateMainFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("prescriptionMain", prescriptionMain);
        bookPrescriptionCreateMainFragment.setArguments(bundle);
        return bookPrescriptionCreateMainFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookdocCreatePrescriptionActivity) {
            ((BookdocCreatePrescriptionActivity) context).setOnPassBookInfoListener(this);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_create_main, container, false);

        bookPrescriptionCreateMainBookImage = (CustomImageView) view.findViewById(R.id.book_prescription_create_book_img);
        bookPrescriptionCreateMainBookTitle = (TextView) view.findViewById(R.id.book_prescription_create_book_title);
        bookPrescriptionCreateMainBookAuthor = (TextView) view.findViewById(R.id.book_prescription_create_book_author);
        bookPrescriptionCreateMainBookPublisher = (TextView) view.findViewById(R.id.book_prescription_create_book_publisher);
        isbn = (TextView) view.findViewById(R.id.isbn);

        bookPrescriptionCreateMainBlur = (LinearLayout) view.findViewById(R.id.book_prescription_create_main_blur);
        bookPrescriptionCreateMainEdit = (EditText) view.findViewById(R.id.book_prescription_create_title_edit);
        bookPrescriptionCreateMainSearchBook = (LinearLayout) view.findViewById(R.id.book_prescription_create_main_search_book);
        bookPrescriptionCreateMainSearchBook.setOnClickListener(this);
        bookPrescriptionCreateMainAddBackground = (CardView) view.findViewById(R.id.book_prescription_create_main_add_background_img);
        bookPrescriptionCreateMainAddBackground.setOnClickListener(this);
        bookPrescriptionCreateMainBackgroundImg = (ImageView) view.findViewById(R.id.book_prescription_create_main_background_img);

        if (getArguments() != null) {
            prescriptionMain = new PrescriptionMain();
            prescriptionMain = (PrescriptionMain) getArguments().getSerializable("prescriptionMain");
            bookPrescriptionCreateMainSearchBook.setVisibility(View.GONE);
        }

        if (prescriptionMain != null) {
            Log.d("prescriptionMain", "not null");
            bookPrescriptionCreateMainEdit.setText(prescriptionMain.getTitle());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bookPrescriptionCreateMainEdit.setTextColor(getResources().getColor(R.color.white, null));
            } else {
                bookPrescriptionCreateMainEdit.setTextColor(getResources().getColor(R.color.white));
            }
            bookPrescriptionCreateMainBookTitle.setText(prescriptionMain.getBookTitle());
            bookPrescriptionCreateMainBookAuthor.setText(prescriptionMain.getBookAuthor());
            bookPrescriptionCreateMainBookPublisher.setText(prescriptionMain.getBookPublisher());
            imageType = prescriptionMain.getImageType();
            if (imageType != 0) {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(BookdocImageList.getBookdocImage(imageType).intValue())
                        .into(bookPrescriptionCreateMainBackgroundImg);
            } else {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(prescriptionMain.getImagePath())
                        .into(bookPrescriptionCreateMainBackgroundImg);
            }
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(prescriptionMain.getBookImagePath())
                    .into(bookPrescriptionCreateMainBookImage);
            bookPrescriptionCreateMainBlur.setVisibility(View.VISIBLE);
        } else {
            Log.d("prescriptionMain", "null");
        }

        if (savedInstanceState != null) {
            String imagePath = savedInstanceState.getString("imagePath");
            imageType = savedInstanceState.getInt("imageType");
            Log.d("imageType saveinstance", "imagetype: " + imageType);
            if (imageType != 0) {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(BookdocImageList.getBookdocImage(imageType))
                        .into(bookPrescriptionCreateMainBackgroundImg);
            } else {
                imageCaptureUri = Uri.parse(imagePath);
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(imageCaptureUri)
                        .into(bookPrescriptionCreateMainBackgroundImg);
            }
            daumBookData = (DaumBookData) savedInstanceState.getSerializable("bookInfo");
            if (daumBookData != null) {
                setBookInfo(daumBookData);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bookPrescriptionCreateMainEdit.setTextColor(getResources().getColor(R.color.white, null));
            } else {
                bookPrescriptionCreateMainEdit.setTextColor(getResources().getColor(R.color.white));
            }
            bookPrescriptionCreateMainBlur.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageCaptureUri != null) {
            outState.putString("imagePath", String.valueOf(imageCaptureUri));
        } else {
            if (prescriptionMain != null) {
                outState.putString("imagePath", prescriptionMain.getImagePath());
            } else {
                outState.putString("imagePath", String.valueOf(imageCaptureUri));
            }
        }
        outState.putSerializable("imageType", imageType);
        outState.putSerializable("bookInfo", daumBookData);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.book_prescription_create_main_search_book:
                Intent searchBookIntent = new Intent(getActivity(), BookdocBookSearchActivity.class);
                getActivity().startActivityForResult(searchBookIntent, SEARCH_BOOK_REQUEST_CODE);
                break;
            case R.id.book_prescription_create_main_add_background_img:
                BookdocImageSelectDialog bookdocImageSelectDialog = BookdocImageSelectDialog.newInstance(true);
                bookdocImageSelectDialog.show(getActivity().getSupportFragmentManager(), "imageSelect");
                break;
        }
    }

    @Subscribe
    public void setBookdocImage(BookdocImage bookdocImage) {
        boolean imageFrom = bookdocImage.isCheckImageFrom();
        if (imageFrom) {
            imageCaptureUri = bookdocImage.getImagePath();
            s = getRealPathFromURI(imageCaptureUri);
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(imageCaptureUri)
                    .into(bookPrescriptionCreateMainBackgroundImg);
            imageType = 0;
        } else {
            imageType = Integer.parseInt(String.valueOf(bookdocImage.getImagePath()));
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(BookdocImageList.getBookdocImage(imageType))
                    .into(bookPrescriptionCreateMainBackgroundImg);
            imageCaptureUri = null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bookPrescriptionCreateMainEdit.setTextColor(getResources().getColor(R.color.white, null));
        } else {
            bookPrescriptionCreateMainEdit.setTextColor(getResources().getColor(R.color.white));
        }
        bookPrescriptionCreateMainBlur.setVisibility(View.VISIBLE);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        String imageUri;
        @SuppressWarnings("deprecation")
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        imageUri = cursor.getString(column_index);
        return imageUri;
    }

    public void setPrescriptionMainData() {
        PrescriptionPost prescriptionPost = PrescriptionPost.getInstance();
        prescriptionPost.setTitle(bookPrescriptionCreateMainEdit.getText().toString());
        prescriptionPost.setImageType(imageType);
        prescriptionPost.setImage(s);
        prescriptionPost.setDescription("this is not used");
        prescriptionPost.setBookIsbn(isbn.getText().toString());
    }

    @Override
    public void setBookInfo(DaumBookData daumBookData) {
        this.daumBookData = daumBookData;
        String isbn13 = daumBookData.getIsbn13();
        Glide.with(getContext())
                .load(daumBookData.getCover_l_url())
                .into(bookPrescriptionCreateMainBookImage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bookPrescriptionCreateMainBookTitle.setText(Html.fromHtml(Html.fromHtml(daumBookData.getTitle(), Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY));
            bookPrescriptionCreateMainBookAuthor.setText(Html.fromHtml(Html.fromHtml(daumBookData.getAuthor(), Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            bookPrescriptionCreateMainBookTitle.setText(Html.fromHtml(Html.fromHtml(daumBookData.getTitle()).toString()));
            bookPrescriptionCreateMainBookAuthor.setText(Html.fromHtml(Html.fromHtml(daumBookData.getAuthor()).toString()));
        }
        bookPrescriptionCreateMainBookPublisher.setText(daumBookData.getPub_nm());
        if (isbn13 != null || !TextUtils.isEmpty(isbn13) || isbn13.length() != 0 || isbn13 != "") {
            isbn.setText(isbn13);
        } else {
            isbn.setText(daumBookData.getIsbn());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
}
