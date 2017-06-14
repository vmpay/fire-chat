package vmpay.com.firechat.utlis.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import vmpay.com.firechat.activities.main.MainActivity;

/**
 * Created by Andrew on 13/06/2017.
 */

public class FirebaseSignInService implements IFirebaseSignInService
{
	private static final String TAG = "FirebaseSignInService";

	private FirebaseAuth firebaseAuth;

	@Override
	public void startAuth(AuthCredential credential, final Activity activity)
	{
		firebaseAuth.signInWithCredential(credential)
				.addOnCompleteListener(activity, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						// If sign in fails, display a message to the user. If sign in succeeds
						// the auth state listener will be notified and logic to handle the
						// signed in user can be handled in the listener.
						if(!task.isSuccessful())
						{
							Toast.makeText(activity, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}
						else
						{
							activity.startActivity(new Intent(activity, MainActivity.class));
							activity.finish();
						}
					}
				});
	}

	@Override
	public void build()
	{
		firebaseAuth = FirebaseAuth.getInstance();
	}
}
