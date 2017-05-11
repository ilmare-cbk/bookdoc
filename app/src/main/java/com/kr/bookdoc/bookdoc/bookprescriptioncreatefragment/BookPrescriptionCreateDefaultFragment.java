package com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kr.bookdoc.bookdoc.BookdocBookPrescriptionBase;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocPrescriptionCardDeleteDialog;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.Card;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;

import static com.facebook.FacebookSdk.getApplicationContext;


public class BookPrescriptionCreateDefaultFragment extends Fragment implements View.OnTouchListener {
    EditText bookdocCreatePrescriptionDefaultEdit, bookdocCreatePrescriptionDefaultQuestion;
    TextView bookdocCreatePrescriptionDefaultTv;
    com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card card;
    int guideIndex;
    private float mDownX;
    private float mDownY;
    BookdocPrescriptionCardDeleteDialog bookdocPrescriptionCardDeleteDialog;
    Point mScreenSize;
    BookdocBookPrescriptionBase mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BookdocBookPrescriptionBase) context;
    }

    public static BookPrescriptionCreateDefaultFragment newInstance() {
        BookPrescriptionCreateDefaultFragment bookPrescriptionCreateDefaultFragment = new BookPrescriptionCreateDefaultFragment();
        return bookPrescriptionCreateDefaultFragment;
    }


    public static BookPrescriptionCreateDefaultFragment newInstance(com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card card, int index) {
        BookPrescriptionCreateDefaultFragment bookPrescriptionCreateDefaultFragment = new BookPrescriptionCreateDefaultFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("card", card);
        bundle.putInt("guideIndex", index);
        bookPrescriptionCreateDefaultFragment.setArguments(bundle);
        return bookPrescriptionCreateDefaultFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_create_default, container, false);
        bookdocCreatePrescriptionDefaultEdit = (EditText) view.findViewById(R.id.bookdoc_create_prescription_default_edit);
        bookdocCreatePrescriptionDefaultQuestion = (EditText) view.findViewById(R.id.bookdoc_create_prescription_default_question);
        bookdocCreatePrescriptionDefaultTv = (TextView) view.findViewById(R.id.bookdoc_create_prescription_default_tv);
        bookdocCreatePrescriptionDefaultEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = bookdocCreatePrescriptionDefaultEdit.getText().toString().length();
                bookdocCreatePrescriptionDefaultTv.setText(String.valueOf(length));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
        });

        if (getArguments() != null) {
            card = new com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card();
            card = (com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card) getArguments().getSerializable("card");
            guideIndex = getArguments().getInt("getIndex");
            bookdocCreatePrescriptionDefaultQuestion.setText(card.getTitle());
            bookdocCreatePrescriptionDefaultEdit.setText(card.getDescription());
        }


        mScreenSize = new Point();
        mActivity.getWindowManager().getDefaultDisplay().getSize(mScreenSize);


        view.setOnTouchListener(this);
        return view;
    }

    public void setPrescriptionMainData() {
        PrescriptionPost prescriptionPost = PrescriptionPost.getInstance();
        Card card = new Card();
        card.setTitle(bookdocCreatePrescriptionDefaultQuestion.getText().toString());
        card.setDescription(bookdocCreatePrescriptionDefaultEdit.getText().toString());
        prescriptionPost.getCards().add(card);
    }


    float dx, dy, newY, newX;
    boolean bDelete;

    @Override
    public boolean onTouch(final View view, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                bookdocPrescriptionCardDeleteDialog = null;
                mDownX = event.getX();
                mDownY = event.getY();
                bDelete = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                dy = event.getY() - mDownY;
                dx = event.getX() - mDownX;
                newY = view.getY() + dy;
                newX = view.getX() + dx;

                if (view.getY() > 50) {
                    ReturnCardView(view);
                    return false;
                } else
                    view.setY(newY);

                if (dy < 0 && mActivity.mPagerState == ViewPager.SCROLL_STATE_IDLE) {
                    if (Math.abs(view.getY() - dy) > 50)
                        bDelete = Math.abs(view.getY() - dy) > (mScreenSize.y / 4);
                }

                break;
            }
            case MotionEvent.ACTION_UP: {

                if (bDelete) {
                    if (bookdocPrescriptionCardDeleteDialog == null) {
                        bookdocPrescriptionCardDeleteDialog = BookdocPrescriptionCardDeleteDialog.newInstace(new BookdocPrescriptionCardDeleteDialog.DialogResultInterface() {
                            @Override
                            public void onResult(boolean bOK) {
                                RemoveDialog(bOK, view);
                                bookdocPrescriptionCardDeleteDialog.onDestroyView();
                            }
                        });

                        bookdocPrescriptionCardDeleteDialog.show(getActivity(), "prescription_main_dialog");

                    }
                } else ReturnCardView(view);
                break;
            }
        }

        return true;

    }

    private void RemoveDialog(boolean bOK, View view) {
        if (bOK) {
            int nFinishPoint = -mScreenSize.y;

            if (view.getY() > mScreenSize.y / 10)
                nFinishPoint = mScreenSize.y;

            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("translationY", view.getY(), nFinishPoint));
            anim.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.anim.accelerate_interpolator));
            anim.setDuration(400);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    Toast.makeText(getApplicationContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    mActivity.removePagerItem();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationStart(Animator animator) {
                }

            });
            anim.start();

        } else
            ReturnCardView(view);

    }

    private void ReturnCardView(View view) {
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("translationY", view.getY(), 0f));
        anim.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.anim.anticipate_overshoot_interpolator));
        anim.setDuration(400);
        anim.start();
    }


}
