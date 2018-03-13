package com.example.abdo.trendingrepos;

/**
 * Created by abdo on 13/03/2018.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //function to convert nb_start into K thousands M million suffix in jsp
    public static String withSuffix(String num) {
        long count = Integer.parseInt(num);
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables for listitem_view
        TextView owner_name;
        TextView description;
        TextView rep_name;
        TextView nb_star;
        ImageView imageView;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);

        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        owner_name = (TextView) itemView.findViewById(R.id.owner_name);
        description = (TextView) itemView.findViewById(R.id.description);
        rep_name = (TextView) itemView.findViewById(R.id.rep_name);
        nb_star = (TextView) itemView.findViewById(R.id.nb_star);

        // Locate the ImageView in listview_item.xml
        imageView = (ImageView) itemView.findViewById(R.id.avatar);

        // Capture position and set results to the TextViews
        description.setText(resultp.get("description"));
        owner_name.setText(resultp.get("owner_name"));
        rep_name.setText(resultp.get("rep_name"));
        nb_star.setText(withSuffix(resultp.get("nb_star")));

        String url = resultp.get("avatar");

        ImageLoader imageLoader = ImageLoader.getInstance();
        //download and display image from url
        imageLoader.getInstance().displayImage(url, imageView);


        return itemView;
    }
}

