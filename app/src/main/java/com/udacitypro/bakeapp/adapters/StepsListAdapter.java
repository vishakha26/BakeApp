package com.udacitypro.bakeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacitypro.bakeapp.R;
import com.udacitypro.bakeapp.objects.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StepsListAdapter extends RecyclerView.Adapter<StepsListAdapter.MakeRecipeListView> {

    private static final String TAG = "StepsListAdapter";

    Recipe mRecipe = new Recipe();
    Context mContext;
    int decide_whether_should_draw = 0;

    public StepsListAdapter(Recipe recipe, Context context) {
        mRecipe = recipe;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mRecipe.getSteps() == null) {
            Log.e(TAG, "getItemCount: mRecipe.getSteps() is null");
            return 0;
        }
        return mRecipe.getSteps().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? R.layout.ingredients_list : R.layout.step_list_item;
    }

    @Override
    public MakeRecipeListView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == R.layout.step_list_item) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.step_list_item, parent, false);
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.ingredients_list, parent, false);
        }

        return new MakeRecipeListView(itemView);
    }

    @Override
    public void onBindViewHolder(MakeRecipeListView holder, int position) {

        ArrayList<String> steps = mRecipe.getSteps();
        ArrayList<String> ingredients = mRecipe.getIngredients();
        String name = mRecipe.getName();

        if (position == 0) {
            holder.mRecipeName.setText(name);
            for (int i = 0; i < ingredients.size(); i++) {
                try {
                    JSONObject ingr = new JSONObject(ingredients.get(i));
                    String quantity = ingr.getString("quantity");
                    String measure = ingr.getString("measure");
                    String ingredient = ingr.getString("ingredient");
                    if (decide_whether_should_draw == 0) {
                        String finished_ingr_list = quantity + "  " + measure + "  ~  " + ingredient;
                        holder.mIngredients.append(finished_ingr_list + "\n");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onBindViewHolder: ", e);
                }
            }
            decide_whether_should_draw = decide_whether_should_draw + 1;
        } else {
            if(position == 1){
                String introduction = "Introduction";
                holder.mStepNumber.setText(introduction);
            }else {
                int num = position - 1;
                String step_number = "Step: " + num;
                holder.mStepNumber.setText(step_number);
            }
            if (mContext.getResources().getBoolean(R.bool.isTablet)) {
                holder.mMoreDetails.setVisibility(View.INVISIBLE);
            }

            try {
                JSONObject steps_list = new JSONObject(steps.get(position - 1));
                String short_descr = steps_list.getString("shortDescription");
                holder.mShortDescr.setText(short_descr);
            } catch (JSONException e) {
                Log.e(TAG, "onBindViewHolder: ", e);
            }
        }
    }

    public class MakeRecipeListView extends RecyclerView.ViewHolder {

        TextView mStepNumber;
        TextView mShortDescr;
        TextView mIngredients;
        TextView mRecipeName;
        TextView mMoreDetails;

        public MakeRecipeListView(View view) {
            super(view);
            mStepNumber = (TextView) view.findViewById(R.id.step_number_text);
            mShortDescr = (TextView) view.findViewById(R.id.step_short_descr);
            mIngredients = (TextView) view.findViewById(R.id.ingr_items);
            mRecipeName = (TextView) view.findViewById(R.id.r_name);
            mMoreDetails = (TextView) view.findViewById(R.id.more_details_tv);
        }
    }
}
