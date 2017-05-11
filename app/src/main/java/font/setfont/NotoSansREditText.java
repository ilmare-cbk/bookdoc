package font.setfont;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

public class NotoSansREditText extends EditText {
    public NotoSansREditText(Context context) {
        super(context);
        setType(context);
    }

    public NotoSansREditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public NotoSansREditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NotoSansREditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setType(context);
    }

    private void setType(Context context) {
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "NotoSansKR_Regular_Hestia.otf"));
    }
}
