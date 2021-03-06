package mobi.mele.beers.ui.main

/**
 * Created by Antonio Fernández
 * date   : 27/12/21
 * e-mail : meleappdev@gmail.com
 */
import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.Observer
import mobi.mele.beers.R
import mobi.mele.beers.databinding.ActivityMainBinding
import mobi.mele.beers.extensions.app
import mobi.mele.beers.extensions.getViewModel
import mobi.mele.beers.extensions.startActivity
import mobi.mele.beers.ui.detail.DetailActivity
import mobi.mele.beers.ui.main.adapter.BeersAdapter
import mobi.mele.beers.ui.main.MainViewModel.UIModelBeers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var beersAdapter: BeersAdapter

    private lateinit var component: MainComponent
    private val mainViewModel: MainViewModel by lazy {
        getViewModel { component.mainViewModel }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //include component before call super in onCreate
        component = app.component.plus(MainModule())
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        beersAdapter = BeersAdapter(mainViewModel::onBeerClicked)
        binding.recyclerBeers.adapter = beersAdapter
        mainViewModel.uiModelBeers.observe(this, Observer(::updateUI))

        beersAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /*
        * Cómo configurar el widget de búsqueda: https://developer.android.com/guide/topics/search/search-dialog?hl=es-419
        */
        menuInflater.inflate(R.menu.search_option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val menuItem = menu?.findItem(R.id.search)
        val searchView = menuItem?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                /*if (!query.isNullOrBlank()) {
                    searchBeers(query)
                }*/
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    searchBeers(newText)
                }
                return true
            }
        })

        /*
        * Si queremos que al cargar la activity automáticamente se despliegue la opción de buscar
        */
        //menuItem.expandActionView()

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchView.isIconifiedByDefault = false
                searchView.requestFocusFromTouch()
                setEmptySerachView()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                setRecipesInitialState()
                return true
            }
        })

        return true
    }

    private fun updateUI(uiModelBeers: UIModelBeers) {

        binding.progress.visibility =
            if (uiModelBeers is UIModelBeers.Loading) View.VISIBLE else View.GONE

        when (uiModelBeers){
            is UIModelBeers.Content -> beersAdapter.beers = uiModelBeers.beers
            is UIModelBeers.Navigation -> startActivity<DetailActivity> {
                println(uiModelBeers.beer.id)
                putExtra(DetailActivity.BEER, uiModelBeers.beer.id)
            }
        }
        notifyChangesToAdapter()
    }

    private fun searchBeers(query: String) {
        mainViewModel.doSearch(query)
    }

    private fun setRecipesInitialState() {
        mainViewModel.refresh()
    }

    private fun setEmptySerachView() {
        mainViewModel.cleanResponseSearch()
    }

    private fun notifyChangesToAdapter(){
        beersAdapter.notifyDataSetChanged()
    }
}