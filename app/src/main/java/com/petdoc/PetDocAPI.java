package com.petdoc;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by STU on 2016-05-19.
 */
public interface PetDocAPI {


    @GET("/RetrofitExample/data.php")
    public void getPetDocs(Callback<ArrayList<DocItem>> response);
}
