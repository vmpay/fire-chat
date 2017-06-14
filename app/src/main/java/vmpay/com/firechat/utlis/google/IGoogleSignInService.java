package vmpay.com.firechat.utlis.google;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

/**
 * Created by Andrew on 10/06/2017.
 */

public interface IGoogleSignInService
{
	void startSignInActivity(Activity activity);

	void startSignOut(ResultCallback<Status> resultCallback);

	void connectToGoogleApi();

	void disconnectFromGoogleApi();

	Scope[] getScopeArray();

	void build(GoogleApiClient.OnConnectionFailedListener listener, GoogleApiClient.ConnectionCallbacks callbacks);

	GoogleSignInResult getSignInResultFromIntent(Intent data);
}
