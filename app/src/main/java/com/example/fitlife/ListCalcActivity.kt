package com.example.fitlife

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitlife.model.Calc
import java.text.SimpleDateFormat
import java.util.Locale

class ListCalcActivity : AppCompatActivity() {

    private lateinit var rvDb: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc)

        val dataItems = mutableListOf<Calc>()
        val adapter = DataAdapter(dataItems)
        rvDb = findViewById(R.id.rv_bd_activity)
        rvDb.layoutManager = LinearLayoutManager(this)
        rvDb.adapter = adapter

        val type =
            intent?.extras?.getString("type") ?: throw IllegalStateException("type not found")
        Thread {
            val app = application as App
            val dao = app.db.calcDao()
            val response = dao.getRegisterByType(type)

            runOnUiThread {
                dataItems.addAll(response)
                adapter.notifyDataSetChanged()
            }
        }.start()

    }

    private inner class DataAdapter(private val listDb: List<Calc>) :
        RecyclerView.Adapter<DataAdapter.DataViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
            val inflate = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return DataViewHolder(inflate)
        }

        override fun getItemCount(): Int {
            return listDb.size
        }

        override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
            val itemCurrent = listDb[position]
            holder.bind(itemCurrent)
        }

        private inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Calc) {
                val simpleList = item as TextView

                val sdf = SimpleDateFormat("dd:MM:yyyy HH:mm", Locale("pt", "BR"))
                val resData = sdf.format(item.createDate)
                val res = item.res
                simpleList.text = getString(R.string.list_response, res, resData)

            }
        }

    }

}


