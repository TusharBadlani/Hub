package cessini.technology.commonui.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.R
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.common.BottomSheetLevelInterface
import cessini.technology.commonui.databinding.FragmentShareMyworldBinding
import cessini.technology.commonui.viewmodel.BaseViewModel

import cessini.technology.newrepository.myworld.ProfileRepository

import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class ShareMyworldFragment
    : BaseBottomSheet<FragmentShareMyworldBinding>(R.layout.fragment_share_myworld),
    BottomSheetLevelInterface {

    companion object {
        private const val TAG = "ShareMyWorldFragment"
    }


    var username = ""
    var profileimg = ""
    lateinit var sharelink: String
    val baseViewModel: BaseViewModel by viewModels()
    lateinit var sharedPreferences: SharedPreferences
    var count = 0
    lateinit var adapter: Adapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(cessini.technology.commonui.R.string.sharedPrefName),
            Context.MODE_PRIVATE
        )
        lateinit var profileRepository: ProfileRepository
        lateinit var recAdapter: RecAdapter
        val bundle = this.arguments

        if (bundle != null) {
            username = bundle.getString("username").toString()
            profileimg = bundle.getString("profileimg").toString()
            sharelink = bundle.getString("sharelink").toString()
        }
        var txt = view.findViewById<TextView>(R.id.textView13)

        txt.text = "Share profile via "
        var usernametxt = view.findViewById<TextView>(R.id.textView14)
        usernametxt.text = username

        Glide.with(requireContext()).load(profileimg).into(view.findViewById(R.id.imageView9))




        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isDraggable = false
        }

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
        var linktxt = view.findViewById<TextView>(R.id.textView16)
        binding.textView16.text = "https://myworld-311307-default-rtdb..."
        //for dark theme
//        val recview = view.findViewById<ListView>(R.id.myrecview)
        val pm: PackageManager = activity!!.packageManager
        val finalLaunchables: MutableList<ResolveInfo> = ArrayList()
        val recyclerView: RecyclerView = view.findViewById(R.id.myrecview)
        recAdapter = RecAdapter(pm, finalLaunchables, context, sharelink)
        recyclerView.adapter = recAdapter
        val linearLayoutManager: LinearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager
        val main = Intent(Intent.ACTION_MAIN, null)
        main.addCategory(Intent.CATEGORY_LAUNCHER)
        val launchables: List<ResolveInfo> = pm.queryIntentActivities(main, 0)
        for (resolveInfo in launchables) {
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName.contains("om.viber.voip") ||
                packageName.contains("com.twitter.android") ||
                packageName.contains("com.google.android.gm") ||
                packageName.contains("com.whatsapp") ||
                packageName.contains("com.facebook.katana") ||
                packageName.contains("com.instagram.android")
            ) {
//                Log.d(TAG,"The code goes here while adding new activity")
                finalLaunchables.add(resolveInfo)
            }
        }
        Collections.sort(
            finalLaunchables,
            ResolveInfo.DisplayNameComparator(pm)
        )
        recAdapter.notifyDataSetChanged()

    }



    private fun handleFirstTimeUser() {
        TODO("Not yet implemented")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
        }

        return dialog
    }

    override fun onSheet2Dismissed() {
        setLevel(-1)
    }

    override fun onSheet2Created() {
        setLevel(-2)
    }

    override fun onSheet1Dismissed() {
        setLevel(0)

    }

    override fun getHeightOfBottomSheet(height: Int) {
        setLevel(-1)
    }

    internal class RecAdapter(
        pm: PackageManager?,
        private val apps: List<ResolveInfo?>,
        val context: Context?,
        val link: String
    ) :
        RecyclerView.Adapter<RecAdapter.MyViewHolder>() {
        internal class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val label = view.findViewById<View>(R.id.mylabel) as TextView
            var icon: ImageView = view.findViewById<View>(R.id.myicon) as ImageView
        }

        private var pm: PackageManager? = null
        override fun getItemCount(): Int {
            return apps.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.myrow, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val label = apps[position]?.loadLabel(pm)
            holder.label.text = label
            val icon = apps.get(position)?.loadIcon(pm)
            holder.icon.setImageDrawable(icon)
            holder.itemView.setOnClickListener {
                val launchable = apps.get(position) as ResolveInfo
                val activity: ActivityInfo = launchable.activityInfo
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                val packageName = activity.packageName
                i.putExtra(Intent.EXTRA_TEXT, link)
                i.setPackage(packageName)
                context?.startActivity(i)
            }
        }

        init {
            this.pm = pm
        }
    }
}