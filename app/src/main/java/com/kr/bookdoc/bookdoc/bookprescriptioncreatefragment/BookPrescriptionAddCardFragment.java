package com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.kr.bookdoc.bookdoc.BookdocBookPrescriptionEditAcitivity;
import com.kr.bookdoc.bookdoc.BookdocCreatePrescriptionActivity;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;


public class BookPrescriptionAddCardFragment extends Fragment implements View.OnClickListener {
    ImageButton bookPrescriptionAddCardView;
    Activity activity;

    public static BookPrescriptionAddCardFragment newInstance() {
        BookPrescriptionAddCardFragment bookPrescriptionAddCardFragment = new BookPrescriptionAddCardFragment();
        return bookPrescriptionAddCardFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookdocCreatePrescriptionActivity) {
            activity = (BookdocCreatePrescriptionActivity) context;
        } else if (context instanceof BookdocBookPrescriptionEditAcitivity) {
            activity = (BookdocBookPrescriptionEditAcitivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_add_card, container, false);
        bookPrescriptionAddCardView = (ImageButton) view.findViewById(R.id.bookdoc_create_prescription_default_card);
        bookPrescriptionAddCardView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (activity instanceof BookdocCreatePrescriptionActivity) {
            ((BookdocCreatePrescriptionActivity) activity).bookPrescriptionCreatePagerAdapter.appendBookPrescriptionDefaultFragment(
                    BookPrescriptionCreateDefaultFragment.newInstance()
            );
        } else if (activity instanceof BookdocBookPrescriptionEditAcitivity) {
            ((BookdocBookPrescriptionEditAcitivity) activity).bookdocBookPrescriptionEditPagerAdapter.appendBookPrescriptionDefaultFragment(
                    BookPrescriptionCreateDefaultFragment.newInstance()
            );
        }
    }
}
