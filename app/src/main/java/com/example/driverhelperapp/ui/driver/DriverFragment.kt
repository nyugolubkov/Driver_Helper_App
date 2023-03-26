package com.example.driverhelperapp.ui.driver

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.R
import com.example.driverhelperapp.models.*
import com.example.driverhelperapp.ui.auth.TokenViewModel
import com.example.driverhelperapp.ui.main.MainFragmentDirections
import com.example.driverhelperapp.utils.ApiResponse
import com.example.driverhelperapp.utils.MaskWatcher
import com.google.android.material.textfield.TextInputEditText
import java.io.*
import java.lang.Integer.min

class DriverFragment : Fragment() {

    companion object {
        fun newInstance() = DriverFragment()
    }

    private val viewModel: DriverViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()

    private lateinit var navController: NavController

    //private var insuranceFile: InsuranceData? = null
    //private var medicineFile: MedicineData? = null
    //private var vmFile: VehicleMaintenanceData? = null

    private var tempContacts: List<Contact>? = null
    private var tempRide: Ride? = null
    private var tempEnabled: Boolean? = null
    private var tempId: Long? = null
    private var tempUsername: String? = null

    //private lateinit var insuranceButton: Button
    //private lateinit var medicineButton: Button
    //private lateinit var vmButton: Button
    //private lateinit var deleteInsuranceImageButton: ImageButton
    //private lateinit var deleteMedicineImageButton: ImageButton
    //private lateinit var deleteVmImageButton: ImageButton

