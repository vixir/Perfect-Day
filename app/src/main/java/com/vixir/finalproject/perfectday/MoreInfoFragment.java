package com.vixir.finalproject.perfectday;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.data;


public class MoreInfoFragment extends Fragment {

    private View mView;
    @BindView(R.id.profile_image)
    protected CircleImageView imageView;

    @BindView(R.id.profile_name)
    protected TextView mProfileName;


    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.more_info_fragment, container, false);
        ButterKnife.bind(this, mView);
        auth.getCurrentUser();
        Uri photoUrl = auth.getCurrentUser().getPhotoUrl();
        Glide.with(this).load(photoUrl).into(imageView);
        String strMeatFormat = getResources().getString(R.string.welcome_format);
        String strMeatMsg = String.format(strMeatFormat, auth.getCurrentUser().getDisplayName());
        mProfileName.setText(strMeatMsg);
        return mView;
    }

    @OnClick(R.id.feedback)
    public void onClickFeedBack() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.my_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
        startActivity(Intent.createChooser(intent, ""));
    }

    @OnClick(R.id.invite)
    public void onClickInvite() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = getString(R.string.invite_message);
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getContext().getPackageName() + "\n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Pick one"));
        } catch (Exception e) {
            e.toString();
        }
    }

    @OnClick(R.id.rate_the_app)
    public void onClickRateTheApp() {
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }
    }

    @OnClick(R.id.about)
    public void onClickAbout() {

    }

    @OnClick(R.id.logout)
    public void onClickLogOut() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
    }

}
