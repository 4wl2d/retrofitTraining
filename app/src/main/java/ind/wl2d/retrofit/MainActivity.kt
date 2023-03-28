package ind.wl2d.retrofit

import android.os.Bundle
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ind.wl2d.retrofit.adapter.ProductAdapter
import ind.wl2d.retrofit.databinding.ActivityMainBinding
import ind.wl2d.retrofit.retrofit1.AuthRequest
import ind.wl2d.retrofit.retrofit1.MainAPI
import ind.wl2d.retrofit.retrofit1.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ProductAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder() // клиент для logcat
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder() // создание ретрофита
            .baseUrl("https://dummyjson.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mainAPI = retrofit.create(MainAPI::class.java) // инстанция ретрофита

        var user: User? = null

        binding.sv.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { // когда на клавиатуре есть кнопка поиска
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {

                    val list = text?.let { mainAPI.getProductsByNameAuth(user?.token ?: "", it) }

                    runOnUiThread {
                        binding.apply {
                            adapter.submitList(list?.products)
                        }
                    }
                }
                return true
            }

        })

    }
}