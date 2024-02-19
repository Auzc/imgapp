package com.example.demo.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.demo.R;


import java.util.List;

public class StaggeredGridAdapter extends RecyclerView.Adapter<StaggeredGridAdapter.ViewHolder> {

    private final double STANDARD_SCALE = 1.1;
    private final float SCALE = 4 * 1.0f / 3;
    private List<Card> cards;
    private Context context;
    private int space;

    public StaggeredGridAdapter(Context context, int space) {
        this.context = context;
        this.space = space;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = cards.get(position);
        //重要
        if (card != null) {
            setCardView(holder, card);
        }
    }

    private void setCardView(ViewHolder holder, Card card) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.image.getLayoutParams();
        float itemWidth = (ScreenUtil.getScreenWidth(context) - space) / 2;
        layoutParams.width = (int) itemWidth;

        float width = card.getWidth();
        float height = card.getHeight();
        float scale = height / width;

        if (scale > STANDARD_SCALE) {
            layoutParams.height = (int) (itemWidth * SCALE);
        } else {
            layoutParams.height = (int) itemWidth;
        }

        holder.image.setLayoutParams(layoutParams);

        Glide.with(context)
                .load(card.getImg_url())
                .placeholder(R.drawable.rounded_box)
                .centerCrop()
                .into(holder.image);
        //Glide.with(context).load(card.getImg_url()).into(holder.image);

        holder.title.setText(card.getTitle());

        String temptxt = card.getAuthor();
        if(temptxt.length()>30){
            temptxt=temptxt.substring(0, 27);
            temptxt+="...";
        }
        holder.author.setText(temptxt);
        holder.love.setImageResource(R.drawable.love);
    }

    @Override
    public int getItemCount() {
        return cards != null ? cards.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image,love;
        TextView title,author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_image);
            title = itemView.findViewById(R.id.card_title);
            love = itemView.findViewById(R.id.card_love);
            author = itemView.findViewById(R.id.card_author);
        }
    }
}
//package com.example.demo.card;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.demo.R;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.List;
//
//public class StaggeredGridAdapter extends RecyclerView.Adapter<StaggeredGridAdapter.ViewHolder> {
//
//    private final double STANDARD_SCALE = 1.1;
//    private final float SCALE = 4 * 1.0f / 3;
//    private List<Card> cards;
//    private Context context;
//    private int space;
//
//    public StaggeredGridAdapter(Context context, int space) {
//        this.context = context;
//        this.space = space;
//    }
//
//    public void setCards(List<Card> cards) {
//        this.cards = cards;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_card, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Card card = cards.get(position);
//        // Important
//        if (card != null) {
//            setCardView(holder, card);
//        }
//    }
//
//    private void setCardView(ViewHolder holder, Card card) {
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.image.getLayoutParams();
//        float itemWidth = (ScreenUtil.getScreenWidth(context) - space) / 2;
//        layoutParams.width = (int) itemWidth;
//
//        float width = card.getWidth();
//        float height = card.getHeight();
//        float scale = height / width;
//
//        if (scale > STANDARD_SCALE) {
//            layoutParams.height = (int) (itemWidth * SCALE);
//        } else {
//            layoutParams.height = (int) itemWidth;
//        }
//
//        holder.image.setLayoutParams(layoutParams);
//
//        // Use AsyncTask to load the image in the background
//        new AsyncTask<Void, Void, Bitmap>() {
//            @Override
//            protected Bitmap doInBackground(Void... voids) {
//                try {
//                    // Load the image URL into a Bitmap
//                    URL url = new URL(card.getImg_url());
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    InputStream input = connection.getInputStream();
//                    return BitmapFactory.decodeStream(input);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(Bitmap bitmap) {
//                if (bitmap != null) {
//                    // Set the loaded bitmap to the ImageView
//                    holder.image.setImageBitmap(bitmap);
//                }
//            }
//        }.execute();
//
//        holder.title.setText(card.getTitle());
//        holder.love.setImageResource(R.drawable.love);
//    }
//
//    @Override
//    public int getItemCount() {
//        return cards != null ? cards.size() : 0;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView image, love;
//        TextView title;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            image = itemView.findViewById(R.id.card_image);
//            title = itemView.findViewById(R.id.card_title);
//            love = itemView.findViewById(R.id.card_love);
//        }
//    }
//}
