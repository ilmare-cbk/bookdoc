package com.kr.bookdoc.bookdoc.bookdoccustomdialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.kr.bookdoc.bookdoc.R;


public class BookdocCommentDialog extends DialogFragment implements View.OnClickListener{
    TextView bookdocCommentEdit, bookdocCommentDelete;
    OnEditCommentListener onEditCommentListener;
    OnDeleteCommentListener onDeleteCommentListener;
    int commentId;
    int position;

    public static BookdocCommentDialog newInstances(int commentId, int position) {
        BookdocCommentDialog bookdocCommentDialog = new BookdocCommentDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("commentId", commentId);
        bundle.putInt("position", position);
        bookdocCommentDialog.setArguments(bundle);
        return bookdocCommentDialog;
    }

    @Override
    public void onStart() {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setStyle(STYLE_NORMAL, R.style.BookdocShareDialogTheme);
        View view = inflater.from(getContext()).inflate(R.layout.bookdoc_comment_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        commentId = getArguments().getInt("commentId");
        position = getArguments().getInt("position");

        bookdocCommentEdit = (TextView) view.findViewById(R.id.bookdoc_comment_dialog_edit);
        bookdocCommentDelete = (TextView) view.findViewById(R.id.bookdoc_comment_dialog_delete);
        bookdocCommentEdit.setOnClickListener(this);
        bookdocCommentDelete.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bookdoc_comment_dialog_edit:
                if(onEditCommentListener != null){
                    onEditCommentListener.onEditComment(commentId);
                }
                break;
            case R.id.bookdoc_comment_dialog_delete:
                if(onDeleteCommentListener != null){
                    onDeleteCommentListener.onDeleteComment(commentId, position);
                }
                break;
        }
    }

    public interface OnEditCommentListener {
        void onEditComment(int commentId);
    }

    public void setOnEditComment(OnEditCommentListener onEditCommentListener){
        this.onEditCommentListener = onEditCommentListener;
    }

    public interface OnDeleteCommentListener {
        void onDeleteComment(int commentId, int position);
    }

    public void setOnDeleteComment(OnDeleteCommentListener onDeleteCommentListener){
        this.onDeleteCommentListener = onDeleteCommentListener;
    }
}
