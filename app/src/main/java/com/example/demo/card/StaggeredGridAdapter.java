package com.example.demo.card;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.demo.CardDetailActivity;
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 在这里处理点击事件，跳转到详情界面
                    Intent intent = new Intent(context, CardDetailActivity.class);
                    // 在这里可以传递卡片的信息到详情界面
                    intent.putExtra("card_id", card.getId());
                    context.startActivity(intent);
                }
            });
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

        holder.love.setTag(R.drawable.love); // 设置初始状态为喜欢
        int currentImageResource = (Integer)holder.love.getTag();
        if (currentImageResource == R.drawable.love) {
            holder.love.setImageResource(R.drawable.love);
            holder.love.setTag(R.drawable.love);
        } else {
            holder.love.setImageResource(R.drawable.loved);
            holder.love.setTag(R.drawable.loved);
        }
        holder.love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前的图片资源ID
                int currentImageResource = (Integer)holder.love.getTag();

                // 检查当前的图片资源ID，根据不同的状态设置不同的图片
                if (currentImageResource == R.drawable.love) {
                    // 如果当前是喜欢状态，则切换为取消喜欢状态
                    holder.love.setImageResource(R.drawable.loved);
                    // 更新标签以便下次点击知道当前状态
                    holder.love.setTag(R.drawable.loved);
                } else {
                    // 如果当前是取消喜欢状态，则切换为喜欢状态
                    holder.love.setImageResource(R.drawable.love);
                    // 更新标签以便下次点击知道当前状态
                    holder.love.setTag(R.drawable.love);
                }
            }
        });

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
