package com.example.administrator.firebase_social_media_app;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataSet> {
    private ArrayList<DataSet> dataSets;
    private Context context;

    public CustomAdapter(Context context,ArrayList dataSets){
        super(context,R.layout.post_image_and_description,dataSets);
        this.context = context;
        dataSets = this.dataSets;
    }

    private static class ViewHolder{
        TextView textView;
        ImageView imageView;
    }

    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        DataSet dataSet = getItem(position);
        ViewHolder viewHolder;
        //final View result;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.post_image_and_description, parent, false);
            viewHolder.textView =  convertView.findViewById(R.id.postDesc);
            viewHolder.imageView = convertView.findViewById(R.id.postImage);
            //result = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
           // result = convertView;

        }
      //  Log.i("datasnap", "getView: "+dataSet.getImageDescription()+"");
        viewHolder.textView.setText(dataSet.getImageDescription()+"");
        viewHolder.textView.setTextColor(Color.WHITE);
        viewHolder.textView.setGravity(Gravity.CENTER_HORIZONTAL);
        Picasso.get().load(dataSet.getImageDownloadLink()).into(viewHolder.imageView);
        return convertView;
    }
}
