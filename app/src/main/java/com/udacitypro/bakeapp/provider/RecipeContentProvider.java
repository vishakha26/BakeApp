package com.udacitypro.bakeapp.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = RecipeContentProvider.AUTHORITY,
        database = RecipeDatabase.class)
public class RecipeContentProvider {

    public static final String AUTHORITY = "com.udacitypro.recipe.provider";

    @TableEndpoint(table = RecipeDatabase.RECIPE_ITEM_TABLE)
    public static class RecipeItemTable {

        @ContentUri(
                path = "recipe",
                type = "vnd.android.cursor.dir/inven")
        public static final Uri RECIPE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipe/");
    }

}
