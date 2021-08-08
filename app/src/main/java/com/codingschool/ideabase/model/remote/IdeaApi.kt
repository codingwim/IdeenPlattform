package com.codingschool.ideabase.model.remote

import com.codingschool.ideabase.model.data.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.*

interface IdeaApi {

    @POST("user")
    fun registerUser(@Body createUser: CreateUser): Completable

    @PUT("user")
    fun updateUser(@Body updateUser: UpdateUser): Completable

    @GET("user/me")
    fun getOwnUser(): Single<User>

    @GET("user/{id}")
    fun getUserById(@Path ("id") id: String): Single<User>

    // cath response error or empty
    @POST("user/image")
    fun updateMyProfilePicture(@Body multiPartImageBody:  RequestBody): Completable

    @GET("category")
    fun getAllCategories(): Single<List<Category>>

    // without categories = no filter
    @GET("idea")
    fun getAllIdeasNoFilter(): Single<List<Idea>>

    // catch reponse error or Idea
    @POST("idea")
    fun addIdea(@Body multiPartIdeaBody:  RequestBody): Single<Idea>

    @GET("idea/{id}")
    fun getIdeaById(@Path ("id") id: String): Single<Idea>

    @PUT("idea/{id}")
    fun updateIdea(@Path ("id") id: String, @Body updateIdea:  CreateIdea): Completable

    @DELETE("idea/{id}")
    fun deleteIdeaById(@Path ("id") id: String): Completable

    @GET("idea/search")
    fun searchIdeas(@Query ("searchQuery") searchQuery: String): Single<List<Idea>>

    @POST("idea/{id}/released")
    fun releaseIdea(@Path ("id") id: String, @Body updateReleased:  UpdateReleased): Completable

    @POST("idea/{id}/comment")
    fun commentIdea(@Path ("id") id: String, @Body createComment:  CreateComment): Completable

    @POST("idea/{id}/rating")
    fun rateIdea(@Path ("id") id: String, @Body postIdeaRating:  PostIdeaRating): Completable

    @POST("idea/{id}/image")
    fun updateImageIdea(@Path ("id") id: String, @Body multiPartImageBody:  RequestBody): Completable

}