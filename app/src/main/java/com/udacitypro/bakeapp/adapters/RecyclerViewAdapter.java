package com.udacitypro.bakeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


import com.udacitypro.bakeapp.R;
import com.udacitypro.bakeapp.objects.Recipe;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ListViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Recipe> mRecipe;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<Recipe> recipe, Context context){
        this.mContext = context;
        this.mRecipe = recipe;
    }

    @Override
    public int getItemCount() {
        if(mRecipe == null)return 0;
        return mRecipe.size();
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recipe_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        Recipe recipe = mRecipe.get(position);
        String recipe_name = recipe.getName();
        holder.mName.setText(recipe_name);

        if (recipe.getImage() != null && !TextUtils.isEmpty(recipe.getImage()))
            Picasso.with(mContext)
                    .load(recipe.getImage())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.mImage);
    }

    public void swapData(ArrayList<Recipe> recipe){
        mRecipe = recipe;
        if(mRecipe != null){
            notifyDataSetChanged();
        }
    }

    class ListViewHolder extends RecyclerView.ViewHolder{
        TextView mName;
        ImageView mImage;

        public ListViewHolder(View itemView){
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.recipe_name);
            mImage = (ImageView) itemView.findViewById(R.id.recipe_image);
        }
    }


}
