package app.fyreplace.client.data.services

import app.fyreplace.client.data.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface of the WildFyre API.
 */
interface WildFyreService {
    // Authentication

    @POST("/account/auth/")
    @Headers("Content-Type: application/json")
    suspend fun postAuth(
        @Body auth: Auth
    ): AuthToken


    // Account

    @GET("/account/")
    suspend fun getAccount(): Account

    @GET("/bans/")
    suspend fun getBans(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperBan

    @GET("/users/")
    suspend fun getSelf(): Author

    @GET("/users/{userId}/")
    suspend fun getUser(
        @Path("userId") userId: Long
    ): Author

    @PUT("/users/")
    @Multipart
    suspend fun putAvatar(
        @Part avatar: MultipartBody.Part
    ): Author

    @PATCH("/users/")
    @Headers("Content-Type: application/json")
    suspend fun patchBio(
        @Body bio: AuthorPatch
    ): Author

    @PATCH("/account/")
    @Headers("Content-Type: application/json")
    suspend fun patchAccount(
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
    ): List<Choice>

    @POST("/areas/{areaName}/{postId}/flag/")
    @Headers("Content-Type: application/json")
    suspend fun postFlag(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body flag: Flag
    ): Response<Unit>

    @POST("/areas/{areaName}/{postId}/{commentId}/flag/")
    @Headers("Content-Type: application/json")
    suspend fun postFlag(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long,
        @Body flag: Flag
    ): Response<Unit>


    // Notifications

    @GET("/areas/{areaName}/subscribed/")
    suspend fun getPosts(
        @Path("areaName") areaName: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperPost

    @GET("/areas/notification/")
    suspend fun getNotifications(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperNotification

    @DELETE("/areas/notification/")
    suspend fun deleteNotifications(
    ): Response<Unit>


    // Areas

    @GET("/areas/{areaName}/rep/")
    suspend fun getAreaRep(
        @Path("areaName") areaName: String
    ): Reputation

    @GET("/areas/")
    suspend fun getAreas(): List<Area>


    // Posts

    @GET("/areas/{areaName}/")
    suspend fun getNextPosts(
        @Path("areaName") areaName: String,
        @Query("limit") limit: Int
    ): SuperPost

    @GET("/areas/{areaName}/own/")
    suspend fun getOwnPosts(
        @Path("areaName") areaName: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperPost

    @GET("/areas/{areaName}/{postId}/")
    suspend fun getPost(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Post

    @POST("/areas/{areaName}/")
    @Headers("Content-Type: application/json")
    suspend fun postPost(
        @Path("areaName") areaName: String,
        @Body post: Post
    ): Post

    @POST("/areas/{areaName}/{postId}/spread/")
    @Headers("Content-Type: application/json")
    suspend fun postSpread(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body spread: Spread
    ): Response<Unit>

    @PUT("/areas/{areaName}/{postId}/subscribe/")
    @Headers("Content-Type: application/json")
    suspend fun putSubscription(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body subscription: Subscription
    ): Subscription

    @DELETE("/areas/{areaName}/{postId}/")
    suspend fun deletePost(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Response<Unit>


    // Drafts

    @GET("/areas/{areaName}/drafts/")
    suspend fun getDrafts(
        @Path("areaName") areaName: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SuperPost

    @GET("/areas/{areaName}/drafts/{postId}/")
    suspend fun getDraft(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Post

    @POST("/areas/{areaName}/drafts/")
    @Headers("Content-Type: application/json")
    suspend fun postDraft(
        @Path("areaName") areaName: String,
        @Body draft: Draft
    ): Post

    @POST("/areas/{areaName}/drafts/{postId}/publish/")
    @Headers("Content-Type: application/json")
    suspend fun postDraftPublication(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Response<Unit>

    @PUT("/areas/{areaName}/drafts/{postId}/")
    @Multipart
    suspend fun putImage(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Part image: MultipartBody.Part,
        @Part text: MultipartBody.Part
    ): Post

    @PUT("/areas/{areaName}/drafts/{postId}/")
    @Headers("Content-Type: application/json")
    suspend fun putEmptyImage(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body text: DraftNoImageContent
    ): Post

    @PUT("/areas/{areaName}/drafts/{postId}/img/{slot}/")
    @Multipart
    suspend fun putImage(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("slot") slot: Int,
        @Part image: MultipartBody.Part,
        @Part comment: MultipartBody.Part
    ): Image

    @PATCH("/areas/{areaName}/drafts/{postId}/")
    @Headers("Content-Type: application/json")
    suspend fun patchDraft(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body draft: Draft
    ): Post

    @DELETE("/areas/{areaName}/drafts/{postId}/")
    suspend fun deleteDraft(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long
    ): Response<Unit>

    @DELETE("/areas/{areaName}/drafts/{postId}/img/{slot}/")
    suspend fun deleteImage(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("slot") slot: Int
    ): Response<Unit>


    // Comments

    @POST("/areas/{areaName}/{postId}/")
    @Headers("Content-Type: application/json")
    suspend fun postComment(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Body comment: CommentText
    ): Comment

    @POST("/areas/{areaName}/{postId}/")
    @Multipart
    suspend fun postImage(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Part image: MultipartBody.Part,
        @Part text: MultipartBody.Part
    ): Comment

    @DELETE("/areas/{areaName}/{postId}/{commentId}/")
    suspend fun deleteComment(
        @Path("areaName") areaName: String,
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long
    ): Response<Unit>
}
