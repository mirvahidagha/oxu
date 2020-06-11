package com.mirvahidagha.betterbet.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.mirvahidagha.betterbet.Activities.Main;
import com.mirvahidagha.betterbet.Client.SurahClient;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.MyData;
import com.mirvahidagha.betterbet.Others.TabFragment;
import com.mirvahidagha.betterbet.Player.FileDialog;
import com.mirvahidagha.betterbet.Player.MByteArrayDataSource;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.dialog.BetterDialog;
import com.mirvahidagha.betterbet.dialog.ListenDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ListenFragment extends TabFragment {

    private String[] links;
    private SimpleExoPlayer exoPlayer;
    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.i(TAG, "onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.i(TAG, "onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady)
                    + " playbackState = " + playbackState);
            switch (playbackState) {
                case ExoPlayer.STATE_ENDED:
                    Log.i(TAG, "Playback ended!");
                    //Stop playback and return to start position
                    setPlayPause(false);
                    exoPlayer.seekTo(0);
                    break;
                case ExoPlayer.STATE_READY:
                    Log.i(TAG, "ExoPlayer ready! pos: " + exoPlayer.getCurrentPosition()
                            + " max: " + stringForTime((int) exoPlayer.getDuration()));
                    if (!playWhenReady) setProgress();
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    Log.i(TAG, "Playback buffering!");
                    break;
                case ExoPlayer.STATE_IDLE:
                    Log.i(TAG, "ExoPlayer idle!");
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.i(TAG, "onPlaybackError: " + error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity() {
            Log.i(TAG, "onPositionDiscontinuity");
        }
    };

    private SeekBar seekPlayerProgress;
    private Handler handler;
    private ImageButton btnPlay, btnPrevious, btnNext;
    private TextView txtCurrentTime, txtEndTime;
    private boolean isPlaying = false;
    private RecyclerView recycler;
    SurahViewModel viewModel;
    private RecycleAdapter adapter;
    ArrayList<Surah> surahs = new ArrayList<>();
    private static final String TAG = "PlayState";
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class
    private int currentPosition;

    public ListenFragment() {
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player, container, false);

        links = getResources().getStringArray(R.array.links);
        recycler = view.findViewById(R.id.recycler_listen);
        GridLayoutManager grid = new GridLayoutManager(getActivity(), 1);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(grid);
        adapter = new RecycleAdapter();
        recycler.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(SurahViewModel.class);
        viewModel.getSurahs().observe(getViewLifecycleOwner(), new Observer<List<Surah>>() {
            @Override
            public void onChanged(List<Surah> surahList) {
                surahs.clear();
                surahs.addAll(surahList);
                adapter.setSurahs(surahs);
            }
        });


        btnPlay = (ImageButton) view.findViewById(R.id.btnPlay);
        btnPrevious = (ImageButton) view.findViewById(R.id.btnPrevious);
        btnNext = (ImageButton) view.findViewById(R.id.btnNext);

        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayPause(!isPlaying);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnPrevious.setVisibility(View.VISIBLE);
                if (currentPosition < 113) playNew(++currentPosition);
                if (currentPosition >= 113) btnNext.setVisibility(View.INVISIBLE);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNext.setVisibility(View.VISIBLE);

                if (currentPosition > 0)
                    playNew(--currentPosition);
                if (currentPosition <= 0) btnPrevious.setVisibility(View.INVISIBLE);


            }
        });
        txtCurrentTime = (TextView) view.findViewById(R.id.time_current);
        txtEndTime = (TextView) view.findViewById(R.id.player_end_time);


        //  File f_ext_files_dir = getContext().getExternalFilesDir(null);

        //  FileDialog fileDialog = new FileDialog(getActivity(), f_ext_files_dir, "");
