package com.kr.bookdoc.bookdoc.bookprescriptiondetailfragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.BookdocPropertyManager;
import com.kr.bookdoc.bookdoc.BookdocUserBookActivity;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocPrescriptionDetailMainDialog;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocDateFormat;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocutils.FlowLayout;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptiondetail.PrescriptionMain;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Tag;
import de.hdodenhof.circleimageview.CircleImageView;


public class BookPrescriptionMainFragment extends Fragment implements View.OnClickListener {
    private ImageView bookPrescriptionMainMore, bookPrescriptionMainBackgound;
    private PrescriptionMain prescriptionMain;
    private TextView bookPrescriptionMainTitle;
    private CircleImageView bookPrescriptionMainProfile;
    TextView bookPrescriptionMainUserName;
    TextView bookPrescriptionMainCreatedTime;
    private CustomImageView bookPrescriptionMainBookImg;
    TextView bookPrescriptionMainBookTitle;
    TextView bookPrescriptionMainBookAuthor;
    TextView bookPrescriptionMainBookPublisher;
    FlowLayout bookPrescriptionMainTagContainer;
    Typeface typeNotoSansR;
    ArrayList<Tag> tagArrayList;
    int tagCount;
    int margin;
    private LinearLayout bookPrescriptionMainFrgUserInfo;

    public BookPrescriptionMainFragment() {
    }

    public static BookPrescriptionMainFragment newInstance(PrescriptionMain prescriptionMain) {
        BookPrescriptionMainFragment bookPrescriptionMainFragment = new BookPrescriptionMainFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("prescriptionMain", prescriptionMain);
        bookPrescriptionMainFragment.setArguments(bundle);
        return bookPrescriptionMainFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prescriptionMain = new PrescriptionMain();
        prescriptionMain = (PrescriptionMain) getArguments().getSerializable("prescriptionMain");
        typeNotoSansR = Typeface.createFromAsset(getContext().getAssets(), "NotoSansKR_Regular_Hestia.otf");
        tagArrayList = prescriptionMain.getTags();
        tagCount = tagArrayList.size();
        margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_main, container, false);

        bookPrescriptionMainMore = (ImageView) view.findViewById(R.id.book_prescription_detail_main_more);
        bookPrescriptionMainMore.setOnClickListener(this);
        if (prescriptionMain.getUserId() != BookdocPropertyManager.getInstance().getUserId()) {
            bookPrescriptionMainMore.setVisibility(View.GONE);
        }

        bookPrescriptionMainTagContainer = (FlowLayout) view.findViewById(R.id.book_prescription_main_tag_container);
        for (int i = 0; i < tagCount; i++) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View rootview = layoutInflater.inflate(R.layout.bookdoc_prescription_tag, null);
            TextView textView = (TextView) rootview.findViewById(R.id.bookdoc_prescription_tag);
            textView.setTypeface(typeNotoSansR);
            textView.setText(tagArrayList.get(i).description);
            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(margin, margin, margin, margin);
            bookPrescriptionMainTagContainer.addView(textView, layoutParams);
        }

        bookPrescriptionMainFrgUserInfo = (LinearLayout) view.findViewById(R.id.book_prescription_main_userinfo);
        bookPrescriptionMainFrgUserInfo.setOnClickListener(this);
        bookPrescriptionMainBackgound = (ImageView) view.findViewById(R.id.book_prescription_main_background);
        bookPrescriptionMainTitle = (TextView) view.findViewById(R.id.book_prescription_main_title);
        bookPrescriptionMainProfile = (CircleImageView) view.findViewById(R.id.book_prescription_main_profile);
        bookPrescriptionMainUserName = (TextView) view.findViewById(R.id.book_prescription_main_username);
        bookPrescriptionMainCreatedTime = (TextView) view.findViewById(R.id.book_prescription_main_created_time);
        bookPrescriptionMainBookImg = (CustomImageView) view.findViewById(R.id.book_prescription_main_book_img);
        bookPrescriptionMainBookTitle = (TextView) view.findViewById(R.id.book_prescription_main_book_title);
        bookPrescriptionMainBookAuthor = (TextView) view.findViewById(R.id.book_prescription_main_book_author);
        bookPrescriptionMainBookPublisher = (TextView) view.findViewById(R.id.book_prescription_main_book_publisher);

        if (prescriptionMain.getImageType() != 0) {
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(BookdocImageList.getBookdocImage(prescriptionMain.getImageType()))
                    .into(bookPrescriptionMainBackgound);
        } else {
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(prescriptionMain.getImagePath())
                    .into(bookPrescriptionMainBackgound);
        }
        Glide.with(BookdocApplication.getBookdocContext())
                .load(prescriptionMain.getUserImagePath())
                .error(R.drawable.profile_default)
                .into(bookPrescriptionMainProfile);
        Glide.with(BookdocApplication.getBookdocContext())
                .load(prescriptionMain.getBookImagePath())
                .into(bookPrescriptionMainBookImg);

        bookPrescriptionMainTitle.setText(prescriptionMain.getTitle());
        bookPrescriptionMainUserName.setText(prescriptionMain.getUserName());
        bookPrescriptionMainCreatedTime.setText(BookdocDateFormat.setDateFormat("yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd", prescriptionMain.getCreated()));
        bookPrescriptionMainBookTitle.setText(prescriptionMain.getBookTitle());
        bookPrescriptionMainBookAuthor.setText(prescriptionMain.getBookAuthor());
        bookPrescriptionMainBookPublisher.setText(prescriptionMain.getBookPublisher());

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.book_prescription_detail_main_more:
                BookdocPrescriptionDetailMainDialog bookdocPrescriptionDetailMainDialog
                        = BookdocPrescriptionDetailMainDialog.newInstances(prescriptionMain.getPrescriptionId(), prescriptionMain.getPosition());
                bookdocPrescriptionDetailMainDialog.show(getActivity().getSupportFragmentManager(), "prescription_main_dialog");
                break;
            case R.id.book_prescription_main_userinfo:
                Intent intent = new Intent(getContext(), BookdocUserBookActivity.class);
                intent.putExtra("anotherUserId", prescriptionMain.getUserId());
                startActivity(intent);
                break;
        }
    }

}
