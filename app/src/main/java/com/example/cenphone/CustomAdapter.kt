// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(
    private var phones: List<Phone>,
    private val onPhoneSelected: (String, Int, Long) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var selectedPosition: Int = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.modelName)
        val radioButton: RadioButton = view.findViewById(R.id.radioButton)
        val imageView: ImageView = view.findViewById(R.id.modelImage)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_model, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val phone = phones[position]

        // Set the model name and price dynamically
        viewHolder.textView.text = phone.phoneModel

        // Set the image based on the model
        val imageRes = when (phone.phoneMake) {
            "iPhone" -> R.mipmap.iphonemodel_foreground
            "Samsung" -> R.mipmap.samsungmodel_foreground
            "Google Pixel" -> R.mipmap.googlemodel_foreground
            "Xiaomi" -> R.mipmap.xiaomi_model_foreground
            else -> R.mipmap.iphonemodel_foreground
        }
        viewHolder.imageView.setImageResource(imageRes)

        viewHolder.radioButton.isChecked = position == selectedPosition

        // When a model is selected, update the total price
        viewHolder.radioButton.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = if (selectedPosition == position) -1 else position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            if (selectedPosition != -1) {
                val selectedPhone = phones[selectedPosition]
                Log.d("CustomAdapter", "Selected Phone ID: ${selectedPhone.productId}")
                onPhoneSelected(selectedPhone.phoneModel, selectedPhone.price.toInt(), selectedPhone.productId)
            } else {
                onPhoneSelected("", 0, 0)
            }
        }
    }

    override fun getItemCount() = phones.size

    fun getSelectedModel(): String? {
        return if (selectedPosition != -1) phones[selectedPosition].phoneModel else null
    }

    fun getSelectedModelPrice(): Int {
        return if (selectedPosition != -1) {
            phones[selectedPosition].price.toInt()
        } else {
            0
        }
    }

    fun updatePhones(newPhones: List<Phone>) {
        phones = newPhones
        notifyDataSetChanged()
    }
}