//        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
//            @Override
//            public void fileSelected(File file) {
//                Log.i("File selected:", file.getAbsolutePath());
//                prepareExoPlayerFromFileUri(Uri.fromFile(file));
//
//                /*
//                try {
//                    FileInputStream inputStream = new FileInputStream(file);
//                    byte[] fileData = new byte[(int)file.length()];
//                    Log.i(TAG,"Data before read: "+fileData.length);
//                    int bytesRead = inputStream.read(fileData);
//                    Log.i(TAG,"Bytes read: "+bytesRead);
//                    if(bytesRead>0) {
//                        prepareExoPlayerFromByteArray(fileData);
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                */
//            }
//        });
//        //fileDialog.showDialog();

        //prepareExoPlayerFromRawResourceUri(RawResourceDataSource.buildRawResourceUri(R.raw.audio));

        prepareExoPlayerFromURL(Uri.parse("https://www.ixlasla.com/files/quran/az/alixan_musayev/001.el-Fatihe.mp3"));

        seekPlayerProgress = (SeekBar) view.findViewById(R.id.mediacontroller_progress);
        seekPlayerProgress.requestFocus();

        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                exoPlayer.seekTo(progress * 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 100);

        return view;

    }

    //TODO
    private void prepareExoPlayerFromByteArray(byte[] data) {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector(null), new DefaultLoadControl());
        exoPlayer.addListener(eventListener);

        final MByteArrayDataSource byteArrayDataSource = new MByteArrayDataSource(data);
        Log.i(TAG, "ByteArrayDataSource constructed.");
        /*
        DataSpec dataSpec = new DataSpec(byteArrayDataSource.getUri());
        try {
            byteArrayDataSource.open(dataSpec);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return byteArrayDataSource;
            }
        };
        Log.i(TAG, "DataSource.Factory constructed.");

        MediaSource audioSource = new ExtractorMediaSource(byteArrayDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);
        Log.i(TAG, "Audio source constructed.");
        exoPlayer.prepare(audioSource);
    }

    /**
     * Prepares exoplayer for audio playback from a local file
     *
     * @param uri
     */
    private void prepareExoPlayerFromFileUri(Uri uri) {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector(null), new DefaultLoadControl());
        exoPlayer.addListener(eventListener);

        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        exoPlayer.prepare(audioSource);
    }

    /**
     * Prepares exoplayer for audio playback from a remote URL audiofile. Should work with most
     * popular audiofile types (.mp3, .m4a,...)
     *
     * @param uri Provide a Uri in a form of Uri.parse("http://blabla.bleble.com/blublu.mp3)
     */
    private void prepareExoPlayerFromURL(Uri uri) {

        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "exoplayer2example"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        exoPlayer.addListener(eventListener);


        exoPlayer.prepare(audioSource);

    }

    private void prepareExoPlayerFromRawResourceUri(Uri uri) {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector(null), new DefaultLoadControl());
        exoPlayer.addListener(eventListener);

        DataSpec dataSpec = new DataSpec(uri);
        final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(getContext());
        try {
            rawResourceDataSource.open(dataSpec);
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return rawResourceDataSource;
            }
        };

        MediaSource audioSource = new ExtractorMediaSource(rawResourceDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        exoPlayer.prepare(audioSource);
    }

    /**
     * Starts or stops playback. Also takes care of the Play/Pause button toggling
     *
     * @param play True if playback should be started
     */
    private void setPlayPause(boolean play) {
        isPlaying = play;
        exoPlayer.setPlayWhenReady(play);

        if (!isPlaying) {
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
        } else {
            setProgress();
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void playNew(int position) {


        currentPosition = position;
        setPlayPause(false);
        exoPlayer.stop();

        File file = new File(getContext().getExternalFilesDir(null) + File.separator + links[position]);

        if (file.exists())
            prepareExoPlayerFromFileUri(Uri.parse(file.getAbsolutePath()));
        else
            prepareExoPlayerFromURL(Uri.parse("https://www.ixlasla.com/files/quran/az/alixan_musayev/" + links[position]));

        setPlayPause(true);
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
        seekPlayerProgress.setProgress((int) exoPlayer.getCurrentPosition() / 100);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 100);
        txtCurrentTime.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int) exoPlayer.getDuration()));

        if (handler == null) handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && isPlaying) {
                    seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 100);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 100;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int) exoPlayer.getDuration()));
                    handler.postDelayed(this, 100);
                }
            }
        });
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> implements Filterable {

        List<Surah> surahs = new ArrayList<>();
        List<Surah> filteredSurahs = new ArrayList<>();

        @NotNull
        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_listen, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        void setSurahs(List<Surah> surahs) {
            this.surahs = surahs;
            this.filteredSurahs = surahs;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            File file = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(null) + File.separator + links[position]);
            return file.exists() ? 1 : 0;
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, int position) {

            final Surah surah = filteredSurahs.get(position);
            holder.azeri.setTypeface(Main.bold);
            holder.meaning.setTypeface(Main.regular);
            holder.azeri.setText(surah.getId() + ". " + surah.getAzeri());
            holder.meaning.setText("(" + surah.getMeaning() + ")");

            switch (getItemViewType(position)) {
                case 1:
                    holder.cardOrCloud.setImageDrawable(getResources().getDrawable(R.drawable.ic_storage));
                    holder.cardOrCloud.setOnClickListener(v -> {

                        ListenDialog dialog = new ListenDialog(getContext(), links[position], 1);
                        dialog.setTitleText(surah.getAzeri()).setMessage("Fayl artıq yaddaşdadır.\nSilmək istəyirsiniz?")
                                .show();

                        dialog.setOnCancelListener(dialog1 -> notifyItemChanged(position));
                    });

                    break;
                case 0:
                    holder.cardOrCloud.setOnClickListener(v -> {
                        ListenDialog dialog = new ListenDialog(getContext(), links[position], 0);
                        dialog.setTitleText(surah.getAzeri()).setMessage("Faylı yükləmək istəyirsiniz?")
                                .show();
                        dialog.setOnCancelListener(dialog12 -> notifyItemChanged(position));
                    });
                    break;
            }

            holder.cardView.setOnClickListener(v -> {
                playNew(position);
                // download(position);
            });

        }

        @Override
        public int getItemCount() {
            return filteredSurahs.size();
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    String Key = constraint.toString();
                    if (Key.isEmpty()) {

                        filteredSurahs = surahs;

                    } else {
                        List<Surah> lstFiltered = new ArrayList<>();
                        for (Surah row : surahs) {

                            if (row.getAzeri().toLowerCase().contains(Key.toLowerCase())) {
                                lstFiltered.add(row);
                            }
                        }

                        filteredSurahs = lstFiltered;

                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredSurahs;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredSurahs = (List<Surah>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView azeri, meaning;
            View view;
            FrameLayout cardView;
            ImageView cardOrCloud;

            ViewHolder(final View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.card);
                azeri = itemView.findViewById(R.id.sura_az);
                meaning = itemView.findViewById(R.id.sura_meaning);
                cardOrCloud = itemView.findViewById(R.id.card_or_cloud);
                view = itemView;

            }
        }

    }


}






























