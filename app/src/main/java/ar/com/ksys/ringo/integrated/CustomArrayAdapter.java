package ar.com.ksys.ringo.integrated;

/**
 * Created by Escritorio on 27/02/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ar.com.ksys.ringo.R;
import ar.com.ksys.ringo.VisitActivity;
import ar.com.ksys.ringo.service.VisitDetails;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    private static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    public CustomArrayAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.custom_list, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
         ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_list, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.label);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.logo);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(values.get(position));
        viewHolder.textView.setTextColor(Color.BLACK);
        viewHolder.imageView.setImageResource(R.drawable.ic_action_about);
        viewHolder.imageView.setClickable(true);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"Ud a hecho click en el item "+String.valueOf(position),Toast.LENGTH_SHORT).show();
                VisitActivity.hacerIntent(getContext(),position,values);
            }
        });
        return convertView;
    }

}
