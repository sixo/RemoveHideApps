package eu.sisik.removehideapps

import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.package_item.view.*

/**
 * Copyright (c) 2019 by Roman Sisik. All rights reserved.
 */
class PackageAdapter(
    private val packageList: List<PackageInfo>,
    private val onPackageSelected: (packageInfo: PackageInfo) -> Unit):
    RecyclerView.Adapter<PackageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.package_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return packageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= 0 && position < packageList.size) {
            val item = packageList[position]
            holder.tvPackageName.text = item.packageName
        }
    }


    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvPackageName = v.findViewById<TextView>(R.id.tvPackageName)

        init {
            tvPackageName.setOnClickListener {
                val position = adapterPosition
                if (position >= 0 && position < packageList.size) {
                    onPackageSelected(packageList[position])
                }
            }
        }
    }
}