package vmpay.com.firechat.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import vmpay.com.firechat.ChatMessage;
import vmpay.com.firechat.R;
import vmpay.com.firechat.activities.auth.AuthActivity;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
	private static final String DEFAULT_NAME = "Unknown Droid";
	private DatabaseReference mSimpleFirechatDatabaseReference;
	private FirebaseRecyclerAdapter<ChatMessage, MainActivity.FirechatMsgViewHolder>
			mFirebaseAdapter;
	private GoogleApiClient mGoogleApiClient;
	private FirebaseAuth mFirebaseAuth;
	private FirebaseUser mFirechatUser;

	private RecyclerView mMessageRecyclerView;
	private LinearLayoutManager mLinearLayoutManager;
	private ProgressBar mProgressBar;
	private FloatingActionButton mSendButton;
	private EditText mMsgEditText;

	private String mUsername;
	private String mPhotoUrl;

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
	{
		Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
		mLinearLayoutManager = new LinearLayoutManager(this);
		mLinearLayoutManager.setStackFromEnd(true);
		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

		mMsgEditText = (EditText) findViewById(R.id.msgEditText);
		mMsgEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
			{
				boolean handled = false;
				if(actionId == EditorInfo.IME_ACTION_SEND)
				{
					handled = true;
					sendMessage();
				}
				return handled;
			}
		});
		mSendButton = (FloatingActionButton) findViewById(R.id.fab);
		mSendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				sendMessage();
			}
		});

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API)
				.build();

		mFirebaseAuth = FirebaseAuth.getInstance();
		mFirechatUser = mFirebaseAuth.getCurrentUser();
		if(mFirechatUser == null)
		{
			startActivity(new Intent(this, AuthActivity.class));
			finish();
			return;
		}
		else
		{
			mUsername = mFirechatUser.getDisplayName();
			if(mFirechatUser.getPhotoUrl() != null)
			{
				mPhotoUrl = mFirechatUser.getPhotoUrl().toString();
			}
		}

		mSimpleFirechatDatabaseReference = FirebaseDatabase.getInstance().getReference();
		mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage,
				MainActivity.FirechatMsgViewHolder>(
				ChatMessage.class,
				R.layout.chat_message,
				MainActivity.FirechatMsgViewHolder.class,
				mSimpleFirechatDatabaseReference.child("rooms/general"))
		{

			@Override
			protected void populateViewHolder(MainActivity.FirechatMsgViewHolder viewHolder, ChatMessage friendlyMessage, int position)
			{
				mProgressBar.setVisibility(ProgressBar.INVISIBLE);
				viewHolder.msgTextView.setText(friendlyMessage.getText());
				viewHolder.userTextView.setText(friendlyMessage.getName());
				if(friendlyMessage.getPhotoUrl() == null)
				{
					viewHolder.userImageView
							.setImageDrawable(ContextCompat
									.getDrawable(MainActivity.this,
											R.mipmap.ic_launcher));
				}
				else
				{
					Glide.with(MainActivity.this)
							.load(friendlyMessage.getPhotoUrl())
							.into(viewHolder.userImageView);
				}
			}
		};

		mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
		{
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount)
			{
				super.onItemRangeInserted(positionStart, itemCount);
				int chatMessageCount = mFirebaseAdapter.getItemCount();
				int lastVisiblePosition =
						mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
				if(lastVisiblePosition == -1 ||
						(positionStart >= (chatMessageCount - 1) &&
								lastVisiblePosition == (positionStart - 1)))
				{
					mMessageRecyclerView.scrollToPosition(positionStart);
				}
			}
		});

		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
		mMessageRecyclerView.setAdapter(mFirebaseAdapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.sign_out_menu:
				mFirebaseAuth.signOut();
				Auth.GoogleSignInApi.signOut(mGoogleApiClient);
				mUsername = DEFAULT_NAME;
				startActivity(new Intent(this, AuthActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void sendMessage()
	{
		ChatMessage chatMessage = new
				ChatMessage(mMsgEditText.getText().toString(),
				mUsername,
				mPhotoUrl);
		mSimpleFirechatDatabaseReference.child("rooms/general")
				.push().setValue(chatMessage);
		mMsgEditText.setText("");
	}

	public static class FirechatMsgViewHolder extends RecyclerView.ViewHolder
	{
		public TextView msgTextView;
		public TextView userTextView;
		public CircleImageView userImageView;

		public FirechatMsgViewHolder(View v)
		{
			super(v);
			msgTextView = (TextView) itemView.findViewById(R.id.msgTextView);
			userTextView = (TextView) itemView.findViewById(R.id.userTextView);
			userImageView = (CircleImageView) itemView.findViewById(R.id.userImageView);
		}
	}
}
