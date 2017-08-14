package com.tidalsolutions.magic89_9;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tidalsolutions.magic89_9.bio.ArtistsList;
import com.tidalsolutions.magic89_9.bio.DJsList;
import com.tidalsolutions.magic89_9.bio.DJsListTakeOver;
import com.tidalsolutions.magic89_9.bio.SongsList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BioFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    LinearLayout btn_artists, btn_djs, btn_djs_to, btn_songs;

    public BioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bio, container, false);

        btn_artists = (LinearLayout) v.findViewById(R.id.btn_artists);
        btn_djs = (LinearLayout) v.findViewById(R.id.btn_djs);
        btn_djs_to = (LinearLayout) v.findViewById(R.id.btn_djs_to);
        btn_songs = (LinearLayout) v.findViewById(R.id.btn_songs);

        btn_artists.setOnClickListener(this);
        btn_djs.setOnClickListener(this);
        btn_djs_to.setOnClickListener(this);
        btn_songs.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_artists) {
            Intent intent = new Intent(getActivity(), ArtistsList.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.btn_djs) {
            Intent intent = new Intent(getActivity(), DJsList.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.btn_djs_to) {
            Intent intent = new Intent(getActivity(), DJsListTakeOver.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.btn_songs) {
            Intent intent = new Intent(getActivity(), SongsList.class);
            startActivity(intent);
        }
    }
}
