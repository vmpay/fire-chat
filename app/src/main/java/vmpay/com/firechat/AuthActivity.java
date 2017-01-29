package vmpay.com.firechat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class AuthActivity extends AppCompatActivity
{

//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_auth);
//	}
	private DatabaseReference mSimpleFirechatDatabaseReference;
	private FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder>
			mFirebaseAdapter;
	private RecyclerView mMessageRecyclerView;
	private LinearLayoutManager mLinearLayoutManager;
	private ProgressBar mProgressBar;

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
		setContentView(R.layout.activity_auth);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
		mLinearLayoutManager = new LinearLayoutManager(this);
		mLinearLayoutManager.setStackFromEnd(true);
		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

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
									.getDrawable(AuthActivity.this,
											R.drawable.ic_account_circle_black_36dp));
				} else {
					Glide.with(AuthActivity.this)
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

}
