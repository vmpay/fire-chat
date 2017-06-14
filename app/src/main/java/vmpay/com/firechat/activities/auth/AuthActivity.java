package vmpay.com.firechat.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import vmpay.com.firechat.R;
import vmpay.com.firechat.activities.auth.fragments.LoginFragment;
import vmpay.com.firechat.activities.main.MainActivity;
import vmpay.com.firechat.controller.AppController;
import vmpay.com.firechat.presenters.UserLoginPresenter;

public class AuthActivity extends AppCompatActivity
//		implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{
	private static final int RC_SIGN_IN = 9001;
	private SignInButton mAuthButton;
	private FirebaseAuth mFirebaseAuth;
	private GoogleApiClient mGoogleApiClient;

	private AppController appController = AppController.getInstance();
	private UserLoginPresenter userLoginPresenter;

	// Firebase instance variables

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		appController.setUp(this);

		if(userLoginPresenter == null)
		{
			userLoginPresenter = appController.getUserLoginPresenter();
		}

		FragmentManager fragmentTransaction = getSupportFragmentManager();
		fragmentTransaction.beginTransaction().replace(R.id.llRoot, new LoginFragment(), "LoginFragment").commit();


//		mAuthButton = (SignInButton) findViewById(R.id.auth_button);
//		mAuthButton.setOnClickListener(this);
//
//		mFirebaseAuth = FirebaseAuth.getInstance();
//
//		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//				.requestIdToken("924027928305-ptc4gjfs1nnbjikcogmm3c9l5ctrhg6p.apps.googleusercontent.com")
//				.requestEmail()
//				.build();
//		mGoogleApiClient = new GoogleApiClient.Builder(this)
//				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//				.build();

	}

//	@Override
//	public void onClick(View v)
//	{
//		switch(v.getId())
//		{
//			case R.id.auth_button:
//				Authorize();
//				break;
//		}
//	}

	private void Authorize()
	{
		Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(authorizeIntent, RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		userLoginPresenter.onActivityResult(requestCode, resultCode, data);

		super.onActivityResult(requestCode, resultCode, data);

//		if(requestCode == RC_SIGN_IN)
//		{
//			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//			if(result.isSuccess())
//			{
//				GoogleSignInAccount account = result.getSignInAccount();
//				firebaseAuthWithGoogle(account);
//			}
//			else
//			{
//				// Google Sign In failed
//				Toast.makeText(AuthActivity.this, "Google Authentication failed.",
//						Toast.LENGTH_SHORT).show();
//			}
//		}
	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
	{
		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mFirebaseAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						// If sign in fails, display a message to the user. If sign in succeeds
						// the auth state listener will be notified and logic to handle the
						// signed in user can be handled in the listener.
						if(!task.isSuccessful())
						{
							Toast.makeText(AuthActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}
						else
						{
							startActivity(new Intent(AuthActivity.this, MainActivity.class));
							finish();
						}
					}
				});
	}

//	@Override
//	public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
//	{
//		Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
//	}
}