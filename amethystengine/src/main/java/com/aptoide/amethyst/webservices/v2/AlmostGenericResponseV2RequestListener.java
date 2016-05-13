package com.aptoide.amethyst.webservices.v2;

import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.webservices.Errors;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;


/**
 * Created by asantos on 29-07-2014.
 */
public abstract class AlmostGenericResponseV2RequestListener implements RequestListener<GenericResponseV2> {


    public AlmostGenericResponseV2RequestListener() {
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(Aptoide.getContext(), Aptoide.getContext().getString(R.string.error_occured), Toast.LENGTH_LONG).show();
    }

    public abstract void CaseOK();

    @Override
    public void onRequestSuccess(GenericResponseV2 genericResponse) {
        if ("OK".equals(genericResponse.getStatus())) {
            CaseOK();
        } else {
            HashMap<String, Integer> errorsMap = Errors.getErrorsMap();
            Integer stringId;
            String message;
            for (ErrorResponse error : genericResponse.getErrors()) {
                stringId = errorsMap.get(error.code);
                if (stringId != null) {
                    message = Aptoide.getContext().getString(stringId);
                } else {
                    message = error.msg;
                }
                Toast.makeText(Aptoide.getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
