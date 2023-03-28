package ind.wl2d.retrofit.retrofitLib

import retrofit2.Response
import retrofit2.http.*

interface MainAPI {
    @Headers(
        "Content-Type: application/json"
    )
    @GET("auth/products/{id}") // getting one product by id
    suspend fun getProductById(@Header("Authorization") token: String,
                               @Path("id") id: String): Product

    @Headers(
        "Content-Type: application/json"
    )
    @POST("auth/login") // logging in
    suspend fun auth(@Body authRequest: AuthRequest): Response<User>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("auth/products") // getting all products if we need
    suspend fun getAllProducts(@Header("Authorization") token: String): Products

    @Headers(
        "Content-Type: application/json"
    )
    @GET("auth/products/search") // searching products with searchView by name
    suspend fun getProductsByName(@Header("Authorization") token: String,
                                  @Query("q") name: String): Products
}