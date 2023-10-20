package br.com.igorbag.githubsearch.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val context: Context, private val repositories: List<Repository>) : RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteúdo da view e troca pela informação de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // TDO 8 - Realizar o bind do viewHolder
        val repository = repositories[position]

        holder.cardRepositorio.setOnClickListener {
            openBrowser(context, repository.htmlUrl)
        }

        // Exemplo de Bind
        holder.nomeRepositorio.text = repository.name
        //holder.repositoryDescription.text = repository.description

        // Exemplo de click no item
        holder.shareIconButton.setOnClickListener {
            shareRepositoryLink(context, repository.htmlUrl)
        }

        // Exemplo de click no btn Share
        //holder.btnShare.setOnClickListener {
        //    btnShareListener(repository)
        //}
    }

    // Pega a quantidade de repositórios da lista
    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TDO 10 - Implementar o ViewHolder para os repositórios


        val cardRepositorio : CardView
        val nomeRepositorio : TextView
        val shareIconButton : ImageView

        init {
            view.apply {
                cardRepositorio = findViewById(R.id.cv_car)
                nomeRepositorio = findViewById(R.id.tv_preco)
                shareIconButton = findViewById(R.id.iv_favorite)
            }
        }
    }

    fun shareRepositoryLink(context: Context, urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
    // @Tdo 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(context: Context, urlRepository: String) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}


