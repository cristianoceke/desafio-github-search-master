package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    lateinit var carregamento: ProgressBar
    lateinit var icWifiOff: ImageView
    lateinit var txtWifiOff: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        setupListeners()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
        carregamento = findViewById(R.id.pb_carregamento)
        icWifiOff = findViewById(R.id.iv_wifi_off)
        txtWifiOff = findViewById(R.id.tv_titulo)
        //@TDO 1 - Recuperar os Id's da tela para a Activity com o findViewById
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            val conexao = isInternetAvailable()
            if (!conexao) {
                icWifiOff.isVisible = true
                txtWifiOff.isVisible = true
            } else {
                icWifiOff.isVisible = false
                txtWifiOff.isVisible = false

                val nomePesquisar = nomeUsuario.text.toString()
                getAllReposByUserName(nomePesquisar)
                saveUserLocal()
                listaRepositories.isVisible = false
            }
        }
        //@TDO 2 - colocar a acao de click do botao confirmar
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {

        val usuarioInformado = nomeUsuario.text.toString()

        val sharedPreference = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPreference.edit()) {
            putString("saved_username", usuarioInformado)
            apply()
        }

        //Toast.makeText(this, "Usuário salvo com sucesso!",Toast.LENGTH_SHORT).show()
        //@TDO 3 - Persistir o usuario preenchido na editText com a SharedPref no listener do botao salvar
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)
        /*
           @TDO 5 -  realizar a Configuracao base do retrofit
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName(userName: String) {

        if (userName.isNotEmpty()) {

            carregamento.isVisible = true

            githubApi.getAllRepositoriesByUser(userName)
                .enqueue(object : Callback<List<Repository>> {

                    override fun onResponse(
                        call: Call<List<Repository>>,
                        response: Response<List<Repository>>
                    ) {
                        if (response.isSuccessful) {

                            carregamento.isVisible = false
                            listaRepositories.isVisible = true

                            val repositories = response.body()

                            repositories?.let {
                                setupAdapter(repositories)
                            }

                        } else {

                            carregamento.isVisible = false

                            val context = applicationContext
                            Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<List<Repository>>, t: Throwable) {

                        carregamento.isVisible = false

                        val context = applicationContext
                        Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show()
                    }

                })
        }
        // TDO 6 - realizar a implementacao do callback do retrofit e chamar o metodo setupAdapter se retornar os dados com sucesso
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {

        val adapter = RepositoryAdapter(
            this, list
        )

        listaRepositories.adapter = adapter
        /*
            @TDO 7 - Implementar a configuracao do Adapter , construir o adapter e instancia-lo
            passando a listagem dos repositorios
         */
    }

    private fun showUserName() {

        val sharedPreference = getPreferences(MODE_PRIVATE) ?: return
        val ultimoPesquisado = sharedPreference.getString("saved_username", null)

        if (!ultimoPesquisado.isNullOrEmpty()) {
            nomeUsuario.setText(ultimoPesquisado)
        }
        //@TDO 4- depois de persistir o usuario exibir sempre as informacoes no EditText  se a sharedpref possuir algum valor, exibir no proprio editText o valor salvo
    }

}