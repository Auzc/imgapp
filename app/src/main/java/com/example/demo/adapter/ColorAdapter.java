package com.example.demo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demo.R;
import com.example.demo.data.MyColor;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private Context context;
    private List<MyColor> colors;

    public ColorAdapter(Context context, List<MyColor> colors) {
        this.context = context;
        this.colors = colors;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        MyColor color = colors.get(position);
        holder.textView1.setText(color.getChineseName());
        holder.textView2.setText(color.getEnglishName());
        holder.textView3.setText(color.getRgb());
        holder.textView.setBackgroundColor(Color.parseColor(color.getRgb()));
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView1,textView2,textView3;

        public ColorViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_color);
            textView1 = itemView.findViewById(R.id.text1);
            textView2 = itemView.findViewById(R.id.text2);
            textView3 = itemView.findViewById(R.id.text3);
        }
    }
}
