package net.wildfyre.client.data

import androidx.annotation.StringRes
import net.wildfyre.client.Constants
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


object Services {
    val webService: WebService = Retrofit.Builder()
        .baseUrl(Constants.Api.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WebService::class.java)
}

fun <T> Call<T>.then(failureHandler: FailureHandler, @StringRes errorMessage: Int, callback: (result: T) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                callback(response.body()!!)
            } else {
                val body = response.errorBody()?.use { it.charStream().readText() } ?: "<no body>"
                onFailure(call, ApiCallException(response.code(), response.message(), body))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            failureHandler.onFailure(Failure(errorMessage, t))
        }
    })
}

class ApiCallException(code: Int, message: String, body: String) : Exception("$code: $message\n\t$body")

interface WebService {
    // Authentication

    @POST("/account/auth/")
    @Headers("Content-Type: application/json")
    fun postAuth(
        @Body auth: Account.Auth
    ): Call<Auth>


    // Account

    @GET("/account/")
    fun getAccount(
        @Header("Authorization") authorization: String
    ): Call<Account>

    @GET("/bans/")
    fun getBans(
        @Header("Authorization/") authorization: String,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): Call<SuperBan>

    @GET("/users/")
    fun getSelf(
        @Header("Authorization") authorization: String
    ): Call<Author>

    @GET("/users/{userId}/")
    fun getUser(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long
    ): Call<Author>

    @PUT("/users/")
    @Multipart
    fun putAvatar(
        @Header("Authorization") authorization: String,
        @Part avatar: MultipartBody.Part // name = "avatar"
    ): Call<Author>

    @PATCH("/users/")
    @Headers("Content-Type: application/json")
    fun patchBio(
        @Header("Authorization") authorization: String,
        @Body bio: Author
    ): Call<Author>

    @PATCH("/account/")
    @Headers("Content-Type: application/json")
    fun patchAccount(
        @Header("Authorization") authorization: String,
        @Body accountPatch: Account.Patch
    ): Call<Unit>


    // Registration

    @POST("/account/register/")
    @Headers("Content-Type: application/json")
    fun postRegistration(
        @Body recover: Account.Registration
    ): Call<Registration>

    @POST("/account/recover/")
    @Headers("Content-Type: application/json")
    fun postRecovery(
        @Body recovery: Account.PasswordRecoveryStep1
    ): Call<RecoverTransaction>

    @POST("/account/recover/reset/")
    @Headers("Content-Type: application/json")
    fun postRecovery(
        @Body recovery: Account.PasswordRecoveryStep2
    ): Call<Reset>

    @POST("/account/recover/reset/")
    @Headers("Content-Type: application/json")
    fun postRecovery(
        @Body recovery: Account.UsernameRecovery
    ): Call<RecoverTransaction>


    // Flags

    @GET("/choices/flag/reasons/")
    fun getFlagReasons(
        @Header("Authorization") authorization: String
    ): Call<List<Choice>>

    @POST("/areas/{areaName}/{postId}/flag/")
    @Headers("Content-Type: application/json")
    fun postFlag(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body flag: Flag
    ): Call<Unit>

    @POST("/areas/{areaName}/{postId}/{commentId}/flag/")
    @Headers("Content-Type: application/json")
    fun postFlag(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long,
        @Body flag: Flag
    ): Call<Unit>


    // Notifications

    @GET("/areas/{areaName}/subscribed/")
    fun getArchive(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): Call<SuperPost>

    @GET("/areas/notifications/")
    fun getSuperNotification(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): Call<SuperNotification>

    @DELETE("/areas/notification/")
    fun deleteNotifications(
        @Header("Authorization") authorization: String
    ): Call<Unit>


    // Areas

    @GET("/areas/{areaName}/rep/")
    fun getAreaRep(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String
    ): Call<Reputation>

    @GET("/areas/")
    fun getAreas(
        @Header("Authorization") authorization: String
    ): Call<List<Area>>


    // Posts

    @GET("/areas/{areaName}/")
    fun getPosts(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("limit") limit: Long
    ): Call<List<Post>>

    @GET("/areas/{areaName}/own/")
    fun getOwnPosts(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): Call<SuperPost>

    @GET("/areas/{areaName}/{postId}/")
    fun getPost(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("postId") postId: Long
    ): Call<SuperPost>

    @POST("/areas/{areaName}/")
    @Headers("Content-Type: application/json")
    fun postPost(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Body post: Post
    ): Call<Unit>

    @POST("/areas/{areaName}/{postId}/")
    @Headers("Content-Type: application/json")
    @Multipart
    fun postPicture(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Part image: MultipartBody.Part, // name = "image"
        @Part commentText: MultipartBody.Part // name = "text"
    ): Call<Comment>

    @POST("/areas/{areaName}/{postId}/spread/")
    @Headers("Content-Type: application/json")
    fun postSpread(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body spread: Post.Spread
    ): Call<Unit>

    @PUT("/areas/{areaName}/{postId}/subscribe/")
    @Headers("Content-Type: application/json")
    fun putSubscription(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body subscription: Post.Subscription
    ): Call<Unit>

    @DELETE("/areas/{areaName}/{postId}/")
    fun deletePost(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Call<Unit>


    // Drafts

    @GET("/areas/{areaName}/drafts/")
    fun getDrafts(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): Call<SuperPost>

    @GET("/areas/{areaName}/drafts/{postId}/")
    fun getDraft(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("postId") postId: Long
    ): Call<SuperPost>

    @POST("/areas/{areaName}/drafts/")
    @Headers("Content-Type: application/json")
    fun postDraft(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Body post: Post
    ): Call<Unit>

    @POST("/areas/{areaName}/drafts/{postId}/publish/")
    @Headers("Content-Type: application/json")
    fun postDraftPublication(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("postId") postId: Long
    ): Call<Unit>

    @PUT("/areas/{areaName}/drafts/{postId}/")
    @Headers("Content-Type: application/json")
    @Multipart
    fun putPicture(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Part image: MultipartBody.Part, // name = "image"
        @Part postText: MultipartBody.Part // name = "text"
    ): Call<Post>

    @PUT("/areas/{areaName}/drafts/{postId}/")
    fun putPicture(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body post: Post
    ): Call<Unit>

    @PUT("/areas/{areaName}/drafts/{postId}/img/{slot}/")
    @Multipart
    fun putImage(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("postId") slot: Long,
        @Part image: MultipartBody.Part, // name = "image"
        @Part comment: MultipartBody.Part // name = "comment"
    ): Call<Image>

    @PATCH("/areas/{areaName}/drafts/{postId}/")
    @Headers("Content-Type: application/json")
    fun patchDraft(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body post: Post
    ): Call<Unit>

    @DELETE("/areas/{areaName}/drafts/{postId}/")
    fun deleteDraft(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Call<Unit>

    @DELETE("/areas/{areaName}/drafts/{postId}/img/{slot}/")
    fun deleteImage(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("postId") slot: Long
    ): Call<Unit>


    // Comments

    @POST("/areas/{areaName}/{postId}/")
    @Headers("Content-Type: application/json")
    fun postComment(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("postId") postId: Long,
        @Body comment: Comment
    ): Call<Comment>

    @DELETE("/areas/{areaName}/{postId}/{commentId}/")
    fun deleteComment(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long
    ): Call<Unit>
}