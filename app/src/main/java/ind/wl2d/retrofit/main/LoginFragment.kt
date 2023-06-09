package ind.wl2d.retrofit.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import ind.wl2d.retrofit.R
import ind.wl2d.retrofit.databinding.FragmentLoginBinding
import ind.wl2d.retrofit.retrofitLib.AuthRequest
import ind.wl2d.retrofit.retrofitLib.MainAPI
import ind.wl2d.retrofit.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mainAPI: MainAPI
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRetrofit()
        binding.apply {
            bNext.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_productsFragment)
            }
            bSignIn.setOnClickListener {
                auth(
                    AuthRequest(
                        username.text.toString(),
                        password.text.toString()
                    )
                )
            }
        }
    }

    private fun initRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder() // logcat client(for debugging)
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder() // retrofit creating
            .baseUrl("https://dummyjson.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mainAPI = retrofit.create(MainAPI::class.java) // retrofit instance
    }

    private fun auth(authRequest: AuthRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainAPI.auth(authRequest)
            val message = response.errorBody()?.string()?.let{
                JSONObject(it).getString("message")
            }
            requireActivity().runOnUiThread{
                binding.error.text = message
                val user = response.body()
                if (user != null) {
                    Picasso.get().load(user.image).into(binding.imageView) // load image
                    binding.name.text = user.firstName // update name
                    binding.bNext.visibility = View.VISIBLE // button -> visible
                    viewModel.token.value = user.token // LoginViewModel get token for second screen!
                }
            }
        }
    }
}