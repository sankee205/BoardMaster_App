package com.example.boardmaster.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardmaster.Photo;
import com.example.boardmaster.game.Game;
import com.example.boardmaster.game.JoinBottomDialogFragment;
import com.example.boardmaster.R;
import com.example.boardmaster.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.AppViewHolder>{
    private Context mContext;
    private ArrayList<Game> games = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private OnItemListener mOnItemListener;
    private StorageReference mStorageRef;



    ItemAdapter(ArrayList<Game> data, OnItemListener onItemListener){
        games = data;
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_game_layout, parent, false);
        return new AppViewHolder(view, mOnItemListener);
    }


    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        try{
            Photo photo = games.get(position).getProfileImages().get(0);
            if(games.get(position).getProfileImages().size()>0){
                String photoid = photo.getId();

                File localFile = null;
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StorageReference image = mStorageRef.child("images/" + photoid);

                image.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.image.setImageBitmap(bitmap);

                    }
                });
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        String title = games.get(position).getTitle();
        holder.title.setText(title);

        String game = games.get(position).getGameName();
        holder.game.setText(game);

        String date = games.get(position).getDate().toString();
        holder.date.setText(date.toString());

        String time = games.get(position).getTime().toString();
        holder.time.setText(time.toString());

        String description = games.get(position).getDescription();

        Long id = games.get(position).getId();

        ArrayList<User> playerlist = games.get(position).getPlayers();
        String players = "";
        for(int i = 0; i< playerlist.size(); i++){
            User user = playerlist.get(i);
            players += user.getUsername()+ " ";

        }
        String finalPlayers = players;
        holder.players.setText(finalPlayers);





        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity)view.getContext();
                JoinBottomDialogFragment fragment = new JoinBottomDialogFragment();
                fragment.setParameters(id,game,title, description, finalPlayers, date, time);
                fragment.show(activity.getSupportFragmentManager(), "PurchaseBottomDialogFragment");
                //activity.getSupportFragmentManager().beginTransaction().replace(R.id.itemsRecyclerView, fragment).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (games == null) {
            return 0;
        }
        return games.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        CardView cardView;
        TextView game, title, time, date, players;

        OnItemListener onItemListener;

        public AppViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            game = itemView.findViewById(R.id.listGroupName);
            image = itemView.findViewById(R.id.listGroupImage);
            title = itemView.findViewById(R.id.listGameTitle);
            players = itemView.findViewById(R.id.listGamePlayers);
            date = itemView.findViewById(R.id.listGroupDate);
            time = itemView.findViewById(R.id.listGroupTime);
            cardView = itemView.findViewById(R.id.gameListCard);
            this.onItemListener = onItemListener;
            mStorageRef = FirebaseStorage.getInstance().getReference();


        }

    }

    public interface OnItemListener{
        void onItemClick(int position);
    }
}
