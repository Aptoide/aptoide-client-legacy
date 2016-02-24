package com.aptoide.amethyst.Ads;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by fabio on 15-10-2015.
 */
public class FaceBookAdButton extends Button {

    public FaceBookAdButton(Context context) {
        super(context);
    }
    public FaceBookAdButton (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceBookAdButton (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
/*    @Override
    public void setOnClickListener(OnClickListener l) {
        final Field[] fields = l.getClass().getFields();
        for (Field field : fields) {
            System.out.println("pois#  field :" +field );
        }

       final Method[] methods = l.getClass().getMethods();
        for (Method method : methods) {
            System.out.println("pois#  method :" +method );
        }
    }*/
}