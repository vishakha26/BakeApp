package com.udacitypro.bakeapp.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RecipeDatabase.VERSION)
public class RecipeDatabase {

    public static final int VERSION = 1;

    @Table(RecipeContract.RecipeEntry.class)
    public static final String RECIPE_ITEM_TABLE = "recipe_table";
}
