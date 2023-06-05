package com.example.reflex_projekt

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.reflex_projekt.databinding.FragmentTop10listBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.jvm.internal.Intrinsics.Kotlin
import android.graphics.Color
import android.widget.ImageButton

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Top10list.newInstance] factory method to
 * create an instance of this fragment.
 */
class Top10list : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var rv: RecyclerView;
    lateinit var arrayList:ArrayList<Player>;

    lateinit var dbref: DatabaseReference

    private var ClickedColor: Int = Color.parseColor("#EDC967")
    private var notClickedColor: Int = Color.parseColor("#35A306")
    private var _binding: FragmentTop10listBinding? = null
    private val binding get() = _binding!!
    var level:Int=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)






        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTop10listBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_top10list, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arrayList= arrayListOf<Player>()
        rv=view.findViewById(R.id.recyclerView)
        rv.layoutManager= LinearLayoutManager(this.context)
        rv.setHasFixedSize(true)
        getGamedata()
        var but1=view.findViewById<Button>(R.id.firstLevelButton)
        var but2=view.findViewById<Button>(R.id.secondLevelButton)

        var but3=view.findViewById<Button>(R.id.thirdLevelButton)

        var menubut=view.findViewById<ImageButton>(R.id.imageButton2)

        menubut.setOnClickListener(){




                Navigation.findNavController(view).navigate(R.id.action_top10list_to_mainMenu)


        }
but1.setOnClickListener(){
    level=1

    but1.setBackgroundColor(ClickedColor)
    but2.setBackgroundColor(notClickedColor)
    but3.setBackgroundColor(notClickedColor)
    getGamedata()
}
but2.setOnClickListener(){
    level=2
    but2.setBackgroundColor(ClickedColor)
    but1.setBackgroundColor(notClickedColor)
    but3.setBackgroundColor(notClickedColor)
getGamedata()
}

        but3.setOnClickListener(){
            level=3
            but3.setBackgroundColor(ClickedColor)
            but2.setBackgroundColor(notClickedColor)
            but1.setBackgroundColor(notClickedColor)
getGamedata()

        }
    }

        private fun getGamedata() {
        arrayList.clear()

        dbref= FirebaseDatabase
            .getInstance("https://top10-clickers-list-default-rtdb.firebaseio.com/")
            .getReference("lists")
            .child(level.toString())

        dbref.addValueEventListener(object : ValueEventListener {


            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
               arrayList.clear()
                if(snapshot.exists()){
                    for (gamesnapshot in snapshot.children){
                        val game=gamesnapshot.getValue(Player::class.java)


                                   arrayList.add(game!!)




            }
                   sortPlayersByTime(arrayList)

                 rv.adapter = MyAdapter(arrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }


    fun sortPlayersByTime(players: ArrayList<Player>) {
        players.sortBy { it.time }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Top10list.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Top10list().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}