package app.fyreplace.client.data

import app.fyreplace.client.Constants
import app.fyreplace.client.data.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 * Object containing services used by the different repositories.
 */
object Services {
    /**
     * Service connecting to the WildFyre API.
     */
    val webService: WebService = Retrofit.Builder()
        .baseUrl(Constants.Api.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WebService::class.java)
}

/**
 * Retrofit implementation of the WildFyre API.
 */
interface WebService {
    // Authentication

    @POST("/account/auth/")
    @Headers("Content-Type: application/json")
    suspend fun postAuth(
        @Body auth: Auth
    ): AuthToken


    // Account

    @GET("/account/")
    suspend fun getAccount(
        @Header("Authorization") authorization: String
    ): Account

    @GET("/bans/")
    suspend fun getBans(
        @Header("Authorization/") authorization: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperBan

    @GET("/users/")
    suspend fun getSelf(
        @Header("Authorization") authorization: String
    ): Author

    @GET("/users/{userId}/")
    suspend fun getUser(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long
    ): Author

    @PUT("/users/")
    @Multipart
    suspend fun putAvatar(
        @Header("Authorization") authorization: String,
        @Part avatar: MultipartBody.Part // name = "avatar"
    ): Author

    @PATCH("/users/")
    @Headers("Content-Type: application/json")
    suspend fun patchBio(
        @Header("Authorization") authorization: String,
        @Body bio: AuthorPatch
    ): Author

    @PATCH("/account/")
    @Headers("Content-Type: application/json")
    suspend fun patchAccount(
        @Header("Authorization") authorization: String,
        @Body accountPatch: AccountPatch
    ): Response<Unit>


    // Registration

    @POST("/account/register/")
    @Headers("Content-Type: application/json")
    suspend fun postRegistration(
        @Body recover: Registration
    ): RegistrationResult

    @POST("/account/recover/")
    @Headers("Content-Type: application/json")
    suspend fun postRecovery(
        @Body recovery: PasswordRecoveryStep1
    ): RecoverTransaction

    @POST("/account/recover/reset/")
    @Headers("Content-Type: application/json")
    suspend fun postRecovery(
        @Body recovery: PasswordRecoveryStep2
    ): Reset

    @POST("/account/recover/reset/")
    @Headers("Content-Type: application/json")
    suspend fun postRecovery(
        @Body recovery: UsernameRecovery
    ): RecoverTransaction


    // Flags

    @GET("/choices/flag/reasons/")
    suspend fun getFlagReasons(
        @Header("Authorization") authorization: String
    ): List<Choice>

    @POST("/areas/{areaName}/{postId}/flag/")
    @Headers("Content-Type: application/json")
    suspend fun postFlag(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body flag: Flag
    ): Response<Unit>

    @POST("/areas/{areaName}/{postId}/{commentId}/flag/")
    @Headers("Content-Type: application/json")
    suspend fun postFlag(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long,
        @Body flag: Flag
    ): Response<Unit>


    // Notifications

    @GET("/areas/{areaName}/subscribed/")
    suspend fun getPosts(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperPost

    @GET("/areas/notification/")
    suspend fun getNotifications(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperNotification

    @DELETE("/areas/notification/")
    suspend fun deleteNotifications(
        @Header("Authorization") authorization: String
    ): Response<Unit>


    // Areas

    @GET("/areas/{areaName}/rep/")
    suspend fun getAreaRep(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String
    ): Reputation

    @GET("/areas/")
    suspend fun getAreas(
        @Header("Authorization") authorization: String
    ): List<Area>


    // Posts

    @GET("/areas/{areaName}/")
    suspend fun getNextPosts(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("limit") limit: Int
    ): SuperPost

    @GET("/areas/{areaName}/own/")
    suspend fun getOwnPosts(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperPost

    @GET("/areas/{areaName}/{postId}/")
    suspend fun getPost(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Post

    @POST("/areas/{areaName}/")
    @Headers("Content-Type: application/json")
    suspend fun postPost(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Body post: Post
    ): Response<Unit>

    @POST("/areas/{areaName}/{postId}/")
    @Headers("Content-Type: application/json")
    @Multipart
    suspend fun postPicture(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Part image: MultipartBody.Part, // name = "image"
        @Part commentText: MultipartBody.Part // name = "text"
    ): Comment

    @POST("/areas/{areaName}/{postId}/spread/")
    @Headers("Content-Type: application/json")
    suspend fun postSpread(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body spread: Spread
    ): Response<Unit>

    @PUT("/areas/{areaName}/{postId}/subscribe/")
    @Headers("Content-Type: application/json")
    suspend fun putSubscription(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body subscription: Subscription
    ): Subscription

    @DELETE("/areas/{areaName}/{postId}/")
    suspend fun deletePost(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Response<Unit>


    // Drafts

    @GET("/areas/{areaName}/drafts/")
    suspend fun getDrafts(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperPost

    @GET("/areas/{areaName}/drafts/{postId}/")
    suspend fun getDraft(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("postId") postId: Long
    ): Post

    @POST("/areas/{areaName}/drafts/")
    @Headers("Content-Type: application/json")
    suspend fun postDraft(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Body post: Post
    ): Response<Unit>

    @POST("/areas/{areaName}/drafts/{postId}/publish/")
    @Headers("Content-Type: application/json")
    suspend fun postDraftPublication(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Query("postId") postId: Long
    ): Response<Unit>

    @PUT("/areas/{areaName}/drafts/{postId}/")
    @Headers("Content-Type: application/json")
    @Multipart
    suspend fun putPicture(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Part image: MultipartBody.Part, // name = "image"
        @Part postText: MultipartBody.Part // name = "text"
    ): Post

    @PUT("/areas/{areaName}/drafts/{postId}/")
    suspend fun putPicture(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body post: Post
    ): Response<Unit>

    @PUT("/areas/{areaName}/drafts/{postId}/img/{slot}/")
    @Multipart
    suspend fun putImage(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("postId") slot: Int,
        @Part image: MultipartBody.Part, // name = "image"
        @Part comment: MultipartBody.Part // name = "comment"
    ): Image

    @PATCH("/areas/{areaName}/drafts/{postId}/")
    @Headers("Content-Type: application/json")
    suspend fun patchDraft(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body post: Post
    ): Response<Unit>

    @DELETE("/areas/{areaName}/drafts/{postId}/")
    suspend fun deleteDraft(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Response<Unit>

    @DELETE("/areas/{areaName}/drafts/{postId}/img/{slot}/")
    suspend fun deleteImage(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("postId") slot: Int
    ): Response<Unit>


    // Comments

    @POST("/areas/{areaName}/{postId}/")
    @Headers("Content-Type: application/json")
    suspend fun postComment(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body comment: CommentText
    ): Comment

    @POST("/areas/{areaName}/{postId}/")
    @Headers("Content-Type: application/json")
    suspend fun postImage(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Part image: MultipartBody.Part, // name = "image"
        @Part commentText: MultipartBody.Part // name = "text"
    ): Comment

    @DELETE("/areas/{areaName}/{postId}/{commentId}/")
    suspend fun deleteComment(
        @Header("Authorization") authorization: String,
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long
    ): Response<Unit>
}
