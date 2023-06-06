package com.example.reflex_projekt
import java.math.BigDecimal
import java.math.RoundingMode
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.example.reflex_projekt.database.AppDatabase
import com.example.reflex_projekt.database.Stats3x3
import com.example.reflex_projekt.database.Stats4x4
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlin.random.Random
import java.text.DecimalFormat
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdLevel.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdLevel : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var playing = false

    private lateinit var dbref: DatabaseReference
    private var refreshNumber = 50
    private var refreshCounter = 0
    private var points = 0
    private var maxTimeInMilis = 10*60*1000L
    private var timeInMilis = 0L

    private lateinit var timer: CountDownTimer

    private var listOfSquares = mutableListOf<View>()

    private var listOfColors = arrayListOf("#167288", "#8cdaec", "#b45248", "#d48c84", "#a89a49", "#d6cfa2",
        "#3cb464", "#9bddb1", "#643c6a", "#836394", "#d25935", "#5d73d7", "#cc3284", "#b88e5d", "#8ababc", "#415e20")
    private var goldColor = "#fbd815"

    private lateinit var pointsField: TextView
    private lateinit var timeField: TextView
    private lateinit var refreshNumberField: TextView
    private lateinit var nameOfLevel: TextView
    private lateinit var recordField: TextView
    lateinit var arrayList:ArrayList<Player>;
    private lateinit var btnBack: Button
    private lateinit var btnStart: Button

    private val audio = ToneGenerator(AudioManager.STREAM_MUSIC, 1000)

    private lateinit var appDatabase: AppDatabase

    private var listOfRecords = mutableListOf<Stats4x4>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    @SuppressLint("DiscouragedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third_level, container, false)

        appDatabase = AppDatabase.getDatabase(requireContext())
        arrayList= arrayListOf<Player>()
        pointsField = view.findViewById(R.id.pointsField)
        timeField = view.findViewById(R.id.timeField)
        refreshNumberField = view.findViewById(R.id.refreshNumber)
        recordField = view.findViewById(R.id.recordField)
        nameOfLevel = view.findViewById(R.id.nameOfLevel)

        btnBack = view.findViewById(R.id.buttonBack)
        btnStart = view.findViewById(R.id.buttonStart)

        dataInit()

        timer = object : CountDownTimer(maxTimeInMilis, 1) {
            override fun onTick(p0: Long) {
                timeInMilis = maxTimeInMilis - p0
                timeField.text =  timeFormatConvert(timeInMilis)
            }

            override fun onFinish() {
                endGame(view.context)
            }
        }

        for (i in 0..15) {
            val id = resources.getIdentifier("item_$i", "id", context?.packageName)
            listOfSquares.add(view.findViewById(id) as View)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        readRecords()

        startAnimation()

        listOfSquares.forEach {
            it.isEnabled = false
        }

        pointsField.isVisible = false
        timeField.isVisible = false
        refreshNumberField.isVisible = false

        btnBack.setOnClickListener {
            if (playing) {
                (activity?.let {
                    val builder =
                        AlertDialog.Builder(it).setMessage("Na pewno chcesz wyjść podczas trwającej rozgrywki?")
                    builder.apply {
                        setPositiveButton("TAK",
                            DialogInterface.OnClickListener { dialog, id ->
                                Navigation.findNavController(view).navigate(R.id.action_thirdLevel_to_mainMenu)
                            })
                        setNegativeButton("NIE",
                            DialogInterface.OnClickListener { dialog, id ->
                                //
                            })
                    }
                    builder.create()
                } ?: throw IllegalStateException("Activity cannot be null")).show()
            } else {
                Navigation.findNavController(view).navigate(R.id.action_thirdLevel_to_mainMenu)
            }        }

        btnStart.setOnClickListener {
            startGame()
        }

        listOfSquares.forEach {
            it.setOnClickListener {
                if((it.background as ColorDrawable).color == Color.parseColor(goldColor)) {
                    audio.startTone(ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE, 150)
                    points++
                } else {
                    if(points > 0) {
                        audio.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                        points--
                    }
                }

                refreshArea()

                if(refreshCounter - 1 == refreshNumber) {
                   endGame(view.context)
                }
            }
        }

        (activity?.let {
            val builder =
                AlertDialog.Builder(it).setMessage("LEVEL 3 - plansza 4x4\n" +
                        "\nGra polega na klikaniu w ŻÓŁTY kwadrat." +
                        "\nPlansza zmienia swoje ułożenie po kliknięciu w nią.\n" +
                        "\nTrafienie w ŻÓŁTY kwadrat dodaje punkt a kliknięcie w kwadrat" +
                        " o innym kolorze odejmuje go.\n" +
                        "\nGra kończy się po 50 kliknięciach lub po upływie 10 minut.\n" +
                        "\nPowodzenia!")
            builder.apply {
                setPositiveButton("DALEJ",
                    DialogInterface.OnClickListener { dialog, id ->
                        //
                    })
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")).show()
    }

    private fun startAnimation() {
        val animation1 = AnimationUtils.loadAnimation(this.context, R.anim.anim_1)

        recordField.startAnimation(animation1)
    }

    private fun dataInit() {
        runBlocking(Dispatchers.IO) {
            listOfRecords = appDatabase.stats4x4Dao().getAll()
        }
    }

    @SuppressLint("SetTextI18n")
    fun readRecords() {
        val list = mutableListOf<Stats4x4>()

        var maxPointsResult = 0
        var minTimeResult: Long = 10*60*1000

        var recordObject = Stats4x4(50000L, 0)

        if(listOfRecords.isNotEmpty()) {
            listOfRecords.forEach {
                if(it.points4x4 >= maxPointsResult) {
                    maxPointsResult = it.points4x4
                    list.add(it)
                }
            }

            list.forEach {
                if(it.time4x4 < minTimeResult) {
                    minTimeResult = it.time4x4
                    recordObject = it
                }
            }

            recordField.text = "Twój rekord: \nPunktów: ${recordObject.points4x4}/50\nCzas: ${recordObject.time4x4?.let {
                timeFormatConvert(
                    it
                )
            }}"
        }
        else {
            recordField.text = "Twój rekord: \nBrak rekordów!"
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun writeResultToRecords(timeInMilis: Long, points: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.stats4x4Dao().insert(Stats4x4(timeInMilis, points))
        }

        listOfRecords.add(Stats4x4(timeInMilis, points))
    }

    fun timeFormatConvert(timeInMilis: Long): String {
        var zero = "0"
        if ((timeInMilis/1000)%60 > 9) zero = ""

        var doubleZero = "00"
        if (timeInMilis%1000 > 9) doubleZero = "0"
        if (timeInMilis%1000 > 99) doubleZero = ""

        return "0${(timeInMilis/1000)/60}:$zero${(timeInMilis/1000)%60}:$doubleZero${timeInMilis%1000}"
    }

    fun startGame() {
        playing = true

        refreshArea()

        timer.start()

        listOfSquares.forEach {
            it.isEnabled = true
        }

        btnStart.isVisible = false
        nameOfLevel.isVisible = false

        pointsField.isVisible = true
        timeField.isVisible = true
        refreshNumberField.isVisible = true
    }

    @SuppressLint("SetTextI18n")
    fun refreshArea() {
        listOfSquares.forEach {
            it.setBackgroundColor(Color.parseColor(listOfColors[Random.nextInt(0, listOfColors.size)]))
        }

        refreshCounter++

        pointsField.text = "Punkty: $points"
        refreshNumberField.text = "$refreshCounter/$refreshNumber"

        listOfSquares[Random.nextInt(0, listOfSquares.size)].setBackgroundColor(Color.parseColor(goldColor))
    }

    fun endGame(con: Context) {
        playing = false

        listOfSquares.forEach {
            it.setBackgroundColor(Color.WHITE)
            it.isEnabled = false
        }

        timer.cancel()

        writeResultToRecords(timeInMilis, points)

        readRecords()
        dbref= FirebaseDatabase
            .getInstance("https://top10-clickers-list-default-rtdb.firebaseio.com/")
            .getReference("lists")
            .child("3")


        btnBack.isVisible = true
        btnStart.isVisible = true
        nameOfLevel.isVisible = true

        pointsField.isVisible = false
        timeField.isVisible = false
        refreshNumberField.isVisible = false

        getGamedata()


        var   times:Float =(timeInMilis).toFloat()/1000
var t=1
        if(arrayList.size==10)
        {
            if(arrayList[9].time!!<times)
                t=0
        }

if(points==refreshNumber && t==1){
        showDialog(con,timeInMilis)}
        else {
        (activity?.let {
            val builder =
                AlertDialog.Builder(it).setMessage("\nTwój wynik: \nCzas: ${timeFormatConvert(timeInMilis)} \nPunkty: $points/$refreshNumber \n\nGratulacje!")
            builder.apply {
                setPositiveButton("DALEJ",
                    DialogInterface.OnClickListener { dialog, id ->
                        //
                    })
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")).show()
}

        //getGamedata()


        refreshCounter = 0
        timeInMilis = 0
        points = 0
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdLevel.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdLevel().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    @SuppressLint("SuspiciousIndentation")
    private fun showDialog(
        con: Context,
        long: Long


    ) {
        val dialog = Dialog(con)

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.get_ontop10_view)
        val playername=dialog.findViewById<EditText>(R.id.editTextTextPersonName)
        val wynik=dialog.findViewById<TextView>(R.id.wyniktext)


        wynik.setText("\nTwój wynik: \nCzas: ${timeFormatConvert(timeInMilis)} \nPunkty: $points/$refreshNumber \n\nGratulacje!")




      val savebutton=dialog.findViewById<MaterialButton>(R.id.savebut)
        savebutton.setOnClickListener(){




            dbref= FirebaseDatabase
                .getInstance("https://top10-clickers-list-default-rtdb.firebaseio.com/")
                .getReference("lists")
                .child("3")
            var database=FirebaseDatabase.getInstance().getReference();
            var id=database.push().key;




       var   times:Float =(long).toFloat()/1000



            var df = DecimalFormat("#.##")
            df.maximumFractionDigits = 2
            var roundedNumber = df.format(times)

            var rounded = roundedNumber.toFloat()


         if(arrayList.size==10)
           id=arrayList[9].id

            var player=Player(id,playername.text.toString(), rounded)
            dbref.child(player.id.toString()).setValue(player)
           dialog.dismiss()

        }
       dialog.show()

}

    private fun getGamedata() {
        arrayList.clear()

        dbref= FirebaseDatabase
            .getInstance("https://top10-clickers-list-default-rtdb.firebaseio.com/")
            .getReference("lists")
            .child("3")

        dbref.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                if(snapshot.exists()){
                    for (gamesnapshot in snapshot.children){
                        val game=gamesnapshot.getValue(Player::class.java)


                        arrayList.add(game!!)




                    }
                    sortPlayersByTime(arrayList)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}


    fun sortPlayersByTime(players: ArrayList<Player>) {
        players.sortBy { it.time }
    }
}