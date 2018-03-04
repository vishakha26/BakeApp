package com.udacitypro.bakeapp.provider;

import net.simonvt.schematic.annotation.DataType;

public class RecipeContract {

    public static class RecipeEntry{

        @DataType(DataType.Type.INTEGER)
        public static final String COLUMN_ID = "id";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_NAME = "name";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_INGREDIENTS_ARRAY = "ingr_array";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_STEPS_ARRAY = "step_array";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_SERVINGS = "servings";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_IMAGES = "images";
    }
}
