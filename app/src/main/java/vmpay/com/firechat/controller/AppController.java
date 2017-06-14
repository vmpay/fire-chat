package vmpay.com.firechat.controller;

import android.content.Context;

import vmpay.com.firechat.presenters.UserLoginPresenter;
import vmpay.com.firechat.utlis.firebase.FirebaseSignInService;
import vmpay.com.firechat.utlis.firebase.IFirebaseSignInService;
import vmpay.com.firechat.utlis.google.GoogleSignInService;
import vmpay.com.firechat.utlis.google.IGoogleSignInService;

/**
 * Created by Andrew on 10/06/2017.
 */

public class AppController
{
	private static AppController ourInstance = new AppController();

	private Context context;
	private boolean initialized = false;

	//--------------------PRESENTERS--------------------
	private UserLoginPresenter userLoginPresenter;

	//---------------------SERVICES---------------------
	private IGoogleSignInService googleSignInService;
	private IFirebaseSignInService firebaseSignInService;

	private AppController()
	{
	}

	public static AppController getInstance()
	{
		return ourInstance;
	}

	public void setUp(Context context)
	{

		if(initialized)
		{
			this.context = context;
		}
		else
		{
			this.context = context;

			createDataAccessModels();
			createBusinessLogicLayer();

			initialized = true;
		}
	}

	private void createDataAccessModels()
	{
		googleSignInService = new GoogleSignInService(context);
		firebaseSignInService = new FirebaseSignInService();
	}

	private void createBusinessLogicLayer()
	{
		userLoginPresenter = new UserLoginPresenter(googleSignInService, firebaseSignInService);

	}

	public void tearDown()
	{

	}

	public UserLoginPresenter getUserLoginPresenter()
	{
		return userLoginPresenter;
	}
}
