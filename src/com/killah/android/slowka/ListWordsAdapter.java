package com.killah.android.slowka;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class ListWordsAdapter extends ArrayAdapter {
	private final Activity activity;
    private final List items;

	@SuppressWarnings("unchecked")
	public ListWordsAdapter(Activity activity, List objects) {
        super(activity, R.layout.addlistitem, objects);
        this.activity = activity;
        this.items = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        StockQuoteView sqView = null;

        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.addlistitem, null);

            // Hold the view objects in an object,
            // so they don't need to be re-fetched
            sqView = new StockQuoteView();
            sqView.word1 = (TextView) rowView.findViewById(R.id.textView1);
            sqView.word2 = (TextView) rowView.findViewById(R.id.textView2);
            sqView.delete = (Button) rowView.findViewById(R.id.button1);

            // Cache the view objects in the tag,
            // so they can be re-accessed later
            rowView.setTag(sqView);
        } else {
            sqView = (StockQuoteView) rowView.getTag();
        }

        // Transfer the stock data from the data object
        // to the view objects
        try {
	        String current = (String) items.get(position);
	        sqView.word1.setText(current.split(":")[0]);
	        sqView.word2.setText(current.split(":")[1]);
        } catch (Exception e) {
        	
        }
        sqView.delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((Main) activity).removeItem(position);
			}
		});

        return rowView;
    }

    protected static class StockQuoteView {
        protected TextView word1;
        protected TextView word2;
        protected Button delete;
    }
}
