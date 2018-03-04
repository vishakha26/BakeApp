package com.udacitypro.bakeapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable {

    private int mId;
    private String mName;
    private String mServings;
    private String mImage;
    private ArrayList<String> mIngredients;
    private ArrayList<String> mSteps;

    public Recipe(int id, String name, String servings, String image, ArrayList<String> ingredients, ArrayList<String> steps) {
        mId = id;
        mName = name;
        mServings = servings;
        mImage = image;
        mIngredients = ingredients;
        mSteps = steps;
    }

    public Recipe(){
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getServings() {
        return mServings;
    }

    public String getImage() {
        return mImage;
    }

    public ArrayList<String> getIngredients() {
        return mIngredients;
    }

    public ArrayList<String> getSteps() {
        return mSteps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mServings);
        dest.writeString(this.mImage);
        dest.writeStringList(this.mIngredients);
        dest.writeStringList(this.mSteps);
    }

    protected Recipe(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
        this.mServings = in.readString();
        this.mImage = in.readString();
        this.mIngredients = in.createStringArrayList();
        this.mSteps = in.createStringArrayList();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
