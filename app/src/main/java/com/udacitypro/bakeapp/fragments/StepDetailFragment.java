package com.udacitypro.bakeapp.fragments;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.udacitypro.bakeapp.R;
import com.udacitypro.bakeapp.objects.Recipe;

import org.json.JSONObject;

import java.util.ArrayList;

public class StepDetailFragment extends Fragment
        implements ExoPlayer.EventListener {

    private static final String TAG = "StepDetailFragment";
    private Recipe mRecipe;
    private int mPosition;
    private ProgressBar mProgressBar;
    private ImageView mPlaceHolderImage;
    private SimpleExoPlayerView mRegularPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private String mVideoUrl;
    private boolean mNeedReinitialized;
    private boolean shouldAutoPlay;
    private MediaSessionCompat mMediaSession;


    public StepDetailFragment() {
    }

    OnNextOrPreviousSelected mButtonCallback;

    public interface OnNextOrPreviousSelected {
        void onButtonClicked(Bundle bundle);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mButtonCallback = (OnNextOrPreviousSelected) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement OnImageClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_details, container, false);

        final Bundle bundle = this.getArguments();
        mRecipe = bundle.getParcelable("parcelable_object");

        mPosition = bundle.getInt("position");

        mNeedReinitialized = false;
        shouldAutoPlay = true;
        ArrayList<String> steps = mRecipe.getSteps();


        Button mNextButton = (Button) rootView.findViewById(R.id.next_button);
        Button mPreviousButton = (Button) rootView.findViewById(R.id.previous_button);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.exo_player_progress_bar);
        mPlaceHolderImage = (ImageView) rootView.findViewById(R.id.place_image_view);
        mRegularPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.player_view);

        if (getResources().getBoolean(R.bool.isTablet)) {
            mNextButton.setVisibility(View.GONE);
            mPreviousButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            if (steps.size() == mPosition) {
                mNextButton.setAlpha(.5f);
                mNextButton.setEnabled(false);
            }
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle newBundle = new Bundle();
                    newBundle.putParcelable("parcelable_object", mRecipe);
                    newBundle.putInt("position", mPosition + 1);
                    mButtonCallback.onButtonClicked(newBundle);
                }
            });
            if (mPosition == 1) {
                mPreviousButton.setAlpha(.5f);
                mPreviousButton.setEnabled(false);
            }
            mPreviousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle newBundle = new Bundle();
                    newBundle.putParcelable("parcelable_object", mRecipe);
                    newBundle.putInt("position", mPosition - 1);
                    mButtonCallback.onButtonClicked(newBundle);
                }
            });
        }

        try {
            String step = steps.get(mPosition - 1);
            JSONObject json_steps = new JSONObject(step);
            String descr = json_steps.getString("description");
            TextView mDescription = (TextView) rootView.findViewById(R.id.step_description);
            mDescription.setText(descr);

            String video = json_steps.getString("videoURL");

            String thumbnail = json_steps.getString("thumbnailURL");


            if (video != null && !video.isEmpty()) {
                mVideoUrl = video;
                initializePlayer(Uri.parse(mVideoUrl));
            } else if(thumbnail != null && !thumbnail.isEmpty()) {
                mVideoUrl = thumbnail;
                Uri builtUri = Uri.parse(mVideoUrl).buildUpon().build();
                ImageView thumbImage = (ImageView) rootView.findViewById(R.id.thumbImage);
                Picasso.with(getContext()).load(builtUri).into(thumbImage);

            }else{
                showPlaceHolderImage();
            }


        } catch (Exception e) {
            Toast.makeText(getContext(), getResources().getString(R.string.bad_json), Toast.LENGTH_LONG).show();
            mRegularPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.no_video_available_place_holder));
            mProgressBar.setVisibility(View.INVISIBLE);
            Log.e(TAG, "onCreateView: Error ", e);
        }

        if (savedInstanceState != null) {
            shouldAutoPlay = savedInstanceState.getBoolean("shouldAutoPlay");
           // currentWindow = savedInstanceState.getInt("currentWindow");
            mPosition = savedInstanceState.getInt("current_position");
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mExoPlayer!=null) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if(mMediaSession!=null) {
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer!=null) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
    }

    long position;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("current_position", position);
        outState.putBoolean("shouldAutoPlay",shouldAutoPlay);
        super.onSaveInstanceState(outState);
    }

    public void onCreate(Bundle outState) {
        super.onCreate(outState);
        if (outState != null){
            position = outState.getLong("current_position");
            shouldAutoPlay = outState.getBoolean("shouldAutoPlay");

        }
    }

    private void releasePlayer() {
        if (mExoPlayer == null) return;
        mExoPlayer.stop();
        shouldAutoPlay = mExoPlayer.getPlayWhenReady();
        position = mExoPlayer.getCurrentPosition();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) return;
        if (mExoPlayer == null) return;
        long current_position = savedInstanceState.getLong("current_position");
        mExoPlayer.seekTo(current_position);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        showToastIfNecessary();
        switch (playbackState) {
            case ExoPlayer.STATE_READY:
                mProgressBar.setVisibility(View.INVISIBLE);
                break;
            case ExoPlayer.STATE_BUFFERING:
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_ENDED:
                mProgressBar.setVisibility(View.INVISIBLE);
                break;
            default:
                Log.e(TAG, "onPlayerStateChanged: Unknown state");
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private void initializePlayer(Uri mediaUri) {
        mRegularPlayerView.setVisibility(View.VISIBLE);
        mPlaceHolderImage.setVisibility(View.INVISIBLE);
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);

        mRegularPlayerView.setPlayer(mExoPlayer);

        mExoPlayer.addListener(this);

        String userAgent = Util.getUserAgent(getContext(), "BakingApp");
        MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(shouldAutoPlay);

    }

    private void showToastIfNecessary() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo !=null&&activeNetworkInfo.isConnected()){
            mProgressBar.setVisibility(View.VISIBLE);
            if(mNeedReinitialized){
                initializePlayer(Uri.parse(mVideoUrl));
            }
            mNeedReinitialized = false;
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.INVISIBLE);
            mNeedReinitialized = true;
        }
    }

    private void showPlaceHolderImage(){
        mRegularPlayerView.setVisibility(View.INVISIBLE);
        mPlaceHolderImage.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mPlaceHolderImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.no_video_available_place_holder));
    }

    /*private void releasePlayer() {
        if (mExoPlayer == null) return;
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }*/
}
