package com.sizii.homty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;


public class menuResto extends AppCompatActivity {
    String s1[];
    RecyclerView recyclerView;
    int images[]={R.drawable.pizza,R.drawable.sandwich,R.drawable.boisson,R.drawable.dessert};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_resto);
        recyclerView = findViewById(R.id.recyclerView);
        s1=getResources().getStringArray(R.array.menu_principal);
        MyAdapter myAdapter=new MyAdapter(this, s1, images);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
