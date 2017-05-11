package font.setfont;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Switch;


public class NotoRSwitch extends Switch{
    public NotoRSwitch(Context context) {
        super(context);
        setType(context);
    }

    public NotoRSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public NotoRSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NotoRSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setType(context);
    }

    private void setType(Context context) {
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "notokr-regular.ttf"));
    }
}
