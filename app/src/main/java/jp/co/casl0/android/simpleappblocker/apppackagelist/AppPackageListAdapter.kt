/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.casl0.android.simpleappblocker.apppackagelist

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.co.casl0.android.simpleappblocker.PackageInfo
import jp.co.casl0.android.simpleappblocker.R

class AppPackageListAdapter(private val context: Context?, var packageInfoList: List<PackageInfo>) :
    RecyclerView.Adapter<AppPackageListAdapter.AppPackageListViewHolder>() {
    inner class AppPackageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val packageIconImageView: ImageView = itemView.findViewById(R.id.package_icon_imageview)
        val appNameTextView: TextView = itemView.findViewById(R.id.app_name_textview)
        val packageTextView: TextView = itemView.findViewById(R.id.package_textview)
    }

    interface OnItemClickListener {
        fun onItemClicked(view: View, position: Int, packageInfo: PackageInfo)
    }

    private val inflater = LayoutInflater.from(context)
    private lateinit var itemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppPackageListViewHolder {
        val itemView = inflater.inflate(R.layout.app_package_view, parent, false)
        return AppPackageListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppPackageListViewHolder, position: Int) {
        holder.packageIconImageView.setImageDrawable(packageInfoList[position].icon)
        holder.appNameTextView.apply {
            text = packageInfoList[position].appName
            if (packageInfoList[position].isAllowed) {
                getColorInt(R.color.filters_enabled)?.also { color ->
                    setTextColor(color)
                }
            }
        }
        holder.packageTextView.text = packageInfoList[position].packageName
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClicked(it, position, packageInfoList[position])
        }
    }

    override fun getItemCount(): Int {
        return packageInfoList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    private fun getColorInt(resourceId: Int): Int? {
        return if (Build.VERSION.SDK_INT >= 23) {
            context?.resources?.getColor(resourceId, context.theme)
        } else {
            context?.resources?.getColor(resourceId)
        }
    }
}