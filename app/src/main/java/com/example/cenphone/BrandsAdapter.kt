// Student Name : Mukund Kapadia : 301403876
//                Komal Mavani : 301472922
//                Kristina Khristi : 301483429
package com.example.cenphone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BrandsAdapter(
    private val brands: List<String>,
    private val onBrandSelected: (String) -> Unit
) : RecyclerView.Adapter<BrandsAdapter.BrandViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_brands, parent, false)
        return BrandViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val brand = brands[position]
        holder.brandName.text = brand

        // Example: Set brand logo dynamically based on name
        val imageResId = when (brand) {
            "iPhone" -> R.mipmap.apple_foreground
            "Samsung" -> R.mipmap.samsung_foreground
            "Google Pixel" -> R.mipmap.google_foreground
            "Xiaomi" -> R.mipmap.xiaomi_foreground
            else -> R.mipmap.apple_foreground
        }
        holder.brandLogo.setImageResource(imageResId)

        holder.itemView.setOnClickListener {
            onBrandSelected(brand)
        }
    }

    override fun getItemCount(): Int = brands.size

    class BrandViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val brandLogo: ImageView = view.findViewById(R.id.brandLogo)
        val brandName: TextView = view.findViewById(R.id.brandName)
    }
}
