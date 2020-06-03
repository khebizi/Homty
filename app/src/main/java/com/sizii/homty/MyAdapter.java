package com.sizii.homty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Comparable {

    String data[];
    int images[];
    Context context;
    public MyAdapter (Context ct, String s1[] , int img[]) {
        context = ct;
        data=s1;
        images= img;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.myText.setText(data[position]);
        holder.myImage.setImageResource(images[position]);
        holder.mainLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                int i= "Pizzas".compareTo(data.toString());
                if (i==0){
                Intent intent = new Intent(context,menuPizza.class);
                context.startActivity(intent);}

            }
        });

    }

    @Override
    public int getItemCount() {
        return images.length;

    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText ;
        ImageView myImage;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText= itemView.findViewById(R.id.myText);
            myImage= itemView.findViewById(R.id.myImage);
            mainLayout=itemView.findViewById(R.id.mainLayout);
        }
    }
}
