package com.example.fitlife

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitlife.model.Calc
import java.text.SimpleDateFormat
import java.util.Locale

class ListCalcActivity : AppCompatActivity(), OnListClickListener {

    private lateinit var adapter: DataAdapter
    private lateinit var result: MutableList<Calc>

    private lateinit var rvDb: RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc)

        val dataItems = mutableListOf<Calc>()
        val adapter = DataAdapter(dataItems, this)
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

    override fun onClick(id: Int, type: String) {
        when (type) {
            "imc" -> {
                val intent = Intent(this, ImcActivity::class.java)
                intent.putExtra("updateId", id)
                startActivity(intent)
            }

            "tmb" -> {
                val intent = Intent(this, TmbActivity::class.java)
                intent.putExtra("updateId", id)
                startActivity(intent)
            }
        }
        finish()
    }

    override fun onLongClick(position: Int, calc: Calc) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_message))
            .setNegativeButton(android.R.string.cancel) { dialog, which ->
            }
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                //FIXME: melhorar o codigo entender pq nao esta atualizando o historico
                //FIXME: e sim, abrindo a activitiy main
                Thread {
                    val app = application as App
                    val dao = app.db.calcDao()
                    val response = dao.delete(calc)
                    if (response > 0) {
                        runOnUiThread {
                            result.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }.start()
            }
            .create()
            .show()
    }

    private inner class DataAdapter(
        private val listDb: List<Calc>,
        private var onListClickListener: OnListClickListener
    ) :
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
                val simpleList = itemView as TextView

                val sdf = SimpleDateFormat("dd:MM:yyyy HH:mm", Locale("pt", "BR"))
                val resData = sdf.format(item.createDate)
                val res = item.res
                simpleList.text = getString(R.string.list_response, res, resData)

                simpleList.setOnLongClickListener {
                    onListClickListener.onLongClick(adapterPosition, item)
                    true
                }
                simpleList.setOnClickListener {
                    onListClickListener.onClick(item.id, item.type)
                }

            }
        }

    }

}


