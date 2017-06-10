package vmpay.com.firechat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
	private static final String DEFAULT_NAME = "Unknown Droid";
	private DatabaseReference mSimpleFirechatDatabaseReference;
	private FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder>
			mFirebaseAdapter;
	private RecyclerView mMessageRecyclerView;
	private LinearLayoutManager mLinearLayoutManager;
	private ProgressBar mProgressBar;
	private FloatingActionButton mSendButton;
	private EditText mMsgEditText;
	private String mUsername;
	private String mPhotoUrl;

	private GoogleApiClient mGoogleApiClient;
	private FirebaseAuth mFirebaseAuth;
	private FirebaseUser mFirechatUser;

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
	{
		Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		mGoogleApiClient.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://vmpay.com.firechat/http/host/path")
		);
		AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://vmpay.com.firechat/http/host/path")
		);
		AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
		mGoogleApiClient.disconnect();
	}

	public static class FirechatMsgViewHolder extends RecyclerView.ViewHolder {
		public TextView msgTextView;
		public TextView userTextView;
		public CircleImageView userImageView;

		public FirechatMsgViewHolder(View v) {
			super(v);
			msgTextView = (TextView) itemView.findViewById(R.id.msgTextView);
			userTextView = (TextView) itemView.findViewById(R.id.userTextView);
			userImageView = (CircleImageView) itemView.findViewById(R.id.userImageView);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API)
				.build();

		mFirebaseAuth = FirebaseAuth.getInstance();
		mFirechatUser = mFirebaseAuth.getCurrentUser();
		if (mFirechatUser == null) {
			startActivity(new Intent(this, AuthActivity.class));
			finish();
			return;
		} else {
			mUsername = mFirechatUser.getDisplayName();
			if (mFirechatUser.getPhotoUrl() != null) {
				mPhotoUrl = mFirechatUser.getPhotoUrl().toString();
			}
		}

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
		mLinearLayoutManager = new LinearLayoutManager(this);
		mLinearLayoutManager.setStackFromEnd(true);
		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

		mSendButton = (FloatingActionButton) findViewById(R.id.fab);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ChatMessage friendlyMessage = new
						ChatMessage(mMsgEditText.getText().toString(),
						mUsername,
						mPhotoUrl);
				mSimpleFirechatDatabaseReference.child("messages")
						.push().setValue(friendlyMessage);
				mMsgEditText.setText("");
			}
		});

		mSimpleFirechatDatabaseReference = FirebaseDatabase.getInstance().getReference();
		mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage,
				FirechatMsgViewHolder>(
				ChatMessage.class,
				R.layout.chat_message,
				FirechatMsgViewHolder.class,
				mSimpleFirechatDatabaseReference.child("messages")) {

			@Override
			protected void populateViewHolder(FirechatMsgViewHolder viewHolder, ChatMessage friendlyMessage, int position) {
				mProgressBar.setVisibility(ProgressBar.INVISIBLE);
				viewHolder.msgTextView.setText(friendlyMessage.getText());
				viewHolder.userTextView.setText(friendlyMessage.getName());
				if (friendlyMessage.getPhotoUrl() == null) {
					viewHolder.userImageView
							.setImageDrawable(ContextCompat
									.getDrawable(MainActivity.this,
											R.mipmap.ic_launcher));
				} else {
					Glide.with(MainActivity.this)
							.load(friendlyMessage.getPhotoUrl())
							.into(viewHolder.userImageView);
				}
			}
		};

		mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				int chatMessageCount = mFirebaseAdapter.getItemCount();
				int lastVisiblePosition =
						mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
				if (lastVisiblePosition == -1 ||
						(positionStart >= (chatMessageCount - 1) &&
								lastVisiblePosition == (positionStart - 1))) {
					mMessageRecyclerView.scrollToPosition(positionStart);
				}
			}
		});

		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
		mMessageRecyclerView.setAdapter(mFirebaseAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sign_out_menu:
				mFirebaseAuth.signOut();
				Auth.GoogleSignInApi.signOut(mGoogleApiClient);
				mUsername = DEFAULT_NAME;
				startActivity(new Intent(this, AuthActivity.class));
				return true;
			case R.id.reconfig:
//				fetchConfig();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
