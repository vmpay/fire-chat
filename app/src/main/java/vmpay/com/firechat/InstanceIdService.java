package vmpay.com.firechat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Andrew on 30/01/2017.
 */

public class InstanceIdService extends FirebaseInstanceIdService
{
	private static final String CHAT_ENGAGE_TOPIC = "chat_engage";

	public void onTokenRefresh() {
		String token = FirebaseInstanceId.getInstance().getToken();
		FirebaseMessaging.getInstance()
				.subscribeToTopic(CHAT_ENGAGE_TOPIC);
	}
}
