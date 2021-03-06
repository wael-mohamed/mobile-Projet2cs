package com.waelkhelil.sayara_dz.view.compare

import android.app.Dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.waelkhelil.sayara_dz.R
import com.waelkhelil.sayara_dz.SharedViewModel
import com.waelkhelil.sayara_dz.database.model.Option
import com.waelkhelil.sayara_dz.database.model.Version

class CompareFragment: BottomSheetDialogFragment() {

    companion object {
        const val TAG = "CompareFragment"
        fun newInstance() = CompareFragment()
    }
    private lateinit var mDialog : BottomSheetDialog
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Share data between fragments
        sharedViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expanded_compare, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { item: MenuItem ->  this.onOptionsItemSelected(item)}
        toolbar.setNavigationOnClickListener {dismissAllowingStateLoss()}

        tableLayout = view.findViewById(R.id.table_layout)
        sharedViewModel.mCompareList.observe(viewLifecycleOwner, Observer<Set<Version>> {
            if (it.size > 1){
                tableLayout.removeAllViews()
                createTable(it)
            }
            else{
                dismissAllowingStateLoss()
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_clear) {
            sharedViewModel.freeVersions()
            true
        } else super.onOptionsItemSelected(item)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return mDialog
    }
    override fun onStart() {
        super.onStart()
        mDialog.behavior.state = STATE_EXPANDED
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun createTable(versionsList:Set<Version>) {
        val optionSet = mutableSetOf<Option>()
        val list :List<Option> = listOf(Option("1","op1"),Option("2","op2"))

//      Create header
        val row = TableRow(context)
        row.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        var lTextView = TextView(context)
        lTextView.setPadding(0,0,24,0)
        lTextView.text = getString(R.string.options)
        if(Build.VERSION.SDK_INT < 23){
        lTextView.setTextAppearance(this.activity,android.R.style.TextAppearance_Material_Medium)}
        else{  lTextView.setTextAppearance(android.R.style.TextAppearance_Material_Medium)}
        row.addView(lTextView)
        versionsList.forEach {
            lTextView = TextView(context)
            lTextView.setPadding(0,0,24,0)


            if(Build.VERSION.SDK_INT < 23){
                lTextView.setTextAppearance(this.activity,android.R.style.TextAppearance_Material_Medium)}
            else{  lTextView.setTextAppearance(android.R.style.TextAppearance_Material_Medium)}

            lTextView.text = it.name
            var lChip = Chip(context).apply {
                text = it.name
                isCloseIconVisible = true
                isClickable = false
            }
            lChip.setOnCloseIconClickListener {_ ->
                sharedViewModel.removeFromCompareList(it)
            }

                row.addView(lChip)
            optionSet.addAll(/*it.compatibleOptions*/ list )
        }
        tableLayout.addView(row)

        //Create Content
        optionSet.forEach {
            val row = TableRow(context)
            lTextView = TextView(context)
            lTextView.text = it.name
            row.addView(lTextView)
            versionsList.forEach { v ->
                lTextView = TextView(context)
                if (/*v.compatibleOptions*/list.contains(it))
                    lTextView.text = getString(R.string.yes)
                else
                    lTextView.text = getString(R.string.no)
                row.addView(lTextView)
            }
            tableLayout.addView(row)
        }
    }

}