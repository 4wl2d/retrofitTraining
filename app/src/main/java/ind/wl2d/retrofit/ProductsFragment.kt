package ind.wl2d.retrofit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ind.wl2d.retrofit.adapter.ProductAdapter
import ind.wl2d.retrofit.databinding.FragmentProductsBinding
import ind.wl2d.retrofit.retrofit1.MainAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ProductsFragment : Fragment() {
    private lateinit var adapter: ProductAdapter
    private lateinit var binding: FragmentProductsBinding
    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var mainAPI: MainAPI


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRetrofit()
        initRcView()
        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                binding.sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean { // когда на клавиатуре есть кнопка поиска
                        return true
                    }

                    override fun onQueryTextChange(text: String?): Boolean {
                        CoroutineScope(Dispatchers.IO).launch {

                            val list = text?.let { mainAPI.getProductsByNameAuth(token ?: "", it) }

                            requireActivity().runOnUiThread {
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
    }

    private fun initRetrofit() {
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
        mainAPI = retrofit.create(MainAPI::class.java) // инстанция ретрофита
    }

    private fun initRcView() = with(binding) {
        adapter = ProductAdapter()
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = adapter
    }
}