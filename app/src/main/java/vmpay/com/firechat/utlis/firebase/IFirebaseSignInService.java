package vmpay.com.firechat.utlis.firebase;

import android.app.Activity;

import com.google.firebase.auth.AuthCredential;

/**
 * Created by Andrew on 13/06/2017.
 */

public interface IFirebaseSignInService
{
	void startAuth(AuthCredential credential, Activity activity);

	void build();
}
