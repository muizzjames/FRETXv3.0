package fretx.version3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;


public class PlayFragmentSearchList extends Fragment implements SearchView.OnQueryTextListener {

    private MainActivity mActivity;

    private View rootView = null;

    public SearchView svNews = null;

    public GridView lvListNews = null;
    public ArrayList<SongItem> mainData;

    public PlayFragmentSearchList(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        rootView = inflater.inflate(R.layout.play_fragment_search_list, container, false);

        svNews = (SearchView)rootView.findViewById(R.id.svSongs);
        svNews.setOnQueryTextListener(this);

        initData();

        lvListNews = (GridView)rootView.findViewById(R.id.lvSongList);

        lvListNews.setAdapter(new CustomGridViewAdapter(mActivity,R.layout.play_fragment_search_list_row_item, mainData));

        return rootView;
    }
    public void initData(){
        Bitmap homeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pic_9);
        mainData = new ArrayList<SongItem>();
        mainData.add(Util.setSongItem("The Beatles - Come Together",        "eTNitq77Utg",  R.raw.one, homeIcon));
        mainData.add(Util.setSongItem("The Beatles - Here Comes The Sun",   "Y6GNEEi7x4c",  R.raw.two,homeIcon));
        mainData.add(Util.setSongItem("Oasis - Wonderwall",                 "SLZ7uzFIMoY",  R.raw.three,homeIcon));
        mainData.add(Util.setSongItem("Led Zeppelin - Immigrant Song", "TlmrQfSTmiY", R.raw.four,homeIcon));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        lvListNews.setVisibility(View.VISIBLE);
        if (!query.equals(null)){
            ArrayList<SongItem> arrResultTemp = new ArrayList<SongItem>();
            for (int i = 0; i < mainData.size(); i ++){
                if(mainData.get(i).songName.toLowerCase().contains(query.toLowerCase())){
                    arrResultTemp.add(mainData.get(i));
                }
                lvListNews.setAdapter(new CustomGridViewAdapter(mActivity,R.layout.play_fragment_search_list_row_item, arrResultTemp));
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        lvListNews.setVisibility(View.VISIBLE);
        if (newText.equals(null)){
            lvListNews.setAdapter(new CustomGridViewAdapter(mActivity,R.layout.play_fragment_search_list_row_item, mainData));
        }else{
            ArrayList<SongItem> arrResultTemp = new ArrayList<SongItem>();
            for (int i = 0; i < mainData.size(); i ++){
                if(mainData.get(i).songName.toLowerCase().contains(newText.toLowerCase())) {
                    arrResultTemp.add(mainData.get(i));
                }
                lvListNews.setAdapter(new CustomGridViewAdapter(mActivity,R.layout.play_fragment_search_list_row_item, arrResultTemp));
            }
        }
        return false;
    }
}