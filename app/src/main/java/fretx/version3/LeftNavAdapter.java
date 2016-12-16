package fretx.version3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * The Adapter class for the ListView displayed in the left navigation drawer.
 */
public class LeftNavAdapter extends BaseAdapter
{

	/** The items. */
	private String items[];

	/** The context. */
	private Context context;

	/** The selected. */
	private int selected;

	/** The icons. */
	private int icons[] = { R.drawable.ic_nav1, R.drawable.ic_nav2,
			R.drawable.ic_nav3, R.drawable.ic_nav4, R.drawable.ic_nav5};

	/**
	 * Setup the current selected position of adapter.
	 * 
	 * @param position
	 *            the new selection
	 */
	public void setSelection(int position)
	{
		selected = position;
		notifyDataSetChanged();
	}

	/**
	 * Instantiates a new left navigation adapter.
	 * 
	 * @param context
	 *            the context of activity
	 * @param items
	 *            the array of items to be displayed on ListView
	 */
	public LeftNavAdapter(Context context, String items[])
	{
		this.context = context;
		this.items = items;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return items.length;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public String getItem(int arg0)
	{
		return items[arg0];
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
			convertView = LayoutInflater.from(context).inflate(
					R.layout.left_nav_item, null);
		TextView lbl = (TextView) convertView;
		lbl.setText(getItem(position));
		lbl.setCompoundDrawablesWithIntrinsicBounds(icons[position], 0, 0, 0);

		if (position == selected)
			lbl.setBackgroundColor(context.getResources().getColor(
					R.color.main_color_red_dk));
		else
			lbl.setBackgroundResource(0);
		return lbl;
	}

}
