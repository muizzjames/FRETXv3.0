package fretx.version3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * 
 * @author manish.s
 *
 */
public class CustomGridViewAdapter extends ArrayAdapter<SongItem> {
	MainActivity context;
	int layoutResourceId;
	ArrayList<SongItem> data = new ArrayList<SongItem>();

	public CustomGridViewAdapter(MainActivity context, int layoutResourceId,
								 ArrayList<SongItem> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();

			holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
			holder.imageItem = (ImageView) row.findViewById(R.id.item_image);

			row.setTag(holder);

		} else {
			holder = (RecordHolder) row.getTag();
		}

		final SongItem item = data.get(position);
		holder.txtTitle.setText(item.songName);
		holder.imageItem.setImageBitmap(item.image);
		row.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PlayFragmentYoutubeFragment fragmentYoutubeFragment = new PlayFragmentYoutubeFragment();
				FragmentManager fragmentManager = context.getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				Bundle args = new Bundle();
				args.putString("URL", item.songURl);
				args.putInt("RAW", item.songTxt);
				fragmentYoutubeFragment.setArguments(args);
				fragmentTransaction.replace(R.id.play_container, fragmentYoutubeFragment);
				fragmentTransaction.commit();
			}
		});
		return row;

	}

	static class RecordHolder {
		TextView txtTitle;
		ImageView imageItem;

	}
}