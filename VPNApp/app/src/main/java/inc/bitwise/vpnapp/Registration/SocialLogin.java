package inc.bitwise.vpnapp.Registration;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import inc.bitwise.vpnapp.CommonUtils.TransitionManager;
import inc.bitwise.vpnapp.R;
import inc.bitwise.vpnapp.Util;
import inc.bitwise.vpnazure.Models.UserRegistration;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Vipuld on 17/09/2019.
 */
public class SocialLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    SignInButton mGoogleSignInButton;

    int ConnectivityStatus;
    String socialToken;
    String provider;
    private static final String EMAIL = "email";


    private ImageView bookIconImageView;
    private TextView bookITextView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;

    private UserRegistration userRegistration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Util.setTheme(this);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sociallogin);


        try{

            initViews();
            new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    bookITextView.setVisibility(GONE);
                    loadingProgressBar.setVisibility(GONE);
                    rootView.setBackgroundColor(ContextCompat.getColor(SocialLogin.this, R.color.colorSplashText));
                    bookIconImageView.setImageResource(R.drawable.ic_launcher);
                    startAnimation();
                    googleLogin();
                    facebookLogin();
                }
            }.start();


        }catch(Exception e){
            Log.i("MSA","onCreate"+e.toString());
        }
    }


    private void initViews() {
        userRegistration = new UserRegistration();
        bookIconImageView = findViewById(R.id.bookIconImageView);
        bookITextView = findViewById(R.id.bookITextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.afterAnimationView);

    }


    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void facebookLogin(){
        try{
            final LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
            loginButton.setVisibility(VISIBLE);
            loginButton.setReadPermissions(Arrays.asList(EMAIL));
            FacebookSdk.sdkInitialize(getApplicationContext());

            loginButton.setReadPermissions("email");

            // If using in a fragment
            // Other app specific specialization
            callbackManager = CallbackManager.Factory.create();


            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInWithFacebook(loginButton);
                }
            });

        }catch(Exception e){
            Log.i("MSA","ServiceException"+e.toString());

        }

    }

    private void googleLogin(){
        try{
            mGoogleSignInButton = (SignInButton)findViewById(R.id.sign_in_button);

            mGoogleSignInButton.setVisibility(VISIBLE);
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(this.getResources().getString(R.string.google_server_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            TextView textView = (TextView) mGoogleSignInButton.getChildAt(0);
            textView.setText(getResources().getString(R.string.GoogleSignButtonText));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.googleSignInButtonSize));
            mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGoogleApiClient.clearDefaultAccountAndReconnect();
                    signInWithGoogle();
                }
            });
        }catch(Exception e){
            Log.i("MSA","ServiceException"+e.toString());
        }
    }

    private static final int RC_SIGN_IN = 9001;
    private void signInWithGoogle() {
        try {
                final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                Log.i("MSA","RCSIGNIN");
                mGoogleApiClient.connect();
        }
        catch(Exception e){
            Log.i("MSA"," "+e.getStackTrace().toString());
        }

    }


    public void signInWithFacebook(LoginButton loginButton)
    {
        try{
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
            {
                @Override
                public void onSuccess(LoginResult loginResult) {
                        // Response - com.facebook.login.LoginResult@d77663
                    Log.i("MSA","Google"+ loginResult.getAccessToken().getToken());
                    TransitionManager.startActivity(SocialLogin.this, inc.bitwise.vpnapp.Registration.UserRegistration.class);
                }

                @Override
                public void onCancel() {
                    // App code
                    Log.i("MSA","onCancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.i("MSA","onError"+exception.toString());
                }
            });
        }catch(Exception Exception){
            Log.i("MSA","ServiceException"+Exception.toString());
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    final GoogleApiClient client = mGoogleApiClient;
                    TransitionManager.startActivity(SocialLogin.this, inc.bitwise.vpnapp.Registration.UserRegistration.class);
                    Log.i("MSA","Google"+ result.getSignInAccount().getIdToken());
                }
            }

        }
        catch(Exception e){
            Log.i("MSA","Exception2"+e.toString());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
