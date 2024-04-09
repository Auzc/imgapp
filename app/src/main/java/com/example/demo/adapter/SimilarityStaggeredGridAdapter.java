package com.example.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.demo.R;
import com.example.demo.activity.CardDetailActivity;
import com.example.demo.data.Card;
import com.example.demo.data.CardSimilarity;
import com.example.demo.sqlhelper.DbOpenHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class SimilarityStaggeredGridAdapter extends RecyclerView.Adapter<SimilarityStaggeredGridAdapter.ViewHolder> {

    private final double STANDARD_SCALE = 1.1;
    private final float SCALE = 4 * 1.0f / 3;
    private List<CardSimilarity> cardSimilaritys;
    private Context context;
    private int space;

    private static final String jdbcUrl = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign" +
            "?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private String user = "admin1";
    private String password = "Jzc123456";
    private String userId = "user123";
    DecimalFormat decimalFormat = new DecimalFormat("0.00%");

    public SimilarityStaggeredGridAdapter(Context context, int space) {
        this.context = context;
        this.space = space;
    }
//    public StaggeredGridAdapter(List<Card> cards) {
//        this.cards = cards;
//        notifyDataSetChanged();
//    }
    public void setCardsSimilaritys(List<CardSimilarity> cardSimilaritys) {
        this.cardSimilaritys = cardSimilaritys;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_card_similarity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardSimilarity cardSimilarity = cardSimilaritys.get(position);
        Card card = cardSimilaritys.get(position).getCard();
        //重要
        if (cardSimilarity != null) {
            setCardView(holder, cardSimilarity);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 在这里处理点击事件，跳转到详情界面
                    new visAsyncTask(cardSimilarity.getCard().getId()).execute();
                    Intent intent = new Intent(context, CardDetailActivity.class);
                    // 在这里可以传递卡片的信息到详情界面
                    //intent.putExtra("card_id", card.getId());
                    intent.putExtra("card", card);

                    context.startActivity(intent);
                }
            });
        }
    }

    private void setCardView(ViewHolder holder, CardSimilarity cardSimilarity) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.image.getLayoutParams();
        float itemWidth = (ScreenUtil.getScreenWidth(context) - space) / 2;
        layoutParams.width = (int) itemWidth;
        float width = cardSimilarity.getCard().getWidth();
        float height = cardSimilarity.getCard().getHeight();
        String imgid = cardSimilarity.getCard().getId();
        float scale = height / width;

        if (scale > STANDARD_SCALE) {
            layoutParams.height = (int) (itemWidth * SCALE);
        } else {
            layoutParams.height = (int) itemWidth;
        }

        holder.image.setLayoutParams(layoutParams);

        Glide.with(context)
                .load(cardSimilarity.getCard().getImg_url())
                .placeholder(R.drawable.rounded_box)
                .centerCrop()
                .into(holder.image);
        String temptxt1 = cardSimilarity.getCard().getTitle()+"";
        holder.title.setText(temptxt1);

        String temptxt = cardSimilarity.getCard().getAuthor();
        temptxt += "";
        if(temptxt.length()>30){
            temptxt=temptxt.substring(0, 27);
            temptxt+="...";
        }
        holder.author.setText(temptxt);

        holder.love.setImageResource(R.drawable.love);

        holder.love.setTag(R.drawable.love); // 设置初始状态为喜欢
        if(cardSimilarity.getSimilarity() == 100){

            holder.similarity.setText("同地点推荐");

        }else{
            holder.similarity.setText("相关度："+decimalFormat.format(cardSimilarity.getSimilarity()));
        }

        holder.author.setText(temptxt);
        holder.love.setTag(R.drawable.love); // 设置初始状态为未喜欢
        holder.love.setImageResource(R.drawable.love);

        new CheckLikeAsyncTask(holder, imgid).execute();

        holder.love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前的图片资源ID
                int currentImageResource = (Integer) holder.love.getTag();
                // 检查当前的图片资源ID，根据不同的状态设置不同的图片
                if (currentImageResource == R.drawable.love) {

//                // 在这里调用 AsyncTask 来执行数据库操作
                    new LikeAsyncTask(imgid).execute();

                    //insert(userId,contentId);
                    holder.love.setImageResource(R.drawable.loved);
                    // 更新标签以便下次点击知道当前状态
                    holder.love.setTag(R.drawable.loved);
                } else {
                    new DisLikeAsyncTask(imgid).execute();
                    holder.love.setImageResource(R.drawable.love);
                    // 更新标签以便下次点击知道当前状态
                    holder.love.setTag(R.drawable.love);
                }

            }
        });




    }
    public class visAsyncTask extends AsyncTask<String, Void, Boolean> {

        private String contentId;

        public visAsyncTask(String contentId) {
            this.contentId = contentId;
        }

        @Override
        protected Boolean doInBackground(String... voids) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
                String sql = "INSERT IGNORE INTO history_table (user_id, content_id) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, userId);
                    preparedStatement.setString(2, contentId); // 使用传入的 contentId
                    // 执行插入操作
                    int rowsInserted = preparedStatement.executeUpdate();
                    return rowsInserted > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // 插入成功，显示成功的 Toast 消息
                //Toast.makeText(context, "点赞成功！", Toast.LENGTH_SHORT).show();
            } else {
                // 插入失败，显示失败的 Toast 消息
                Toast.makeText(context, "传输失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class LikeAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private String contentId;

        public LikeAsyncTask(String contentId) {

            this.contentId = contentId;

        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
                String sql = "INSERT IGNORE INTO Like_Table (user_id, content_id) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, userId);
                    preparedStatement.setString(2, contentId);
                    int rowsInserted = preparedStatement.executeUpdate();
                    return rowsInserted > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // 插入成功，显示成功的 Toast 消息
                //Toast.makeText(context, "点赞成功！", Toast.LENGTH_SHORT).show();
            } else {
                // 插入失败，显示失败的 Toast 消息
                Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public class DisLikeAsyncTask extends AsyncTask<String, Void, Boolean> {

        private String contentId;

        public DisLikeAsyncTask(String contentId) {
            this.contentId = contentId;
        }

        @Override
        protected Boolean doInBackground(String... voids) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
                String sql = "DELETE FROM Like_Table WHERE user_id = ? AND content_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, userId);
                    preparedStatement.setString(2, contentId); // 使用传入的 contentId
                    // 执行删除操作
                    int rowsDeleted = preparedStatement.executeUpdate();
                    return rowsDeleted > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // 插入成功，显示成功的 Toast 消息
                //Toast.makeText(context, "点赞成功！", Toast.LENGTH_SHORT).show();
            } else {
                // 插入失败，显示失败的 Toast 消息
                //Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CheckLikeAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private ViewHolder holder;
        private ImageView loveImageView;
        private String contentId;


        public CheckLikeAsyncTask(ViewHolder holder, String contentId) {
            this.holder = holder;
            this.contentId = contentId;

        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
                String sql = "SELECT * FROM Like_Table WHERE user_id = ? AND content_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, userId);
                    preparedStatement.setString(2, contentId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        return resultSet.next();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean alreadyLiked) {
            if (alreadyLiked) {
                // 用户已经点赞，更新UI显示点赞状态
                holder.love.setImageResource(R.drawable.loved);
                holder.love.setTag(R.drawable.loved);
            } else {
                // 用户未点赞，保持UI显示原状态
                holder.love.setImageResource(R.drawable.love);
                holder.love.setTag(R.drawable.love);
            }
        }

    }





    @Override
    public int getItemCount() {
        return cardSimilaritys != null ? cardSimilaritys.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image,love;
        TextView title,author,similarity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_image);
            title = itemView.findViewById(R.id.card_title);
            love = itemView.findViewById(R.id.card_love);
            author = itemView.findViewById(R.id.card_author);
            similarity = itemView.findViewById(R.id.card_similarity);
        }
    }

    public static void insert(String user_id, String content_id) {
        // 插入数据的 sql 语句
        String sql = "INSERT INTO Like_Table (user_id, content_id) VALUES (?, ?)";
        Connection connection = DbOpenHelper.getConnection();
        PreparedStatement ps = null;
        if (connection == null) {
            return;
        }
        try {
            ps = connection.prepareStatement(sql);
            // 为两个 ? 设置具体的值
            ps.setString(1, user_id);
            ps.setString(2, content_id);
            // 执行语句
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
