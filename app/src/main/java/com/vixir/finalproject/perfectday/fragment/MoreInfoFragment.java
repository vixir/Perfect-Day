package com.vixir.finalproject.perfectday.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.vixir.finalproject.perfectday.activities.LoginActivity;
import com.vixir.finalproject.perfectday.R;
import com.vixir.finalproject.perfectday.db.TaskItemsContract;
import com.vixir.finalproject.perfectday.db.TaskItemsDbHelper;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class MoreInfoFragment extends Fragment {

    private View mView;
    @BindView(R.id.profile_image)
    protected CircleImageView imageView;

    @BindView(R.id.profile_name)
    protected TextView mProfileName;

    @BindString(R.string.banner_ad_unit_id)
    protected String adUnitId;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private AdView adView;

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
        adView = (AdView) mView.findViewById(R.id.adView);
        AdRequest builder = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(builder);
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
        WebView view = (WebView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/licence.html");
        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.action_licenses))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @OnClick(R.id.logout)
    public void onClickLogOut() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext()));
                        dispatcher.cancelAll();
                        Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                        getActivity().getContentResolver().delete(uri, null, null);
                        getActivity().finish();
                    }
                });
    }

}
