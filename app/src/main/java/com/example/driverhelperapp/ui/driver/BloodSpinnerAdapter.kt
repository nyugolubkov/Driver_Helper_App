package com.example.driverhelperapp.ui.driver

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.driverhelperapp.R

class BloodSpinnerAdapter(
    private val context: Context,
    private val items: List<String>,
    private val image: Int
) : ArrayAdapter<String>(context, R.layout.spinner_layout, items) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.spinner_item_layout, parent, false)

        val spinnerText = view.findViewById<TextView>(R.id.spinnerItemTextView)

        spinnerText.text = items[position]

        return view
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.spinner_layout, parent, false)

        val spinnerImage = view.findViewById<ImageView>(R.id.spinnerLeftImageView)
        val spinnerText = view.findViewById<TextView>(R.id.spinnerMainTextView)

        spinnerImage.setImageResource(image)
        spinnerText.text = items[position]

        return view
    }

    override fun getItem(position: Int): String {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

}