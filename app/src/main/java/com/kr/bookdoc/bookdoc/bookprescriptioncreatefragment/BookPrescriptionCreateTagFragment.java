package com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.FlowLayout;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Tag;


public class BookPrescriptionCreateTagFragment extends Fragment implements View.OnClickListener {
    EditText bookPrescriptionCreateTagEdit;
    TextView bookPrescriptionCreateTagBtn;
    FlowLayout bookPrescriptionCreateTagContainer;
    ArrayList<String> tagsList = new ArrayList<>();
    ArrayList<Tag> tagArrayList = new ArrayList<>();

    public static BookPrescriptionCreateTagFragment newInstance() {
        BookPrescriptionCreateTagFragment bookPrescriptionCreateTagFragment = new BookPrescriptionCreateTagFragment();
        return bookPrescriptionCreateTagFragment;
    }

    public static BookPrescriptionCreateTagFragment newInstance(ArrayList<Tag> tagList) {
        BookPrescriptionCreateTagFragment bookPrescriptionCreateTagFragment = new BookPrescriptionCreateTagFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tagList", tagList);
        bookPrescriptionCreateTagFragment.setArguments(bundle);
        return bookPrescriptionCreateTagFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_create_tag, container, false);
        bookPrescriptionCreateTagEdit = (EditText) view.findViewById(R.id.edittext_bookdoc_create_prescription_tag);
        bookPrescriptionCreateTagEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (bookPrescriptionCreateTagEdit.getText().toString().trim().length() > 0) {
                        if (bookPrescriptionCreateTagContainer.getChildCount() > 4) {
                            Toast.makeText(getContext(), "최대 5개의 태그까지 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            addTag(bookPrescriptionCreateTagEdit.getText().toString());
                        }
                    } else {
                        Toast.makeText(getContext(), "태그를 입력 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    bookPrescriptionCreateTagEdit.setText("");
                }
                return false;
            }
        });
        bookPrescriptionCreateTagBtn = (TextView) view.findViewById(R.id.textview_create_prescription_tag);
        bookPrescriptionCreateTagBtn.setOnClickListener(this);
        bookPrescriptionCreateTagContainer = (FlowLayout) view.findViewById(R.id.flowlayout_create_prescription_tag);

        if (getArguments() != null) {
            tagArrayList = (ArrayList<Tag>) getArguments().getSerializable("tagList");
            for (int i = 0; i < tagArrayList.size(); i++) {
                addTag(tagArrayList.get(i).getDescription());
            }
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textview_create_prescription_tag:
                if (bookPrescriptionCreateTagEdit.getText().toString().trim().length() > 0) {
                    if (bookPrescriptionCreateTagContainer.getChildCount() > 4) {
                        Toast.makeText(getContext(), "최대 5개의 태그까지 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        addTag(bookPrescriptionCreateTagEdit.getText().toString());
                    }
                } else {
                    Toast.makeText(getContext(), "태그를 입력 해주세요.", Toast.LENGTH_SHORT).show();
                }
                bookPrescriptionCreateTagEdit.setText("");
                break;
        }
    }


    public void addTag(String tagContent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        final View rootview = layoutInflater.inflate(R.layout.bookdoc_tag_item, null);
        TextView tag = (TextView) rootview.findViewById(R.id.bookdoc_tag_content);
        tag.setText(tagContent);
        tagsList.add(tagContent);
        ImageView tagDelBtn = (ImageView) rootview.findViewById(R.id.bookdoc_tag_delete);
        tagDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagsList.remove(bookPrescriptionCreateTagContainer.indexOfChild(rootview));
                bookPrescriptionCreateTagContainer.removeView(rootview);
            }
        });
        final int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());
        final int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                getResources().getDisplayMetrics());
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, top, right, 0);
        bookPrescriptionCreateTagContainer.addView(rootview, layoutParams);
    }

    public void setPrescriptionMainData() {
        PrescriptionPost prescriptionPost = PrescriptionPost.getInstance();
        prescriptionPost.setTags(tagsList);
    }
}