    /*private val pickInsuranceFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = uri.path?.let { it1 -> File(it1) }

            val inputStream = this.context?.contentResolver?.openInputStream(uri)
            val buffer = ByteArray(inputStream!!.available())
            inputStream.read(buffer)
            inputStream.close()

            if (file != null) {
                insuranceFile = InsuranceData(
                    insuranceFile?.expirationDate,
                    FileData(String(buffer), null, file.name),
                    insuranceFile?.id
                )
                insuranceButton.text = "Открыть"
                deleteInsuranceImageButton.visibility = ImageButton.VISIBLE
                insuranceButton.setOnClickListener {
                    val newFile = File(context?.filesDir, insuranceFile!!.file?.name ?: "")
                    val outputStream = FileOutputStream(newFile)
                    outputStream.write(insuranceFile!!.file?.content?.toByteArray())
                    outputStream.close()

                    val fileUri = FileProvider.getUriForFile(this.requireContext(),
                        (context?.packageName) + ".fileprovider",newFile)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = fileUri
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }
            }

            Log.d(TAG, "File 1 selected: $file")
        }
    }

    private val pickMedicineFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = uri.path?.let { it1 -> File(it1) }

            val inputStream = this.context?.contentResolver?.openInputStream(uri)
            val buffer = ByteArray(inputStream!!.available())
            inputStream.read(buffer)
            inputStream.close()

            if (file != null) {
                medicineFile = MedicineData(
                    medicineFile?.expirationDate,
                    FileData(String(buffer), null, file.name),
                    medicineFile?.id
                )
                medicineButton.text = "Открыть"
                deleteMedicineImageButton.visibility = ImageButton.VISIBLE
                medicineButton.setOnClickListener {
                    val newFile = File(context?.filesDir, medicineFile!!.file?.name ?: "")
                    val outputStream = FileOutputStream(newFile)
                    outputStream.write(medicineFile!!.file?.content?.toByteArray())
                    outputStream.close()

                    val fileUri = FileProvider.getUriForFile(this.requireContext(),
                        (context?.packageName) + ".fileprovider", newFile)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = fileUri
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }
            }

            Log.d(TAG, "File 2 selected: $file")
        }
    }

    private val pickVmFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = uri.path?.let { it1 -> File(it1) }

            val inputStream = this.context?.contentResolver?.openInputStream(uri)
            val buffer = ByteArray(inputStream!!.available())
            inputStream.read(buffer)
            inputStream.close()

            if (file != null) {
                vmFile = VehicleMaintenanceData(
                    vmFile?.expirationDate,
                    FileData(String(buffer), null, file.name),
                    vmFile?.id
                )
                vmButton.text = "Открыть"
                deleteVmImageButton.visibility = ImageButton.VISIBLE
                vmButton.setOnClickListener {
                    val newFile = File(context?.filesDir, vmFile!!.file?.name ?: "")
                    val outputStream = FileOutputStream(newFile)
                    outputStream.write(vmFile!!.file?.content?.toByteArray())
                    outputStream.close()

                    val fileUri = FileProvider.getUriForFile(this.requireContext(),
                        (context?.packageName) + ".fileprovider", newFile)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = fileUri
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }
            }

            Log.d(TAG, "File 3 selected: $file")
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        val lastNameEditText = view.findViewById<TextInputEditText>(R.id.lastNameEditText)
        val nameEditText = view.findViewById<TextInputEditText>(R.id.nameDriverEditText)
        val middleNameEditText = view.findViewById<TextInputEditText>(R.id.middleNameEditText)
        val driverPhoneEditText = view.findViewById<TextInputEditText>(R.id.driverPhoneEditText)
        val diseaseEditText = view.findViewById<TextInputEditText>(R.id.diseaseEditText)
        val drugsEditText = view.findViewById<TextInputEditText>(R.id.drugsEditText)
        val bloodTypeSpinner = view.findViewById<Spinner>(R.id.bloodTypeSpinner)
        val rhFactorSpinner = view.findViewById<Spinner>(R.id.rhFactorSpinner)
        //insuranceButton = view.findViewById<Button>(R.id.insuranceButton)
        //deleteInsuranceImageButton = view.findViewById<ImageButton>(R.id.deleteInsuranceImageButton)
        //medicineButton = view.findViewById<Button>(R.id.medicineButton)
        //deleteMedicineImageButton = view.findViewById<ImageButton>(R.id.deleteMedicineImageButton)
        //vmButton = view.findViewById<Button>(R.id.vehicleMaintenanceButton)
        //deleteVmImageButton = view.findViewById<ImageButton>(R.id.deleteVehicleMaintenanceImageButton)
        val maxHoursEditText = view.findViewById<TextInputEditText>(R.id.maxHoursEditText)
        val maxMinutesEditText = view.findViewById<TextInputEditText>(R.id.maxMinutesEditText)
        val maxSecondsEditText = view.findViewById<TextInputEditText>(R.id.maxSecondsEditText)
        val submitButton = view.findViewById<Button>(R.id.submitDriverButton)
        val errorTextView = view.findViewById<TextView>(R.id.errorDriverTextView)
        val progressBar = view.findViewById<ProgressBar>(R.id.driverProgressBar)
        val overlay = view.findViewById<View>(R.id.driverOverlay)

        view.findViewById<ImageButton>(R.id.backDriverButton).setOnClickListener {
            navController.navigate(MainFragmentDirections.actionGlobalMainFragment())
        }

        viewModel.setupOneLineInputFilter(lastNameEditText)
        viewModel.setupOneLineInputFilter(nameEditText)
        viewModel.setupOneLineInputFilter(middleNameEditText)
        viewModel.setupPhoneInputFilter(driverPhoneEditText)
        viewModel.setupMultiLineInputFilter(diseaseEditText)
        viewModel.setupMultiLineInputFilter(drugsEditText)
        driverPhoneEditText.addTextChangedListener(MaskWatcher("8(###) ###-##-##"))
        maxSecondsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString()
                if (text != null && text.length == 3 && before == 0 && count == 1) {
                    val newText = if (start == 0) {
                        if (text[0].toString().toIntOrNull()!! > 5) {
                            "0${s[2]}"
                        } else {
                            "${s[0]}${s[2]}"
                        }
                    } else  {
                        if (text[0].toString().toIntOrNull()!! > 5) {
                            "0${s[1]}"
                        } else {
                            "${s[0]}${s[1]}"
                        }
                    }
                    maxSecondsEditText.setText(newText)
                    maxSecondsEditText.setSelection(min(start + 1, 2))
                } else {

                    val filteredText = text?.replaceFirst("^0+(?!$)", "") ?: ""

                    if (filteredText.length > 2) {
                        var newText = filteredText.substring(0, 2)
                        if (newText.toIntOrNull()!! >= 60) {
                            newText = "0${newText.toIntOrNull()!!.div(10) ?: 0}"
                        }
                        maxSecondsEditText.setText(newText)
                    } else {
                        var newText = filteredText
                        if (newText.toIntOrNull()!! >= 60) {
                            newText = "0${filteredText.toIntOrNull()!!.div(10)}"
                        } else if (newText.toIntOrNull()!! < 10) {
                            newText = "0${filteredText.toIntOrNull() ?: 0}"
                        }
                        if (newText != filteredText) {
                            maxSecondsEditText.setText(newText)
                        }
                    }
                }
            }
        })
        maxMinutesEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString()
                if (text != null && text.length == 3 && before == 0 && count == 1) {
                    val newText = if (start == 0) {
                        if (text[0].toString().toIntOrNull()!! > 5) {
                            "0${s[2]}"
                        } else {
                            "${s[0]}${s[2]}"
                        }
                    } else  {
                        if (text[0].toString().toIntOrNull()!! > 5) {
                            "0${s[1]}"
                        } else {
                            "${s[0]}${s[1]}"
                        }
                    }
                    maxMinutesEditText.setText(newText)
                    maxMinutesEditText.setSelection(min(start + 1, 2))
                } else {
                    val filteredText = text?.replaceFirst("^0+(?!$)", "") ?: ""

                    if (filteredText.length > 2) {
                        var newText = filteredText.substring(0, 2)
                        if (newText.toIntOrNull()!! >= 60) {
                            newText = "0${newText.toIntOrNull()!!.div(10) ?: 0}"
                        }
                        maxMinutesEditText.setText(newText)
                    } else {
                        var newText = filteredText
                        if (newText.toIntOrNull()!! >= 60) {
                            newText = "0${filteredText.toIntOrNull()!!.div(10)}"
                        } else if (newText.toIntOrNull()!! < 10) {
                            newText = "0${filteredText.toIntOrNull() ?: 0}"
                        }
                        if (newText != filteredText) {
                            maxMinutesEditText.setText(newText)
                        }
                    }
                }
            }
        })
        maxHoursEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString()
                if (text != null && text.length == 3 && before == 0 && count == 1) {
                    val newText = if (start == 0) {
                        "${s[0]}${s[2]}"
                    } else  {
                        "${s[0]}${s[1]}"
                    }
                    maxHoursEditText.setText(newText)
                    maxHoursEditText.setSelection(min(start + 1, 2))
                } else {
                    val filteredText = s?.toString()?.replaceFirst("^0+(?!$)", "") ?: ""

                    if (filteredText.length > 2) {
                        val newText = filteredText.substring(0, 2)
                        maxHoursEditText.setText(newText)
                    } else {
                        var newText = filteredText
                        if (newText.toIntOrNull()!! < 10) {
                            newText = "0${filteredText.toIntOrNull() ?: 0}"
                        }
                        if (newText != filteredText) {
                            maxHoursEditText.setText(newText)
                        }
                    }
                }
            }
        })

        val bloodTypeItems = listOf(
            "Группа крови",
            BloodType.O_1.type,
            BloodType.A_2.type,
            BloodType.B_3.type,
            BloodType.AB_4.type
        )
        val bloodTypeImage = R.drawable.blood_def
        bloodTypeSpinner.adapter = BloodSpinnerAdapter(this.requireContext(), bloodTypeItems, bloodTypeImage)

        val rhFactorItems = listOf(
            "Резус",
            RhFactor.NEGATIVE.type,
            RhFactor.POSITIVE.type
        )
        val rhFactorImage = R.drawable.rh_def
        rhFactorSpinner.adapter = BloodSpinnerAdapter(this.requireContext(), rhFactorItems, rhFactorImage)

        if (tokenViewModel.isInternetConnected(this.requireContext())) {
            viewModel.driverGet.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiResponse.Failure -> {
                        progressBar.visibility = View.GONE
                        overlay.visibility = View.GONE
                        errorTextView.text = it.errorMessage
                        submitButton.isEnabled = false
                        Log.d("ERROR", it.errorMessage)
                    }
                    is ApiResponse.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        overlay.visibility = View.VISIBLE
                        errorTextView.text = ""
                        submitButton.isEnabled = false
                    }
                    is ApiResponse.Success -> {
                        progressBar.visibility = View.GONE
                        overlay.visibility = View.GONE
                        errorTextView.text = ""
                        submitButton.isEnabled = true

                        initData(view, it.data)

                        /*
                        deleteInsuranceImageButton.setOnClickListener {
                            insuranceFile = InsuranceData(
                                insuranceFile!!.expirationDate,
                                null, insuranceFile!!.id
                            )
                            insuranceButton.text = "Загрузка"
                            deleteInsuranceImageButton.visibility = ImageButton.GONE
                            deleteInsuranceImageButton.setOnClickListener { }
                            insuranceButton.setOnClickListener {
                                pickInsuranceFile.launch("application/*")
                            }
                        }
                        deleteMedicineImageButton.setOnClickListener {
                            medicineFile = MedicineData(
                                medicineFile!!.expirationDate,
                                null, medicineFile!!.id
                            )
                            medicineButton.text = "Загрузка"
                            deleteMedicineImageButton.visibility = ImageButton.GONE
                            deleteMedicineImageButton.setOnClickListener { }
                            medicineButton.setOnClickListener {
                                pickMedicineFile.launch("application/*")
                            }
                        }
                        deleteVmImageButton.setOnClickListener {
                            vmFile = VehicleMaintenanceData(
                                vmFile!!.expirationDate,
                                null, vmFile!!.id
                            )
                            vmButton.text = "Загрузка"
                            deleteVmImageButton.visibility = ImageButton.GONE
                            deleteVmImageButton.setOnClickListener { }
                            vmButton.setOnClickListener {
                                pickVmFile.launch("application/*")
                            }
                        }

                        if (insuranceFile?.file == null) {
                            insuranceButton.text = "Загрузка"
                            deleteInsuranceImageButton.visibility = ImageButton.GONE
                            insuranceButton.setOnClickListener {
                                pickInsuranceFile.launch("application/*")
                            }
                        } else {
                            try {
                                insuranceButton.text = "Открыть"
                                deleteInsuranceImageButton.visibility = ImageButton.VISIBLE
                                insuranceButton.setOnClickListener {
                                    val file = File(context?.filesDir, insuranceFile!!.file?.name ?: "")
                                    val outputStream = FileOutputStream(file)
                                    outputStream.write(insuranceFile!!.file?.content?.toByteArray())
                                    outputStream.close()

                                    val fileUri = FileProvider.getUriForFile(this.requireContext(),
                                        (context?.packageName) + ".fileprovider", file)
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = fileUri
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    startActivity(intent)
                                }
                            } catch (_: Exception) {
                                Toast.makeText(this.requireContext(),
                                    "Не получилось подгрузить локальные данные",
                                    Toast.LENGTH_LONG).show()
                            }
                        }

                        if (medicineFile?.file == null) {
                            medicineButton.text = "Загрузка"
                            deleteMedicineImageButton.visibility = ImageButton.GONE
                            medicineButton.setOnClickListener {
                                pickMedicineFile.launch("application/*")
                            }
                        } else {
                            try {
                                medicineButton.text = "Открыть"
                                deleteMedicineImageButton.visibility = ImageButton.VISIBLE
                                medicineButton.setOnClickListener {
                                    val file = File(context?.filesDir, medicineFile!!.file?.name ?: "")
                                    val outputStream = FileOutputStream(file)
                                    outputStream.write(medicineFile!!.file?.content?.toByteArray())
                                    outputStream.close()

                                    val fileUri = FileProvider.getUriForFile(this.requireContext(),
                                        (context?.packageName) + ".fileprovider", file)
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = fileUri
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    startActivity(intent)
                                }
                            } catch (_: Exception) {
                                Toast.makeText(this.requireContext(),
                                    "Не получилось подгрузить локальные данные",
                                    Toast.LENGTH_LONG).show()
                            }
                        }


                        if (vmFile?.file == null) {
                            vmButton.text = "Загрузка"
                            deleteVmImageButton.visibility = ImageButton.GONE
                            vmButton.setOnClickListener {
                                pickVmFile.launch("application/*")
                            }
                        } else {
                            try {
                                vmButton.text = "Открыть"
                                deleteVmImageButton.visibility = ImageButton.VISIBLE
                                vmButton.setOnClickListener {
                                    val file = File(context?.filesDir, vmFile!!.file?.name ?: "")
                                    val outputStream = FileOutputStream(file)
                                    outputStream.write(vmFile!!.file?.content?.toByteArray())
                                    outputStream.close()

                                    val fileUri = FileProvider.getUriForFile(this.requireContext(),
                                        (context?.packageName) + ".fileprovider", file)
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = fileUri
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    startActivity(intent)
                                }
                            } catch (_: Exception) {
                                Toast.makeText(this.requireContext(),
                                    "Не получилось подгрузить локальные данные",
                                    Toast.LENGTH_LONG).show()
                            }
                        } */ */ */ */ */ */ */

