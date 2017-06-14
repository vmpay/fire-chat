package vmpay.com.firechat.presenters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.GoogleAuthProvider;

import vmpay.com.firechat.AppConstants;
import vmpay.com.firechat.activities.auth.fragments.LoginFragment;
import vmpay.com.firechat.utlis.firebase.IFirebaseSignInService;
import vmpay.com.firechat.utlis.google.IGoogleSignInService;

/**
 * Created by Andrew on 10/06/2017.
 */

public class UserLoginPresenter
{
	private static final String TAG = "UserLoginPresenter";

	private LoginFragment loginFragment;

	private boolean signInApiBuilt = false;

	private IGoogleSignInService googleSignInService;
	private IFirebaseSignInService firebaseSignInService;

	public UserLoginPresenter(IGoogleSignInService googleSignInService, IFirebaseSignInService firebaseSignInService)
	{
		this.googleSignInService = googleSignInService;
		this.firebaseSignInService = firebaseSignInService;
	}

	public void setLoginFragment(LoginFragment loginFragment)
	{
		this.loginFragment = loginFragment;
	}

	public void prepareGoogleSignIn()
	{
		if(!signInApiBuilt)
		{
			googleSignInService.build(onConnectionFailedListener, connectionCallbacks);
			signInApiBuilt = true;
		}
	}

	private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener()
	{
		@Override
		public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
		{
			Log.d(TAG, "onConnectionFailed ");
		}
	};

	private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks()
	{
		@Override
		public void onConnected(@Nullable Bundle bundle)
		{
			Log.d(TAG, "onConnected ");
		}

		@Override
		public void onConnectionSuspended(int i)
		{
			Log.d(TAG, "onConnectionSuspended ");
		}
	};

	public void activateLoginApi()
	{
		googleSignInService.connectToGoogleApi();
	}

	public void loginWithGoogle(Activity activity)
	{
		prepareGoogleSignIn();
		googleSignInService.startSignInActivity(activity);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == AppConstants.ActivityRequestCode.REQUEST_GOOGLE_SIGN_IN && signInApiBuilt)
		{
			handleGoogleSignInResult(googleSignInService.getSignInResultFromIntent(data));
		}
	}

	private void handleGoogleSignInResult(GoogleSignInResult result)
	{
		Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess());
//		StringBuilder stringBuilder = new StringBuilder();

		GoogleSignInAccount signInAccount = result.getSignInAccount();

		if(signInAccount != null)
		{
//			stringBuilder.append("Succes: ").append(result.isSuccess()).append("\n");
//			stringBuilder.append("Status: ").append(result.getStatus()).append("\n");
//
//			stringBuilder.append("Emial: ").append(acct.getEmail()).append("\n");
//			stringBuilder.append("Id: ").append(acct.getId()).append("\n");
//			stringBuilder.append("IdToken: ").append(acct.getIdToken()).append("\n");
//			stringBuilder.append("AuthoCode: ").append(acct.getServerAuthCode()).append("\n");
//			stringBuilder.append("Photo: ").append(acct.getPhotoUrl()).append("\n");

			if(result.isSuccess())
			{
				firebaseSignInService.build();
				firebaseSignInService.startAuth(GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null), loginFragment.getActivity());
			}
			else
			{
//				Google Sign In failed
//				Toast.makeText(AuthActivity.this, "Google Authentication failed.",
//						Toast.LENGTH_SHORT).show();
			}

//			Gson gson = new Gson();
//			Log.d(TAG, stringBuilder.toString());
//			Log.d(TAG, "handleGoogleSignInResult: " + gson.toJson(acct));
//
//			userAccount.setUserEmail(acct.getEmail());
//			userAccount.setUserName(acct.getGivenName());

		}
	}
}
