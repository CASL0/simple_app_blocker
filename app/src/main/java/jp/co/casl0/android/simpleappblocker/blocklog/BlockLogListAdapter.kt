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

package jp.co.casl0.android.simpleappblocker.blocklog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.co.casl0.android.simpleappblocker.R

class BlockLogListAdapter(context: Context?, var blockLogList: List<Pair<String, String>>) :
    RecyclerView.Adapter<BlockLogListAdapter.BlockLogListViewHolder>() {
    inner class BlockLogListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blockAddressesTextView: TextView = itemView.findViewById(R.id.block_packet_addresses)
        val blockTimeTextView: TextView = itemView.findViewById(R.id.block_time)
    }

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockLogListViewHolder {
        val itemView = inflater.inflate(R.layout.block_log_view, parent, false)
        return BlockLogListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return blockLogList.size
    }

    override fun onBindViewHolder(holder: BlockLogListViewHolder, position: Int) {
        holder.blockAddressesTextView.text = blockLogList[position].first
        holder.blockTimeTextView.text = blockLogList[position].second
    }
}