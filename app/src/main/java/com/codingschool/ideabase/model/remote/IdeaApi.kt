package com.codingschool.ideabase.model.remote

import com.codingschool.ideabase.model.data.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.RequestBody
import org.w3c.dom.Comment
import retrofit2.Response
import retrofit2.http.*

interface IdeaApi {

    @POST("user")
    fun registerUser(@Body createUser: CreateUser): Completable

    @GET("user")
    fun getAllUsers(): Single<List<User>>

    @PUT("user")
    fun updateUser(@Body updateUser: UpdateUser): Single<User>

    @GET("user/me")
    fun getOwnUser(): Single<User>

    @GET("user/{id}")
    fun getUserById(@Path ("id") id: String): Single<User>

    // cath response error or empty
    @POST("user/image")
    fun updateMyProfilePicture(@Body multiPartImageBody:  RequestBody): Response<Unit>

    @DELETE("user/image")
    fun deleteProfilePicture(): Completable

    @GET("category")
    fun getAllCategories(): Single<List<Category>>

    @GET("category/{id}")
    fun getCategoryById(@Path ("id") id: String): Single<Category>

    // todo WithCatgories List<CatogryId> as String "catx, catx, catc"
    @GET("idea")
    fun getAllIdeas(@Query ("categoryId") categoryId: String): Single<List<Idea>>

    // without categories = no filter
    @GET("idea")
    fun getAllIdeasNoFilter(): Single<List<Idea>>

    // catch reponse error or Idea
    @POST("idea")
    fun addIdea(@Body multiPartIdeaBody:  RequestBody): Response<Unit>

    @GET("idea/{id}")
    fun getIdeaById(@Path ("id") id: String): Single<Idea>

    @PUT("idea/{id}")
    fun updateIdea(@Path ("id") id: String, @Body updateIdea:  CreateIdea): Single<Idea>

    @DELETE("idea/{id}")
    fun deleteIdeaById(@Path ("id") id: String): Completable

    @GET("idea/search")
    fun searchIdeas(@Body searchQuery: String): Single<List<Idea>>

    @POST("idea/{id}/released")
    fun releaseIdea(@Path ("id") id: String, @Body updateReleased:  UpdateReleased): Completable


    @POST("idea/{id}/comment")
    fun commentIdea(@Path ("id") id: String, @Body createComment:  CreateComment): Completable

    @GET("idea/{id}/comment")
    fun getIdeaComments(@Path ("id") id: String): Single<List<Comment>>

    @DELETE("idea/{id}/comment/{commentId}")
    fun deleteCommentByIdWithIdeaId(@Path ("id") id: String, @Path ("commentI") commentId: String): Completable

    @POST("idea/{id}/rating")
    fun rateIdea(@Path ("id") id: String, @Body postIdeaRating:  PostIdeaRating): Completable

    @DELETE("idea/{id}/rating")
    fun deleteOwnRatingForIdeaById(@Path ("id") id: String): Completable

    @POST("idea/{id}/image")
    fun updateImageIdea(@Path ("id") id: String, @Body multiPartImageBody:  RequestBody): Response<Unit>

}