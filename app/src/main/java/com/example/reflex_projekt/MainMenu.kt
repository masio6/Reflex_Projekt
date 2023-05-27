package com.example.reflex_projekt

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import com.example.reflex_projekt.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenu : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var textLevelChoose: TextView

    private lateinit var appDatabase: AppDatabase

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
        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)

        appDatabase = AppDatabase.getDatabase(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textLevelChoose = view.findViewById(R.id.levelChooseText)

        startAnimation()

        view.findViewById<Button>(R.id.firstLevelButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainMenu_to_firstLevel)
        }

        view.findViewById<Button>(R.id.secondLevelButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainMenu_to_secondLevel)
        }

        view.findViewById<Button>(R.id.thirdLevelButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainMenu_to_thirdLevel)
        }


        view.findViewById<Button>(R.id.top10button).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainMenu_to_top10list)
        }

        view.findViewById<Button>(R.id.deleteData).setOnClickListener {
            deleteData()
        }
    }

    private fun deleteData() {
        (activity?.let {
            val builder =
                AlertDialog.Builder(it).setMessage("Czy na pewno chcesz usunąć wszystkie zapisane rekordy?")
            builder.apply {
                setPositiveButton("TAK",
                    DialogInterface.OnClickListener { dialog, id ->
                        runBlocking(Dispatchers.IO) {
                            appDatabase.stats2x2Dao().deleteAll()
                            appDatabase.stats3x3Dao().deleteAll()
                            appDatabase.stats4x4Dao().deleteAll()
                        }
                    })
                setNegativeButton("NIE",
                    DialogInterface.OnClickListener { dialog, id ->
                        //
                    })
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")).show()
    }

    private fun startAnimation() {
        val animation1 = AnimationUtils.loadAnimation(this.context, R.anim.anim_1)

        textLevelChoose.startAnimation(animation1)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainMenu.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainMenu().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}