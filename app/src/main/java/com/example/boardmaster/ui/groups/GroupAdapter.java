package com.example.boardmaster.ui.groups;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardmaster.Photo;
import com.example.boardmaster.R;
import com.example.boardmaster.User;
import com.example.boardmaster.game.Game;
import com.example.boardmaster.game.JoinBottomDialogFragment;
import com.example.boardmaster.message.MessageFragment;
import com.example.boardmaster.ui.home.ItemAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * adapt the groups in the group fragment
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.AppViewHolder>{
    private Context mContext;
    private ArrayList<Game> games = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private ItemAdapter.OnItemListener mOnItemListener;
    private StorageReference mStorageRef;



    GroupAdapter(ArrayList data, ItemAdapter.OnItemListener onItemListener){
        games = data;
        this.mOnItemListener = onItemListener;
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    public GroupAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_group_layout, parent, false);
        return new GroupAdapter.AppViewHolder(view, mOnItemListener);
    }

    /**
     *
     * @param holder
     * @param position
     */
    public void onBindViewHolder(@NonNull GroupAdapter.AppViewHolder holder, int position) {
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

                image.getBytes(1024*1024*5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.image.setImageBitmap(bitmap);

                    }
                });
            }
            else{
                holder.image.setImageResource(R.drawable.icon_home_foreground);
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        Long id = games.get(position).getId();
        String title = games.get(position).getTitle();
        String description = games.get(position).getDescription();
        ArrayList<User> playerlist = games.get(position).getPlayers();
        String players = "";
        for(int i = 0; i< playerlist.size(); i++){
            User user = playerlist.get(i);
            players += user.getUsername()+ " ";

        }
        String finalPlayers = players;

        String game = games.get(position).getGameName();
        holder.game.setText(game);

        String date = "Date: "+ games.get(position).getDate();
        holder.date.setText(date);

        String time = "Time: "+games.get(position).getTime();
        holder.time.setText(time);

        String photoId = games.get(position).getProfileImages().get(0).getId();

        int maxplayers = games.get(position).getMaxPlayers();




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity)view.getContext();
                MessageFragment fragment = new MessageFragment();
                fragment.setParameters(id,game, date, time);
                fragment.show(activity.getSupportFragmentManager(), "PurchaseBottomDialogFragment");
                //activity.getSupportFragmentManager().beginTransaction().replace(R.id.itemsRecyclerView, fragment).addToBackStack(null).commit();
            }
        });

        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity)view.getContext();
                JoinBottomDialogFragment fragment = new JoinBottomDialogFragment();
                fragment.setParameters(id,game,title, description, finalPlayers,maxplayers, date, time, photoId);
                fragment.show(activity.getSupportFragmentManager(), "PurchaseBottomDialogFragment");
                //activity.getSupportFragmentManager().beginTransaction().replace(R.id.itemsRecyclerView, fragment).addToBackStack(null).commit();
            }
        });

    }

    /**
     *
     * @return number of games
     */
    public int getItemCount() {
        if (games == null) {
            return 0;
        }
        return games.size();
    }


    public class AppViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        CardView cardView;
        TextView game, time, date;
        ImageView infoButton;

        ItemAdapter.OnItemListener onItemListener;

        public AppViewHolder(@NonNull View itemView, ItemAdapter.OnItemListener onItemListener) {
            super(itemView);
            game = itemView.findViewById(R.id.listGroupName);
            image = itemView.findViewById(R.id.listGroupImage);
            date = itemView.findViewById(R.id.listGroupDate);
            time = itemView.findViewById(R.id.listGroupTime);
            infoButton = itemView.findViewById(R.id.listGroupInfoButton);
            cardView = itemView.findViewById(R.id.gameListCard);
            this.onItemListener = onItemListener;
            mStorageRef = FirebaseStorage.getInstance().getReference();


        }

    }

    public interface OnItemListener{
        void onItemClick(int position);
    }
}
