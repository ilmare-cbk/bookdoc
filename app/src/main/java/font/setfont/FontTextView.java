package font.setfont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.kr.bookdoc.bookdoc.R;

public class FontTextView extends TextView {
    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyTypeface(context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyTypeface(context, attrs);
    }

    public FontTextView(Context context) {
        super(context);
    }

    private void applyTypeface(Context context, AttributeSet attrs){
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.FontTextView);
        String typefaceName = arr.getString(R.styleable.FontTextView_typeface);

        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
        } catch (Exception e) {
            Log.e("FontTextView",e.getMessage());
            arr.recycle();
            return;
        }

        setTypeface(typeface);
        arr.recycle();
    }

}