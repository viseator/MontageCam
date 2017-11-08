package com.viseator.montagecam;

import android.app.Application;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by viseator on 11/8/17.
 * Wu Di
 * viseator@gmail.com
 */

public class BaseApplication extends Application implements IAdobeAuthClientCredentials {
    private static final String CREATIVE_SDK_CLIENT_ID = "ada1c989091b455bb0425f5956968268";
    private static final String CREATIVE_SDK_CLIENT_SECRET = "2d291696-6565-457e-bd2f-9007a38be972";
    private static final String CREATIVE_SDK_REDIRECT_URI = "ams" +
            "+85b9a4e9d90ca1de4a7e79694c71f22ab06c0339://adobeid/ada1c989091b455bb0425f5956968268";
    private static final String[] CREATIVE_SDK_SCOPES = {"email", "profile", "address"};

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String[] getAdditionalScopesList() {
        return CREATIVE_SDK_SCOPES;
    }

    @Override
    public String getRedirectURI() {
        return CREATIVE_SDK_REDIRECT_URI;
    }
}
