package com.duy.radiocean.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.radiocean.R;
import com.duy.radiocean.adapter.AlbumSongAdapter;
import com.duy.radiocean.adapter.ListSongAdapter;
import com.duy.radiocean.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "MainActivity";
    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void
    onViewCreated(@NonNull View view,
                  @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        loadLst(view);
        loadAlbum(view);
    }

    private void loadLst(@NonNull View view) {
        List<Song> items = new ArrayList<Song>();
        items.add(new Song(1, "Nhạc hay", "The Big", "4:20", R.drawable.a));
        items.add(new Song(2, "Ca khúc ưa thích", "Ca sĩ nổi tiếng", "3:45", R.drawable.a));
        items.add(new Song(3, "Bản nhạc du dương", "Ban nhạc tự tạo", "5:10", R.drawable.a));
        items.add(new Song(4, "Nhạc ballad cảm động", "Ca sĩ tài năng", "4:55", R.drawable.a));
        items.add(new Song(5, "Âm nhạc phục vụ dân ca", "Ban nhạc dân ca", "3:30", R.drawable.a));
        items.add(new Song(6, "Ca khúc phổ biến", "Ca sĩ nổi tiếng", "3:15", R.drawable.a));
        items.add(new Song(7, "Nhạc rock nổi tiếng", "Ban nhạc rock", "4:40", R.drawable.a));
        items.add(new Song(8, "Bản nhạc jazz thú vị", "Ban nhạc jazz", "6:25", R.drawable.a));
        items.add(new Song(9, "Ca khúc hòa nhạc tinh tế", "Nghệ sĩ hòa nhạc", "7:05", R.drawable.a));
        items.add(new Song(10, "Nhạc dance sôi động", "DJ nổi tiếng", "3:50", R.drawable.a));

        RecyclerView recyclerView
                = view.findViewById(R.id.recyclerViewListSong);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ListSongAdapter(getActivity(), items));
    }

    private void loadAlbum(@NonNull View view){
        mImageUrls.add("https://i.pinimg.com/564x/2e/a0/1a/2ea01a8f3eed75cc20edf64f217fae00.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/736x/96/4a/de/964ade8967fec3aaaf38d2b39a0ae040.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/564x/2e/a0/1a/2ea01a8f3eed75cc20edf64f217fae00.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/736x/96/4a/de/964ade8967fec3aaaf38d2b39a0ae040.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/564x/4f/86/77/4f867765bba422b17e3b450af42e7df1.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/564x/4f/86/77/4f867765bba422b17e3b450af42e7df1.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/564x/4f/86/77/4f867765bba422b17e3b450af42e7df1.jpg");
        mNames.add("Album 1");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView
                = view.findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AlbumSongAdapter adapter = new AlbumSongAdapter(getActivity(), mNames, mImageUrls);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}