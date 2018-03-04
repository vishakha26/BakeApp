package com.udacitypro.bakeapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacitypro.bakeapp.R;
import com.udacitypro.bakeapp.adapters.StepsListAdapter;
import com.udacitypro.bakeapp.objects.Recipe;
import com.udacitypro.bakeapp.utils.ItemClickListener;


public class RecipeStepListFragment extends Fragment {

    private static final String TAG = "RecipeStepListFragment";
    private Bundle mBundle;

    OnRecipeClickListener mCallback;

    public interface OnRecipeClickListener {
        void onRecipeStepSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnRecipeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnImageClickListener");
        }
    }

    public RecipeStepListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        Recipe recipe = bundle.getParcelable("parcelable_object");

        final View rootView = inflater.inflate(R.layout.recipe_details_list, container, false);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recipe_detail_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        StepsListAdapter mStepsListAdapter = new StepsListAdapter(recipe, getContext());

        mRecyclerView.setAdapter(mStepsListAdapter);

        mRecyclerView.addOnItemTouchListener(new ItemClickListener(getContext(), mRecyclerView, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == 0)return;
                mCallback.onRecipeStepSelected(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        return rootView;
    }
}
