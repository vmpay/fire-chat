package vmpay.com.firechat.activities.auth.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import vmpay.com.firechat.R;
import vmpay.com.firechat.controller.AppController;
import vmpay.com.firechat.presenters.UserLoginPresenter;

/**
 * Created by Andrew on 10/06/2017.
 */

public class LoginFragment extends Fragment
{
	private Unbinder unbinder;

	private static UserLoginPresenter userLoginPresenter;

	@BindView(R.id.auth_button)
	SignInButton googleSignInButton;


	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_login, container, false);
		unbinder = ButterKnife.bind(this, v);

		if(userLoginPresenter == null)
		{
			userLoginPresenter = AppController.getInstance().getUserLoginPresenter();
		}
		userLoginPresenter.setLoginFragment(this);
		userLoginPresenter.prepareGoogleSignIn();

		return v;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		userLoginPresenter.activateLoginApi();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		userLoginPresenter.setLoginFragment(null);
		unbinder.unbind();
	}

	@OnClick(R.id.auth_button)
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.auth_button:
				userLoginPresenter.loginWithGoogle(getActivity());
				break;
			default:
				break;
		}
	}
}
