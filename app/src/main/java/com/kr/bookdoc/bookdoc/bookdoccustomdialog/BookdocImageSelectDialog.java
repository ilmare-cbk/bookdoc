package com.kr.bookdoc.bookdoc.bookdoccustomdialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.kr.bookdoc.bookdoc.BookdocImageListActivity;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookdocImage;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookdocImageSingle;


public class BookdocImageSelectDialog extends DialogFragment implements OnClickListener{
    private TextView bookdocImageSelectGallery;
    private TextView bookdocImageSelectBookdoc;
    private final int PICK_FROM_ALBUM = 1;
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private boolean checkFrom;

    public static BookdocImageSelectDialog newInstance(boolean checkFrom){
        BookdocImageSelectDialog bookdocImageSelectDialog = new BookdocImageSelectDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("checkFrom", checkFrom);
        bookdocImageSelectDialog.setArguments(bundle);
        return bookdocImageSelectDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.bookdoc_image_select_dialog, container, false);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        setStyle(STYLE_NORMAL, R.style.BookdocShareDialogTheme);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        checkFrom = getArguments().getBoolean("checkFrom");

        bookdocImageSelectGallery = (TextView) view.findViewById(R.id.bookdoc_image_select_gallery);
        bookdocImageSelectBookdoc = (TextView) view.findViewById(R.id.bookdoc_image_select_bookdoc);
        bookdocImageSelectGallery.setOnClickListener(this);
        bookdocImageSelectBookdoc.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bookdoc_image_select_gallery:
                checkPermission();
                break;
            case R.id.bookdoc_image_select_bookdoc:
                Intent bookdocImageListIntet = new Intent(getContext(),BookdocImageListActivity.class);
                bookdocImageListIntet.putExtra("checkFrom", checkFrom);
                startActivity(bookdocImageListIntet);
                dismiss();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            switch (requestCode){
                case PICK_FROM_ALBUM:
                    if(checkFrom){
                        BusProvider.getInstance().post(new BookdocImage(data.getData(), true));
                    }else {
                        BusProvider.getInstance().post(new BookdocImageSingle(data.getData(), true));
                    }
                    dismiss();
                    break;
            }
        }
    }

    public void doTaskAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_STORAGE);
            }else{
                doTaskAlbumAction();
            }
        }else{
            doTaskAlbumAction();
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    doTaskAlbumAction();
                }
                break;
        }
    }
}
