package com.example.driverhelperapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.LocationProvider
import com.example.driverhelperapp.R
import com.example.driverhelperapp.models.BloodType
import com.example.driverhelperapp.models.Contact
import com.example.driverhelperapp.models.RhFactor
import com.example.driverhelperapp.ui.auth.TokenViewModel
import com.example.driverhelperapp.ui.contacts.ContactViewModel
import com.example.driverhelperapp.ui.driver.DriverViewModel
import com.example.driverhelperapp.utils.ApiResponse
import com.google.android.gms.location.LocationServices
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.translate.Translation
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.lang.Integer.min
import java.util.*


@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val driverViewModel: DriverViewModel by activityViewModels()
    private val rideViewModel: RideViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val contactViewModel: ContactViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()

    private lateinit var navController: NavController

    private var compass: Compass? = null

    private lateinit var weatherImageView: ImageView
    private lateinit var weatherTextView: TextView
    private lateinit var tempTextView: TextView

    private lateinit var stopwatchButton: Button
    private lateinit var stopwatchTextView: TextView
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var localityProvider: LocationProvider
    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                localityProvider.fetchLocation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        view.findViewById<ImageButton>(R.id.driverButton).setOnClickListener {
            navController.navigate(MainFragmentDirections.actionMainFragmentToDriverFragment())
        }

        tokenLogic(view)

        weatherLogic(view)

        contactLogic(view)

        buttonsLogic(view)

        stopwatchLogic(view)

        driverViewModel.driverGet.observe(viewLifecycleOwner) { it ->
            when(it) {
                is ApiResponse.Failure -> {
                    Toast.makeText(this.context, it.errorMessage, Toast.LENGTH_LONG).show();
                    Log.d("ERROR", it.errorMessage);
                }
                is ApiResponse.Success -> {
                    val file = File(context?.filesDir, "driver.dat")
                    val fileOutputStream = FileOutputStream(file)
                    val objectOutputStream = ObjectOutputStream(fileOutputStream)
                    objectOutputStream.writeObject(it.data)
                    objectOutputStream.close()
                }
                else -> {}
            }
        }

        if (tokenViewModel.isInternetConnected(this.requireContext()))
            driverViewModel.getDriver(
                object: CoroutinesErrorHandler {
                @SuppressLint("SetTextI18n")
                override fun onError(message: String) {
                    Toast.makeText(this@MainFragment.context, "Ошибка f! $message", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        compass?.start()
    }

    override fun onPause() {
        super.onPause()
        compass?.stop()
    }

    private fun updateElapsedTime() {
        val elapsedSeconds = rideViewModel.getElapsedTime() / 1000
        stopwatchTextView.text = String.format(
            "%02d:%02d:%02d",
            elapsedSeconds / 3600,
            (elapsedSeconds % 3600) / 60,
            elapsedSeconds % 60)
    }

    private fun translateToRussian(text: String): String {
        val translate: Translate = TranslateOptions.getDefaultInstance().service
        val translation: Translation = translate.translate(text, Translate.TranslateOption.targetLanguage("ru"))
        return translation.translatedText
    }

    private fun tokenLogic(view: View) {
        tokenViewModel.accessToken.observe(viewLifecycleOwner) { token ->
            if (token == null)
                navController.navigate(R.id.action_global_logInFragment)
        }

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            tokenViewModel.deleteAccessToken()
            tokenViewModel.deleteRefreshToken()
        }
    }

    private fun weatherLogic(view: View) {
        weatherImageView = view.findViewById(R.id.weatherImageView)
        weatherTextView = view.findViewById(R.id.weatherTextView)
        tempTextView = view.findViewById(R.id.tempTextView)

        localityProvider = LocationProvider(
            requireActivity(),
            LocationServices.getFusedLocationProviderClient(requireActivity()),
            permReqLauncher,
            weatherViewModel
        )
        localityProvider.fetchLocation()

        weatherViewModel.weather.observe(viewLifecycleOwner) {
            when(it) {
                is ApiResponse.Failure -> {
                    weatherImageView.setImageResource(0)
                    weatherTextView.text = ""
                    weatherImageView.setOnClickListener {}
                    Log.e("ERROR", it.errorMessage);
                }
                is ApiResponse.Success -> {
                    val imageResource = weatherViewModel.getWeatherImage(it.data.weather[0].icon)
                    if (imageResource != null) {
                        weatherImageView.setImageResource(imageResource)
                    }
                    weatherTextView.text = it.data.weather[0].main//translateToRussian(it.data.weather[0].main)
                    val temp = if (it.data.main.temp > 0) {
                        "+${it.data.main.temp}℃"
                    } else {
                        "${it.data.main.temp}℃"
                    }
                    tempTextView.text = temp
                    val feelsLike = if (it.data.main.feels_like > 0) {
                        "+${it.data.main.feels_like}℃"
                    } else {
                        "${it.data.main.feels_like}℃"
                    }
                    val desc = it.data.weather[0].description//translateToRussian(it.data.weather[0].description)
                    weatherImageView.setOnClickListener {
                        Toast.makeText(this@MainFragment.context, "$desc \nчувствуется как $feelsLike", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {}
            }
        }
    }

    private fun contactLogic(view: View) {

        if (tokenViewModel.isInternetConnected(this.requireContext())) {

            contactViewModel.contacts.observe(viewLifecycleOwner) { it ->
                when(it) {
                    is ApiResponse.Failure -> {
                        Toast.makeText(this.context, it.errorMessage, Toast.LENGTH_LONG).show();
                        Log.d("ERROR", it.errorMessage);
                    }
                    is ApiResponse.Success -> {
                        formContactLayout(view, it.data)

                        val file = File(context?.filesDir, "contacts.dat")
                        val fileOutputStream = FileOutputStream(file)
                        val objectOutputStream = ObjectOutputStream(fileOutputStream)
                        objectOutputStream.writeObject(it.data)
                        objectOutputStream.close()
                    }
                    else -> {}
                }
            }

            contactViewModel.getContacts(
                object : CoroutinesErrorHandler {
                    @SuppressLint("SetTextI18n")
                    override fun onError(message: String) {
                        Toast.makeText(context, "Ошибка! $message", Toast.LENGTH_LONG).show()
                    }
                }
            )
        } else {
            val file = File(context?.filesDir, "contacts.dat")
            val fileInputStream = FileInputStream(file)
            val objectInputStream = ObjectInputStream(fileInputStream)
            var contacts: List<Contact>? = null
            try {
                contacts = objectInputStream.readObject() as List<Contact>
            } catch (_: java.lang.ClassCastException) {
                Toast.makeText(this.requireContext(), "Не получилось подгрузить локальные данные", Toast.LENGTH_LONG).show()
            }
            objectInputStream.close()
            if (contacts != null) {
                formContactLayout(view, contacts)
            }
        }
    }

    private fun buttonsLogic(view: View) {
        view.findViewById<LinearLayout>(R.id.mapButtonLayout).setOnClickListener {
            val location = weatherViewModel.getLocation()
            val gmmIntentUri: Uri = Uri.parse("geo:${location?.lat},${location?.lon}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(activity, "Приложение карты не устаовлено.", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<LinearLayout>(R.id.stationButtonLayout).setOnClickListener {
            val location = weatherViewModel.getLocation()
            val gmmIntentUri: Uri = Uri.parse("geo:${location?.lat},${location?.lon}?q=заправка")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(activity, "Приложение карты не устаовлено.", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<LinearLayout>(R.id.sosButtonLayout).setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:112")
            startActivity(dialIntent)
        }

        try {
            compass = Compass(this.requireContext())
        } catch (e:IllegalStateException) {
            e.printStackTrace()
            Toast.makeText(this.context, "Ни акселерометр, ни магнитный датчик найдены не были." , Toast.LENGTH_LONG).show()
        }
        compass?.arrowView = view.findViewById<ImageView>(R.id.compassButtonImage)
    }

    private fun stopwatchLogic(view: View) {

        stopwatchButton = view.findViewById(R.id.stopwatchButton)
        stopwatchTextView = view.findViewById(R.id.stopwatchTextView)

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                updateElapsedTime()
                handler.postDelayed(this, 10) // update every 10 milliseconds
            }
        }

        rideViewModel.start.observe(viewLifecycleOwner) {
            when(it) {
                is ApiResponse.Failure -> {
                    Toast.makeText(this.context, it.errorMessage, Toast.LENGTH_LONG).show();
                    Log.d("ERROR", it.errorMessage);
                }
                is ApiResponse.Success -> {
                    handler.post(runnable)
                    stopwatchButton.text = getString(R.string.stop)
                }
                else -> {}
            }
        }

        rideViewModel.finish.observe(viewLifecycleOwner) {
            when(it) {
                is ApiResponse.Failure -> {
                    Toast.makeText(this.context, it.errorMessage, Toast.LENGTH_LONG).show();
                    Log.d("ERROR", it.errorMessage);
                }
                is ApiResponse.Success -> {
                    handler.removeCallbacks(runnable)
                    stopwatchButton.text = getString(R.string.reset)
                    updateElapsedTime()
                }
                else -> {}
            }
        }

        stopwatchButton.setOnClickListener {
            if (rideViewModel.rideStatus() == CarRideStatus.BASE) {
                rideViewModel.startRide(
                    object : CoroutinesErrorHandler {
                        @SuppressLint("SetTextI18n")
                        override fun onError(message: String) {
                            Toast.makeText(context, "Ошибка! $message", Toast.LENGTH_LONG).show()
                        }
                    })
            } else if (rideViewModel.rideStatus() == CarRideStatus.STARTED) {
                rideViewModel.finishRide(
                    object : CoroutinesErrorHandler {
                        @SuppressLint("SetTextI18n")
                        override fun onError(message: String) {
                            Toast.makeText(context, "Ошибка! $message", Toast.LENGTH_LONG).show()
                        }
                    })
            } else {
                rideViewModel.resetRide()
                stopwatchButton.text = getString(R.string.start)
                updateElapsedTime()
            }
        }
    }

    private fun formContactLayout(view: View, data: List<Contact>) {
        val layouts = listOf<LinearLayout>(
            view.findViewById<LinearLayout>(R.id.firstContactLinearLayout),
            view.findViewById<LinearLayout>(R.id.secondContactLinearLayout),
            view.findViewById<LinearLayout>(R.id.thirdContactLinearLayout),
            view.findViewById<LinearLayout>(R.id.fourthContactLinearLayout)
        )
        val contacts = listOf<List<TextView>>(
            listOf(
                view.findViewById<TextView>(R.id.firstContactImageTextView),
                view.findViewById<TextView>(R.id.firstContactNameTextView),
                view.findViewById<TextView>(R.id.firstContactPhoneTextView)
            ),
            listOf(
                view.findViewById<TextView>(R.id.secondContactImageTextView),
                view.findViewById<TextView>(R.id.secondContactNameTextView),
                view.findViewById<TextView>(R.id.secondContactPhoneTextView)
            ),
            listOf(
                view.findViewById<TextView>(R.id.thirdContactImageTextView),
                view.findViewById<TextView>(R.id.thirdContactNameTextView),
                view.findViewById<TextView>(R.id.thirdContactPhoneTextView)
            ),
            listOf(
                view.findViewById<TextView>(R.id.fourthContactImageTextView),
                view.findViewById<TextView>(R.id.fourthContactNameTextView),
                view.findViewById<TextView>(R.id.fourthContactPhoneTextView)
            ),
        )
        for (el in layouts) {
            el.visibility = LinearLayout.INVISIBLE
            el.setOnClickListener {}
            el.setOnLongClickListener { false }
        }
        for (i in 0..min(3, data.size - 1)) {
            val dataId = data[i].id
            val dataName = data[i].name
            val dataNumber = data[i].number
            val initials = dataName.split(" ")
                .filter { name -> name.isNotBlank() }
                .map { word -> word.substring(0, 1) }
                .take(2)
                .joinToString("")
            contacts[i][0].text = initials
            contacts[i][0].setTextColor(Color.WHITE)
            val shapeDrawable = ShapeDrawable(OvalShape())
            shapeDrawable.paint.color = Color.BLUE
            contacts[i][0].background = shapeDrawable
            contacts[i][1].text = dataName
            contacts[i][2].text = dataNumber
            layouts[i].setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$dataNumber")
                startActivity(dialIntent)
            }
            layouts[i].setOnLongClickListener {
                contactViewModel.setChangingContact(dataId, dataName, dataNumber)
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToChangeContactFragment()
                )
                false
            }
            layouts[i].visibility = LinearLayout.VISIBLE
        }

        if (data.size < 4) {
            contacts[data.size][0].text = "+"
            contacts[data.size][0].setTextColor(Color.BLUE)
            val shapeDrawable = ShapeDrawable(OvalShape())
            shapeDrawable.paint.color = Color.WHITE
            contacts[data.size][0].background = shapeDrawable
            contacts[data.size][1].text = "Добавить контакт"
            contacts[data.size][2].text = ""
            layouts[data.size].setOnClickListener {
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToCreateContactFragment())
            }
            layouts[data.size].setOnLongClickListener { false }
            layouts[data.size].visibility = LinearLayout.VISIBLE
        }
    }
}