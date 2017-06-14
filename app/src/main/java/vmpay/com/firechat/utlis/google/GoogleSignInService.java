package vmpay.com.firechat.utlis.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import vmpay.com.firechat.AppConstants;

/**
 * Created by Andrew on 10/06/2017.
 */

public class GoogleSignInService implements IGoogleSignInService
{
	private static final String TAG = "GoogleSignInService";

	private GoogleSignInOptions googleSignInOptions;
	private GoogleApiClient googleApiClient;

	private static final String SERVER_ID_CLIENT = "924027928305-ptc4gjfs1nnbjikcogmm3c9l5ctrhg6p.apps.googleusercontent.com";
	private Context context;

	public GoogleSignInService(Context context)
	{
		this.context = context;
	}

	@Override
	public void startSignInActivity(Activity activity)
	{
//		googleApiClient.clearDefaultAccountAndReconnect();

		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
		activity.startActivityForResult(signInIntent, AppConstants.ActivityRequestCode.REQUEST_GOOGLE_SIGN_IN);
	}

	@Override
	public void startSignOut(ResultCallback<Status> resultCallback)
	{
		if(googleApiClient == null) return;

		if(googleApiClient.isConnected())
		{
			Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(resultCallback);
		}
	}

	@Override
	public void connectToGoogleApi()
	{
		if(googleApiClient == null)
		{
			Log.d(TAG, "connectToGoogleApi without build googleApiClient");
			return;
		}

		googleApiClient.connect();
	}

	@Override
	public void disconnectFromGoogleApi()
	{
		if(googleApiClient == null)
		{
			Log.d(TAG, "disconnectFromGoogleApi without build googleApiClient");
			return;
		}

		googleApiClient.disconnect();
	}

	@Override
	public Scope[] getScopeArray()
	{
		return googleSignInOptions.getScopeArray();
	}

	@Override
	public void build(GoogleApiClient.OnConnectionFailedListener listener, GoogleApiClient.ConnectionCallbacks callbacks)
	{
		googleSignInOptions = new GoogleSignInOptions.Builder()
				.requestId()        //add 'openid' scope
				.requestEmail()     //add 'email' scope
				.requestProfile()   //add 'profile' scope
				.requestIdToken(SERVER_ID_CLIENT)
				.build();

		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
				.addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions);

		if(listener != null)
		{
			builder.addOnConnectionFailedListener(listener);
		}
		if(callbacks != null)
		{
			builder.addConnectionCallbacks(callbacks);
		}

		googleApiClient = builder.build();
	}

	@Override
	public GoogleSignInResult getSignInResultFromIntent(Intent data)
	{
		return Auth.GoogleSignInApi.getSignInResultFromIntent(data);
	}
}