                        val file = File(context?.filesDir, "driver.dat")
                        val fileOutputStream = FileOutputStream(file)
                        val objectOutputStream = ObjectOutputStream(fileOutputStream)
                        objectOutputStream.writeObject(it.data)
                        objectOutputStream.close()
                    }
                    else -> {}
                }
            }

            viewModel.getDriver(
                object : CoroutinesErrorHandler {
                    @SuppressLint("SetTextI18n")
                    override fun onError(message: String) {
                        errorTextView.text = "Ошибка! $message"
                    }
                })
        } else {
            val file = File(context?.filesDir, "driver.dat")
            val fileInputStream = FileInputStream(file)
            val objectInputStream = ObjectInputStream(fileInputStream)
            var driver: Driver? = null
            try {
                driver = objectInputStream.readObject() as Driver
            } catch (e: ClassCastException) {
                Toast.makeText(this.requireContext(), "Не получилось подгрузить локальные данные", Toast.LENGTH_LONG).show()
            }
            objectInputStream.close()
            if (driver != null) {
                initData(view, driver)
            }
        }

        submitButton.setOnClickListener {

            viewModel.driverUp.observe(viewLifecycleOwner) {
                when(it) {
                    is ApiResponse.Failure -> {
                        progressBar.visibility = View.GONE
                        overlay.visibility = View.GONE
                        errorTextView.text = it.errorMessage
                        submitButton.isEnabled = false
                        Log.d("ERROR", it.errorMessage)
                    }
                    is ApiResponse.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        overlay.visibility = View.VISIBLE
                        errorTextView.text = ""
                        submitButton.isEnabled = false
                    }
                    is ApiResponse.Success -> {
                        progressBar.visibility = View.GONE
                        overlay.visibility = View.GONE
                        errorTextView.text = ""
                        submitButton.isEnabled = true
                        navController.navigate(MainFragmentDirections.actionGlobalMainFragment())
                    }
                    else -> {}
                }
            }
            val bloodType = if (bloodTypeSpinner.selectedItemPosition == 0) {
                null
            } else {
                BloodType.values()[bloodTypeSpinner.selectedItemPosition - 1]
            }
            val rhFactor = if (rhFactorSpinner.selectedItemPosition == 0) {
                null
            } else {
                RhFactor.values()[rhFactorSpinner.selectedItemPosition - 1]
            }
            val duration = maxHoursEditText.text.toString().toLong() * 3600 +
                        maxMinutesEditText.text.toString().toLong() * 60 +
                        maxSecondsEditText.text.toString().toLong()
            viewModel.updateDriver(
                Driver(
                    bloodType?.type,
                    diseaseEditText.text.toString(),
                    tempContacts,
                    tempRide,
                    tempEnabled,
                    tempId,
                    null,//insuranceFile,
                    tempUsername,
                    duration,//durationReq,
                    null,//medicineFile,
                    middleNameEditText.text.toString(),
                    drugsEditText.text.toString(),
                    nameEditText.text.toString(),
                    driverPhoneEditText.text.toString(),
                    rhFactor?.type,
                    lastNameEditText.text.toString(),
                    null//vmFile,
                ),
                object: CoroutinesErrorHandler {
                    @SuppressLint("SetTextI18n")
                    override fun onError(message: String) {
                        errorTextView.text = "Ошибка! $message"
                    }
                })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData(view: View, data: Driver) {

        val lastNameEditText = view.findViewById<TextInputEditText>(R.id.lastNameEditText)
        val nameEditText = view.findViewById<TextInputEditText>(R.id.nameDriverEditText)
        val middleNameEditText = view.findViewById<TextInputEditText>(R.id.middleNameEditText)
        val driverPhoneEditText = view.findViewById<TextInputEditText>(R.id.driverPhoneEditText)
        val diseaseEditText = view.findViewById<TextInputEditText>(R.id.diseaseEditText)
        val drugsEditText = view.findViewById<TextInputEditText>(R.id.drugsEditText)
        val bloodTypeSpinner = view.findViewById<Spinner>(R.id.bloodTypeSpinner)
        val rhFactorSpinner = view.findViewById<Spinner>(R.id.rhFactorSpinner)

        val maxHoursEditText = view.findViewById<TextInputEditText>(R.id.maxHoursEditText)
        val maxMinutesEditText = view.findViewById<TextInputEditText>(R.id.maxMinutesEditText)
        val maxSecondsEditText = view.findViewById<TextInputEditText>(R.id.maxSecondsEditText)

        lastNameEditText.setText(data.surname)
        nameEditText.setText(data.name)
        middleNameEditText.setText(data.middleName)
        driverPhoneEditText.setText(data.phoneNumber)
        diseaseEditText.setText(data.chronicDisease)
        drugsEditText.setText(data.myMedicines)
        bloodTypeSpinner.setSelection((data.bloodType?.let { it1 ->
            BloodType.fromType(
                it1
            )
        }?.ordinal
            ?: -1) + 1)
        rhFactorSpinner.setSelection((data.rhFactor?.let { it1 ->
            RhFactor.fromType(
                it1
            )
        }?.ordinal ?: -1) + 1)

        val hours = data.maxDriveDurationSeconds?.div(3600) ?: 0
        val minutes = data.maxDriveDurationSeconds?.div(60)?.mod(60) ?: 0
        val seconds = data.maxDriveDurationSeconds?.mod(60) ?: 0
        maxHoursEditText.setText("${hours.div(10)}${hours.mod(10)}")
        maxMinutesEditText.setText("${minutes.div(10)}${minutes.mod(10)}")
        maxSecondsEditText.setText("${seconds.div(10)}${seconds.mod(10)}")

        //insuranceFile = data.insuranceData
        //medicineFile = data.medicineData
        //vmFile = data.vehicleMaintenanceData

        tempContacts = data.contacts
        tempRide = data.currentRide
        tempEnabled = data.enabled
        tempId = data.id
        tempUsername = data.login
    }


}